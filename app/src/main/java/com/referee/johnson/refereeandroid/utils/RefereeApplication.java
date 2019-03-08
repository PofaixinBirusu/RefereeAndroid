package com.referee.johnson.refereeandroid.utils;

import android.app.Application;
import android.content.Context;

public class RefereeApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
