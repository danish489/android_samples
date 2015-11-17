package com.dsiddiqui.android.tunein.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.dsiddiqui.android.tunein.R;
import com.dsiddiqui.android.tunein.sdk.TuneError;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by dsiddiqui on 15-11-07.
 */
public class TaskAddFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    // Constants

    static class Result {
        public static final int ADD_SUCCESS = 0;
        public static final int ADD_FAILURE = 1;
        public static final String NEW_TASK_TITLE_KEY = "new-task-title";
        public static final String NEW_TASK_DESC_KEY = "new-task-desc";
        public static final String NEW_TASK_DATE_KEY = "new-task-date";
    }

    // Instance Variables

    @Bind(R.id.new_task_title)
    EditText mTitleText;

    @Bind(R.id.new_task_desc)
    EditText mDescText;

    private Date mDate;

    // Constructors

    public static TaskAddFragment newInstance() {
        return new TaskAddFragment();
    }

    public TaskAddFragment() {
        // Required empty public constructor
    }

    // Life cycle

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDate = (savedInstanceState == null) ? null : (Date) savedInstanceState.getSerializable("date");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_task_add, container, false);
        ButterKnife.bind(this, view);
        setupDialog();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        // Android EditText class has memory leak with blinking cursor
        // Link: https://code.google.com/p/android/issues/detail?id=188551
        mTitleText.setCursorVisible(false);
        mDescText.setCursorVisible(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Android EditText class has memory leak with blinking cursor
        // Link: https://code.google.com/p/android/issues/detail?id=188551
        mTitleText.setCursorVisible(true);
        mDescText.setCursorVisible(true);
    }

    // Setup

    private void setupDialog() {
        // Dialog frame
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        outState.putSerializable("date", mDate);
    }

    // UI callbacks

    @OnClick(R.id.new_task_date) void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        new DatePickerDialog(getActivity(), this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @OnClick(R.id.new_task_ok) void addTask() {

        // basic "validation"
        if ((mTitleText.getText().length() == 0) ||
                (mDescText.getText().length() == 0) ||
                (mDate == null)) {
            final String errorMsg = TuneError.validationError(getActivity()).getMessage();
            Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
        } else {
            Intent res = new Intent();
            res.putExtra(Result.NEW_TASK_TITLE_KEY, mTitleText.getText());
            res.putExtra(Result.NEW_TASK_DESC_KEY, mDescText.getText());
            res.putExtra(Result.NEW_TASK_DATE_KEY, mDate);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Result.ADD_SUCCESS, res);
            dismiss();
        }
    }

    @OnClick(R.id.new_task_cancel) void cancelTask() {
        dismiss();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mDate = new GregorianCalendar(year,monthOfYear,dayOfMonth).getTime();
    }

}
