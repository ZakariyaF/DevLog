package com.zakariyaf.DevLog;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import com.zakariyaf.DevLog.DevLogDBContract.CourseInfoEntry;
import com.zakariyaf.DevLog.DevLogDBContract.ProjectInfoEntry;
import com.zakariyaf.DevLog.DevLogProviderContract.Courses;
import com.zakariyaf.DevLog.DevLogProviderContract.Projects;

public class DevLogProvider extends ContentProvider {

    private DevLogOpenHelper mDbOpenHelper;

    private static final String MIME_VENDOR_TYPE = "vnd." + DevLogProviderContract.AUTHORITY + ".";

    public static final int COURSES = 0;
    public static final int PROJECTS = 1;
    private static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final int PROJECTS_EXPANDED = 2;

    public static final int PROJECTS_ROW = 3;

    public static final int PROJECTS_EXPANDED_ROW = 5;
    private static final int COURSES_ROW = 4;

    static {
        sUriMatcher.addURI(DevLogProviderContract.AUTHORITY, Courses.PATH, COURSES);
        sUriMatcher.addURI(DevLogProviderContract.AUTHORITY, Projects.PATH, PROJECTS);
        sUriMatcher.addURI(DevLogProviderContract.AUTHORITY, Projects.PATH_EXPANDED, PROJECTS_EXPANDED);
        sUriMatcher.addURI(DevLogProviderContract.AUTHORITY, Courses.PATH + "/#", COURSES_ROW);
        sUriMatcher.addURI(DevLogProviderContract.AUTHORITY, Projects.PATH + "/#", PROJECTS_ROW);
        sUriMatcher.addURI(DevLogProviderContract.AUTHORITY, Projects.PATH_EXPANDED + "/#", PROJECTS_EXPANDED_ROW);
    }
    public DevLogProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        long rowId = -1;
        String rowSelection = null;
        String[] rowSelectionArgs = null;
        int nRows = -1;
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch) {
            case COURSES:
                nRows = db.delete(CourseInfoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PROJECTS:
                nRows = db.delete(ProjectInfoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PROJECTS_EXPANDED:
                // throw exception saying that this is a read-only table
                throw new UnsupportedOperationException("Read-Only table!");
            case COURSES_ROW:
                rowId = ContentUris.parseId(uri);
                rowSelection = CourseInfoEntry._ID + " = ?";
                rowSelectionArgs = new String[]{Long.toString(rowId)};
                nRows = db.delete(CourseInfoEntry.TABLE_NAME, rowSelection, rowSelectionArgs);
                break;
            case PROJECTS_ROW:
                rowId = ContentUris.parseId(uri);
                rowSelection = ProjectInfoEntry._ID + " = ?";
                rowSelectionArgs = new String[]{Long.toString(rowId)};
                nRows = db.delete(ProjectInfoEntry.TABLE_NAME, rowSelection, rowSelectionArgs);
                break;
            case PROJECTS_EXPANDED_ROW:
                // throw exception saying that this is a read-only table
                throw new UnsupportedOperationException("Read-Only table!");
        }

        return nRows;
    }

    @Override
    public String getType(Uri uri) {
        String mimeType = null;
        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch) {
            case COURSES:
                // vnd.android.cursor.dir/vnd.com.zakariyaf.devlog.provider.courses
                mimeType = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                        MIME_VENDOR_TYPE + Courses.PATH;
                break;
            case PROJECTS:
                mimeType = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + MIME_VENDOR_TYPE + Projects.PATH;
                break;
            case PROJECTS_EXPANDED:
                mimeType = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + MIME_VENDOR_TYPE + Projects.PATH_EXPANDED;
                break;
            case COURSES_ROW:
                mimeType = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + MIME_VENDOR_TYPE + Courses.PATH;
                break;
            case PROJECTS_ROW:
                mimeType = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + MIME_VENDOR_TYPE + Projects.PATH;
                break;
            case PROJECTS_EXPANDED_ROW:
                mimeType = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + MIME_VENDOR_TYPE + Projects.PATH_EXPANDED;
                break;
        }
        return mimeType;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        long rowId = -1;
        Uri rowUri = null;
        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch) {
            case PROJECTS:
                rowId = db.insert(ProjectInfoEntry.TABLE_NAME, null, values);
                rowUri = ContentUris.withAppendedId(Projects.CONTENT_URI, rowId);
                break;
            case COURSES:
                rowId = db.insert(CourseInfoEntry.TABLE_NAME, null, values);
                rowUri = ContentUris.withAppendedId(Courses.CONTENT_URI, rowId);
                break;
            case PROJECTS_EXPANDED:
                throw new UnsupportedOperationException("Read-Only table!");
        }
        return rowUri;
    }

    @Override
    public boolean onCreate() {
        mDbOpenHelper = new DevLogOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        long rowId = -1;
        String rowSelection = null;
        String[] rowSelectionArgs = null;
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
            case COURSES_ROW:
                rowId = ContentUris.parseId(uri);
                rowSelection = CourseInfoEntry._ID + " = ?";
                rowSelectionArgs = new String[]{Long.toString(rowId)};
                cursor = db.query(CourseInfoEntry.TABLE_NAME, projection, rowSelection,
                        rowSelectionArgs, null, null, null);
                break;
            case PROJECTS_ROW:
                rowId = ContentUris.parseId(uri);
                rowSelection = ProjectInfoEntry._ID + " = ?";
                rowSelectionArgs = new String[]{Long.toString(rowId)};
                cursor = db.query(ProjectInfoEntry.TABLE_NAME, projection, rowSelection,
                        rowSelectionArgs, null, null, null);
                break;
            case PROJECTS_EXPANDED_ROW:
                rowId = ContentUris.parseId(uri);
                rowSelection = ProjectInfoEntry.getQName(ProjectInfoEntry._ID) + " = ?";
                rowSelectionArgs = new String[]{Long.toString(rowId)};
                cursor = projectsExpandedQuery(db, projection, rowSelection, rowSelectionArgs, null);
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
