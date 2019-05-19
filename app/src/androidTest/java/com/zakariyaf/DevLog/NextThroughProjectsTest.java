package com.zakariyaf.DevLog;

import org.junit.Rule;
import org.junit.Test;

import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsNot.not;

import java.util.List;

public class NextThroughProjectsTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule(MainActivity.class);

    @Test
    public void NextThroughProjects() {
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_projects));

        onView(withId(R.id.list_items)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        List<ProjectInfo> projects = DataManager.getInstance().getProjects();
        for (int index = 0; index < projects.size(); index++) {
            ProjectInfo project = projects.get(index);

            onView(withId(R.id.spinner_courses)).check(
                    matches(withSpinnerText(project.getCourse().getTitle())));
            onView(withId(R.id.text_project_title)).check(matches(withText(project.getTitle())));
            onView(withId(R.id.text_project_text)).check(matches(withText(project.getText())));

            if (index < projects.size() - 1)
                onView(allOf(withId(R.id.action_next), isEnabled())).perform(click());
        }
        onView(withId(R.id.action_next)).check(matches(not(isEnabled())));
        pressBack();
    }
}










