package com.zakariyaf.DevLog;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zakariyaf.DevLog.DevLogDBContract.CourseInfoEntry;
import com.zakariyaf.DevLog.DevLogDBContract.ProjectInfoEntry;
import com.zakariyaf.DevLog.DevLogProviderContract.Courses;
import com.zakariyaf.DevLog.DevLogProviderContract.Projects;

public class ProjectActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String PROJECT_ID = "com.zakariyaf.DevLog.PROJECT_ID";
    public static final String ORIGINAL_PROJECT_COURSE_ID = "com.zakariyaf.DevLog.ORIGINAL_PROJECT_COURSE_ID";
    public static final String ORIGINAL_PROJECT_TITLE = "com.zakariyaf.DevLog.ORIGINAL_PROJECT_TITLE";
    public static final String ORIGINAL_PROJECT_TEXT = "com.zakariyaf.DevLog.ORIGINAL_PROJECT_TEXT";
    public static final int ID_NOT_SET = -1;
    public static final int LOADER_PROJECTS = 0;
    public static final int LOADER_COURSES = 1;
    private final String TAG = getClass().getSimpleName();
    private ProjectInfo mProject = new ProjectInfo(
            DataManager.getInstance().getCourses().get(0), "", "");
    private boolean mIsNewProject;
    private Spinner mSpinnerCourses;
    private EditText mTextProjectTitle;
    private EditText mTextProjectText;
    private FloatingActionButton mReminderFab;
    private int mProjectID;
    private boolean mIsCancelling;
    private String mOriginalProjectCourseId;
    private String mOriginalProjectTitle;
    private String mOriginalProjectText;
    private DevLogOpenHelper mDbOpenHelper;
    private Cursor mProjectCursor;
    private int mCourseIdPos;
    private int mProjectTitlePos;
    private int mProjectTextPos;
    private SimpleCursorAdapter mAdapterCourses;

    /**
     * To be used as a flag to indicate that courses are ready.
     * If we attempt accessing projects before this field becomes true the app crashes.
     * That's why we will use it to assure that courses are the first to be loaded.
     */
    private boolean mCoursesQueryFinished;
    /**
     * To be used as a flag indicating that projects loading is finished.
     * The purpose is to orchestrate loading projects and notes to avoid accessing one from
     * another while it hasn't been loaded yet.
     */
    private boolean mProjectsQueryFinished;
    private boolean mIsEmptyProject;
    private Uri mProjectUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDbOpenHelper = new DevLogOpenHelper(ProjectActivity.this);

        mSpinnerCourses = (Spinner) findViewById(R.id.spinner_courses);

        mReminderFab = findViewById(R.id.reminder_fab);
        mReminderFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReminderNotification();
            }
        });

        mAdapterCourses = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, null,
                new String[]{Courses.COLUMN_COURSE_TITLE},
                new int[]{android.R.id.text1},
                0
        );
        mAdapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCourses.setAdapter(mAdapterCourses);

        LoaderManager.getInstance(this).initLoader(LOADER_COURSES, null, this);

        readDisplayStateValues();
        if (savedInstanceState == null) {
            saveOriginalProjectValues();
        } else {
            restoreOriginalProjectValues(savedInstanceState);
        }

        mTextProjectTitle = (EditText) findViewById(R.id.text_project_title);
        mTextProjectText = (EditText) findViewById(R.id.text_project_text);

        if (!mIsNewProject)
            LoaderManager.getInstance(this).initLoader(LOADER_PROJECTS, null, this);
        Log.d(TAG, "onCreate");
    }

    private void loadCourseData() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        String[] courseColumns = {
                Courses.COLUMN_COURSE_TITLE,
                Courses.COLUMN_COURSE_ID,
                Courses._ID
        };
        Cursor cursor = db.query(CourseInfoEntry.TABLE_NAME, courseColumns,
                null, null, null, null,
                Courses.COLUMN_COURSE_TITLE
        );
        mAdapterCourses.changeCursor(cursor);
    }

    private void loadProjectData() {
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        String courseId = "android";
        String titleStart = "activity";
        String selection = ProjectInfoEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(mProjectID)};

        String[] projectColumns = {
                Projects.COLUMN_COURSE_ID,
                Projects.COLUMN_PROJECT_TITLE,
                Projects.COLUMN_PROJECT_TEXT
        };

        mProjectCursor = db.query(ProjectInfoEntry.TABLE_NAME, projectColumns,
                selection, selectionArgs, null, null, null);
        mCourseIdPos = mProjectCursor.getColumnIndex(Projects.COLUMN_COURSE_ID);
        mProjectTitlePos = mProjectCursor.getColumnIndex(Projects.COLUMN_PROJECT_TITLE);
        mProjectTextPos = mProjectCursor.getColumnIndex(Projects.COLUMN_PROJECT_TEXT);
        //move from position -1 to position 0
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
            Log.i(TAG, "Cancelling project at position: " + mProjectID);
            if (mIsNewProject) {
                deleteProjectFromDatabase();
            } else {
                storePreviousProjectValues();
            }
        } else if (mTextProjectTitle.getText().toString().equals("")
                && mTextProjectText.getText().toString().equals("")) {
            deleteProjectFromDatabase();
        } else {
            saveProject();
        }
        Log.d(TAG, "onPause");
    }

    private void deleteProjectFromDatabase() {
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                getContentResolver().delete(mProjectUri, null, null);
                return null;
            }
        };
        task.execute();

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
        String courseId = selectedCourseId();
        String projectTitle = mTextProjectTitle.getText().toString();
        String projectText = mTextProjectText.getText().toString();
        saveProjectToDatabase(courseId, projectTitle, projectText);
    }

    private String selectedCourseId() {
        int selectedPosition = mSpinnerCourses.getSelectedItemPosition();
        Cursor cursor = mAdapterCourses.getCursor();
        cursor.moveToPosition(selectedPosition);
        int courseIdPos = cursor.getColumnIndex(Courses.COLUMN_COURSE_ID);
        String courseId = cursor.getString(courseIdPos);
        return courseId;
    }

    private void saveProjectToDatabase(String courseId, String projectTitle, String projectText) {
        final ContentValues values = new ContentValues();
        values.put(Projects.COLUMN_COURSE_ID, courseId);
        values.put(Projects.COLUMN_PROJECT_TITLE, projectTitle);
        values.put(Projects.COLUMN_PROJECT_TEXT, projectText);

        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                getContentResolver().update(mProjectUri, values, null, null);
                return null;
            }
        };
        task.execute();


    }

    private void displayProject() {
        String courseId = mProjectCursor.getString(mCourseIdPos);
        String projectTitle = mProjectCursor.getString(mProjectTitlePos);
        String projectText = mProjectCursor.getString(mProjectTextPos);

        int courseIndex = getIndexOfCourseId(courseId);
        mSpinnerCourses.setSelection(courseIndex);
        mTextProjectTitle.setText(projectTitle);
        mTextProjectText.setText(projectText);
    }

    private int getIndexOfCourseId(String courseId) {
        Cursor cursor = mAdapterCourses.getCursor();
        int courseIdPos = cursor.getColumnIndex(Courses.COLUMN_COURSE_ID);
        int courseRowIndex = 0;

        boolean more = cursor.moveToFirst();
        while (more) {
            String cursorCourseId = cursor.getString(courseIdPos);
            if (cursorCourseId.equals(courseId)) {
                break;
            }
            courseRowIndex++;
            more = cursor.moveToNext();
        }
        return courseRowIndex;
    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
        mProjectID = intent.getIntExtra(PROJECT_ID, ID_NOT_SET);
        mIsNewProject = mProjectID == ID_NOT_SET;
        if (mIsNewProject) {
            createNewProject();
        }

        Log.i(TAG, "mProjectID: " + mProjectID);
        //mProject = DataManager.getInstance().getProjects().get(mProjectID);

    }

    private void createNewProject() {
        AsyncTask<ContentValues, Integer, Uri> task = new AsyncTask<ContentValues, Integer, Uri>() {
            private ProgressBar mProgressBar;

            @Override
            protected void onPreExecute() {
                mProgressBar = findViewById(R.id.progress_bar);
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected Uri doInBackground(ContentValues... contentValues) {
                publishProgress(1);
                ContentValues insertValues = contentValues[0];
                publishProgress(2);
                Uri rowUri = getContentResolver().insert(Projects.CONTENT_URI, insertValues);
                publishProgress(3);
                return rowUri;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                int progressValue = values[0];
                mProgressBar.setProgress(progressValue);
            }

            @Override
            protected void onPostExecute(Uri uri) {
                mProjectUri = uri;
                mProgressBar.setVisibility(View.GONE);
            }
        };
        final ContentValues values = new ContentValues();
        values.put(Projects.COLUMN_COURSE_ID, "");
        values.put(Projects.COLUMN_PROJECT_TITLE, "");
        values.put(Projects.COLUMN_PROJECT_TEXT, "");

        task.execute(values);


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
        item.setEnabled(mProjectID < lastProjectIndex);
        return super.onPrepareOptionsMenu(menu);
    }

    private void moveNext() {
        saveProject();

        ++mProjectID;
        mProject = DataManager.getInstance().getProjects().get(mProjectID);

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


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if (id == LOADER_PROJECTS) {
            loader = createLoaderProjects();
        } else if (id == LOADER_COURSES) {
            loader = createLoaderCourses();
        }
        return loader;
    }

    private CursorLoader createLoaderCourses() {

        mCoursesQueryFinished = false;
        Uri uri = Courses.CONTENT_URI;
        String[] courseColumns = {
                Courses.COLUMN_COURSE_TITLE,
                Courses.COLUMN_COURSE_ID,
                Courses._ID
        };
        return new CursorLoader(this, uri, courseColumns, null, null,
                Courses.COLUMN_COURSE_TITLE);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_PROJECTS) {
            loadFinishProjects(data);
        } else if (loader.getId() == LOADER_COURSES) {
            mAdapterCourses.changeCursor(data);
            //At this point, we know that courses have been loaded and we can proceed to projects.
            mCoursesQueryFinished = true;
            displayProjectsWhenQueriesFinish();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if (loader.getId() == LOADER_PROJECTS) {
            if (mProjectCursor != null) {
                mProjectCursor.close();
            }
        } else if (loader.getId() == LOADER_COURSES) {
            mAdapterCourses.changeCursor(null);
        }
    }

    private CursorLoader createLoaderProjects() {

        mProjectsQueryFinished = false;
        String[] projectColumns = {
                Projects.COLUMN_COURSE_ID,
                Projects.COLUMN_PROJECT_TITLE,
                Projects.COLUMN_PROJECT_TEXT
        };
        mProjectUri = ContentUris.withAppendedId(Projects.CONTENT_URI, mProjectID);
        return new CursorLoader(this, mProjectUri, projectColumns, null, null, null);
    }

    private void loadFinishProjects(Cursor data) {
        mProjectCursor = data;
        mCourseIdPos = mProjectCursor.getColumnIndex(Projects.COLUMN_COURSE_ID);
        mProjectTitlePos = mProjectCursor.getColumnIndex(Projects.COLUMN_PROJECT_TITLE);
        mProjectTextPos = mProjectCursor.getColumnIndex(Projects.COLUMN_PROJECT_TEXT);
        //move from position -1 to position 0
        mProjectCursor.moveToNext();
        //At this point we know that projects have been loaded correctly.
        mProjectsQueryFinished = true;
        displayProjectsWhenQueriesFinish();
    }

    private void displayProjectsWhenQueriesFinish() {
        if (mCoursesQueryFinished && mProjectsQueryFinished) {
            displayProject();
        }
    }

    private void showReminderNotification() {
        String projectTitle = mTextProjectTitle.getText().toString();
        String projectText = mTextProjectText.getText().toString();
        int projectId = (int) ContentUris.parseId(mProjectUri);
        ProjectReminderNotification.notify(this, projectTitle, projectText, projectId);
    }
}












