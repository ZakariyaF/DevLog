package com.zakariyaf.DevLog;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataManagerTest {
    static DataManager sDataManager;

    @BeforeClass
    public static void classSetUp() throws Exception {
        sDataManager = DataManager.getInstance();
    }

    @Before
    public void setUp() throws Exception {
        sDataManager.getProjects().clear();
        sDataManager.initializeExampleProjects();
    }

    @Test
    public void createNewProject() throws Exception {
        final CourseInfo course = sDataManager.getCourse("java");
        final String projectTitle = "Test project title";
        final String projectText = "This is the body text of my test project";

        int projectIndex = sDataManager.createNewProject();
        ProjectInfo newProject = sDataManager.getProjects().get(projectIndex);
        newProject.setCourse(course);
        newProject.setTitle(projectTitle);
        newProject.setText(projectText);

        ProjectInfo compareProject = sDataManager.getProjects().get(projectIndex);
        assertEquals(course, compareProject.getCourse());
        assertEquals(projectTitle, compareProject.getTitle());
        assertEquals(projectText, compareProject.getText());
    }

    @Test
    public void findSimilarProjects() throws Exception {
        final CourseInfo course = sDataManager.getCourse("java");
        final String projectTitle = "Test project title";
        final String projectText1 = "This is the body text of my test project";
        final String projectText2 = "This is the body of my second test project";

        int projectIndex1 = sDataManager.createNewProject();
        ProjectInfo newProject1 = sDataManager.getProjects().get(projectIndex1);
        newProject1.setCourse(course);
        newProject1.setTitle(projectTitle);
        newProject1.setText(projectText1);

        int projectIndex2 = sDataManager.createNewProject();
        ProjectInfo newProject2 = sDataManager.getProjects().get(projectIndex2);
        newProject2.setCourse(course);
        newProject2.setTitle(projectTitle);
        newProject2.setText(projectText2);

        int foundIndex1 = sDataManager.findProject(newProject1);
        assertEquals(projectIndex1, foundIndex1);

        int foundIndex2 = sDataManager.findProject(newProject2);
        assertEquals(projectIndex2, foundIndex2);
    }

    @Test
    public void createNewProjectOneStepCreation() {
        final CourseInfo course = sDataManager.getCourse("java");
        final String projectTitle = "Test project title";
        final String projectText = "This is the body of my test project";

        int projectIndex = sDataManager.createNewProject(course, projectTitle, projectText);

        ProjectInfo compareProject = sDataManager.getProjects().get(projectIndex);
        assertEquals(course, compareProject.getCourse());
        assertEquals(projectTitle, compareProject.getTitle());
        assertEquals(projectText, compareProject.getText());
    }

}






















