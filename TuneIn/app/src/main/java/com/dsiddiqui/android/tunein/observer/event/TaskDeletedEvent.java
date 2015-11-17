package com.dsiddiqui.android.tunein.observer.event;

/**
 * Created by dsiddiqui on 15-11-07.
 */
public class TaskDeletedEvent {

    private int mId;

    public TaskDeletedEvent(int mId) {
        this.mId = mId;
    }

    public int getTaskId() {
        return mId;
    }

    public void setTaskId(int mId) {
        this.mId = mId;
    }
}
