package org.feedhenry.apps.arthenry.vo;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by summers on 11/3/15.
 */
public class Commit implements Comparable<Commit>{

    private String ownerId;
    private Date createdOn = new Date();
    private Date updatedOn = new Date();
    private URL photoUrl;
    private ArrayList<Comment> comments = new ArrayList<>();


    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
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

    public URL getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(URL photoUrl) {
        this.photoUrl = photoUrl;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public int compareTo(Commit another) {
        return createdOn.compareTo(another.createdOn);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Commit commit = (Commit) o;

        if (!ownerId.equals(commit.ownerId)) return false;
        if (!createdOn.equals(commit.createdOn)) return false;
        if (!updatedOn.equals(commit.updatedOn)) return false;
        if (!photoUrl.equals(commit.photoUrl)) return false;
        return comments.equals(commit.comments);

    }

    @Override
    public int hashCode() {
        int result = ownerId.hashCode();
        result = 31 * result + createdOn.hashCode();
        result = 31 * result + updatedOn.hashCode();
        result = 31 * result + photoUrl.hashCode();
        result = 31 * result + comments.hashCode();
        return result;
    }
}
