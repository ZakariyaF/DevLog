package com.zakariyaf.DevLog;

import androidx.test.espresso.ViewInteraction;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static org.hamcrest.Matchers.*;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.assertion.ViewAssertions.*;

@RunWith(AndroidJUnit4.class)
public class ProjectCreationTest {
    static DataManager sDataManager;
    @Rule
    public ActivityTestRule<ProjectListActivity> mProjectListActivityRule =
            new ActivityTestRule<>(ProjectListActivity.class);

    @BeforeClass
    public static void classSetUp() throws Exception {
        sDataManager = DataManager.getInstance();
    }

    @Test
    public void createNewProject() {
        final CourseInfo course = sDataManager.getCourse("java");
        final String projectTitle = "Test project title";
        final String projectText = "This is the body of our test project";
        ViewInteraction fabNewProject = onView(withId(R.id.new_project_fab));
        fabNewProject.perform(click());
        //onView(withId(R.id.fab)).perform(click());

        onView(withId(R.id.spinner_courses)).perform(click());
        onData(allOf(instanceOf(CourseInfo.class), equalTo(course))).perform(click());

        onView(withId(R.id.spinner_courses)).check(matches(withSpinnerText(
                containsString(course.getTitle()))));

        onView(withId(R.id.text_project_title)).perform(typeText(projectTitle))
                .check(matches(withText(containsString(projectTitle))));

        onView(withId(R.id.text_project_text)).perform(typeText(projectText),
                closeSoftKeyboard());
        onView(withId(R.id.text_project_text)).check(matches(withText(containsString(projectText))));

        pressBack();

        int projectIndex = sDataManager.getProjects().size() - 1;
        ProjectInfo project = sDataManager.getProjects().get(projectIndex);
        assertEquals(course, project.getCourse());
        assertEquals(projectTitle, project.getTitle());
        assertEquals(projectText, project.getText());
    }
}








