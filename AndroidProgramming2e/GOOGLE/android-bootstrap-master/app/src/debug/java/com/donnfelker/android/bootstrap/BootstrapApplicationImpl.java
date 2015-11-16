package com.donnfelker.android.bootstrap;


import timber.log.Timber;

public class BootstrapApplicationImpl extends BootstrapApplication {

    @Override
    protected void onAfterInjection() {

    }

    @Override
    protected void init() {
        Timber.plant(new Timber.DebugTree());
    }
}
