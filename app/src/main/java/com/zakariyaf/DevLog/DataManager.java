package com.zakariyaf.DevLog;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zakariyaf.DevLog.DevLogDBContract.CourseInfoEntry;
import com.zakariyaf.DevLog.DevLogDBContract.ProjectInfoEntry;

import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static DataManager ourInstance = null;

    private List<CourseInfo> mCourses = new ArrayList<>();
    private List<ProjectInfo> mProjects = new ArrayList<>();

    private DataManager() {
    }

    public static DataManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new DataManager();
            //use the DB data instead, through "loadDataFromDatabase"
            //ourInstance.initializeCourses();
            //ourInstance.initializeExampleProjects();
        }
        return ourInstance;
    }

    public static void loadFromDatabase(DevLogOpenHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] courseColumns = {
                CourseInfoEntry.COLUMN_COURSE_ID,
                CourseInfoEntry.COLUMN_COURSE_TITLE};
        Cursor courseCursor = db.query(CourseInfoEntry.TABLE_NAME, courseColumns,
                null, null, null, null, null);
        loadCoursesFromDatabase(courseCursor);
        String[] projectColumns = {
                ProjectInfoEntry.COLUMN_PROJECT_TITLE,
                ProjectInfoEntry.COLUMN_PROJECT_TITLE,
                ProjectInfoEntry.COLUMN_COURSE_ID};
        Cursor projectCursor = db.query(ProjectInfoEntry.SQL_CREATE_TABLE, projectColumns,
                null, null, null, null, null);

    }

    private static void loadCoursesFromDatabase(Cursor cursor) {
        int courseIdPos = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_ID);
        int courseTitlePos = cursor.getColumnIndex(CourseInfoEntry.COLUMN_COURSE_TITLE);

        DataManager dataManager = getInstance();
        dataManager.mCourses.clear();
        //make sure to move the cursor one step before accessing the row:
        while (cursor.moveToNext()) {
            String courseId = cursor.getString(courseIdPos);
            String courseTitle = cursor.getString(courseTitlePos);
            CourseInfo course = new CourseInfo(courseId, courseTitle, null);

            dataManager.mCourses.add(course);
        }
        cursor.close();
    }

    public String getCurrentUserName() {
        return "User Name";
    }

    public String getCurrentUserEmail() {
        return "user@email.com";
    }

    public List<ProjectInfo> getProjects() {
        return mProjects;
    }

    public int createNewProject() {
        ProjectInfo project = new ProjectInfo(null, null, null);
        mProjects.add(project);
        return mProjects.size() - 1;
    }

    public int findProject(ProjectInfo project) {
        for (int index = 0; index < mProjects.size(); index++) {
            if (project.equals(mProjects.get(index)))
                return index;
        }

        return -1;
    }

    public void removeProject(int index) {
        mProjects.remove(index);
    }

    public List<CourseInfo> getCourses() {
        return mCourses;
    }

    public CourseInfo getCourse(String id) {
        for (CourseInfo course : mCourses) {
            if (id.equals(course.getCourseId()))
                return course;
        }
        return null;
    }

    public List<ProjectInfo> getProjects(CourseInfo course) {
        ArrayList<ProjectInfo> projects = new ArrayList<>();
        for (ProjectInfo project : mProjects) {
            if (course.equals(project.getCourse()))
                projects.add(project);
        }
        return projects;
    }

    public int getProjectCount(CourseInfo course) {
        int count = 0;
        for (ProjectInfo project : mProjects) {
            if (course.equals(project.getCourse()))
                count++;
        }
        return count;
    }

    //region Initialization code

    private void initializeCourses() {
        mCourses.add(initializeCourse1());
        mCourses.add(initializeCourse2());
        mCourses.add(initializeCourse3());
        mCourses.add(initializeCourse4());
    }

    public void initializeExampleProjects() {
        final DataManager dm = getInstance();

        CourseInfo course = dm.getCourse("android");
        course.getModule("android_m01").setComplete(true);
        course.getModule("android_m02").setComplete(true);
        course.getModule("android_m03").setComplete(true);
        mProjects.add(new ProjectInfo(course, "Activity Lifecycle",
                "Life, death and your activity"));
        mProjects.add(new ProjectInfo(course, "Intent",
                "An intent to do what?"));

        course = dm.getCourse("intermediate_android");
        course.getModule("intermediate_android_m01").setComplete(true);
        course.getModule("intermediate_android_m02").setComplete(true);
        mProjects.add(new ProjectInfo(course, "Service default threads",
                "Did you know that by default an Android Service will tie up the UI thread?"));
        mProjects.add(new ProjectInfo(course, "Long running operations",
                "Foreground Services can be tied to a notification icon"));

        course = dm.getCourse("java");
        course.getModule("java_m01").setComplete(true);
        course.getModule("java_m02").setComplete(true);
        course.getModule("java_m03").setComplete(true);
        course.getModule("java_m04").setComplete(true);
        course.getModule("java_m05").setComplete(true);
        course.getModule("java_m06").setComplete(true);
        course.getModule("java_m07").setComplete(true);
        mProjects.add(new ProjectInfo(course, "Parameters",
                "Leverage variable-length parameter lists"));
        mProjects.add(new ProjectInfo(course, "Anonymous classes",
                "Anonymous classes simplify implementing one-use types"));

        course = dm.getCourse("intermediate_java");
        course.getModule("intermediate_java_m01").setComplete(true);
        course.getModule("intermediate_java_m02").setComplete(true);
        course.getModule("intermediate_java_m03").setComplete(true);
        mProjects.add(new ProjectInfo(course, "Compiler options",
                "The -jar option isn't compatible with with the -cp option"));
        mProjects.add(new ProjectInfo(course, "Serialization",
                "Remember to include SerialVersionUID to assure version compatibility"));
    }

    private CourseInfo initializeCourse1() {
        List<ModuleInfo> modules = new ArrayList<>();
        modules.add(new ModuleInfo("android_m01", "Android Late Binding and Intents"));
        modules.add(new ModuleInfo("android_m02", "Component activation with intents"));
        modules.add(new ModuleInfo("android_m03", "Delegation and Callbacks through PendingIntents"));
        modules.add(new ModuleInfo("android_m04", "IntentFilter data tests"));
        modules.add(new ModuleInfo("android_m05", "Working with Platform Features Through Intents"));

        return new CourseInfo("android", "Android Development", modules);
    }

    private CourseInfo initializeCourse2() {
        List<ModuleInfo> modules = new ArrayList<>();
        modules.add(new ModuleInfo("intermediate_android_m01", "Challenges to a responsive user experience"));
        modules.add(new ModuleInfo("intermediate_android_m02", "Implementing long-running operations as a service"));
        modules.add(new ModuleInfo("intermediate_android_m03", "Service lifecycle management"));
        modules.add(new ModuleInfo("intermediate_android_m04", "Interacting with services"));

        return new CourseInfo("intermediate_android", "Intermediate Android Development", modules);
    }

    private CourseInfo initializeCourse3() {
        List<ModuleInfo> modules = new ArrayList<>();
        modules.add(new ModuleInfo("java_m01", "Introduction and Setting up Your Environment"));
        modules.add(new ModuleInfo("java_m02", "Creating a Simple App"));
        modules.add(new ModuleInfo("java_m03", "Variables, Data Types, and Math Operators"));
        modules.add(new ModuleInfo("java_m04", "Conditional Logic, Looping, and Arrays"));
        modules.add(new ModuleInfo("java_m05", "Representing Complex Types with Classes"));
        modules.add(new ModuleInfo("java_m06", "Class Initializers and Constructors"));
        modules.add(new ModuleInfo("java_m07", "A Closer Look at Parameters"));
        modules.add(new ModuleInfo("java_m08", "Class Inheritance"));
        modules.add(new ModuleInfo("java_m09", "More About Data Types"));
        modules.add(new ModuleInfo("java_m10", "Exceptions and Error Handling"));
        modules.add(new ModuleInfo("java_m11", "Working with Packages"));
        modules.add(new ModuleInfo("java_m12", "Creating Abstract Relationships with Interfaces"));
        modules.add(new ModuleInfo("java_m13", "Static Members, Nested Types, and Anonymous Classes"));

        return new CourseInfo("java", "Java Programming", modules);
    }

    private CourseInfo initializeCourse4() {
        List<ModuleInfo> modules = new ArrayList<>();
        modules.add(new ModuleInfo("intermediate_java_m01", "Introduction"));
        modules.add(new ModuleInfo("intermediate_java_m02", "Input and Output with Streams and Files"));
        modules.add(new ModuleInfo("intermediate_java_m03", "String Formatting and Regular Expressions"));
        modules.add(new ModuleInfo("intermediate_java_m04", "Working with Collections"));
        modules.add(new ModuleInfo("intermediate_java_m05", "Controlling App Execution and Environment"));
        modules.add(new ModuleInfo("intermediate_java_m06", "Capturing Application Activity with the Java Log System"));
        modules.add(new ModuleInfo("intermediate_java_m07", "Multithreading and Concurrency"));
        modules.add(new ModuleInfo("intermediate_java_m08", "Runtime Type Information and Reflection"));
        modules.add(new ModuleInfo("intermediate_java_m09", "Adding Type Metadata with Annotations"));
        modules.add(new ModuleInfo("intermediate_java_m10", "Persisting Objects with Serialization"));

        return new CourseInfo("intermediate_java", "Intermediate Java Programming", modules);
    }

    public int createNewProject(CourseInfo course, String projectTitle, String projectText) {
        int index = createNewProject();
        ProjectInfo project = getProjects().get(index);
        project.setCourse(course);
        project.setTitle(projectTitle);
        project.setText(projectText);

        return index;
    }


}
