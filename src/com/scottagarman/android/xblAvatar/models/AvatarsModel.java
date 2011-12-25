package com.scottagarman.android.xblAvatar.models;

import android.content.Context;
import com.scottagarman.android.xblAvatar.utils.FileIOHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class AvatarsModel {

    private static final String FILE_NAME = "avatar_list";
    
    public static void getAvatarList(Context ctx, final AvatarsModelListener listener) {


        FileIOHelper.readFromInternalStorage(ctx, FILE_NAME, new FileIOHelper.FileIOListener() {
            @Override
            public void onSuccess(Object obj) {
                if(listener != null) listener.onAvatarsModelCompete((ArrayList<HashMap<String, String>>) obj);
            }

            @Override
            public void onFailure() {
                if(listener != null) listener.onAvatarsModelFail();
            }
        });

    }

    public static void saveAvatarList(Context ctx, ArrayList<HashMap<String, String>> list) {
        FileIOHelper.saveToInternalStorage(ctx, list, FILE_NAME, new FileIOHelper.FileIOListener() {
            @Override
            public void onSuccess(Object obj) {
                // not reporting
            }

            @Override
            public void onFailure() {
               // not reporting
            }
        });
    }


    public interface AvatarsModelListener {
        public void onAvatarsModelCompete(ArrayList<HashMap<String, String>> list);
        public void onAvatarsModelFail();
    }
    
}
