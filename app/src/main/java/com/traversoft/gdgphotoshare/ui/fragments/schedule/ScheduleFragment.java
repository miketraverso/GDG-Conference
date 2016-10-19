package com.traversoft.gdgphotoshare.ui.fragments.schedule;


import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.traversoft.gdgphotoshare.databinding.FragmentScheduleBinding;
import com.traversoft.gdgphotoshare.ui.fragments.GDGBaseFragment;


public class ScheduleFragment
        extends GDGBaseFragment {

    private static final String TAG = "ScheduleFragment";
    FragmentScheduleBinding viewHolder;
    private DatabaseReference mDatabase;// ...

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
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Query myTopPostsQuery = mDatabase.child("speakers");
        myTopPostsQuery.addChildEventListener(new ChildEventListener() {
            // TODO: implement the ChildEventListener methods as documented above
            // ...

            @Override public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);

            }

            @Override public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

    }

    private void loadSchedulesFromFirebase() {

    }
}
