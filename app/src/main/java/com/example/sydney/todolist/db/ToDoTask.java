package com.example.sydney.todolist.db;

public class ToDoTask {
    private int _id;
    private String _title;
    private long _date, _time;
    private int _done, _repeat;
    private String _desc;

    public ToDoTask() {}
    public ToDoTask(String title, long date, long time, int done, int repeat, String desc) {
        this._title = title;
        this._date = date;
        this._time = time;
        this._done = done;
        this._repeat = repeat;
        this._desc = desc;
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

    public void getTitle(String title) {
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

    public void setTime(long time) {
        this._time = time;
    }
    public long getTime() {
        return this._time;
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
}
