package com.donnfelker.android.bootstrap.authenticator;

public interface LogoutService {
    void logout(Runnable onSuccess);
}
