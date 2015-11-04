package org.feedhenry.apps.arthenry.vo;

import java.util.Date;

/**
 * Created by summers on 11/3/15.
 */
public class Comment implements Comparable<Comment> {

    private String ownerId;
    private Date createdOn = new Date();
    private Date updatedOn = new Date();
    private String comment;


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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public int compareTo(Comment another) {
        return createdOn.compareTo(another.createdOn);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comment comment1 = (Comment) o;


        if (ownerId != null ? !ownerId.equals(comment1.ownerId) : comment1.ownerId != null)
            return false;
        if (createdOn != null ? !createdOn.equals(comment1.createdOn) : comment1.createdOn != null)
            return false;
        if (updatedOn != null ? !updatedOn.equals(comment1.updatedOn) : comment1.updatedOn != null)
            return false;
        return !(comment != null ? !comment.equals(comment1.comment) : comment1.comment != null);

    }

    @Override
    public int hashCode() {
        int result =  (ownerId != null ? ownerId.hashCode() : 0);
        result = 31 * result + (createdOn != null ? createdOn.hashCode() : 0);
        result = 31 * result + (updatedOn != null ? updatedOn.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        return result;
    }
}
