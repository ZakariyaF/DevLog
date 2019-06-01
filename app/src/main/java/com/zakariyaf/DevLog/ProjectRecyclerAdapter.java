package com.zakariyaf.DevLog;

import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class ProjectRecyclerAdapter extends RecyclerView.Adapter<ProjectRecyclerAdapter.ViewHolder> {

    private final Context mContext;
    private final List<ProjectInfo> mProjects;
    private final LayoutInflater mLayoutInflater;

    public ProjectRecyclerAdapter(Context context, List<ProjectInfo> projects) {
        mContext = context;
        mProjects = projects;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.item_project_list, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ProjectInfo project = mProjects.get(position);
        holder.mTextCourse.setText(project.getCourse().getTitle());
        holder.mTextTitle.setText(project.getTitle());
        holder.mID = project.getID();
    }

    @Override
    public int getItemCount() {
        return mProjects.size();
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
                    intent.putExtra(ProjectActivity.PROJECT_POSITION, mID);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}







