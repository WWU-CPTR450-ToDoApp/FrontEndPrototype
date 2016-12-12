package com.example.sydney.todolist.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;

import com.example.sydney.todolist.MainActivity;
import com.example.sydney.todolist.R;
import com.example.sydney.todolist.db.TaskContract;

import java.util.Calendar;

public class EditTaskFragment extends DialogFragment {
    private EditText title_field, date_field, time_field, notes_field;
    private Calendar cal_date;
    private Switch repeat_field;
    private int id, done;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // create a view based on alert_add_task.xmlsk.xml
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View addTaskView = inflater.inflate(R.layout.alert_add_task, null, false);

        // get ids from the layout
        title_field = (EditText) addTaskView.findViewById(R.id.tasktitle);
        date_field = (EditText) addTaskView.findViewById(R.id.date);
        time_field = (EditText) addTaskView.findViewById(R.id.time);
        repeat_field = (Switch) addTaskView.findViewById(R.id.repeat);
        notes_field = (EditText) addTaskView.findViewById(R.id.notes);
        cal_date = Calendar.getInstance();

        // get current values of the task from the bundle arguments
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            id = bundle.getInt(TaskContract.TaskEntry._ID);
            title_field.setText(bundle.getString(TaskContract.TaskEntry.COL_TASK_TITLE));
            cal_date.setTimeInMillis(bundle.getLong(TaskContract.TaskEntry.COL_TASK_DATE));
            date_field.setText(cal_date.get(Calendar.MONTH)+1 + "/" + cal_date.get(Calendar.DAY_OF_MONTH) + "/" + cal_date.get(Calendar.YEAR));
            int am_pm = cal_date.get(Calendar.AM_PM);
            time_field.setText( String.format("%02d", cal_date.get(Calendar.HOUR_OF_DAY)%12)
                        + ":" + String.format("%02d", cal_date.get(Calendar.MINUTE))
                        + " " + ((am_pm==Calendar.AM) ? "AM" : "PM"));
            done = bundle.getInt(TaskContract.TaskEntry.COL_TASK_DONE);
            repeat_field.setChecked(bundle.getInt(TaskContract.TaskEntry.COL_TASK_REPEAT) != 0);
            notes_field.setText(bundle.getString(TaskContract.TaskEntry.COL_TASK_DESC));
        }

        // Show a date-picker when the date field is clicked
        date_field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dfrag;
                dfrag = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        cal_date.set(year, month, day, 0, 0, 0);
                        month++;    // since month is 0 based, add 1 to display correctly
                        date_field.setText(month + "/" + day + "/" + year);
                    }
                }, cal_date.get(Calendar.YEAR), cal_date.get(Calendar.MONTH), cal_date.get(Calendar.DAY_OF_MONTH));
                dfrag.show();
            }
        });

        // Show a time-picker when the time field is clicked
        time_field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog tfrag;
                tfrag = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int min) {
                        cal_date.set(Calendar.HOUR_OF_DAY, hour);
                        cal_date.set(Calendar.MINUTE, min);
                        int am_pm = cal_date.get(Calendar.AM_PM);
                        time_field.setText( String.format("%02d", hour%12)
                                    + ":" + String.format("%02d", min)
                                    + " " + ((am_pm==Calendar.AM) ? "AM" : "PM"));
                    }
                }, cal_date.get(Calendar.HOUR_OF_DAY), cal_date.get(Calendar.MINUTE), false);
                tfrag.show();
            }
        });

        // Create a new instance of DatePickerDialog and return it
        return new AlertDialog.Builder(getActivity())
                .setTitle("Edit Task")
                .setMessage("Edit the details of the task")
                .setView(addTaskView)
                // Create the Save button to update the task task
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ViewPager vp = ((MainActivity)getActivity()).getViewPager();
                        Fragment page = getFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + vp.getCurrentItem());
                        AbstractFragment page2 = (AbstractFragment) page;
                        page2.editTaskReturnCall (
                                id,
                                String.valueOf(title_field.getText()),
                                cal_date.getTimeInMillis(),
                                done,
                                repeat_field.isChecked() ? 1 : 0,
                                String.valueOf(notes_field.getText())
                        );
                    }
                })
                // Create a Cancel button to cancel creating a new task
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                // Create a Delete button to delete a created task
                .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ViewPager vp = ((MainActivity)getActivity()).getViewPager();
                        Fragment page = getFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + vp.getCurrentItem());
                        AbstractFragment page2 = (AbstractFragment) page;
                        String selection = TaskContract.TaskEntry._ID + " = ?";
                        String[] selectionArgs = new String[]{String.valueOf(id)};
                        page2.deleteTask(selection, selectionArgs);
                    }
                })
                .create();
    }
}