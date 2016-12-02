package com.example.sydney.todolist;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {
    private ArrayList<String> mTaskList;
    private ArrayList<String> mDescList;
    private Context mContext;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView mCardView;
        public TextView mTaskView;
        public TextView mDescView;
        public MyViewHolder(View v) {
            super(v);

            mCardView = (CardView) v.findViewById(R.id.card_view);
            mTaskView = (TextView) v.findViewById(R.id.tv_title);
            mDescView = (TextView) v.findViewById(R.id.tv_desc);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerAdapter(Context context, ArrayList<String> taskList, ArrayList<String> descList) {
        mContext = context;
        mTaskList = taskList;
        mDescList = descList;
    }

    private Context getContext(){
        return mContext;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        Context context = parent.getContext();
        View v = LayoutInflater.from(context)
                .inflate(R.layout.card_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mTaskView.setText(mTaskList.get(position));
        holder.mDescView.setText(mDescList.get(position));
    }

    @Override
    public int getItemCount() {
        return mTaskList.size();
    }
}