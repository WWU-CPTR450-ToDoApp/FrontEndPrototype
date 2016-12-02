package com.example.sydney.todolist;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sydney.todolist.db.TaskContract;
import com.example.sydney.todolist.db.TaskDbHelper;
import com.example.sydney.todolist.db.ToDoTask;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Sydney on 11/23/2016.
 */

public class TomorrowFragment extends Fragment {
    private TaskDbHelper mHelper;
    private ArrayList<String> taskList;
    private ArrayList<String> descList;
    private AlertDialog.Builder alertDialog;

    private RecyclerView mRecyclerView;
    private RecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Paint p = new Paint();

    private int mPos;

    public TomorrowFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new TaskDbHelper(getActivity());
        taskList = new ArrayList<>();
        descList = new ArrayList<>();
        alertDialog = new AlertDialog.Builder(getActivity());
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mPos = bundle.getInt("position", -1);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.today_fragment, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new RecyclerAdapter(getActivity(), taskList, descList);
        mRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(llm);
        updateUI();
        initSwipe();
        return rootView;
    }

    private void updateUI() {
        taskList.clear();
        descList.clear();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = getCursorFromQuery(mPos);
        while (cursor.moveToNext()) {
            int idx_title = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);
            int idx_desc = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_DESC);
            taskList.add(cursor.getString(idx_title));
            descList.add(cursor.getString(idx_desc));
        }
        if (mAdapter == null) {
            mAdapter = new RecyclerAdapter(getActivity(),taskList, descList);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }


        cursor.close();
        db.close();
    }

    // function that is called when the add button is clicked
    public void addTask(View view) {
        DialogFragment addFrag = new AddTaskFragment();
        addFrag.show(getFragmentManager(), "addTask");
    }
    // called when the user clicks the add button on the alert popup
    public void addTaskReturnCall(String data_col1, long data_col2, long data_col3, int data_col4, int data_col5, String data_col6) {
        ToDoTask task = new ToDoTask(data_col1, data_col2, data_col3, data_col4, data_col5, data_col6);
        mHelper.addTask(task);
        updateUI();
    }

    public void editTask(View view) {
        //Get text from view
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.tv_title);
        String task = String.valueOf(taskTextView.getText());
        // Delete old value for task
        deleteTask(view);
        //Create EditText for dialog
        final EditText taskEditText = new EditText(getActivity());
        taskEditText.setText(task);
        alertDialog
                .setTitle("Edit task")
                .setView(taskEditText)
                .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = String.valueOf(taskEditText.getText());
                        SQLiteDatabase db = mHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        //Insert new value
                        values.put(TaskContract.TaskEntry.COL_TASK_TITLE, task);
                        db.insertWithOnConflict(TaskContract.TaskEntry.TABLE,
                                null,
                                values,
                                SQLiteDatabase.CONFLICT_REPLACE);
                        db.close();
                        updateUI();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        alertDialog.show();
    }

    public void deleteTask(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.tv_title);
        String task = String.valueOf(taskTextView.getText());
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(TaskContract.TaskEntry.TABLE,
                TaskContract.TaskEntry.COL_TASK_TITLE + " = ?",
                new String[]{task});
        db.close();
        updateUI();
    }

    public void setTaskToDone(View view) {
        // so currently, we'll be setting all tasks with the same
        // title to done, which we will need to fix later
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.tv_title);
        String task = String.valueOf(taskTextView.getText());

        // set the done column of the task to 1 (TRUE), and update the database
        ContentValues cv = new ContentValues();
        cv.put(TaskContract.TaskEntry.COL_TASK_DONE, 1);
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.update(TaskContract.TaskEntry.TABLE,
                cv,
                TaskContract.TaskEntry.COL_TASK_TITLE + " = ?",
                new String[]{task});
        db.close();
        updateUI();
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                RecyclerAdapter.MyViewHolder vh = (RecyclerAdapter.MyViewHolder) viewHolder;

                if (direction == ItemTouchHelper.LEFT) {
                    //deleteTask(vh.mTaskView);
                    setTaskToDone(vh.mTaskView);
                } else {
                    editTask(vh.mTaskView);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_edit_white);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }


    // returns a cursor pointing to all entries matching the sort criteria
    public Cursor getCursorFromQuery(int sortBy) {
        String[] projection;      // the columns to return
        String selection;       // the columns for the WHERE clause
        String[] selectionArgs;   // the values for the WHERE clause
        String sortOrder;       // the sort order
        Cursor c = null;
        Calendar cal, calHi;
        switch (sortBy) {
            case -1: // ALL
                projection = new String[]{
                        TaskContract.TaskEntry._ID,
                        TaskContract.TaskEntry.COL_TASK_TITLE,
                        TaskContract.TaskEntry.COL_TASK_DESC
                };
                selection = TaskContract.TaskEntry.COL_TASK_TITLE + " LIKE '%'";
                selectionArgs = new String[]{};
                sortOrder = TaskContract.TaskEntry.COL_TASK_DATE + "," + TaskContract.TaskEntry.COL_TASK_TIME;
                c = mHelper.findTask(projection, selection, selectionArgs, sortOrder);
                break;
            case 0: // DONE
                projection = new String[]{
                        TaskContract.TaskEntry._ID,
                        TaskContract.TaskEntry.COL_TASK_TITLE,
                        TaskContract.TaskEntry.COL_TASK_DESC
                };
                selection = TaskContract.TaskEntry.COL_TASK_DONE + " = ?";
                selectionArgs = new String[]{"1"};
                sortOrder = TaskContract.TaskEntry.COL_TASK_DATE + "," + TaskContract.TaskEntry.COL_TASK_TIME;
                c = mHelper.findTask(projection, selection, selectionArgs, sortOrder);
                break;
            case 1: // TODAY
                projection = new String[]{
                        TaskContract.TaskEntry._ID,
                        TaskContract.TaskEntry.COL_TASK_TITLE,
                        TaskContract.TaskEntry.COL_TASK_DESC
                };
                selection = TaskContract.TaskEntry.COL_TASK_DATE + " < ?";
                cal = Calendar.getInstance();
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH) + 1, 0, 0, 0);
                selectionArgs = new String[]{String.valueOf(cal.getTimeInMillis())};
                sortOrder = TaskContract.TaskEntry.COL_TASK_DATE + "," + TaskContract.TaskEntry.COL_TASK_TIME;
                c = mHelper.findTask(projection, selection, selectionArgs, sortOrder);
                break;
            case 2: // TOMORROW
                projection = new String[]{
                        TaskContract.TaskEntry._ID,
                        TaskContract.TaskEntry.COL_TASK_TITLE,
                        TaskContract.TaskEntry.COL_TASK_DESC
                };
                selection = TaskContract.TaskEntry.COL_TASK_DATE + " > ? "
                        + "AND " + TaskContract.TaskEntry.COL_TASK_DATE + " < ?";
                cal = Calendar.getInstance();
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH) + 1, 0, 0, 0);
                calHi = Calendar.getInstance();
                calHi.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH) + 2, 0, 0, 0);
                selectionArgs = new String[]{
                        String.valueOf(cal.getTimeInMillis()),
                        String.valueOf(calHi.getTimeInMillis())};
                sortOrder = TaskContract.TaskEntry.COL_TASK_DATE + "," + TaskContract.TaskEntry.COL_TASK_TIME;
                c = mHelper.findTask(projection, selection, selectionArgs, sortOrder);
                break;
            default:
        }

        return c;
    }


}
