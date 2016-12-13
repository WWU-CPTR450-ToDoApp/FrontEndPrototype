package com.example.sydney.todolist.Fragments;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sydney.todolist.R;
import com.example.sydney.todolist.db.TaskContract;
import com.example.sydney.todolist.db.TaskDbHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.example.sydney.todolist.db.DayAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by kyleelssmann on 12/2/16.
 */

public class StatisticFragment extends Fragment {
    private TaskDbHelper mHelper;
    View view;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new TaskDbHelper(getActivity(), null, null, 1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.statistics_fragment,container,false);
        //---------INITIALIZE VARIABLES---------

        // set colors
        int lowColor = Color.parseColor("#B71C1C"); //Red
        int mediumColor = Color.parseColor("#FFEB3B"); //Yellow
        int highColor = Color.parseColor("#388E3C"); //Green
        int completeColor = Color.parseColor("#125688"); //Blue

        // set boundaries for color changes
        float highLimit = 85f;
        float mediumLimit = 65f;

        // initialize time for start of day
        Calendar startOfDayCal = Calendar.getInstance();
        Calendar tempcal = Calendar.getInstance();
        startOfDayCal.clear();
        startOfDayCal.set(tempcal.get(Calendar.YEAR), tempcal.get(Calendar.MONTH), tempcal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        long todayInMilliseconds = startOfDayCal.getTimeInMillis();

        // initialize time of 2016 (for the date label calculation)
        //      note: based on code in DayAxisValueFormatter, the format will only work until 2020
        Calendar calendar2016 = Calendar.getInstance();
        calendar2016.clear();
        calendar2016.set(2016, 0, 0, 0, 0, 0);
        long year2016InMilliseconds = calendar2016.getTimeInMillis();

        // initialize pointer to last 7 days worth of tasks
        final Uri uri = Uri.parse("content://com.example.sydney.todolist.db.TaskProvider/" + TaskContract.TaskEntry.TABLE);
        String[] projection;      // the columns to return
        String selection;       // the columns for the WHERE clause
        String[] selectionArgs;   // the values for the WHERE clause
        String sortOrder;       // the sort order
        Calendar cal, calLo, calHi;
        // Next 7 Days
        projection = new String[]{
                TaskContract.TaskEntry.COL_TASK_DATE,
                TaskContract.TaskEntry.COL_TASK_DATE_COMPLETED,
                TaskContract.TaskEntry.COL_TASK_DONE
        };
        selection = TaskContract.TaskEntry.COL_TASK_DATE + " >= ?"
                + " AND " + TaskContract.TaskEntry.COL_TASK_DATE + " < ?";
        cal = Calendar.getInstance();
        calLo = Calendar.getInstance();
        calLo.clear();
        calLo.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)-7, 0, 0, 0);
        calHi = Calendar.getInstance();
        calHi.clear();
        calHi.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)+1, 0, 0, 0);
        selectionArgs = new String[]{
                String.valueOf(calLo.getTimeInMillis()),
                String.valueOf(calHi.getTimeInMillis())//,
        };
        sortOrder = TaskContract.TaskEntry.COL_TASK_DATE;

        Cursor lastSevenDays = mHelper.findTask(projection, selection, selectionArgs, sortOrder);
        lastSevenDays.moveToFirst();

        // initialize statistic Gathering Arrays
        //   Order -> oldest to newest
        Float[] openTasks = new Float[7]; //number of tasks that were open on that day
        Float[] completedTasks = new Float[7]; //number of tasks that were completed that day
        Arrays.fill(openTasks,(float) 0);
        Arrays.fill(completedTasks,(float) 0);

        //---------COUNT OPEN AND COMPLETED TASK NUMBERS/DAY---------
        // cycle through each task in the last seven days to calculate statistics
        for (int i = 0; i < lastSevenDays.getCount(); i++){
            int currentArrayIndex = 6; //7 total days and we start searching from the current day, sutract 1 for indexing

            // get information about the task
            long taskDate = lastSevenDays.getLong(lastSevenDays.getColumnIndex(TaskContract.TaskEntry.COL_TASK_DATE)); //Day in Milliseconds
            long completedDate = lastSevenDays.getLong(lastSevenDays.getColumnIndex(TaskContract.TaskEntry.COL_TASK_DATE_COMPLETED)); //Day in Milliseconds
            int completed = lastSevenDays.getInt(lastSevenDays.getColumnIndex(TaskContract.TaskEntry.COL_TASK_DONE));
            long dayToCompareTo = todayInMilliseconds;

            // change Dates to start at beginning of the day
            taskDate = setTimeToBeginningOfDay(taskDate);
            completedDate = setTimeToBeginningOfDay(completedDate);

            // debugging log
            Log.d("Statistics", String.valueOf(taskDate));
            Log.d("Completed", String.valueOf(completedDate));
            Log.d("CompareTo", String.valueOf(dayToCompareTo));


            do {
                //Add to completed list
                if ((completedDate == dayToCompareTo) && (completed == 1)){
                    completedTasks[currentArrayIndex] += (float) 1;
                }
                //Add to open list
                //   case that the task is not yet completed
                if (completed == 0) {
                    openTasks[currentArrayIndex] += 1;
                }
                //   case that task is completed
                else if (dayToCompareTo <= completedDate) {
                    openTasks[currentArrayIndex] += 1;
                }

                currentArrayIndex -= 1;
                dayToCompareTo -= 86400000; //subtract a day
            } while (dayToCompareTo != (taskDate - 86400000));

            lastSevenDays.moveToNext(); // increment to next task
        }

        //---------CALCULATE STATISTICS---------
        Float[] stats = new Float[7];
        Float[] dates = new Float[7];

        for (int i = 0; i<stats.length; i++){
            if (openTasks[i] == 0){
                // no open tasks is counted as 100% complete for that day
                stats[i] = 100f;
            }
            else {
                stats[i] = (completedTasks[i] / openTasks[i]) * 100;
            }
            //Note: Formatted days starts with how many days past 2016
            dates[6-i] = (float) (todayInMilliseconds- year2016InMilliseconds)/86400000 - i - 1; //Today in days - 1 day for each day back in array.
        }


        //---------CONFIGURE CHARTS---------
        //Create Color Array and Value
        int barChartColors[] = new int[stats.length];
        int pieChartColor = highColor;

        //Configure Bar Chart
        BarChart barChart = (BarChart) view.findViewById(R.id.barchart);
        //   Add Entries
        ArrayList<BarEntry> entries = new ArrayList<>();
        //Bar Entry Format: Date in days, percentage
        for (int i=0; i<stats.length; i++)
        {
            entries.add(new BarEntry(dates[i],stats[i]));

            //Set Color
            if (stats[i] == 100f) {
                barChartColors[i] = completeColor;
            }
            else if (stats[i] > highLimit) {
            barChartColors[i] = highColor;
            }
            else if (stats[i] > mediumLimit) {
                barChartColors[i] = mediumColor;
            }
            else {
                barChartColors[i] = lowColor;
            }
        }

        //   Place Entries in DataSets and Add to Chart
        BarDataSet dataset = new BarDataSet(entries, "Week Progress");
        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataset.setDrawValues(false); //Configure Look
        dataset.setColors(barChartColors);
        dataSets.add(dataset);
        BarData data = new BarData(dataSets);
        barChart.setData(data);

        //Format Legend
        barChart.getLegend().setEnabled(false);

        //Format X Axis
        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(barChart);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);

        //Format Y Axis
        barChart.getAxisLeft().setAxisMaximum(100f);   //Left
        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getAxisRight().setEnabled(false);     //Right


        //Remove Description
        Description description = new Description();
        description.setText("");
        barChart.setDescription(description);

        barChart.setDrawBorders(false);
        barChart.animateY(500);
        barChart.setTouchEnabled(false);


        //Configure Pie Chart

        PieChart pieChart = (PieChart) view.findViewById(R.id.piechart);

        ArrayList<PieEntry> pieEntry = new ArrayList<>();
        // add percent completed to pie chart
        pieEntry.add(new PieEntry(stats[stats.length-1], 70));
        // add percent uncompleted to pie chart
        pieEntry.add(new PieEntry(100f-stats[stats.length-1], 30));


        PieDataSet pieDataSet = new PieDataSet(pieEntry,"Today");
        pieDataSet.setDrawValues(false); //Configure
        //Change Color based on percent completed
        if (stats[stats.length-1] == 100f) {
            pieChartColor = completeColor;
        }
        else if (stats[stats.length-1] > highLimit) {
            pieChartColor = highColor;
        }
        else if (stats[stats.length-1] > mediumLimit) {
            pieChartColor = mediumColor;
        }
        else {
            pieChartColor = lowColor;
        }
        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(pieChartColor);
        colors.add(Color.parseColor("#FFFFFF"));
        colors.add(R.color.cardview_light_background);
        pieDataSet.setColors(colors);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);

        pieChart.getLegend().setEnabled(false);
        pieChart.setDescription(description);
        pieChart.setTouchEnabled(false);

        String centerText = String.format("%.0f%%", stats[stats.length-1]);//&#37 is %
        pieChart.setCenterText(centerText);
        pieChart.setCenterTextSizePixels(50f);
        pieChart.animateY(500);

        return view;
    }

    public long setTimeToBeginningOfDay(long time){
        //Set calendar to time given, then remove hours, minutes, seconds, and milliseconds
        long timeAtBeginningOfDay;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        timeAtBeginningOfDay = cal.getTimeInMillis();

        return timeAtBeginningOfDay;
    }
}

