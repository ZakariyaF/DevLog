package com.zakariyaf.DevLog;

import android.net.Uri;

public final class DevLogProviderContract {

    public static final String AUTHORITY = "com.zakariyaf.devlog.provider";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    private DevLogProviderContract() {
    }

    public static final class Courses {
        public static final String PATH = "courses";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
    }

    public static final class Projects {
        public static final String PATH = "projects";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, PATH);
    }
}
