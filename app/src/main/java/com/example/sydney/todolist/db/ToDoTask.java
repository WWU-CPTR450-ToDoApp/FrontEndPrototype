package com.example.sydney.todolist.db;

import android.database.Cursor;

import java.util.UUID;

public class ToDoTask {
    private int _id;
    private String _title;
    private long _date;
    private int _done, _repeat;
    private String _desc;
    private static String _uid;

    public ToDoTask() {}
    public ToDoTask(String title, long date, int done, int repeat, String desc) {
        this._title = title;
        this._date = date;
        this._done = done;
        this._repeat = repeat;
        this._desc = desc;
        this._uid = UUID.randomUUID().toString();
    }
    public ToDoTask(String title) {
        this._title = title;
    }
    public void setID(int id) {
        this._id = id;
    }
    public int getID() {
        return this._id;
    }

    public void setTitle(String title) {
        this._title = title;
    }
    public String getTitle() {
        return this._title;
    }

    public void setDate(long date) {
        this._date = date;
    }
    public long getDate() {
        return this._date;
    }

    public void setDone(int done) {
        this._done = done;
    }
    public int getDone() {
        return this._done;
    }

    public void setRepeat(int repeat) {
        this._repeat = repeat;
    }
    public int getRepeat() {
        return this._repeat;
    }

    public void setDesc(String desc) {
        this._desc = desc;
    }
    public String getDesc() {
        return this._desc;
    }

    public String getUID() { return this._uid; }

    public static ToDoTask fromCursor(Cursor cursor) {
        ToDoTask t = new ToDoTask();
        int idx_title = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);
        int idx_desc = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_DESC);
        t.setTitle(cursor.getString(idx_title));
        t.setDesc(cursor.getString(idx_desc));
        return t;
    }
}
