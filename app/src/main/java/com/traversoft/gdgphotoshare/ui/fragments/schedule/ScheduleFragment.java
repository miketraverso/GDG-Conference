package com.traversoft.gdgphotoshare.ui.fragments.schedule;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.traversoft.gdgphotoshare.databinding.FragmentScheduleBinding;
import com.traversoft.gdgphotoshare.ui.fragments.GDGBaseFragment;


public class ScheduleFragment
        extends GDGBaseFragment {

    FragmentScheduleBinding viewHolder;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        viewHolder = FragmentScheduleBinding.inflate(inflater, container, false);
        setupUI();
        return viewHolder.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedBundleInstance) {
        super.onViewCreated(view, savedBundleInstance);
        loadSchedulesFromFirebase();
    }

    private void setupUI() {

    }

    private void loadSchedulesFromFirebase() {

    }
}
