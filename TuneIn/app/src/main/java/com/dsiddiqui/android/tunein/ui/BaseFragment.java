package com.dsiddiqui.android.tunein.ui;

import android.os.Bundle;
import io.realm.Realm;
import android.support.v4.app.Fragment;

/**
 * Created by dsiddiqui on 15-11-07.
 */
public class BaseFragment extends Fragment {

    // Realm.io db instances are cached so long as they are created on the same thread
    // Instances are reference counted which is why we can safely open / close them
    private Realm mRealm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
        mRealm = null;
    }

    public Realm getRealm() {
        return mRealm;
    }

}
