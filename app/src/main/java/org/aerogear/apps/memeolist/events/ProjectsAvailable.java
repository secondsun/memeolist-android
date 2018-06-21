package org.aerogear.apps.memeolist.events;

import org.aerogear.apps.memeolist.vo.Project;

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
