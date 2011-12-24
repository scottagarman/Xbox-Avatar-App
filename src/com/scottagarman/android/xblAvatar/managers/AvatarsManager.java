package com.scottagarman.android.xblAvatar.managers;

import android.content.Context;
import com.scottagarman.android.xblAvatar.models.AvatarsModel;

import java.util.LinkedHashMap;

public class AvatarsManager {

    private Context mCtx;
    private AvatarsManagerListener mListener;
    private LinkedHashMap<String, String> mAvatarList;
    
    public AvatarsManager(Context ctx) {
        mCtx = ctx;
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
            public void onAvatarsModelCompete(LinkedHashMap list) {
                mAvatarList = list;
                if(mListener != null) mListener.onAvatarsLoaded();
            }

            @Override
            public void onAvatarsModelFail() {
                // empty, make a new one
                mAvatarList = new LinkedHashMap<String, String>();
                if(mListener != null) mListener.onAvatarsLoaded();
            }
        });
    }

    public interface AvatarsManagerListener {
        public void onAvatarsLoaded();
        public void onAvatarPhotoLoaded(int index);
    }

    public interface AvatarsManagerImageListener {
        public void onImageDownloaded();

    }
}


