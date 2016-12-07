package com.example.sydney.todolist.db;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class TaskProvider extends ContentProvider {
    private static final String AUTHORITY =
            "com.example.sydney.todolist.db.TaskProvider";
    private static final String TASKS_TABLE = TaskContract.TaskEntry.TABLE;
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + TASKS_TABLE);
    public static final int TASKS = 1;
    public static final int TASKS_ID = 2;
    private static final UriMatcher sURIMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, TASKS_TABLE, TASKS);
        sURIMatcher.addURI(AUTHORITY, TASKS_TABLE + "/#",
                TASKS_ID);
    }

    private TaskDbHelper mDB;

    public TaskProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDB.getWritableDatabase();
        int rowsDeleted = 0;

        switch (uriType) {
            case TASKS:
                rowsDeleted = sqlDB.delete(TaskContract.TaskEntry.TABLE,
                        selection,
                        selectionArgs);
                break;

            case TASKS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(TaskContract.TaskEntry.TABLE,
                            TaskContract.TaskEntry._ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(TaskContract.TaskEntry.TABLE,
                            TaskContract.TaskEntry._ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);

        SQLiteDatabase sqlDB = mDB.getWritableDatabase();

        long id = 0;
        switch (uriType) {
            case TASKS:
                id = sqlDB.insert(TaskContract.TaskEntry.TABLE,
                        null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: "
                        + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(TASKS_TABLE + "/" + id);
    }

    @Override
    public boolean onCreate() {
        mDB = new TaskDbHelper(getContext(), null, null, 1);
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TaskContract.TaskEntry.TABLE);

        int uriType = sURIMatcher.match(uri);

        switch (uriType) {
            case TASKS_ID:
                queryBuilder.appendWhere(TaskContract.TaskEntry._ID + "="
                        + uri.getLastPathSegment());
                break;
            case TASKS:
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }

        Cursor cursor = queryBuilder.query(mDB.getReadableDatabase(),
                projection, selection, selectionArgs, null, null,
                sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(),
                uri);
        return cursor;    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDB.getWritableDatabase();
        int rowsUpdated = 0;

        switch (uriType) {
            case TASKS:
                rowsUpdated =
                        sqlDB.update(TaskContract.TaskEntry.TABLE,
                                values,
                                selection,
                                selectionArgs);
                break;
            case TASKS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated =
                            sqlDB.update(TaskContract.TaskEntry.TABLE,
                                    values,
                                    TaskContract.TaskEntry._ID + "=" + id,
                                    null);
                } else {
                    rowsUpdated =
                            sqlDB.update(TaskContract.TaskEntry.TABLE,
                                    values,
                                    TaskContract.TaskEntry._ID + "=" + id
                                            + " and "
                                            + selection,
                                    selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " +
                        uri);
        }
        getContext().getContentResolver().notifyChange(uri,
                null);
        return rowsUpdated;    }
}
