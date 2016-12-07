package com.example.sydney.todolist.db;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sydney.todolist.R;


public class SearchResultsCursorAdapter extends CursorRecyclerViewAdapter<SearchResultsCursorAdapter.SearchResultViewHolder>
        implements View.OnClickListener
{
    private final LayoutInflater layoutInflater;
    private OnItemClickListener onItemClickListener;

    public SearchResultsCursorAdapter(final Context context)
    {
        super();

        this.layoutInflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(final OnItemClickListener onItemClickListener)
    {
        this.onItemClickListener = onItemClickListener;
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

     /*
     * View.OnClickListener
     */

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

    public interface OnItemClickListener
    {
        void onItemClicked(Cursor cursor);
    }
}