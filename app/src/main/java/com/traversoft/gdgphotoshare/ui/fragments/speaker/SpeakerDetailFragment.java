package com.traversoft.gdgphotoshare.ui.fragments.speaker;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.traversoft.gdgphotoshare.R;
import com.traversoft.gdgphotoshare.data.Speaker;
import com.traversoft.gdgphotoshare.databinding.FragmentSpeakerBinding;
import com.traversoft.gdgphotoshare.databinding.FragmentSpeakerDetailBinding;
import com.traversoft.gdgphotoshare.ui.fragments.GDGBaseFragment;

import java.util.LinkedHashMap;

import lombok.Getter;
import lombok.Setter;


public class SpeakerDetailFragment
        extends GDGBaseFragment {

    private static final String TAG = "SpeakerFragment";
    private FragmentSpeakerDetailBinding viewHolder;
    @Setter Speaker speaker;

    public SpeakerDetailFragment() {
        // Required empty public constructor
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        viewHolder = FragmentSpeakerDetailBinding.inflate(inflater, container, false);
        setupUI();
        return viewHolder.getRoot();
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedBundleInstance) {
        super.onViewCreated(view, savedBundleInstance);
    }

    private void setupUI() {
        if (speaker != null && !speaker.getPhotoUrl().isEmpty()) {
            Picasso.with(getActivity())
                    .load(getActivity().getString(R.string.speaker_url) + speaker.getPhotoUrl())
                    .into(viewHolder.profileImage);
        }

        viewHolder.name.setText(speaker.getName());
        viewHolder.country.setText(speaker.getCountry().trim());
        viewHolder.company.setText(speaker.getCompany());
        viewHolder.bio.setText(speaker.getBio());
    }

    @Override public void onResume() {
        super.onResume();
    }

    @Override public void onPause() {
        super.onPause();
    }

    @Override public void onDestroyView() {
        teardownUI();
        super.onDestroyView();
    }

    private void teardownUI() {
        viewHolder = null;
    }
}
