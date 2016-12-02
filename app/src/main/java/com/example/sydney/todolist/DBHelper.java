package com.example.sydney.todolist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by bryan on 11/3/16.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String db_NAME = "todolist.db";
    public static final int db_VER = 1;

    public DBHelper(Context context) {
        super(context, db_NAME, null, db_VER);
    }

    /* This onCreate method concatenates this SQL query
       before passing it to the execSQL method:

        CREATE TABLE taskList (
            _id INTEGER PRIMARY KEY AUTOINCREMENT,
            taskName TEXT NOT NULL,
            taskTime INTEGER,
            completed NOT NULL DEFAULT 0
        );
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TaskContract.TaskEntry.table_NAME + " ( " +
                TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TaskContract.TaskEntry.col_TITLE + " TEXT NOT NULL, " +
                TaskContract.TaskEntry.col_TIME + " INTEGER, " +
                TaskContract.TaskEntry.col_COMP + " NOT NULL DEFAULT 0)";
        db.execSQL(createTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        db.execSQL("DROP TABLE IF EXISTS " + TaskContract.TaskEntry.table_NAME);
        onCreate(db);
    }
}
