package com.scottagarman.android.xblAvatar.activities.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.scottagarman.android.xblAvatar.R;
import com.scottagarman.android.xblAvatar.managers.AvatarsManager;

public class HomeFragment extends Fragment {

    private EditText mEditText;
    private AvatarsManager mAvatarManager;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAvatarManager = new AvatarsManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup)inflater.inflate(R.layout.fragment_home, container, false);
        mEditText = (EditText)v.findViewById(R.id.home_edit);
        return v;
    }
    
    public void onAddTouched(View v) {

    }
}
