package org.feedhenry.apps.arthenry.vo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by summers on 11/3/15.
 */
public class Comment implements Comparable<Comment>, Parcelable {

    private String ownerId;
    private Date createdOn = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime();
    private Date updatedOn = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime();
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ownerId);
        dest.writeLong(createdOn != null ? createdOn.getTime() : -1);
        dest.writeLong(updatedOn != null ? updatedOn.getTime() : -1);
        dest.writeString(this.comment);
    }

    public Comment() {
    }

    protected Comment(Parcel in) {
        this.ownerId = in.readString();
        long tmpCreatedOn = in.readLong();
        this.createdOn = tmpCreatedOn == -1 ? null : new Date(tmpCreatedOn);
        long tmpUpdatedOn = in.readLong();
        this.updatedOn = tmpUpdatedOn == -1 ? null : new Date(tmpUpdatedOn);
        this.comment = in.readString();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        public Comment createFromParcel(Parcel source) {
            return new Comment(source);
        }

        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
}
