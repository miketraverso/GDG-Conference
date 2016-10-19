package com.traversoft.gdgphotoshare;


import android.Manifest;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.traversoft.gdgphotoshare.data.BroadcastableAction;
import com.traversoft.gdgphotoshare.databinding.ActivityMainBinding;
import com.traversoft.gdgphotoshare.databinding.FragmentPhotoCaptureBinding;
import com.traversoft.gdgphotoshare.ui.activities.GDGActivity;
import com.traversoft.gdgphotoshare.ui.fragments.photoshare.PhotoCaptureFragment;
import com.traversoft.gdgphotoshare.ui.fragments.photoshare.PhotoPreviewFragment;
import com.traversoft.gdgphotoshare.ui.fragments.schedule.ScheduleFragment;
import com.traversoft.gdgphotoshare.ui.fragments.speaker.SpeakerFragment;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import butterknife.BindDrawable;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;


public class MainActivity
        extends GDGActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TWITTER_KEY = "QRD87ns93BBGE2TJaFJIpJF5S";
    private static final String TWITTER_SECRET = "8tG8tzApFKOC6Njtx7HXaNAlzL5d6NluElpkVUwuDCsW6Nsv8c";

    private Toolbar toolbar;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;

    @BindDrawable(R.drawable.ic_menu) Drawable navIcon;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setUnbinder(ButterKnife.bind(this));

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Crashlytics(), new Twitter(authConfig));

        setupToolbar(toolbar, navIcon);
//        setToolbarText(R.string.app_name, R.dimen.title_normal, android.R.color.white);
        setToolbarImage(R.drawable.overlay);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.snap_fab);
        fab.setOnClickListener(view ->
            switchToFragment(R.id.content_main, new PhotoCaptureFragment(), "PhotoCaptureFragment")
        );
        super.setFab(fab);

        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void setupDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_camera) {
            switchToFragment(R.id.content_main, new PhotoCaptureFragment(), "PhotoCaptureFragment");
        } else if (id == R.id.nav_schedule) {
            switchToFragment(R.id.content_main, new ScheduleFragment(), "ScheduleFragment");
        } else if (id == R.id.nav_speakers) {
            switchToFragment(R.id.content_main, new SpeakerFragment(), "SpeakerFragment");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
