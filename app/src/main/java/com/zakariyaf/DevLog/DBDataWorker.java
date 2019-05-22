package com.zakariyaf.DevLog;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;


public class DBDataWorker {
    private SQLiteDatabase mDb;

    public DBDataWorker(SQLiteDatabase db) {
        mDb = db;
    }

    public void insertCourses() {
        insertCourse("android", "Android Development");
        insertCourse("intermediate_android", "Intermediate Android Development");
        insertCourse("java", "Java Programming");
        insertCourse("intermediate_java", "Intermediate Java Programming");
    }

    public void insertSampleProjects() {
        insertProject("android", "Intent", "An intent to do what?");
        insertProject("android", "Activity Lifecycle", " Life, death and your activity");

        insertProject("intermediate_android", "Service default threads", "Did you know that by default an Android Service will tie up the UI thread?");
        insertProject("intermediate_android", "Long running operations", "Foreground Services can be tied to a notification icon");

        insertProject("java", "Parameters", "Leverage variable-length parameter lists");
        insertProject("java", "Anonymous classes", "Anonymous classes simplify implementing one-use types");

        insertProject("intermediate_java", "Compiler options", "The -jar option isn't compatible with with the -cp option");
        insertProject("intermediate_java", "Serialization", "Remember to include SerialVersionUID to assure version compatibility");
    }

    private void insertCourse(String courseId, String title) {
        ContentValues values = new ContentValues();
        values.put(DevLogDBContract.CourseInfoEntry.COLUMN_COURSE_ID, courseId);
        values.put(DevLogDBContract.CourseInfoEntry.COLUMN_COURSE_TITLE, title);

        long newRowId = mDb.insert(DevLogDBContract.CourseInfoEntry.TABLE_NAME, null, values);
    }

    private void insertProject(String courseId, String title, String text) {
        ContentValues values = new ContentValues();
        values.put(DevLogDBContract.ProjectInfoEntry.COLUMN_COURSE_ID, courseId);
        values.put(DevLogDBContract.ProjectInfoEntry.COLUMN_PROJECT_TITLE, title);
        values.put(DevLogDBContract.ProjectInfoEntry.COLUMN_PROJECT_TEXT, text);

        long newRowId = mDb.insert(DevLogDBContract.ProjectInfoEntry.TABLE_NAME, null, values);
    }

}
