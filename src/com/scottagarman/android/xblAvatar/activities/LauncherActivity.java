package com.scottagarman.android.xblAvatar.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import com.scottagarman.android.xblAvatar.activities.actionbar.ABLauncherActivity;
import com.scottagarman.android.xblAvatar.activities.nonactionbar.NABLauncherActivity;

public class LauncherActivity extends Activity{
    private final static String TAG = "XBL/Launcher";
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Let's see which activity to open
        Log.d(TAG, "OS Version: " + Build.VERSION.SDK_INT);
        if(Build.VERSION.SDK_INT > 10) {
            startActivity(new Intent(this, ABLauncherActivity.class));
        }else {
            startActivity(new Intent(this, NABLauncherActivity.class));
        }

        // end this
        finish();
    }
}
