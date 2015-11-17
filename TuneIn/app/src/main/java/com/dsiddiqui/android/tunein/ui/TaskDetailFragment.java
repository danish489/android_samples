package com.dsiddiqui.android.tunein.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dsiddiqui.android.tunein.R;

import java.util.Date;

/**
 * Created by dsiddiqui on 15-11-07.
 */
public class TaskDetailFragment extends BaseFragment {

    // Constants

    private static final String TUNE_TASK_NAME = "tune:task-name";
    private static final String TUNE_TASK_DESC = "tune:task-desc";
    private static final String TUNE_TASK_DATE = "tune:task-date";

    // Constructors

    public static TaskDetailFragment newInstance(String name,
                                                 String description,
                                                 Date date,
                                                 boolean completed) {
        Bundle arguments = new Bundle();
        arguments.putString(TUNE_TASK_NAME, name);
        arguments.putString(TUNE_TASK_DESC, description);
        arguments.putSerializable(TUNE_TASK_DATE, date);

        TaskDetailFragment fragment = new TaskDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    // Life Cycle

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_task_detail, container, false);

        TextView nameView = (TextView) view.findViewById(R.id.task_name);
        TextView descView = (TextView) view.findViewById(R.id.task_desc);
        TextView dateView = (TextView) view.findViewById(R.id.task_date);

        Bundle args = getArguments();
        String name = getResources().getString(R.string.task_name, args.getString(TUNE_TASK_NAME));
        String email = getResources().getString(R.string.task_desc, args.getString(TUNE_TASK_DESC));

        Date dueDate = (Date) args.getSerializable(TUNE_TASK_DATE);
        String date = getResources().getString(R.string.task_date, dueDate.toString());

        nameView.setText(name);
        descView.setText(email);
        dateView.setText(date);

        return view;
    }
}
