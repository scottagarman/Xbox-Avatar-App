package com.scottagarman.android.xblAvatar.activities.actionbar;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import com.scottagarman.android.xblAvatar.activities.fragments.AboutFragment;
import com.scottagarman.android.xblAvatar.activities.fragments.FriendsListFragment;
import com.scottagarman.android.xblAvatar.activities.fragments.HomeFragment;
import com.scottagarman.android.xblAvatar.adapters.TabsAdapter;

public class ABLauncherActivity extends FragmentActivity {

    private ViewPager mViewPager;
    private TabsAdapter mTabsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mViewPager = new ViewPager(this);
        mViewPager.setId(1234);
        setContentView(mViewPager);

        // setup action bar for tabs
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        mTabsAdapter = new TabsAdapter(this, mViewPager);
        mTabsAdapter.addTab(actionBar.newTab().setText("home"), HomeFragment.class, null);
        mTabsAdapter.addTab(actionBar.newTab().setText("friends"), FriendsListFragment.class, null);
        mTabsAdapter.addTab(actionBar.newTab().setText("about"), AboutFragment.class, null);

        // check for last open tab
        if (savedInstanceState != null) {
            actionBar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // save current selected tab
        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
    }
}
