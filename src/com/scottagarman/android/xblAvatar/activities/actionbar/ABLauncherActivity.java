package com.scottagarman.android.xblAvatar.activities.actionbar;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import com.scottagarman.android.xblAvatar.TabListener;
import com.scottagarman.android.xblAvatar.activities.fragments.HomeFragment;

public class ABLauncherActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

            // Notice that setContentView() is not used, because we use the root
            // android.R.id.content as the container for each fragment

            // setup action bar for tabs
            ActionBar actionBar = getActionBar();
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);

            ActionBar.Tab tab = actionBar.newTab()
                    .setIcon(android.R.drawable.ic_dialog_email)
                    .setTabListener(new TabListener<HomeFragment>(this, "first", HomeFragment.class));
            actionBar.addTab(tab);

            tab = actionBar.newTab()
                .setIcon(android.R.drawable.ic_dialog_dialer)
                .setTabListener(new TabListener<HomeFragment>(this, "second", HomeFragment.class));
            actionBar.addTab(tab);

            // check for last open tab
            if (savedInstanceState != null) {
                actionBar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
            }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save current selected tab
        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
    }
}
