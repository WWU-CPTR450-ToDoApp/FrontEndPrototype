package com.example.sydney.todolist.db;
import com.example.sydney.todolist.db.TaskProvider;

import android.content.ContentResolver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;

public class TaskDbHelper extends SQLiteOpenHelper {
    private ContentResolver mCR;
    public TaskDbHelper(Context context, String name,
                       SQLiteDatabase.CursorFactory factory, int version) {
        super(context, TaskContract.DATABASE_NAME, factory, TaskContract.DATABASE_VERSION);
        mCR = context.getContentResolver();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TASKS_TABLE = "CREATE TABLE " + TaskContract.TaskEntry.TABLE + "("
                + TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TaskContract.TaskEntry.COL_TASK_TITLE  + " TEXT NOT NULL, "
                + TaskContract.TaskEntry.COL_TASK_DATE   + " INTEGER NOT NULL, "
                + TaskContract.TaskEntry.COL_TASK_DONE   + " INTEGER NOT NULL, "
                + TaskContract.TaskEntry.COL_TASK_REPEAT + " INTEGER NOT NULL, "
                + TaskContract.TaskEntry.COL_TASK_DESC   + " INTEGER, "
                + TaskContract.TaskEntry.COL_TASK_DATE_COMPLETED + " INTEGER"
                + ");";
        db.execSQL(CREATE_TASKS_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE);
        onCreate(db);
    }

    public void addTask(ToDoTask task) {
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COL_TASK_TITLE, task.getTitle());
        values.put(TaskContract.TaskEntry.COL_TASK_DATE, task.getDate());
        values.put(TaskContract.TaskEntry.COL_TASK_DONE, task.getDone());
        values.put(TaskContract.TaskEntry.COL_TASK_REPEAT, task.getRepeat());
        values.put(TaskContract.TaskEntry.COL_TASK_DESC, task.getDesc());

        mCR.insert(TaskProvider.CONTENT_URI, values);
    }

    public Cursor findTask(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor c = mCR.query(TaskProvider.CONTENT_URI,
                projection,                     // the columns to return
                selection,                      // the columns for the WHERE clause
                selectionArgs,                  // the values for the WHERE clause
                sortOrder                       // the sort order
        );
        return c;
    }

    public void updateTask(ContentValues values, String selection, String[] selectionArgs) {
        mCR.update(TaskProvider.CONTENT_URI, values, selection, selectionArgs);
    }

    public int deleteTask(String selection, String[] selectionArgs) {
        int rowsDeleted = mCR.delete(TaskProvider.CONTENT_URI, selection, selectionArgs);
        return rowsDeleted;
    }



}
