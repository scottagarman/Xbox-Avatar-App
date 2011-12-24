package com.scottagarman.android.xblAvatar.activities.fragments;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.*;
import com.scottagarman.android.xblAvatar.R;
import com.scottagarman.android.xblAvatar.adapters.FriendsListAdapter;

public class FriendsListFragment extends ListFragment {
    private ViewGroup mView;
    private FriendsListAdapter mAdapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add("Add")
		    .setIcon(android.R.drawable.ic_input_add)
            .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return true;
                }
            })
            .setTitle("Add")
		    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

    }

}
