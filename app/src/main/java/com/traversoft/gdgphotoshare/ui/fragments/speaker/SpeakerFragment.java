package com.traversoft.gdgphotoshare.ui.fragments.speaker;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.traversoft.gdgphotoshare.R;
import com.traversoft.gdgphotoshare.data.Speaker;
import com.traversoft.gdgphotoshare.databinding.FragmentSpeakerBinding;
import com.traversoft.gdgphotoshare.ui.fragments.GDGBaseFragment;

import java.util.LinkedHashMap;


public class SpeakerFragment
        extends GDGBaseFragment {

    private static final String TAG = "SpeakerFragment";
    private FragmentSpeakerBinding viewHolder;
    private LinkedHashMap<Integer, Speaker> speakers;
    private SpeakerAdapter speakerAdapter;
    private static ProgressDialog progressDialog;

    public SpeakerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        viewHolder = FragmentSpeakerBinding.inflate(inflater, container, false);
        setupUI();
        return viewHolder.getRoot();
    }

    private void setupUI() {
        speakers = new LinkedHashMap<>();

        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        viewHolder.speakerRecyclerView.setHasFixedSize(true);
        viewHolder.speakerRecyclerView.setLayoutManager(layoutManager);
        viewHolder.speakerRecyclerView.setItemAnimator(itemAnimator);
        speakerAdapter = new SpeakerAdapter(getActivity());
        viewHolder.speakerRecyclerView.setAdapter(speakerAdapter);
    }

    @Override public void onResume() {
        super.onResume();
        speakerAdapter.setOnClickSpeakerListener(this::showSpeakerDetails);
        loadSpeakersFromFirebase();
    }

    private void showSpeakerDetails(@NonNull Speaker speaker) {
        SpeakerDetailFragment speakerDetailFragment = new SpeakerDetailFragment();
        speakerDetailFragment.setSpeaker(speaker);
        moveToFragment(R.id.content_main, speakerDetailFragment, "SpeakerDetailFragment");
    }

    @Override public void onPause() {
        super.onPause();
        speakerAdapter.setOnClickSpeakerListener(null);
    }

    @Override public void onDestroyView() {
        teardownUI();
        speakerAdapter = null;
        super.onDestroyView();
    }

    private void teardownUI() {
        viewHolder = null;
        speakers = null;
    }

    private void loadSpeakersFromFirebase() {
        progressDialog = ProgressDialog.show(getActivity(),"",getString(R.string.loading));

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        Query myTopPostsQuery = databaseReference.child("speakers");
        myTopPostsQuery.addChildEventListener(new ChildEventListener() {

            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Speaker speaker = dataSnapshot.getValue(Speaker.class);
                speakers.put(speaker.getId(), speaker);
                speakerAdapter.setSpeakers(speakers);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Speaker speaker = dataSnapshot.getValue(Speaker.class);
                speakers.put(speaker.getId(), speaker);
            }

            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {
                Speaker speaker = dataSnapshot.getValue(Speaker.class);
                speakers.remove(speaker.getId());
            }

            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

            @Override public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }
}
