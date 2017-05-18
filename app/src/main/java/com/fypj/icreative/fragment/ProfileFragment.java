package com.fypj.icreative.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fypj.icreative.R;
import com.fypj.icreative.activity.MainActivity;


public class ProfileFragment extends Fragment {


    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout =  inflater.inflate(R.layout.fragment_profile, container, false);


        return layout;
    }

}
