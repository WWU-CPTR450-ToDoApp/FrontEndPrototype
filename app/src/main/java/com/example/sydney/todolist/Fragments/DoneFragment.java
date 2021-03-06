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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import com.example.sydney.todolist.db.ItemClickSupport;
import com.example.sydney.todolist.db.RecyclerAdapter;
import com.example.sydney.todolist.db.TaskContract;
import com.example.sydney.todolist.db.TaskDbHelper;
import com.example.sydney.todolist.db.ToDoTask;
import com.example.sydney.todolist.notifications.NotificationEventReceiver;

import java.util.Calendar;

/**
 * Created by Sydney on 11/23/2016.
 * Fragment for the "Done" page
 */

public class DoneFragment extends AbstractFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_SEARCH_RESULTS = 0;
    private TaskDbHelper mHelper;
    private AlertDialog.Builder alertDialog;

    private RecyclerView mRecyclerView;
    private RecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Paint p = new Paint();

    private int mPos;

    public DoneFragment() {
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



        //Remove tasks over 30 days old
        final Uri uri = Uri.parse("content://com.example.sydney.todolist.db.TaskProvider/" + TaskContract.TaskEntry.TABLE);
        String[] projection;      // the columns to return
        String selection;       // the columns for the WHERE clause
        String[] selectionArgs;   // the values for the WHERE clause
        String sortOrder;       // the sort order
        Calendar cal, calMinusThirty;
        projection = new String[]{
                TaskContract.TaskEntry._ID
        };
        //Find tasks older than 30 days that are done
        selection = TaskContract.TaskEntry.COL_TASK_DATE + " < ?"
                + " AND " + TaskContract.TaskEntry.COL_TASK_DONE + " = ?";;
        cal = Calendar.getInstance();
        calMinusThirty = Calendar.getInstance();
        calMinusThirty.clear();
        calMinusThirty.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)-30, 0, 0, 0);

        selectionArgs = new String[]{
                String.valueOf(calMinusThirty.getTimeInMillis()),
                "1"
        };
        sortOrder = TaskContract.TaskEntry._ID;

        Cursor tasksOverThirtyDaysOld = mHelper.findTask(projection, selection, selectionArgs, sortOrder);
        tasksOverThirtyDaysOld.moveToFirst();

        for (int i = 0; i<tasksOverThirtyDaysOld.getCount(); i++){
            int taskID = tasksOverThirtyDaysOld.getInt(tasksOverThirtyDaysOld.getColumnIndex(TaskContract.TaskEntry._ID));
            deleteTaskSwipe(taskID); //This function is used because it removes tasks by their ID
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.done_fragment, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rv_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new RecyclerAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(llm);
        this.getLoaderManager().initLoader(mPos, null, this);
        initSwipe();
        longClickEvent();
        return rootView;
    }

    public void showStatistics(View view) {
        // Create new fragment and transaction
        Fragment newFragment = new StatisticFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.done_fragment, newFragment);
        transaction.addToBackStack(null);


        // Commit the transaction
        transaction.commit();
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
                Calendar cal, calHi;
                // DONE
                projection = new String[]{
                        TaskContract.TaskEntry._ID,
                        TaskContract.TaskEntry.COL_TASK_TITLE,
                        TaskContract.TaskEntry.COL_TASK_DESC,
                        TaskContract.TaskEntry.COL_TASK_DATE
                };
                selection = TaskContract.TaskEntry.COL_TASK_DONE + " = ?";
                selectionArgs = new String[]{"1"};
                sortOrder = TaskContract.TaskEntry.COL_TASK_DATE;

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
    public void addTask(View view) {
        DialogFragment addFrag = new AddTaskFragment();
        addFrag.show(getFragmentManager(), "addTask");
    }
    // called when the user clicks the add button on the alert popup
    public void addTaskReturnCall(String title, long date, int done, int repeat, String desc) {
        ToDoTask task = new ToDoTask(title, date, done, repeat, desc);
        mHelper.addTask(task);
        if (done == 0) {
            NotificationEventReceiver.setupAlarm(getActivity(), title, Long.toString(task.getID()), date);
        }
    }

    public void editTask(int id) {
        DialogFragment editFrag = new EditTaskFragment();
        Bundle bundle = new Bundle();
        String[] projection = {"*"};
        String selection = TaskContract.TaskEntry._ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(id)};
        Cursor c = mHelper.findTask(projection, selection, selectionArgs, null);

        // pass in the current values as arguments into the EditTaskFragment
        c.moveToFirst();
        String title = c.getString(c.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE));
        long date = c.getLong(c.getColumnIndex(TaskContract.TaskEntry.COL_TASK_DATE));
        int done = c.getInt(c.getColumnIndex(TaskContract.TaskEntry.COL_TASK_DONE));
        int repeat = c.getInt(c.getColumnIndex(TaskContract.TaskEntry.COL_TASK_REPEAT));
        String desc = c.getString(c.getColumnIndex(TaskContract.TaskEntry.COL_TASK_DESC));
        bundle.putInt(TaskContract.TaskEntry._ID, id);
        bundle.putString(TaskContract.TaskEntry.COL_TASK_TITLE, title);
        bundle.putLong(TaskContract.TaskEntry.COL_TASK_DATE, date);
        bundle.putInt(TaskContract.TaskEntry.COL_TASK_DONE, done);
        bundle.putInt(TaskContract.TaskEntry.COL_TASK_REPEAT, repeat);
        bundle.putString(TaskContract.TaskEntry.COL_TASK_DESC, desc);
        editFrag.setArguments(bundle);
        editFrag.show(getFragmentManager(), "editTask");
        NotificationEventReceiver.cancelAlarm(getActivity(),title,Integer.toString(id));
        if (done == 0) {
            NotificationEventReceiver.setupAlarm(getActivity(),title,Integer.toString(id),date);
        }
    }
    // function that is called when the user finishes the editing process
    @Override
    public void editTaskReturnCall(int id, String title, long date, int done, int repeat, String desc) {
        // update the row of the id with the new values
        ContentValues cv = new ContentValues();
        cv.put(TaskContract.TaskEntry.COL_TASK_TITLE, title);
        cv.put(TaskContract.TaskEntry.COL_TASK_DATE, date);
        cv.put(TaskContract.TaskEntry.COL_TASK_DONE, done);
        cv.put(TaskContract.TaskEntry.COL_TASK_REPEAT, repeat);
        cv.put(TaskContract.TaskEntry.COL_TASK_DESC, desc);
        String selection = TaskContract.TaskEntry._ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(id)};
        mHelper.updateTask(cv, selection, selectionArgs);
    }

    // delete function for when swiping left
    public void deleteTaskSwipe(int id) {
        // delete the row in the database with the given unique _ID
        String selection = TaskContract.TaskEntry._ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(id)};
        mHelper.deleteTask(selection, selectionArgs);
    }

    // delete function for when deleting from the edit dialog box
    public void deleteTask(String selection, String[] selectionArgs){
        NotificationEventReceiver.cancelAlarm(getActivity(), "", selectionArgs[0]);
        mHelper.deleteTask(selection, selectionArgs);
    }

    // set task to Today page
    public void setTaskToUndone(int id) {
        //Find today in milliseconds
        Calendar startOfDayCal = Calendar.getInstance();
        Calendar tempcal = Calendar.getInstance();
        startOfDayCal.clear();
        startOfDayCal.set(tempcal.get(Calendar.YEAR), tempcal.get(Calendar.MONTH), tempcal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        long todayInMilliseconds = startOfDayCal.getTimeInMillis();

        //setup cursor to the entry for the id
        String[] dateProjection = new String[]{TaskContract.TaskEntry.COL_TASK_DATE};
        String dateSelection = TaskContract.TaskEntry._ID + " = ?";
        String[] dateSelectionArgs = new String[]{String.valueOf(id)};
        Cursor c = mHelper.findTask(dateProjection, dateSelection, dateSelectionArgs, null);
        c.moveToFirst();

        //initialize content values
        ContentValues cv = new ContentValues();

        // set the done column of the task to 1 (TRUE), and update the database
        cv.put(TaskContract.TaskEntry.COL_TASK_DONE, 0);

        // set task start day to today
        long date = c.getLong(c.getColumnIndex(TaskContract.TaskEntry.COL_TASK_DATE));
        while (date < todayInMilliseconds){
            date += 86400000;
        }
        cv.put(TaskContract.TaskEntry.COL_TASK_DATE,date);


        String selection = TaskContract.TaskEntry._ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(id)};
        mHelper.updateTask(cv, selection, selectionArgs);
    }

    // function called when task is LONG clicked
    private void longClickEvent(){
        ItemClickSupport.addTo(mRecyclerView).setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int position, View v) {
                RecyclerAdapter.SearchResultViewHolder vh = (RecyclerAdapter.SearchResultViewHolder) viewHolder;
                editTask(vh.getID()); // Open edit dialog box
                return false;
            }
        });
    }

    // swipe handler, allows tasks to be swiped left and right
    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                RecyclerAdapter.SearchResultViewHolder vh = (RecyclerAdapter.SearchResultViewHolder) viewHolder;

                //Delete
                if (direction == ItemTouchHelper.LEFT) {
                    deleteTaskSwipe(vh.getID());
                }

                //Send to today
                else {
                    setTaskToUndone(vh.getID());
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    //Swipe right - To Today
                    if (dX > 0) {
                        p.setColor(Color.parseColor("#FF6F00"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_arrow_right_white_192x192);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                    //Swipe left - Delete
                    else {
                        p.setColor(Color.parseColor("#B71C1C"));
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
