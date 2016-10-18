package com.traversoft.gdgphotoshare;


import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.traversoft.gdgphotoshare.data.BroadcastableAction;

import io.fabric.sdk.android.Fabric;
import lombok.AllArgsConstructor;
import lombok.Getter;


public class GDGMeetupApplication extends Application {

    @SuppressLint("StaticFieldLeak") @Getter private static GDGMeetupApplication instance;
    @SuppressLint("StaticFieldLeak") @Getter private static Context appContext;
    @Getter private SharedPreferences preferences;

    public GDGMeetupApplication() {
        instance = this;
    }

    @Override public void onCreate() {
        super.onCreate();

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics(), new Answers());
        }

        appContext = this;
        preferences = GDGMeetupApplication.getAppContext().getSharedPreferences("meetingprefs", Context.MODE_PRIVATE);
    }

    public void broadcastIntent(@NonNull BroadcastableAction action) {
        final Intent intent = new Intent();
        intent.setAction(action.getType().getAction());
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }
}
