package com.example.sydney.todolist.db;

import android.provider.BaseColumns;

public final class TaskContract {
    public static final int     DATABASE_VERSION    = 3;
    public static final String  DATABASE_NAME       = "todotaskDB.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    //public static final String COLUMN_ID = "_id";
    //public static final String COLUMN_TASKNAME = "taskname";

    public static class TaskEntry implements BaseColumns {
        public static final String TABLE            = "taskTable";
        public static final String COL_TASK_TITLE = "title";
        public static final String COL_TASK_DATE = "date";
        public static final String COL_TASK_DONE = "complete";
        public static final String COL_TASK_REPEAT = "repeat";
        public static final String COL_TASK_DESC = "notes";
    }
}
