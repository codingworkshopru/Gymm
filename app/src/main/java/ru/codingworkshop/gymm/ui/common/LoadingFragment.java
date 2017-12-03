package ru.codingworkshop.gymm.ui.common;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.codingworkshop.gymm.R;

public class LoadingFragment extends Fragment {
    public static final String TAG = LoadingFragment.class.getCanonicalName();


    public LoadingFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loading, container, false);
    }

}
