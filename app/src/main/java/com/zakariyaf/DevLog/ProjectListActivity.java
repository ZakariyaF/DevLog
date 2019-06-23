package com.zakariyaf.DevLog;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import java.util.List;

public class ProjectListActivity extends AppCompatActivity {
    private ProjectRecyclerAdapter mProjectRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.new_project_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProjectListActivity.this, ProjectActivity.class));
            }
        });

        initializeDisplayContent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mProjectRecyclerAdapter.notifyDataSetChanged();
    }

    private void initializeDisplayContent() {
        final RecyclerView recyclerProjects = (RecyclerView) findViewById(R.id.list_projects);
        final LinearLayoutManager projectsLayoutManager = new LinearLayoutManager(this);
        recyclerProjects.setLayoutManager(projectsLayoutManager);

        List<ProjectInfo> projects = DataManager.getInstance().getProjects();
        mProjectRecyclerAdapter = new ProjectRecyclerAdapter(this, null);
        recyclerProjects.setAdapter(mProjectRecyclerAdapter);
    }

}
