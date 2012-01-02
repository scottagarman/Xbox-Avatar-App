package com.scottagarman.android.xblAvatar.activities.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.*;
import com.scottagarman.android.xblAvatar.R;
import com.scottagarman.android.xblAvatar.adapters.FriendsListAdapter;
import com.scottagarman.android.xblAvatar.managers.AvatarsManager;

public class FriendsListFragment extends ListFragment implements AvatarsManager.AvatarsManagerListener {
    private ViewGroup mView;
    private FriendsListAdapter mAdapter;
    private AvatarsManager mAvatarsManager;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        mAvatarsManager = new AvatarsManager(getActivity());
        mAvatarsManager.registerListener(this);
        mAvatarsManager.loadAvatars();
        mAdapter = new FriendsListAdapter(getActivity(), mAvatarsManager);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.friends_list_menu, menu);
    }

    /* avatars manager*/
    @Override
    public void onAvatarsLoaded() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAvatarThumbLoaded(int index) {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAvatarLoaded(int index) {

    }
}
