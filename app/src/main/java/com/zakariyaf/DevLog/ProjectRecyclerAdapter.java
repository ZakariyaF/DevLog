package com.zakariyaf.DevLog;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zakariyaf.DevLog.DevLogDBContract.ProjectInfoEntry;

import java.util.List;

public class ProjectRecyclerAdapter extends RecyclerView.Adapter<ProjectRecyclerAdapter.ViewHolder> {

    private final Context mContext;
    private Cursor mCursor;
    private final LayoutInflater mLayoutInflater;
    private int mCoursePos;
    private int mProjectTitlePos;
    private int mIdPos;

    public ProjectRecyclerAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
        mLayoutInflater = LayoutInflater.from(mContext);
        populateColumnPositions();
    }

    private void populateColumnPositions() {
        if (mCursor == null) {
            return;
        }
        mCoursePos = mCursor.getColumnIndex(ProjectInfoEntry.COLUMN_COURSE_ID);
        mProjectTitlePos = mCursor.getColumnIndex(ProjectInfoEntry.COLUMN_PROJECT_TITLE);
        mIdPos = mCursor.getColumnIndex(ProjectInfoEntry._ID);
    }

    public void changeCursor(Cursor cursor) {
        if (mCursor != null && mCursor != cursor) {
            mCursor.close();
        }
        mCursor = cursor;
        populateColumnPositions();
        notifyDataSetChanged();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_project_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String course = mCursor.getString(mCoursePos);
        String projectTitle = mCursor.getString(mProjectTitlePos);
        int id = mCursor.getInt(mIdPos);
        holder.mTextCourse.setText(course);
        holder.mTextTitle.setText(projectTitle);
        holder.mID = id;
    }

    @Override
    public int getItemCount() {
        return (mCursor == null) ? 0 : mCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView mTextCourse;
        public final TextView mTextTitle;
        public int mID;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextCourse = (TextView) itemView.findViewById(R.id.text_course);
            mTextTitle = (TextView) itemView.findViewById(R.id.text_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ProjectActivity.class);
                    intent.putExtra(ProjectActivity.PROJECT_ID, mID);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}







