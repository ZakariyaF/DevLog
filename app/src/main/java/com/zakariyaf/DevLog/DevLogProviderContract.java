package com.zakariyaf.DevLog;

import android.net.Uri;
import android.provider.BaseColumns;

public final class DevLogProviderContract {

    public static final String AUTHORITY = "com.zakariyaf.devlog.provider";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    private DevLogProviderContract() {
    }

    protected interface CoursesIdColumns {
        public static final String COLUMN_COURSE_ID = "course_id";
    }

    protected interface CoursesColumns {
        public static final String COLUMN_COURSE_TITLE = "course_title";
    }

    protected interface ProjectsColumns {
        public static final String COLUMN_PROJECT_TITLE = "project_title";
        public static final String COLUMN_PROJECT_TEXT = "project_text";
    }

    public static final class Courses implements CoursesColumns, CoursesIdColumns, BaseColumns {
        public static final String PATH = "courses";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
    }

    public static final class Projects implements ProjectsColumns, CoursesIdColumns, BaseColumns {
        public static final String PATH = "projects";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
    }
}
