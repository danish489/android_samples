package com.dsiddiqui.android.tunein.sdk;

import android.content.Context;

import com.dsiddiqui.android.tunein.R;


/**
 * Created by dsiddiqui on 15-11-07.
 */
public class TuneError {

    // Instance Variables

    private String mMessage;

    // Constructors

    public TuneError(String message) {
        mMessage = message;
    }

    // Getters

    public String getMessage() {
        return mMessage;
    }

    // Generic Error

    public static TuneError genericError(Context context) {
        return new TuneError(context.getResources().getString(R.string.generic_error));
    }

    public static TuneError validationError(Context context) {
        return new TuneError(context.getResources().getString(R.string.validation_error));
    }
}
