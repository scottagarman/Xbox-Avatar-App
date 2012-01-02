package com.scottagarman.android.xblAvatar.activities.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.scottagarman.android.xblAvatar.R;
import com.scottagarman.android.xblAvatar.managers.AvatarsManager;

public class HomeFragment extends Fragment implements AvatarsManager.AvatarsManagerListener {

    private EditText mEditText;
    private AvatarsManager mAvatarManager;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAvatarManager = new AvatarsManager(getActivity());

        mAvatarManager.registerListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup)inflater.inflate(R.layout.fragment_home, container, false);
        mEditText = (EditText)v.findViewById(R.id.home_edit);
        ((Button)v.findViewById(R.id.home_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAvatarManager != null) mAvatarManager.addAvatar(mEditText.getText().toString());
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /* avatar Manager */
    @Override
    public void onAvatarsLoaded() {

    }

    @Override
    public void onAvatarThumbLoaded(int index) {

    }

    @Override
    public void onAvatarLoaded(int index) {

    }
}
