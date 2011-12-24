package com.scottagarman.android.xblAvatar.activities.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.*;
import com.scottagarman.android.xblAvatar.R;

public class HomeFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}
