package com.zakariyaf.DevLog;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.zakariyaf.DevLog.DevLogDBContract.ProjectInfoEntry;

import java.util.List;

public class ProjectActivity extends AppCompatActivity {
    public static final String PROJECT_POSITION = "com.zakariyaf.DevLog.PROJECT_POSITION";
    public static final String ORIGINAL_PROJECT_COURSE_ID = "com.zakariyaf.DevLog.ORIGINAL_PROJECT_COURSE_ID";
    public static final String ORIGINAL_PROJECT_TITLE = "com.zakariyaf.DevLog.ORIGINAL_PROJECT_TITLE";
    public static final String ORIGINAL_PROJECT_TEXT = "com.zakariyaf.DevLog.ORIGINAL_PROJECT_TEXT";
    public static final int POSITION_NOT_SET = -1;
    private final String TAG = getClass().getSimpleName();
    private ProjectInfo mProject;
    private boolean mIsNewProject;
    private Spinner mSpinnerCourses;
    private EditText mTextProjectTitle;
    private EditText mTextProjectText;
    private int mProjectPosition;
    private boolean mIsCancelling;
    private String mOriginalProjectCourseId;
    private String mOriginalProjectTitle;
    private String mOriginalProjectText;
    private DevLogOpenHelper mDbOpenHelper;
    private Cursor mProjectCursor;
    private int mCourseIdPos;
    private int mProjectTitlePos;
    private int mProjectTextPos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDbOpenHelper = new DevLogOpenHelper(ProjectActivity.this);

        mSpinnerCourses = (Spinner) findViewById(R.id.spinner_courses);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> adapterCourses =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCourses.setAdapter(adapterCourses);

        readDisplayStateValues();
        if (savedInstanceState == null) {
            saveOriginalProjectValues();
        } else {
            restoreOriginalProjectValues(savedInstanceState);
        }

        mTextProjectTitle = (EditText) findViewById(R.id.text_project_title);
        mTextProjectText = (EditText) findViewById(R.id.text_project_text);

        if (!mIsNewProject)
            loadProjectData();
        Log.d(TAG, "onCreate");
    }

    private void loadProjectData() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        String courseId = "android";
        String titleStart = "Life";
        String selection = ProjectInfoEntry.COLUMN_COURSE_ID + " = ? AND " +
                ProjectInfoEntry.COLUMN_PROJECT_TITLE + " LIKE ?";
        String[] selectionArgs = {courseId, titleStart + "%"};

        String[] projectColumns = {
                ProjectInfoEntry.COLUMN_COURSE_ID,
                ProjectInfoEntry.COLUMN_PROJECT_TITLE,
                ProjectInfoEntry.COLUMN_PROJECT_TEXT
        };

        mProjectCursor = db.query(ProjectInfoEntry.TABLE_NAME, projectColumns,
                selection, selectionArgs, null, null, null);
        mCourseIdPos = mProjectCursor.getColumnIndex(ProjectInfoEntry.COLUMN_COURSE_ID);
        mProjectTitlePos = mProjectCursor.getColumnIndex(ProjectInfoEntry.COLUMN_PROJECT_TITLE);
        mProjectTextPos = mProjectCursor.getColumnIndex(ProjectInfoEntry.COLUMN_PROJECT_TEXT);
        //move from position -1 to position0
        mProjectCursor.moveToNext();
        displayProject();
    }

    private void restoreOriginalProjectValues(Bundle savedInstanceState) {
        mOriginalProjectCourseId = savedInstanceState.getString(ORIGINAL_PROJECT_COURSE_ID);
        mOriginalProjectTitle = savedInstanceState.getString(ORIGINAL_PROJECT_TITLE);
        mOriginalProjectText = savedInstanceState.getString(ORIGINAL_PROJECT_TEXT);
    }

    private void saveOriginalProjectValues() {
        if (mIsNewProject)
            return;
        mOriginalProjectCourseId = mProject.getCourse().getCourseId();
        mOriginalProjectTitle = mProject.getTitle();
        mOriginalProjectText = mProject.getText();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsCancelling) {
            Log.i(TAG, "Cancelling project at position: " + mProjectPosition);
            if (mIsNewProject) {
                DataManager.getInstance().removeProject(mProjectPosition);
            } else {
                storePreviousProjectValues();
            }
        } else {
            saveProject();
        }
        Log.d(TAG, "onPause");
    }

    private void storePreviousProjectValues() {
        CourseInfo course = DataManager.getInstance().getCourse(mOriginalProjectCourseId);
        mProject.setCourse(course);
        mProject.setTitle(mOriginalProjectTitle);
        mProject.setText(mOriginalProjectText);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ORIGINAL_PROJECT_COURSE_ID, mOriginalProjectCourseId);
        outState.putString(ORIGINAL_PROJECT_TITLE, mOriginalProjectTitle);
        outState.putString(ORIGINAL_PROJECT_TEXT, mOriginalProjectText);
    }

    private void saveProject() {
        mProject.setCourse((CourseInfo) mSpinnerCourses.getSelectedItem());
        mProject.setTitle(mTextProjectTitle.getText().toString());
        mProject.setText(mTextProjectText.getText().toString());
    }

    private void displayProject() {
        String courseId = mProjectCursor.getString(mCourseIdPos);
        String projectTitle = mProjectCursor.getString(mProjectTitlePos);
        String projectText = mProjectCursor.getString(mProjectTextPos);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        int courseIndex = courses.indexOf(mProject.getCourse());
        mSpinnerCourses.setSelection(courseIndex);
        mTextProjectTitle.setText(projectTitle);
        mTextProjectText.setText(projectText);
    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
        mProjectPosition = intent.getIntExtra(PROJECT_POSITION, POSITION_NOT_SET);
        mIsNewProject = mProjectPosition == POSITION_NOT_SET;
        if (mIsNewProject) {
            createNewProject();
        }

        Log.i(TAG, "mProjectPosition: " + mProjectPosition);
        mProject = DataManager.getInstance().getProjects().get(mProjectPosition);

    }

    private void createNewProject() {
        DataManager dm = DataManager.getInstance();
        mProjectPosition = dm.createNewProject();
//        mProject = dm.getProjects().get(mProjectPosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_project, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_mail) {
            sendEmail();
            return true;
        } else if (id == R.id.action_cancel) {
            mIsCancelling = true;
            finish();
        } else if (id == R.id.action_next) {
            moveNext();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_next);
        int lastProjectIndex = DataManager.getInstance().getProjects().size() - 1;
        item.setEnabled(mProjectPosition < lastProjectIndex);
        return super.onPrepareOptionsMenu(menu);
    }

    private void moveNext() {
        saveProject();

        ++mProjectPosition;
        mProject = DataManager.getInstance().getProjects().get(mProjectPosition);

        saveOriginalProjectValues();
        displayProject();
        invalidateOptionsMenu();
    }

    private void sendEmail() {
        CourseInfo course = (CourseInfo) mSpinnerCourses.getSelectedItem();
        String subject = mTextProjectTitle.getText().toString();
        String text = "Checkout what I learned in the course \"" +
                course.getTitle() + "\"\n" + mTextProjectText.getText().toString();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        mDbOpenHelper.close();
        super.onDestroy();
    }
}












