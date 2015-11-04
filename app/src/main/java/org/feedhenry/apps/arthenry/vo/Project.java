package org.feedhenry.apps.arthenry.vo;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by summers on 11/3/15.
 */
public class Project implements Comparable<Project> {

    private String _id;
    private Long ownerId;
    private Date createdOn;
    private Date updatedOn;

    private ArrayList<Integer> sharedWith = new ArrayList<>();
    private ArrayList<Commit> commits = new ArrayList<>();

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public ArrayList<Integer> getSharedWith() {
        return sharedWith;
    }

    public void setSharedWith(ArrayList<Integer> sharedWith) {
        this.sharedWith = sharedWith;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    @Override
    public int compareTo(Project another) {
        return createdOn.compareTo(another.createdOn);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Project project = (Project) o;

        if (_id != null ? !_id.equals(project._id) : project._id != null) return false;
        if (ownerId != null ? !ownerId.equals(project.ownerId) : project.ownerId != null)
            return false;
        if (createdOn != null ? !createdOn.equals(project.createdOn) : project.createdOn != null)
            return false;
        if (updatedOn != null ? !updatedOn.equals(project.updatedOn) : project.updatedOn != null)
            return false;
        if (sharedWith != null ? !sharedWith.equals(project.sharedWith) : project.sharedWith != null)
            return false;
        return !(commits != null ? !commits.equals(project.commits) : project.commits != null);

    }

    @Override
    public int hashCode() {
        int result = _id != null ? _id.hashCode() : 0;
        result = 31 * result + (ownerId != null ? ownerId.hashCode() : 0);
        result = 31 * result + (createdOn != null ? createdOn.hashCode() : 0);
        result = 31 * result + (updatedOn != null ? updatedOn.hashCode() : 0);
        result = 31 * result + (sharedWith != null ? sharedWith.hashCode() : 0);
        result = 31 * result + (commits != null ? commits.hashCode() : 0);
        return result;
    }
}
