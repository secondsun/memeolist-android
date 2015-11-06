package org.feedhenry.apps.arthenry.events;

import org.feedhenry.apps.arthenry.vo.Project;

import java.util.Collections;
import java.util.List;

public class ProjectsAvailable {

    private final List<Project> projects;

    public ProjectsAvailable(List<Project> projects) {
        this.projects = projects;
    }

    public List<Project> getProjects() {
        return Collections.unmodifiableList(projects);
    }
}
