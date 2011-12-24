package com.scottagarman.android.xblAvatar.models;

import android.content.Context;
import com.scottagarman.android.xblAvatar.utils.FileIOHelper;

import java.util.LinkedHashMap;

public class AvatarsModel {

    private static final String FILE_NAME = "avatar_list";
    
    public static void getAvatarList(Context ctx, final AvatarsModelListener listener) {


        FileIOHelper.readFromInternalStorage(ctx, FILE_NAME, new FileIOHelper.FileIOListener() {
            @Override
            public void onSuccess(Object obj) {
                if(listener != null) listener.onAvatarsModelCompete((LinkedHashMap) obj);
            }

            @Override
            public void onFailure() {
                if(listener != null) listener.onAvatarsModelFail();
            }
        });

    }

    public static void saveAvatarList(Context ctx, LinkedHashMap list) {
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
        public void onAvatarsModelCompete(LinkedHashMap list);
        public void onAvatarsModelFail();
    }
    
}
