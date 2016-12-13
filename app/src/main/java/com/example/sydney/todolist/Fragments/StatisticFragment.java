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
        //Colors
        int lowColor = Color.parseColor("#a91212");
        int mediumColor = Color.parseColor("#FBF008"); //d0c80a
        int highColor = Color.parseColor("#17bd11");
        int completeColor = Color.parseColor("#125688");


        float highLimit = 85f;
        float mediumLimit = 65f;

        //Initialize time for start of day
        Calendar startOfDayCal = Calendar.getInstance();
        Calendar tempcal = Calendar.getInstance();
        startOfDayCal.clear();
        startOfDayCal.set(tempcal.get(Calendar.YEAR), tempcal.get(Calendar.MONTH), tempcal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        long todayInMilliseconds = startOfDayCal.getTimeInMillis();

        //Initialize time of 2016 for the date label calculation
        Calendar calendar2016 = Calendar.getInstance();
        calendar2016.clear();
        calendar2016.set(2016, 0, 0, 0, 0, 0);
        long year2016InMilliseconds = calendar2016.getTimeInMillis();

        //Pointer to last 7 days of tasks
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
                //+ " AND " + TaskContract.TaskEntry.COL_TASK_DONE + " = ?";
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
                //"1"
        };
        sortOrder = TaskContract.TaskEntry.COL_TASK_DATE;

        Cursor lastSevenDays = mHelper.findTask(projection, selection, selectionArgs, sortOrder);
        lastSevenDays.moveToFirst();

        //Statistic Gathering Arrays
        //   Order -> oldest to newest
        Float[] openTasks = new Float[7];
        Float[] completedTasks = new Float[7];
        Arrays.fill(openTasks,(float) 0);
        Arrays.fill(completedTasks,(float) 0);
//        Float openTasks[7] = {};

        //Cycle through tasks to
        for (int i = 0; i < lastSevenDays.getCount(); i++){

            long taskDate = lastSevenDays.getLong(lastSevenDays.getColumnIndex(TaskContract.TaskEntry.COL_TASK_DATE)); //Day in Milliseconds
            long completedDate = lastSevenDays.getLong(lastSevenDays.getColumnIndex(TaskContract.TaskEntry.COL_TASK_DATE_COMPLETED)); //Day in Milliseconds
            int completed = lastSevenDays.getInt(lastSevenDays.getColumnIndex(TaskContract.TaskEntry.COL_TASK_DONE));
            long dayToCompareTo = todayInMilliseconds;

            //Change Dates to start at beginning of the day
            taskDate = setTimeToBeginningOfDay(taskDate);
            completedDate = setTimeToBeginningOfDay(completedDate);
            int currentArrayIndex = 6; //7 total days and we start searching from the current day, sutract 1 for indexing

            Log.d("Statistics", String.valueOf(taskDate));
            Log.d("Completed", String.valueOf(completedDate));
            Log.d("CompareTo", String.valueOf(dayToCompareTo));


            do {
                //Add to completed list
                if ((completedDate == dayToCompareTo) && (completed == 1)){
                    completedTasks[currentArrayIndex] += (float) 1;
                }
                //Add to open list
                //Case that the task is not yet completed
                if (completed == 0) {
                    openTasks[currentArrayIndex] += 1;
                }
                else if (dayToCompareTo <= completedDate) {
                    openTasks[currentArrayIndex] += 1;
                }

                currentArrayIndex -= 1;
                dayToCompareTo -= 86400000; //subtract a day
            } while (dayToCompareTo != (taskDate - 86400000));

            lastSevenDays.moveToNext();
        }

        //Calculate Statistics
        Float[] stats = new Float[7];
        Float[] dates = new Float[7];

        for (int i = 0; i<stats.length; i++){
            if (openTasks[i] == 0){
                stats[i] = 100f;
            }
            else {
                stats[i] = (completedTasks[i] / openTasks[i]) * 100;
            }
            //Note: Formatted days starts with how many days past 2016
            dates[6-i] = (float) (todayInMilliseconds- year2016InMilliseconds)/86400000 - i - 1; //Today in days - 1 day for each day back in array.
        }
        //Get statistics
//        Float stats[] = {54f,100f,73f,92f,100f,46f,(float)lastSevenDays.getCount()};
//        Float dates[] = {4f,5f,6f,7f,8f,9f,10f};




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
//        entries.add(new BarEntry(4f, 54));
//        entries.add(new BarEntry(5f, 100));
//        entries.add(new BarEntry(6f, 40));
//        entries.add(new BarEntry(7f, 62));
//        entries.add(new BarEntry(8f, 100));
//        entries.add(new BarEntry(9f, 46));

        //   Place Entries in DataSets and Add to Chart
        BarDataSet dataset = new BarDataSet(entries, "Week Progress");
        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataset.setDrawValues(false); //Configure Look
//        dataset.setColor(Color.parseColor("#125688"));
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
        pieEntry.add(new PieEntry(stats[stats.length-1], 70));
        pieEntry.add(new PieEntry(100f-stats[stats.length-1], 30));


        PieDataSet pieDataSet = new PieDataSet(pieEntry,"Today");
        pieDataSet.setDrawValues(false); //Configure
        //Change Color
        //Set Color
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
//        String centerText = String.valueOf(lastSevenDays.getCount());
        pieChart.setCenterText(centerText);
        pieChart.animateY(500);

        return view;
    }

    public long setTimeToBeginningOfDay(long time){
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

