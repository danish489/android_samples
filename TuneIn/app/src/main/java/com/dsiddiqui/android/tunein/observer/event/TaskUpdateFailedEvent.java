package com.dsiddiqui.android.tunein.observer.event;

import com.dsiddiqui.android.tunein.sdk.TuneError;

/**
 * Created by dsiddiqui on 15-11-07.
 */
public class TaskUpdateFailedEvent {

    private TuneError mError;

    public TaskUpdateFailedEvent(TuneError error) {
        mError = error;
    }

    public TuneError getError() {
        return mError;
    }
}
