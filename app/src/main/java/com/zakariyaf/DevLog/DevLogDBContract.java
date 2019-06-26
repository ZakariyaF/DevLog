package com.zakariyaf.DevLog;

import android.provider.BaseColumns;

/**
 * Contract class for the SQLite database
 */
public final class DevLogDBContract {
    //deny instantiation through a private constructor
    private DevLogDBContract(){}

    //Nested class for the "CourseInfo" Table in the DB
    public static final class CourseInfoEntry implements BaseColumns {
        public static final String TABLE_NAME = "course_info";
        public static final String COLUMN_COURSE_ID = "course_id";
        public static final String COLUMN_COURSE_TITLE = "course_title";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_COURSE_ID + " TEXT UNIQUE NOT NULL, " +
                        COLUMN_COURSE_TITLE + " TEXT NOT NULL)";

        public static final String getQName(String columnName) {
            return TABLE_NAME + "." + columnName;
        }
    }

    //Nested class for the "Project" Table in DB
    public static final class ProjectInfoEntry implements BaseColumns{
        public static final String TABLE_NAME = "project_info";
        public static final String COLUMN_PROJECT_TITLE = "project_title";
        public static final String COLUMN_PROJECT_TEXT = "project_text";
        //this column relates to the same column from the CourseInfo table
        public static final String COLUMN_COURSE_ID = "course_id";

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COLUMN_PROJECT_TITLE + " TEXT NOT NULL, " +
                        COLUMN_PROJECT_TEXT + " TEXT, " +
                        COLUMN_COURSE_ID + " TEXT NOT NULL)";

        public static final String getQName(String columnName) {
            return TABLE_NAME + "." + columnName;
        }
    }
}
