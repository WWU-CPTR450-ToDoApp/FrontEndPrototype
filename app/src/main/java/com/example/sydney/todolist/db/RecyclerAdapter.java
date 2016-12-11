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

import java.sql.Time;
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
    public static class SearchResultViewHolder extends RecyclerView.ViewHolder
    {
        public CardView mCardView;
        public TextView mTaskView;
        public TextView mDescView;
        public TextView mTimeView;
        private int mID;
        public SearchResultViewHolder(final View itemView)
        {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.card_view);
            mTaskView = (TextView) itemView.findViewById(R.id.tv_title);
            mDescView = (TextView) itemView.findViewById(R.id.tv_desc);
            mTimeView = (TextView) itemView.findViewById(R.id.tv_time);
        }

        public void bindData(final Cursor cursor)
        {
            final String title = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE));
            final String desc = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_DESC));
            final Time time = new Time(cursor.getLong(cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TIME)));
            final int id = cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry._ID));
            this.mTaskView.setText(title);
            this.mDescView.setText(desc);

            this.mTimeView.setText(time.toString().substring(0,5));
            this.mID = id;
        }

        public int getID() {
            return this.mID;
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

}