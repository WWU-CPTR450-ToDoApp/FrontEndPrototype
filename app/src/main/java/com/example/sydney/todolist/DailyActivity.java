package com.example.sydney.todolist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by Sydney on 12/11/2016.
 */

public class DailyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);

        TextView tv = (TextView)findViewById(R.id.textView);
        tv.setText("Sorry, page not implemented yet.");
    }
}
