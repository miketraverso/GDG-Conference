package com.traversoft.gdgphotoshare.ui.fragments;


import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.traversoft.gdgphotoshare.ui.activities.GDGActivity;

import butterknife.Unbinder;
import lombok.Getter;
import lombok.Setter;


public class GDGBaseFragment extends Fragment {

    public interface OnGDGFragmentInteractionListener {
        void onGDGFragmentInteraction(Uri uri);
    }

    @Getter OnGDGFragmentInteractionListener listener;
    @Getter private GDGActivity GdgActivity = null;
    @Getter @Setter Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        if (getActivity() instanceof GDGActivity) {
            GdgActivity = (GDGActivity) getActivity();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        getGdgActivity().getToolbar().setVisibility(View.VISIBLE);
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (listener != null) {
            listener.onGDGFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnGDGFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder!=null) {
            unbinder.unbind();
        }
    }

    public void moveToFragment(int contentFrame, Fragment fragment, String tag) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(contentFrame, fragment, tag);
        ft.addToBackStack(null);
        ft.commit();
    }
}
