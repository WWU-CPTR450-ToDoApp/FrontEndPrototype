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
                + TaskContract.TaskEntry.COL_TASK_TITLE + " TEXT NOT NULL, "
                + TaskContract.TaskEntry.COL_TASK_DATE + " INTEGER NOT NULL, "
                + TaskContract.TaskEntry.COL_TASK_TIME + " INTEGER NOT NULL, "
                + TaskContract.TaskEntry.COL_TASK_DONE + " INTEGER NOT NULL, "
                + TaskContract.TaskEntry.COL_TASK_REPEAT + " INTEGER NOT NULL, "
                + TaskContract.TaskEntry.COL_TASK_DESC + " INTEGER"
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
        values.put(TaskContract.TaskEntry.COL_TASK_TIME, task.getTime());
        values.put(TaskContract.TaskEntry.COL_TASK_DONE, task.getDone());
        values.put(TaskContract.TaskEntry.COL_TASK_REPEAT, task.getRepeat());
        values.put(TaskContract.TaskEntry.COL_TASK_DESC, task.getDesc());

        mCR.insert(TaskProvider.CONTENT_URI, values);
    }

    public Cursor findTask(String key, String col) {
        // Define a projection that specifies which columns from the database
        // we will actually use after this query
        String[] projection = {
                TaskContract.TaskEntry._ID, col
        };

        // Filter results WHERE col = key
        String selection = col + " = ?";
        String[] selectionArgs = {key};

        // How we want the results sorted in the resulting Cursor
        String sortOrder = null;
        //        TaskContract.TaskEntry.COL_TASK_DATE;

        Cursor c = mCR.query(TaskProvider.CONTENT_URI,
                projection,                     // the columns to return
                selection,                      // the columns for the WHERE clause
                selectionArgs,                  // the values for the WHERE clause
                sortOrder                       // the sort order
        );
        return c;
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

    public boolean deleteTask(String s) {
        boolean result = false;
        String query = "Select * FROM " + TaskContract.TaskEntry.TABLE + " WHERE " + TaskContract.TaskEntry.COL_TASK_TITLE + " = \"" + s + "\"";
        /*Cursor cursor = db.rawQuery(query, null);
        ToDoTask task = new ToDoTask();
        if(cursor.moveToFirst()) {
            task.setID(Integer.parseInt(cursor.getString(0)));
            db.delete(TaskContract.TaskEntry.TABLE, TaskContract.COLUMN_ID + " = ?",
                    new String[]{ String.valueOf(task.getID()) });
            cursor.close();
            result = true;
        }*/
        return result;
    }



}
