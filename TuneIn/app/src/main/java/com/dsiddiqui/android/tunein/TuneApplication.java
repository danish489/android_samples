package com.dsiddiqui.android.tunein;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by dsiddiqui on 15-11-07.
 */
public class TuneApplication extends Application {

    @Override public void onCreate() {
        super.onCreate();

        // joda time
        JodaTimeAndroid.init(this);
    }
}
