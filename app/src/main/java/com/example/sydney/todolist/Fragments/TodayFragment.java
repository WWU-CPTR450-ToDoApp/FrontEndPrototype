package com.example.sydney.todolist.Fragments;

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
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sydney.todolist.R;
import com.example.sydney.todolist.db.RecyclerAdapter;
import com.example.sydney.todolist.db.TaskContract;
import com.example.sydney.todolist.db.TaskDbHelper;
import com.example.sydney.todolist.db.ToDoTask;

import java.util.Calendar;

/**
 * Created by Sydney on 11/23/2016.
 */

public class TodayFragment extends AbstractFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_SEARCH_RESULTS = 1;
    private TaskDbHelper mHelper;
    private AlertDialog.Builder alertDialog;

    private RecyclerView mRecyclerView;
    private RecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Paint p = new Paint();

    private int mPos;

    public TodayFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHelper = new TaskDbHelper(getActivity(), null, null, 1);
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
        mAdapter = new RecyclerAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(llm);
        this.getLoaderManager().initLoader(mPos, null, this);
        //updateUI();
        initSwipe();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int id, final Bundle args)
    {
        switch (id)
        {
            case LOADER_SEARCH_RESULTS:
                final Uri uri = Uri.parse("content://com.example.sydney.todolist.db.TaskProvider/" + TaskContract.TaskEntry.TABLE);
                String[] projection;      // the columns to return
                String selection;       // the columns for the WHERE clause
                String[] selectionArgs;   // the values for the WHERE clause
                String sortOrder;       // the sort order
                Cursor c = null;
                Calendar cal, calHi;
                projection = new String[]{
                        TaskContract.TaskEntry._ID,
                        TaskContract.TaskEntry.COL_TASK_TITLE,
                        TaskContract.TaskEntry.COL_TASK_DESC,
                        TaskContract.TaskEntry.COL_TASK_TIME
                };
                selection = TaskContract.TaskEntry.COL_TASK_DATE + " <= ?"
                        + " AND " + TaskContract.TaskEntry.COL_TASK_DONE + " = ?";
                cal = Calendar.getInstance();
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
                selectionArgs = new String[]{String.valueOf(cal.getTimeInMillis()),"0"};
                sortOrder = TaskContract.TaskEntry.COL_TASK_DATE + "," + TaskContract.TaskEntry.COL_TASK_TIME;
                c = mHelper.findTask(projection, selection, selectionArgs, sortOrder);
                return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, sortOrder);
        }

        return null;
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor data)
    {
        switch (loader.getId())
        {
            case LOADER_SEARCH_RESULTS:

                this.mAdapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(final Loader<Cursor> loader)
    {
        switch (loader.getId())
        {
            case LOADER_SEARCH_RESULTS:

                this.mAdapter.swapCursor(null);
                break;
        }
    }

    // function that is called when the add button is clicked
    @Override
    public void addTask(View view) {
        DialogFragment addFrag = new AddTaskFragment();
        addFrag.show(getFragmentManager(), "addTask");
    }
    // called when the user clicks the add button on the alert popup
    @Override
    public void addTaskReturnCall(String title, long date, long time, int done, int repeat, String desc) {
        ToDoTask task = new ToDoTask(title, date, time, done, repeat, desc);
        mHelper.addTask(task);
        //updateUI();
    }

    // function that is called when the user slides over a task to edit it
    public void editTask(int id) {
        DialogFragment editFrag = new EditTaskFragment();
        Bundle bundle = new Bundle();
        // return all columns in query result
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor dbCursor = db.query(TaskContract.TaskEntry.TABLE, null, null, null, null, null, null);
        String[] projection = dbCursor.getColumnNames();
        String selection = TaskContract.TaskEntry._ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(id)};
        Cursor c = mHelper.findTask(projection, selection, selectionArgs, null);

        // pass in the current values as arguments into the EditTaskFragment
        c.moveToFirst();
        String title = c.getString(c.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE));
        long date = c.getLong(c.getColumnIndex(TaskContract.TaskEntry.COL_TASK_DATE));
        long time = c.getLong(c.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TIME));
        int done = c.getInt(c.getColumnIndex(TaskContract.TaskEntry.COL_TASK_DONE));
        int repeat = c.getInt(c.getColumnIndex(TaskContract.TaskEntry.COL_TASK_REPEAT));
        String desc = c.getString(c.getColumnIndex(TaskContract.TaskEntry.COL_TASK_DESC));
        bundle.putInt(TaskContract.TaskEntry._ID, id);
        bundle.putString(TaskContract.TaskEntry.COL_TASK_TITLE, title);
        bundle.putLong(TaskContract.TaskEntry.COL_TASK_DATE, date);
        bundle.putLong(TaskContract.TaskEntry.COL_TASK_TIME, time);
        bundle.putInt(TaskContract.TaskEntry.COL_TASK_DONE, done);
        bundle.putInt(TaskContract.TaskEntry.COL_TASK_REPEAT, repeat);
        bundle.putString(TaskContract.TaskEntry.COL_TASK_DESC, desc);
        editFrag.setArguments(bundle);
        editFrag.show(getFragmentManager(), "editTask");
    }
    // function that is called when the user finishes the editing process
    @Override
    public void editTaskReturnCall(int id, String title, long date, long time, int done, int repeat, String desc) {
        // update the row of the id with the new values
        ContentValues cv = new ContentValues();
        cv.put(TaskContract.TaskEntry.COL_TASK_TITLE, title);
        cv.put(TaskContract.TaskEntry.COL_TASK_DATE, date);
        cv.put(TaskContract.TaskEntry.COL_TASK_TIME, time);
        cv.put(TaskContract.TaskEntry.COL_TASK_DONE, done);
        cv.put(TaskContract.TaskEntry.COL_TASK_REPEAT, repeat);
        cv.put(TaskContract.TaskEntry.COL_TASK_DESC, desc);
        String selection = TaskContract.TaskEntry._ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(id)};
        mHelper.updateTask(cv, selection, selectionArgs);
    }

    public void setTaskToDone(int id) {
        // set the done column of the task to 1 (TRUE), and update the database
        ContentValues cv = new ContentValues();
        cv.put(TaskContract.TaskEntry.COL_TASK_DONE, 1);
        String selection = TaskContract.TaskEntry._ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(id)};
        mHelper.updateTask(cv, selection, selectionArgs);
        //updateUI();
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                RecyclerAdapter.SearchResultViewHolder vh = (RecyclerAdapter.SearchResultViewHolder) viewHolder;

                if (direction == ItemTouchHelper.LEFT) {
                    setTaskToDone(vh.getID());
                } else {
                    editTask(vh.getID());
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
}
