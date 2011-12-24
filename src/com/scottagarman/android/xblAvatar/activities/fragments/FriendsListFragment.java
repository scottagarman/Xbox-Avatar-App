package com.scottagarman.android.xblAvatar.activities.fragments;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.scottagarman.android.xblAvatar.R;
import com.scottagarman.android.xblAvatar.adapters.FriendsListAdapter;

public class FriendsListFragment extends ListFragment {
    private ViewGroup mView;
    private FriendsListAdapter mAdapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new FriendsListAdapter(getActivity());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.setListAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mView = (ViewGroup) inflater.inflate(R.layout.fragment_friends_list, container, false);
        return mView;
    }
}
