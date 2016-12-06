package com.example.sydney.todolist.db;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sydney.todolist.R;

import java.util.ArrayList;


public class RecyclerAdapter extends CursorRecyclerViewAdapter<RecyclerAdapter.SearchResultViewHolder>
        implements View.OnClickListener
{
    private ArrayList<ToDoTask> mTaskList;
    private Context mContext;
    private final LayoutInflater layoutInflater;
    private SearchResultsCursorAdapter.OnItemClickListener onItemClickListener;

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

    public static class SearchResultViewHolder extends RecyclerView.ViewHolder
    {
        TextView textViewName;

        public SearchResultViewHolder(final View itemView)
        {
            super(itemView);
            textViewName =(TextView) itemView.findViewById(R.id.tv_title);
        }

        public void bindData(final Cursor cursor)
        {
            final String name = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE));
            this.textViewName.setText(name);
        }
    }

    @Override
    public SearchResultViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType)
    {
        final View view = this.layoutInflater.inflate(R.layout.card_item, parent, false);
        view.setOnClickListener(this);

        return new SearchResultViewHolder(view);
    }
    @Override
    public void onBindViewHolder(final SearchResultViewHolder holder, final Cursor cursor)
    {
        holder.bindData(cursor);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerAdapter(final Context context) {
        super();
        this.layoutInflater = LayoutInflater.from(context);
    }
    public void setOnItemClickListener(final SearchResultsCursorAdapter.OnItemClickListener onItemClickListener)
    {
        this.onItemClickListener = onItemClickListener;
    }
    @Override
    public void onClick(final View view)
    {
        if (this.onItemClickListener != null)
        {
            final RecyclerView recyclerView = (RecyclerView) view.getParent();
            final int position = recyclerView.getChildLayoutPosition(view);
            if (position != RecyclerView.NO_POSITION)
            {
                final Cursor cursor = this.getItem(position);
                this.onItemClickListener.onItemClicked(cursor);
            }
        }
    }
    public interface OnItemClickListener
    {
        void onItemClicked(Cursor cursor);
    }
    /*
    // Create new views (invoked by the layout manager)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        Context context = parent.getContext();
        View v = LayoutInflater.from(context)
                .inflate(R.layout.card_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, Cursor cursor) {
        ToDoTask t = ToDoTask.fromCursor(cursor);
        holder.mTaskView.setText(t.getTitle());
        holder.mDescView.setText(t.getDesc());
    }*/

    /*@Override
    public int getItemCount() {
        return mTaskList.size();
    }*/

}