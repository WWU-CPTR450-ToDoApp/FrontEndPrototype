package com.example.sydney.todolist;

import android.graphics.Color;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;

import com.example.sydney.todolist.R;
//        import java.util.ArrayList;
//        import android.support.v7.app.AppCompatActivity;
//        import android.os.Bundle;
//        import com.github.mikephil.charting.charts.BarChart;
//        import com.github.mikephil.charting.data.BarData;
//        import com.github.mikephil.charting.data.BarDataSet;
//        import com.github.mikephil.charting.data.BarEntry;
//        import com.github.mikephil.charting.utils.ColorTemplate;
//import android.content.ContentValues;
//import android.content.DialogInterface;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.RectF;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.example.sydney.todolist.db.DayAxisValueFormatter;

//import android.support.v7.app.AlertDialog;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.helper.ItemTouchHelper;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.widget.EditText;
//import android.widget.TextView;

import com.example.sydney.todolist.db.TaskContract;
import com.example.sydney.todolist.db.TaskDbHelper;
//import com.github.mikephil.charting.charts.BarChart;
//import com.github.mikephil.charting.components.XAxis;
//import com.github.mikephil.charting.components.YAxis;
//import com.github.mikephil.charting.formatter.IAxisValueFormatter;

        //import android.app.Fragment;
        import android.os.Bundle;
        import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageButton;
        import android.widget.LinearLayout;
        import android.widget.RelativeLayout;

        import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
        import com.github.mikephil.charting.data.BarDataSet;
        import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

        //import java.util.ArrayList;

import java.util.ArrayList;

/**
 * Created by kyleelssmann on 12/2/16.
 */

public class StatisticFragment extends Fragment {

    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.statistics_fragment,container,false);

        //Configure Bar Chart
        BarChart barChart = (BarChart) view.findViewById(R.id.barchart);
        //   Add Entries
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(4f, 54));
        entries.add(new BarEntry(5f, 100));
        entries.add(new BarEntry(6f, 40));
        entries.add(new BarEntry(7f, 62));
        entries.add(new BarEntry(8f, 100));
        entries.add(new BarEntry(9f, 46));

        //   Place Entries in DataSets and Add to Chart
        BarDataSet dataset = new BarDataSet(entries, "Week Progress");
        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataset.setDrawValues(false); //Configure Look
//        dataset.setColor(R.color.colorPrimary);
        dataset.setColor(Color.parseColor("#125688"));
        //        dataset.setColors(ColorTemplate
        // .COLORFUL_COLORS);
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
        pieEntry.add(new PieEntry(70f, 70));
        pieEntry.add(new PieEntry(30f, 30));


        PieDataSet pieDataSet = new PieDataSet(pieEntry,"Today");
        pieDataSet.setDrawValues(false); //Configure
        //Change Color
        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(Color.parseColor("#125688"));
        colors.add(Color.parseColor("#FFFFFF"));
        colors.add(R.color.cardview_light_background);
        pieDataSet.setColors(colors);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);

        pieChart.getLegend().setEnabled(false);
        pieChart.setDescription(description);
        pieChart.setTouchEnabled(false);
        pieChart.setCenterText("70%");


        return view;

    }
}

