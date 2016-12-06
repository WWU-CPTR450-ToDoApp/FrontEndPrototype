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
    private Calendar cal_date, cal_time;
    private Switch repeat_field;
    private int id;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // create a view based on alert_add_task.xmlsk.xml
        LayoutInflater inflater = LayoutInflater.from(getContext());
        final View addTaskView = inflater.inflate(R.layout.alert_add_task, null, false);
        final Calendar savedCal = Calendar.getInstance();

        // get ids from the layout
        title_field = (EditText) addTaskView.findViewById(R.id.tasktitle);
        date_field = (EditText) addTaskView.findViewById(R.id.date);
        time_field = (EditText) addTaskView.findViewById(R.id.time);
        repeat_field = (Switch) addTaskView.findViewById(R.id.repeat);
        notes_field = (EditText) addTaskView.findViewById(R.id.notes);
        cal_date = Calendar.getInstance();
        cal_time = Calendar.getInstance();
        // get current values of the task from the bundle arguments
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            id = bundle.getInt(TaskContract.TaskEntry._ID);
            title_field.setText(bundle.getString(TaskContract.TaskEntry.COL_TASK_TITLE));
            savedCal.setTimeInMillis(bundle.getLong(TaskContract.TaskEntry.COL_TASK_DATE) + bundle.getLong(TaskContract.TaskEntry.COL_TASK_TIME));
            date_field.setText(savedCal.get(Calendar.MONTH)+1 + "/" + savedCal.get(Calendar.DAY_OF_MONTH) + "/" + savedCal.get(Calendar.YEAR));
            String am_pm = "AM";
            if(savedCal.get(Calendar.HOUR) >= 12) {am_pm = "PM";}
            time_field.setText( String.format("%02d", savedCal.get(Calendar.HOUR)%12)
                        + ":" + String.format("%02d", savedCal.get(Calendar.MINUTE))
                        + " " + am_pm);
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
                }, savedCal.get(Calendar.YEAR), savedCal.get(Calendar.MONTH), savedCal.get(Calendar.DAY_OF_MONTH));
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
                        cal_time.clear();
                        cal_time.set(Calendar.HOUR, hour);
                        cal_time.set(Calendar.MINUTE, min);
                        String am_pm = "AM";
                        if(hour >= 12) {
                            am_pm = "PM";
                        }
                        time_field.setText( String.format("%02d", hour%12)
                                    + ":" + String.format("%02d", min)
                                    + " " + am_pm);
                    }
                }, savedCal.get(Calendar.HOUR), savedCal.get(Calendar.MINUTE), false);
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
                                cal_time.getTimeInMillis(),
                                0,
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
                .create();
    }
}