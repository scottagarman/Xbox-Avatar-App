package com.scottagarman.android.xblAvatar.utils;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.*;
import java.nio.channels.FileChannel;

public class FileIOHelper {
    private static final String TAG = "XBA/FileIOHelper";

    // Used for handling messages from other threads
	private final static int OPERATION_SUCCESS = 900;
	private final static int OPERATION_FAILURE = 901;

    // Is SD card mounted
    public static boolean canWriteToExternal(){
        boolean mExternalStorageAvailable;
        boolean mExternalStorageWriteable;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }

        if(mExternalStorageAvailable && mExternalStorageWriteable)
            return true; // yep

        return false; // nope
    }

    // Create our Sincerely Dirs
    private static void mkdirs(File dirs, boolean noMedia){
        if(canWriteToExternal()){
            if(dirs != null && !dirs.isDirectory()){
                dirs.mkdirs();
                if(noMedia) writeFile(new File(dirs.getAbsolutePath(), ".nomedia"), "(╯°□°)╯ ┻━┻");
            }
        }
    }

    private static void writeFile(File f, Object o){
        if(canWriteToExternal()){
            try {
                f.createNewFile();
                FileOutputStream fos = new FileOutputStream(f.getAbsolutePath());
                ObjectOutputStream out = new ObjectOutputStream(fos);
                out.writeObject(o);
                out.close();
            }catch (IOException e) {
                Log.d(TAG, "File failed to write");
                e.printStackTrace();
            }
        }
    }

    private static void writeBytes(File f, byte[] data){
        if(canWriteToExternal()){
            try {
                f.createNewFile();
                FileOutputStream fos = new FileOutputStream(f.getAbsolutePath());
                fos.write(data);
                fos.close();
            }catch (IOException e) {
                Log.d(TAG, "File failed to write");
                e.printStackTrace();
            }
        }
    }

    // Load an object from a directory
    public static void loadFile(final File file, final FileIOListener listener){
        if(file == null) if(listener != null) listener.onFailure();

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {

                switch(msg.what){

                // Successful network operation
                case OPERATION_SUCCESS:
                    if(listener != null) listener.onSuccess(msg.obj);
                    break;
                // Failed network operation
                case OPERATION_FAILURE:
                    if(listener != null)  listener.onFailure();
                    break;
                }

                super.handleMessage(msg);
            }
	    };

        new Thread(){
            public void run(){
                Object loadedFile;
                Message msg = Message.obtain();
                try{
                    FileInputStream fis = new FileInputStream(file);
                    ObjectInputStream in = new ObjectInputStream(fis);
                    loadedFile = in.readObject();
                    in.close();

                    msg.what = OPERATION_SUCCESS;
                    msg.obj = loadedFile;
                }catch(Exception e){
                    Log.d(TAG, "Failed to read file");
                    e.printStackTrace();
                    msg.what = OPERATION_FAILURE;
                }

                handler.sendMessage(msg);
            }
        }.start();
    }
    // Save bytes to a directory
    public static void saveFile(final byte[] data, final File file, final FileIOListener listener) {
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {

                switch(msg.what){

                // Successful network operation
                case OPERATION_SUCCESS:
                    if(listener != null) listener.onSuccess(msg.obj);
                    break;
                // Failed network operation
                case OPERATION_FAILURE:
                    if(listener != null) listener.onFailure();
                    break;
                }

                super.handleMessage(msg);
            }
	    };

        new Thread(){
            public void run(){
                Message msg = Message.obtain();

                // Need to add a way to pass back errors here
                mkdirs(file.getParentFile(), true); // make directory and .nomedia
                writeBytes(file, data);

                msg.what = OPERATION_SUCCESS;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        }.start();
    }
    // Save object to a directory
    public static void saveFile(final Object obj, final File file, final FileIOListener listener) {
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {

                switch(msg.what){

                // Successful network operation
                case OPERATION_SUCCESS:
                    if(listener != null) listener.onSuccess(msg.obj);
                    break;
                // Failed network operation
                case OPERATION_FAILURE:
                    if(listener != null) listener.onFailure();
                    break;
                }

                super.handleMessage(msg);
            }
	    };

        new Thread(){
            public void run(){
                Message msg = Message.obtain();

                // Need to add a way to pass back errors here
                mkdirs(file.getParentFile(), true); // make directory and .nomedia
                writeFile(file, obj);

                msg.what = OPERATION_SUCCESS;
                msg.obj = obj;
                handler.sendMessage(msg);
            }
        }.start();
    }

    // Save object to a directory
    public static void saveFile(final Object obj, final File file) {
        new Thread(){
            public void run(){
                // Need to add a way to pass back errors here
                mkdirs(file.getParentFile(), true); // make directory and .nomedia
                writeFile(file, obj);
            }
        }.start();
    }

        // Save stream
    public static void saveToInternalStorage(final Context ctx, final Object obj, final String fileLoc, final FileIOListener listener) {
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {

                switch(msg.what){

                // Successful network operation
                case OPERATION_SUCCESS:
                    if(listener != null) listener.onSuccess(msg.obj);
                    break;
                // Failed network operation
                case OPERATION_FAILURE:
                    if(listener != null) listener.onFailure();
                    break;
                }

                super.handleMessage(msg);
            }
	    };

        new Thread(){
            public void run(){
                Message msg = Message.obtain();
                FileOutputStream fos = null;
                ObjectOutputStream oos = null;
                try {
                    fos = ctx.openFileOutput(fileLoc, Context.MODE_PRIVATE);
                    oos = new ObjectOutputStream(fos);
                    oos.writeObject(obj);
                    oos.close();
                    fos.close();
                    msg.what = OPERATION_SUCCESS;
                    msg.obj = obj;
                } catch (IOException e) {
                    e.printStackTrace();
                    msg.what = OPERATION_FAILURE;
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = OPERATION_FAILURE;
                }

                handler.sendMessage(msg);
            }
        }.start();
    }

    // read stream
    public static void readFromInternalStorage(final Context ctx, final String fileLoc, final FileIOListener listener) {
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {

                switch(msg.what){

                // Successful network operation
                case OPERATION_SUCCESS:
                    if(listener != null) listener.onSuccess(msg.obj);
                    break;
                // Failed network operation
                case OPERATION_FAILURE:
                    if(listener != null) listener.onFailure();
                    break;
                }

                super.handleMessage(msg);
            }
	    };

        new Thread(){
            public void run(){
                Message msg = Message.obtain();
                Object loadedFile = null;
                FileInputStream fis = null;
                ObjectInputStream ois = null;
                try {
                    fis = ctx.openFileInput(fileLoc);
                    ois = new ObjectInputStream(fis);
                    loadedFile = ois.readObject();
                    ois.close();
                    fis.close();
                    msg.what = OPERATION_SUCCESS;
                    msg.obj = loadedFile;
                } catch (IOException e) {
                    e.printStackTrace();
                    msg.what = OPERATION_FAILURE;
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = OPERATION_FAILURE;
                }

                handler.sendMessage(msg);
            }
        }.start();
    }

    public static void copyFile(final File source, final File dest) {
        new Thread(){
            @Override
            public void run() {
                mkdirs(dest.getParentFile(), false);
                try{
                    if(canWriteToExternal()) {
                        if(source.exists()) {
                            FileChannel src = new FileInputStream(source).getChannel();
                            FileChannel dst = new FileOutputStream(dest).getChannel();
                            dst.transferFrom(src, 0, src.size());
                            src.close();
                            dst.close();
                            // Yay!
                        }else {
                            // Error source does not exist
                        }
                    }else {
                        // error can't write
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    // Error writing file
                }
            }
        }.start();
    }

    public static void copyFile(final File source, final File dest, final FileIOListener listener) {
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {

                switch(msg.what){

                // Successful network operation
                case OPERATION_SUCCESS:
                    if(listener != null) listener.onSuccess(msg.obj);
                    break;
                // Failed network operation
                case OPERATION_FAILURE:
                    if(listener != null) listener.onFailure();
                    break;
                }

                super.handleMessage(msg);
            }
	    };

        new Thread(){
            @Override
            public void run() {
                Message msg = Message.obtain();

                mkdirs(dest.getParentFile(), false);
                try{
                    if(canWriteToExternal()) {
                        if(source.exists()) {
                            FileChannel src = new FileInputStream(source).getChannel();
                            FileChannel dst = new FileOutputStream(dest).getChannel();
                            dst.transferFrom(src, 0, src.size());
                            src.close();
                            dst.close();
                            // Yay!
                            msg.what = OPERATION_SUCCESS;
                            msg.obj = dest;
                        }else {
                            // Error source does not exist
                        }
                    }else {
                        // error can't write
                        msg.what = OPERATION_FAILURE;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    // Error writing file
                    msg.what = OPERATION_FAILURE;
                }

                handler.sendMessage(msg);
            }
        }.start();
    }


    public static interface FileIOListener{
        public void onSuccess(Object obj);
        public void onFailure();
    }
}
