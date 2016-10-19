package com.traversoft.gdgphotoshare.ui.activities;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.traversoft.gdgphotoshare.GDGMeetupApplication;
import com.traversoft.gdgphotoshare.R;
import com.traversoft.gdgphotoshare.data.BroadcastableAction;
import com.traversoft.gdgphotoshare.ui.common.GDGTextView;
import com.traversoft.gdgphotoshare.ui.fragments.GDGBaseFragment;

import butterknife.Unbinder;
import lombok.Getter;
import lombok.Setter;


public class GDGActivity extends AppCompatActivity
        implements GDGBaseFragment.OnGDGFragmentInteractionListener {

    @Getter private Toolbar toolbar;
    @Getter @Setter private Unbinder unbinder;
    @Getter @Setter private FloatingActionButton fab;

    private static final int GDG_PERMISSIONS_REQUEST_CAMERA = 1000;
    private static final int GDG_PERMISSIONS_REQUEST_WRITE = 1001;
    private static final int GDG_REQUEST_PERMISSIONS = 1000;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override protected void onDestroy() {
        super.onDestroy();

        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public void setupToolbar(android.support.v7.widget.Toolbar toolbar, Drawable navIcon) {

        this.toolbar = toolbar;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                | ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE
                | ActionBar.DISPLAY_USE_LOGO);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (getSupportActionBar() != null && navIcon != null) {
            getSupportActionBar().setHomeAsUpIndicator(navIcon);
        }
        getSupportActionBar().setHomeButtonEnabled(true);
        setToolbarBackgroundColor(R.color.colorPrimary);
    }

    public void setToolbarBackgroundColor(@ColorRes int colorResourceId) {
        if (getSupportActionBar() != null) {
            toolbar.setBackgroundColor(getResources().getColor(colorResourceId));
        }
    }

    public void setToolbarImage(@DrawableRes int imageResourceId) {
        if (getSupportActionBar() != null) {
            int padding = (int) getResources().getDimension(R.dimen.margin_8);
            AppCompatImageView logo = new AppCompatImageView(this);
            logo.setImageResource(imageResourceId);
            logo.setPadding(0, 0, 0, 0);
            ActionBar.LayoutParams params
                    = new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER);

            getSupportActionBar().setCustomView(logo, params);
        }
    }

    public void setFabVisibility(boolean visibility) {
        if (fab != null) {
            fab.setVisibility(visibility ? View.VISIBLE : View.GONE);
        }
    }

    public void setToolbarText(@StringRes int stringResourceId, @DimenRes int dimenResourceId, @ColorRes int colorRes) {
        if (getSupportActionBar() != null) {
            GDGTextView title = new GDGTextView(this);
            title.setText(getResources().getString(stringResourceId));
            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(dimenResourceId));
            title.setTextColor(getResources().getColor(colorRes));
            ActionBar.LayoutParams params
                    = new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER);

            getSupportActionBar().setCustomView(title, params);
        }
    }

    @Override public void onGDGFragmentInteraction(Uri uri) {
    }

    public void switchToFragment(int contentFrame, Fragment fragment, String tag) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(contentFrame, fragment, tag);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void requestPermission(@NonNull String permission, String rationaleMessage) { //@StringRes int rationaleMessage) {
        String[] permissions = new String[]{permission};
        int hasPermission = ContextCompat.checkSelfPermission(this, permission);

        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setPositiveButton("YES", (dialog1, which) -> {
                    ActivityCompat.requestPermissions(this, permissions, GDG_REQUEST_PERMISSIONS);
                });
                dialog.setNegativeButton("NO", null);
                dialog.setMessage(rationaleMessage);
                dialog.setTitle("Please may we");

            } else {
                ActivityCompat.requestPermissions(this, permissions, GDG_REQUEST_PERMISSIONS);
            }
        } else {
            if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equalsIgnoreCase(permission)) {
                GDGMeetupApplication.getInstance().broadcastIntent(new BroadcastableAction.WritePermissionApproved());
            } else if (android.Manifest.permission.CAMERA.equalsIgnoreCase(permission)) {
                GDGMeetupApplication.getInstance().broadcastIntent(new BroadcastableAction.CameraPermissionApproved());
            }
        }
    }

    @Override public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case GDG_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    GDGMeetupApplication.getInstance().broadcastIntent(new BroadcastableAction.CameraPermissionApproved());
                } else {
                    GDGMeetupApplication.getInstance().broadcastIntent(new BroadcastableAction.CameraPermissionDenied());
                }
            }
            break;

            case GDG_PERMISSIONS_REQUEST_WRITE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    GDGMeetupApplication.getInstance().broadcastIntent(new BroadcastableAction.WritePermissionApproved());
                } else {
                    GDGMeetupApplication.getInstance().broadcastIntent(new BroadcastableAction.WritePermissionDenied());
                }
            }
            break;
        }
    }
}
