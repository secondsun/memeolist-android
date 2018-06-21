package org.aerogear.apps.memeolist.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.feedhenry.sdk.sync.FHSyncUtils;

import org.aerogear.apps.memeolist.util.GsonUtil;
import org.json.fh.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by summers on 11/3/15.
 */
public class Project implements Comparable<Project>,Parcelable {

    private String id;
    private String ownerId;
    private Date createdOn = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime();
    private Date updatedOn = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime();

    private ArrayList<String> sharedWith = new ArrayList<>();
    private ArrayList<Commit> commits = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public ArrayList<String> getSharedWith() {
        return sharedWith;
    }

    public void setSharedWith(ArrayList<String> sharedWith) {
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

    public ArrayList<Commit> getCommits() {
        return commits;
    }

    @Override
    public int compareTo(Project another) {
        return another.createdOn.compareTo(createdOn);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Project project = (Project) o;

        if (ownerId != null ? !ownerId.equals(project.ownerId) : project.ownerId != null)
            return false;
        if (createdOn != null ? !createdOn.equals(project.createdOn) : project.createdOn != null)
            return false;
        if (updatedOn != null ? !updatedOn.equals(project.updatedOn) : project.updatedOn != null)
            return false;
        if (sharedWith != null ? !sharedWith.equals(project.sharedWith) : project.sharedWith != null)
            return false;
        return true;

    }

    @Override
    public int hashCode() {
        int result =  (ownerId != null ? ownerId.hashCode() : 0);
        result = 31 * result + (createdOn != null ? createdOn.hashCode() : 0);
        result = 31 * result + (updatedOn != null ? updatedOn.hashCode() : 0);
        result = 31 * result + (sharedWith != null ? sharedWith.hashCode() : 0);

        return result;
    }


    public long getFHhash() {
        JSONObject create = new JSONObject(GsonUtil.GSON.toJson(this));
        create.remove("commits");
        try {
            return FHSyncUtils.generateHash(create.toString()).hashCode();
        } catch (Exception e) {
            //TODO : Refactor the sdk code to expose the correct exception
            throw new RuntimeException(e);
        }

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.ownerId);
        dest.writeLong(createdOn != null ? createdOn.getTime() : -1);
        dest.writeLong(updatedOn != null ? updatedOn.getTime() : -1);
        dest.writeStringList(this.sharedWith);
        dest.writeTypedList(commits);
    }

    public Project() {
    }

    protected Project(Parcel in) {
        this.id = in.readString();
        this.ownerId = in.readString();
        long tmpCreatedOn = in.readLong();
        this.createdOn = tmpCreatedOn == -1 ? null : new Date(tmpCreatedOn);
        long tmpUpdatedOn = in.readLong();
        this.updatedOn = tmpUpdatedOn == -1 ? null : new Date(tmpUpdatedOn);
        this.sharedWith = in.createStringArrayList();
        this.commits = in.createTypedArrayList(Commit.CREATOR);
    }

    public static final Parcelable.Creator<Project> CREATOR = new Parcelable.Creator<Project>() {
        public Project createFromParcel(Parcel source) {
            return new Project(source);
        }

        public Project[] newArray(int size) {
            return new Project[size];
        }
    };
}
