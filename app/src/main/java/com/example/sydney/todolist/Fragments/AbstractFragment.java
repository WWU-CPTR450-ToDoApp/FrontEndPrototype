package com.example.sydney.todolist.Fragments;


import android.support.v4.app.Fragment;
import android.view.View;

public abstract class AbstractFragment extends Fragment {
    public abstract void addTask(View view);
    public abstract void deleteTask(String selection, String[] selectionArgs);
    public abstract void addTaskReturnCall(String title, long date, int done, int repeat, String desc);
    public abstract void editTaskReturnCall(int id, String title, long date, int done, int repeat, String desc);
}
