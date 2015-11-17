package com.dsiddiqui.android.tunein.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.dsiddiqui.android.tunein.data.Task;
import com.dsiddiqui.android.tunein.R;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class TaskListActivity extends AppCompatActivity implements TaskListFragment.Callback {

    // Instance Variables

    private Realm mRealm;
    private boolean mDualPane;

    // Life Cycle

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Setup Realm
        RealmConfiguration config = new RealmConfiguration.Builder(this).name("tunein.realm").build();
        Realm.setDefaultConfiguration(config);
        mRealm = Realm.getDefaultInstance();

        // UI
        setContentView(R.layout.activity_task_list);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.task_list_fragment_container);

        if (fragment == null) {
            fragment = TaskListFragment.newInstance();
            fm.beginTransaction()
                    .add(R.id.task_list_fragment_container, fragment, "TASK_LIST_FRAGMENT")
                    .commit();
        }

        if (findViewById(R.id.task_detail_fragment_container) != null) {
            mDualPane = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
        mRealm = null;
    }

    public Realm getRealm() {
        return mRealm;
    }

    @Override
    public void onTaskSelected(Task task) {

        // all tasks were deleted, therefore, remove the previous details fragment
        if (task == null) {
            if (mDualPane) {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.task_detail_fragment_container);
                if (fragment != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .remove(fragment)
                            .commit();
                }
            }
            return;
        }

        // show selected details in same or split-view depending on device resolution
        TaskDetailFragment taskDetailFragment = TaskDetailFragment.newInstance(
                task.getTitle(),
                task.getDescription(),
                task.getDate(),
                false);

        if (!mDualPane) {
            //single-pane
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.task_list_fragment_container, taskDetailFragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            //dual-pane
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.task_detail_fragment_container, taskDetailFragment)
                    .commit();
        }
    }
}
