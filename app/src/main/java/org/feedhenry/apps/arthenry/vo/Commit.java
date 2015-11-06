package org.feedhenry.apps.arthenry.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.feedhenry.sdk.sync.FHSyncUtils;
import com.google.gson.Gson;

import org.json.fh.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by summers on 11/3/15.
 */
public class Commit implements Comparable<Commit>,Parcelable {

    private String ownerId;
    private Date createdOn = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime();
    private Date updatedOn = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime();
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.ownerId);
        dest.writeLong(createdOn != null ? createdOn.getTime() : -1);
        dest.writeLong(updatedOn != null ? updatedOn.getTime() : -1);
        dest.writeSerializable(this.photoUrl);
        dest.writeTypedList(comments);
    }

    public Commit() {
    }

    protected Commit(Parcel in) {
        this.ownerId = in.readString();
        long tmpCreatedOn = in.readLong();
        this.createdOn = tmpCreatedOn == -1 ? null : new Date(tmpCreatedOn);
        long tmpUpdatedOn = in.readLong();
        this.updatedOn = tmpUpdatedOn == -1 ? null : new Date(tmpUpdatedOn);
        this.photoUrl = (URL) in.readSerializable();
        this.comments = in.createTypedArrayList(Comment.CREATOR);
    }

    public static final Parcelable.Creator<Commit> CREATOR = new Parcelable.Creator<Commit>() {
        public Commit createFromParcel(Parcel source) {
            return new Commit(source);
        }

        public Commit[] newArray(int size) {
            return new Commit[size];
        }
    };

    public long getFHHashCode() {
        JSONObject create = new JSONObject(new Gson().toJson(this));
        try {
            return FHSyncUtils.generateHash(create.toString()).hashCode();
        } catch (Exception e) {
            //TODO : Refactor the sdk code to expose the correct exception
            throw new RuntimeException(e);
        }
    }
}
