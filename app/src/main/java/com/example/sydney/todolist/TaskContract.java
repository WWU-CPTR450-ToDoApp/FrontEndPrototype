package com.example.sydney.todolist;

import android.provider.BaseColumns;

/**
 * Created by bryan on 11/3/16.
 */

public class TaskContract {

    private TaskContract() {
    }

    public static class TaskEntry implements BaseColumns {
        public static final String table_NAME = "taskList";
        public static final String col_TITLE = "taskName";
        public static final String col_TIME = "taskTime";
        public static final String col_COMP = "completed";
    }
}
