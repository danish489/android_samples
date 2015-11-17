package com.dsiddiqui.android.tunein.observer;

/*
 * Copyright (C) 2012 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Maintains a singleton instance for obtaining the bus. Ideally this would be replaced with a more efficient means
 * such as through injection directly into interested classes.
 */
public final class BusProvider {

    private static final AndroidBus sInstance = new AndroidBus(ThreadEnforcer.MAIN);

    public static Bus getInstance() {
        return sInstance;
    }

    private BusProvider() {
        // No instances.
    }

    // Static Nested Class (Android Bus)

    public static class AndroidBus extends Bus {
        private final Handler mMainThread = new Handler(Looper.getMainLooper());

        private AndroidBus() {}

        public AndroidBus(ThreadEnforcer enforcer) {
            super(enforcer);
        }

        @Override public void register(Object obj) {
            final Object objFinal = obj;
            if (Looper.myLooper() == Looper.getMainLooper()) {
                super.register(objFinal);
            } else {
                mMainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        AndroidBus.super.register(objFinal);
                    }
                });
            }
        }

        @Override public void unregister(Object obj) {
            final Object objFinal = obj;
            if (Looper.myLooper() == Looper.getMainLooper()) {
                super.unregister(objFinal);
            } else {
                mMainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        AndroidBus.super.unregister(objFinal);
                    }
                });
            }
        }

        @Override
        public void post(final Object event) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                super.post(event);
            } else {
                mMainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        AndroidBus.super.post(event);
                    }
                });
            }
        }
    }
}