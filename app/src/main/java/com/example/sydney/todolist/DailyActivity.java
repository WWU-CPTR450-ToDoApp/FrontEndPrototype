package com.example.sydney.todolist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by Sydney on 12/11/2016.
 * Daily page, currently not in use
 */

public class DailyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);

        // Set text in the activity
        TextView tv = (TextView)findViewById(R.id.textView);
        tv.setText("Sorry, page not implemented yet.");
    }
}