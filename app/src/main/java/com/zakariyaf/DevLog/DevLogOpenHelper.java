package com.zakariyaf.DevLog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.zakariyaf.DevLog.DevLogDBContract.CourseInfoEntry;
import com.zakariyaf.DevLog.DevLogDBContract.ProjectInfoEntry;

public class DevLogOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "DevLog.db";
    public static final int DATABASE_VERSION = 1;
    public DevLogOpenHelper(@Nullable Context context) {
        /*
        Don't customize the DB behavior so set the CursorFactory parameter to null
        Also, replace the name and version parameters with the constants from this class
        Then remove those three parameters from the constructor and keep context only.
         */
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create the "CourseInfo" table
        db.execSQL(CourseInfoEntry.SQL_CREATE_TABLE);
        //Create the "ProjectInfo" table
        db.execSQL(ProjectInfoEntry.SQL_CREATE_TABLE);
        //Create the "course_title" index
        db.execSQL(CourseInfoEntry.SQL_CREATE_INDEX1);
        //Create the "project_title" index
        db.execSQL(ProjectInfoEntry.SQL_CREATE_INDEX1);
        //Add sample data to the DB
        DBDataWorker dbDataWorker = new DBDataWorker(db);
        dbDataWorker.insertCourses();
        dbDataWorker.insertSampleProjects();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
