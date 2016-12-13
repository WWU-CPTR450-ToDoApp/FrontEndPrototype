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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;

import com.example.sydney.todolist.MainActivity;
import com.example.sydney.todolist.R;
import com.example.sydney.todolist.notifications.NotificationEventReceiver;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import java.util.Calendar;

public class AddTaskFragment extends DialogFragment {
    private EditText title_field, date_field, time_field, notes_field;
    private Calendar cal_date;
    private Switch repeat_field;
    ExpandableRelativeLayout expandableLayout1;
    //TextView txt_help_gest;

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
        expandableLayout1 = (ExpandableRelativeLayout) addTaskView.findViewById(R.id.expandableLayout1);
        Button b = (Button) addTaskView.findViewById(R.id.expandableButton1);

        cal_date = Calendar.getInstance();
        Calendar tempcal = Calendar.getInstance();
        cal_date.clear();
        cal_date.set(tempcal.get(Calendar.YEAR), tempcal.get(Calendar.MONTH), tempcal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

        // Expandable options
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandableLayout1.toggle();
            }
        });

        // Show a date-picker when the date field is clicked
        date_field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                DatePickerDialog dfrag;
                dfrag = new DatePickerDialog(getContext(), R.style.MyDialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        cal_date.set(year, month, day);
                        month++;    // since month is 0 based, add 1 to display correctly
                        date_field.setText(month + "/" + day + "/" + year);
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                dfrag.show();
            }
        });

        // Show a time-picker when the time field is clicked
        time_field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                TimePickerDialog tfrag;
                tfrag = new TimePickerDialog(getContext(),R.style.MyDialogTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int min) {
                        cal_date.set(Calendar.HOUR_OF_DAY, hour);
                        cal_date.set(Calendar.MINUTE, min);
                        String am_pm = "AM";
                        if(hour >= 12) {
                            am_pm = "PM";
                        }
                        time_field.setText( String.format("%02d", hour%12)
                                    + ":" + String.format("%02d", min)
                                    + " " + am_pm);
                    }
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);
                tfrag.show();
            }
        });

        // Create a new instance of DatePickerDialog and return it
        return new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme)
                .setTitle("New Task")
                .setMessage("Add a new task")
                .setView(addTaskView)
                // Create the Add button to create new task
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NotificationEventReceiver.setupAlarm(getActivity(),"hello","0",cal_date.getTimeInMillis());
                        ViewPager vp = ((MainActivity)getActivity()).getViewPager();
                        Fragment page = getFragmentManager().findFragmentByTag("android:switcher:" + R.id.viewpager + ":" + vp.getCurrentItem());
                        AbstractFragment page2 = (AbstractFragment) page;
                        if (vp.getCurrentItem() == 1)
                            page2.addTaskReturnCall (
                            String.valueOf(title_field.getText()),
                                    cal_date.getTimeInMillis(),
                                    0,
                                    repeat_field.isChecked() ? 1 : 0,
                                    String.valueOf(notes_field.getText())
                            );
                        else
                            page2.addTaskReturnCall (
                                    String.valueOf(title_field.getText()),
                                    cal_date.getTimeInMillis() + 86400000,
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