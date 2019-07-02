package com.zakariyaf.DevLog;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import com.zakariyaf.DevLog.DevLogDBContract.CourseInfoEntry;
import com.zakariyaf.DevLog.DevLogDBContract.ProjectInfoEntry;
import com.zakariyaf.DevLog.DevLogProviderContract.Courses;
import com.zakariyaf.DevLog.DevLogProviderContract.Projects;

public class DevLogProvider extends ContentProvider {

    private DevLogOpenHelper mDbOpenHelper;

    public static final int COURSES = 0;
    public static final int PROJECTS = 1;
    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final int PROJECTS_EXPANDED = 2;

    static {
        sUriMatcher.addURI(DevLogProviderContract.AUTHORITY, Courses.PATH, COURSES);
        sUriMatcher.addURI(DevLogProviderContract.AUTHORITY, Projects.PATH, PROJECTS);
        sUriMatcher.addURI(DevLogProviderContract.AUTHORITY, Projects.PATH_EXPANDED, PROJECTS_EXPANDED);
    }
    public DevLogProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        mDbOpenHelper = new DevLogOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch) {
            case COURSES:
                cursor = db.query(CourseInfoEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case PROJECTS:
                cursor = db.query(ProjectInfoEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case PROJECTS_EXPANDED:
                cursor = projectsExpandedQuery(db, projection, selection, selectionArgs, sortOrder);
                break;
        }

        return cursor;
    }

    private Cursor projectsExpandedQuery(SQLiteDatabase db, String[] projection, String selection,
                                         String[] selectionArgs, String sortOrder) {
        String[] columns = new String[projection.length];
        for (int i = 0; i < projection.length; i++) {
            columns[i] = projection[i].equals(BaseColumns._ID) ||
                    projection[i].equals(DevLogProviderContract.CoursesIdColumns.COLUMN_COURSE_ID) ?
                    ProjectInfoEntry.getQName(projection[i]) :
                    projection[i];
        }
        String tablesWithJoin = ProjectInfoEntry.TABLE_NAME + " JOIN " +
                CourseInfoEntry.TABLE_NAME + " ON " +
                ProjectInfoEntry.getQName(ProjectInfoEntry.COLUMN_COURSE_ID) +
                " = " + CourseInfoEntry.getQName(CourseInfoEntry.COLUMN_COURSE_ID);
        return db.query(tablesWithJoin, columns, selection,
                selectionArgs, null, null, sortOrder);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
