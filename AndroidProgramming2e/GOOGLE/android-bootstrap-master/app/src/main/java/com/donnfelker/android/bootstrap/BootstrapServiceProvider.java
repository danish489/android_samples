package com.donnfelker.android.bootstrap;

import android.accounts.AccountsException;
import android.app.Activity;

import com.donnfelker.android.bootstrap.core.BootstrapService;

import java.io.IOException;

public interface BootstrapServiceProvider {
    BootstrapService getService(Activity activity) throws IOException, AccountsException;
}
