package com.scottagarman.android.xblAvatar.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import com.scottagarman.android.xblAvatar.models.AvatarsModel;
import com.scottagarman.android.xblAvatar.operations.IndexedNetworkOperation;
import com.scottagarman.android.xblAvatar.operations.NetworkOperation;

import java.util.ArrayList;
import java.util.HashMap;

public class AvatarsManager implements NetworkOperation.NetworkOperationCompleteListener {
    private static final int MAX_THUMBS = 50;
    private static final int MAX_AVATARS = 5;
    private static final int SECTION_THUMB = 0;
    private static final int SECTION_AVATARS = 1;

    
    private Context mCtx;
    private AvatarsManagerListener mListener;
    private ArrayList<HashMap<String, String>>  mAvatarList;

    // Thumbs
    private LruCache mThumbs;
    private HashMap<String, IndexedNetworkOperation> mThumbDownloaders;

    // Avatars
    private LruCache mAvatars;
    private HashMap<String, IndexedNetworkOperation> mAvatarDownloaders;

    
    public AvatarsManager(Context ctx) {
        mCtx = ctx;
        
        mThumbs = new LruCache<Integer, byte[]>(MAX_THUMBS);
        mThumbDownloaders = new HashMap<String, IndexedNetworkOperation>();

        mAvatars = new LruCache<Integer, byte[]>(MAX_THUMBS);
        mAvatarDownloaders = new HashMap<String, IndexedNetworkOperation>();
    }

    public void registerListener(AvatarsManagerListener listener) {
        mListener = listener;
    }

    public void unregisterListener() {
        mListener = null;
    }

    public void loadAvatars() {
        AvatarsModel.getAvatarList(mCtx, new AvatarsModel.AvatarsModelListener() {
            @Override
            public void onAvatarsModelCompete(ArrayList<HashMap<String, String>> list) {
                mAvatarList = list;
                if(mListener != null) mListener.onAvatarsLoaded();
            }

            @Override
            public void onAvatarsModelFail() {
                // empty, make a new one
                mAvatarList = new ArrayList<HashMap<String, String>>();
                if(mListener != null) mListener.onAvatarsLoaded();
            }
        });
    }
    
    /* standard listview info */
    public int getSize() {
        if(mAvatarList != null) return mAvatarList.size();
        return 0;
    }

    public String getAvatarUrlAtIndex(int index) {
        if(mAvatarList != null) return generateBodyUrl(mAvatarList.get(index).get("tag"));
        return null;
    }

    public String getThumbUrlAtIndex(int index) {
        if(mAvatarList != null) return generateThumbUrl(mAvatarList.get(index).get("tag"));
        return null;
    }
    
    public void addAvatar(String tag) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("tag", tag);
        mAvatarList.add(map);
        AvatarsModel.saveAvatarList(mCtx, mAvatarList);
    }
    
    private String generateBodyUrl(String tag) {
        return "http://avatar.xboxlive.com/avatar/"+ tag.trim() +"/avatar-body.png"; // check to make sure this is correct
    }

    private String generateThumbUrl(String tag) {
        return "http://avatar.xboxlive.com/avatar/"+ tag.trim() +"/avatarpic-l.png"; // check to make sure this is correct
    }
    
    public void remoteAvatarAtIndex(int index) {
        if(mAvatarList != null && index < mAvatarList.size()){
            mAvatarList.remove(index);
            AvatarsModel.saveAvatarList(mCtx, mAvatarList); //TODO: get async ones
        }
    }
    
    public Bitmap getLargeBitmapAtIndex(int index) {
        byte[] image = (byte[]) mAvatars.get(mAvatarList.get(index).get("tag"));
        if(image != null) {
            return BitmapFactory.decodeByteArray(image, 0, image.length);
        }else if(mAvatarDownloaders.get(mAvatarList.get(index).get("tag")) == null){
            IndexedNetworkOperation downloader = new IndexedNetworkOperation(generateBodyUrl(mAvatarList.get(index).get("tag")), this, index, SECTION_AVATARS);
            mAvatarDownloaders.put(mAvatarList.get(index).get("tag"), downloader);
            downloader.beginOperation();
        }

        return null;
    }

    public Bitmap getBitmapAtIndex(int index) {
        byte[] image = (byte[]) mThumbs.get(mAvatarList.get(index).get("tag"));
        if(image != null) {
            return BitmapFactory.decodeByteArray(image, 0, image.length);
        }else if(mThumbDownloaders.get(mAvatarList.get(index).get("tag")) == null){
            IndexedNetworkOperation downloader = new IndexedNetworkOperation(generateThumbUrl(mAvatarList.get(index).get("tag")), this, index, SECTION_THUMB);
            mThumbDownloaders.put(mAvatarList.get(index).get("tag"), downloader);
            downloader.beginOperation();
        }

        return null;
    }
    @Override
    public void onNetworkOperationComplete(NetworkOperation operation) {
        if(operation.responseData != null && operation.responseData.length > 0) {
            switch (((IndexedNetworkOperation)operation).section) {
                case SECTION_THUMB:
                    mThumbs.put(mAvatarList.get(((IndexedNetworkOperation) operation).index).get("tag"), operation.responseData);
                    if(mListener != null) mListener.onAvatarThumbLoaded(((IndexedNetworkOperation) operation).index);
                    mThumbDownloaders.remove(mAvatarList.get(((IndexedNetworkOperation) operation).index).get("tag"));
                    break;

                case SECTION_AVATARS :
                    mAvatars.put(mAvatarList.get(((IndexedNetworkOperation) operation).index).get("tag"), operation.responseData);
                    if(mListener != null) mListener.onAvatarLoaded(((IndexedNetworkOperation) operation).index);
                    mAvatarDownloaders.remove(mAvatarList.get(((IndexedNetworkOperation) operation).index).get("tag"));
                    break;

                default:

                    break;
            }
        }
    }

    @Override
    public void onNetworkOperationCompleteWithError(NetworkOperation operation) {
        switch (((IndexedNetworkOperation)operation).section) {
            case SECTION_THUMB:
                mThumbDownloaders.remove(mAvatarList.get(((IndexedNetworkOperation) operation).index).get("tag"));
                break;

            case SECTION_AVATARS:
                mAvatarDownloaders.remove(mAvatarList.get(((IndexedNetworkOperation) operation).index).get("tag"));
                break;

            default:

                break;
        }
    }

    public interface AvatarsManagerListener {
        public void onAvatarsLoaded();
        public void onAvatarThumbLoaded(int index);
        public void onAvatarLoaded(int index);
    }
}


