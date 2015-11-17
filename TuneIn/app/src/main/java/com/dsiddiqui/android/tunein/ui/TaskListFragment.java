package com.dsiddiqui.android.tunein.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dsiddiqui.android.tunein.data.Task;
import com.dsiddiqui.android.tunein.observer.BusProvider;
import com.dsiddiqui.android.tunein.observer.event.TaskDeletedEvent;
import com.dsiddiqui.android.tunein.observer.event.TaskUpdateFailedEvent;
import com.dsiddiqui.android.tunein.observer.event.TaskUpdatedEvent;
import com.dsiddiqui.android.tunein.sdk.TuneError;
import com.dsiddiqui.android.tunein.R;
import com.dsiddiqui.android.tunein.sdk.TuneLogger;
import com.dsiddiqui.android.tunein.util.SecureSessionGenerator;
import com.squareup.otto.Subscribe;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;


/**
 * Created by dsiddiqui on 15-11-07.
 */
public class TaskListFragment extends BaseFragment {

    // Constants

    public static final int ADD_TASK_FRAGMENT_REQUEST = 10;

    // Instance Variables

    private RecyclerView mTaskView;
    private TaskAdapter mTaskAdapter;
    private Callback mCallback;

    private boolean mAscending;

    // Constructors

    public static TaskListFragment newInstance() {
        return new TaskListFragment();
    }

    // Life Cycle

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (Callback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement TaskListFragment.Callback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Create options menu
        setHasOptionsMenu(true);

        // Load tasks from Realm.io
        loadTasks();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

        mTaskView = (RecyclerView) view.findViewById(R.id.task_list_recycler_view);
        mTaskView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.todo_toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        mAscending = savedInstanceState == null || savedInstanceState.getBoolean("order", true);

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        outState.putBoolean("order", mAscending);
    }

    // Options Menu

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_task_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_task_add:
                TaskAddFragment taskAddFragment = TaskAddFragment.newInstance();
                taskAddFragment.setTargetFragment(TaskListFragment.this, ADD_TASK_FRAGMENT_REQUEST);
                taskAddFragment.show(getChildFragmentManager(), "TaskAddFragment");
                return true;
            case R.id.menu_item_task_sort:
                mAscending = !mAscending;
                BusProvider.getInstance().post(new TaskUpdatedEvent());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // View Holder

    private class TaskHolder extends RealmViewHolder implements View.OnClickListener {

        private TextView mTaskTitleTextView;
        private TextView mTaskDateTextView;
        private CheckBox mTaskFinishedCheckBox;

        TaskHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTaskTitleTextView = (TextView) itemView.findViewById(R.id.list_item_task_name_text_view);
            mTaskDateTextView = (TextView) itemView.findViewById(R.id.list_item_task_due_text_view);
            mTaskFinishedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_task_finished_check_box);
            mTaskFinishedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        mTaskFinishedCheckBox.setChecked(false);
                        Task task = mTaskAdapter.getTask(getLayoutPosition());
                        BusProvider.getInstance().post(new TaskDeletedEvent(task.getId()));
                    }
                }
            });
        }

        public void bindTask(Task item) {
            mTaskTitleTextView.setText(item.getTitle());
            mTaskDateTextView.setText(item.getDate().toString());
        }

        @Override
        public void onClick(View v) {
            final Task task = mTaskAdapter.getTask(getLayoutPosition());
            mCallback.onTaskSelected(task);
        }
    }

    // Adapter

    private class TaskAdapter extends RealmBasedRecyclerViewAdapter<Task,TaskHolder> {

        public TaskAdapter(Context context, RealmResults<Task> realmResults) {
            super(context, realmResults, true, true, false, null);
        }

        @Override
        public TaskHolder onCreateRealmViewHolder(ViewGroup parent, int position) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.item_task_list, parent, false);
            return new TaskHolder(view);
        }

        @Override
        public void onBindRealmViewHolder(TaskHolder taskHolder, int position) {
            taskHolder.bindTask(getTask(position));
        }

        @Override
        public TaskHolder convertViewHolder(RealmViewHolder viewHolder) {
            return TaskHolder.class.cast(viewHolder);
        }

        //TODO: move this to "TaskManager" and not use underlying realmResults
        //RealmRecyclerView provides this for convenience but it can conflict w/ async txns
        public Task getTask(int position) {
            Task user = null;
            if (realmResults.size() > 0 && position >= 0) {
                user = realmResults.get(position);
            }
            return user;
        }

        public void sortResults(boolean order) {
            realmResults.sort("title", order);
        }
    }

    // Add Task Result

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_TASK_FRAGMENT_REQUEST) {
            switch (resultCode) {
                case TaskAddFragment.Result.ADD_SUCCESS:
                    TuneLogger.logMessage("Successfully added request for new task");
                    addTask(data);
                    break;
                case TaskAddFragment.Result.ADD_FAILURE:
                    TuneLogger.logMessage("Cancelled request for new task");
                    break;
            }
        }
    }

    // Helper Methods

    private void loadTasks() {

        //Load tasks list from Realm -- once Realm is setup, it is insanely fast so
        //we can safely do this on the UI thread for even fairly large data sets
        //for an extremely large list, we would want to load the data asynchronously
        //and only update a range of indices rather than everything
        Realm realm = getRealm();
        RealmResults<Task> tasksList = realm.allObjects(Task.class);
        tasksList.sort("date", mAscending);
        mTaskAdapter = new TaskAdapter(getContext(), tasksList);
    }

    private void addTask(Intent data) {

        Bundle extras = data.getExtras();
        int id = SecureSessionGenerator.getInstance().nextSessionId();
        String title = extras.get(TaskAddFragment.Result.NEW_TASK_TITLE_KEY).toString();
        String desc = extras.get(TaskAddFragment.Result.NEW_TASK_DESC_KEY).toString();
        Date date = (Date) extras.get(TaskAddFragment.Result.NEW_TASK_DATE_KEY);
        final Task task = new Task(id,title,desc,date,false);

        //Persist task asynchronously
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                bgRealm.copyToRealmOrUpdate(task);
            }
        }, new Realm.Transaction.Callback() {
            @Override
            public void onSuccess() {
                BusProvider.getInstance().post(new TaskUpdatedEvent());
                TuneLogger.logMessage("Successfully added new task");
            }

            @Override
            public void onError(Exception e) {
                // transaction is automatically rolled-back, do any cleanup here
                showToast("Failed to save new task!");
                TuneLogger.logMessage("Failed to persist new task");
            }
        });

    }

    private void updateUI() {
        if (isAdded()) {
            mTaskAdapter.sortResults(mAscending);
            mTaskView.setAdapter(mTaskAdapter);
            mTaskAdapter.notifyDataSetChanged();
        }
    }

    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    // UI events via Otto event bus (calls are synchronous)

    @Subscribe
    public void taskUpdateSuccessful(TaskUpdatedEvent event) {
        //This is ok for a small list, for a larger one we'd have to do asynchronous updates
        //otherwise the UI thread will be stuck.
        if (mTaskAdapter != null) {
            mTaskAdapter.sortResults(mAscending);
            mTaskAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe
    public void taskUpdateFailed(TaskUpdateFailedEvent event) {
        final String errorMsg = TuneError.genericError(getActivity()).getMessage();
        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
    }

    @Subscribe
    public void taskDeleteSuccessful(final TaskDeletedEvent event) {
        //Delete task asynchronously -- if I had more time all this data modification
        //code would go into a separate manager class
        Realm realm = getRealm();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                RealmResults<Task> result = bgRealm.where(Task.class)
                        .equalTo("id", event.getTaskId())
                        .findAll();

                result.clear(); // delete
            }
        }, new Realm.Transaction.Callback() {
            @Override
            public void onSuccess() {
                // update list
                mTaskAdapter.sortResults(mAscending);
                mTaskAdapter.notifyDataSetChanged(); // TODO: only notify deleted index...

                // relay delete event to parent and select first task in list (only in dual pane)
                boolean dualPane = getResources().getBoolean(R.bool.dual_pane);
                if (dualPane) {
                    final Task task = mTaskAdapter.getTask(0);
                    mCallback.onTaskSelected(task);
                }
                TuneLogger.logMessage("Successfully deleted task");
            }

            @Override
            public void onError(Exception e) {
                // transaction is automatically rolled-back, do any cleanup here
                showToast("Failed to delete task!");
                TuneLogger.logMessage("Failed to delete task");
            }
        });
    }

    //Callback interface for parent activity

    public interface Callback {
        void onTaskSelected(Task task);
    }

}
