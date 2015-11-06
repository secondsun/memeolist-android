package org.feedhenry.apps.arthenry.vo;

import java.net.URL;

/**
 * Created by summers on 11/3/15.
 */
public class Account {
    private String _id;
    private String id;
    private String email;
    private String family_name;
    private String gender;
    private String given_name;
    private URL picture;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFamily_name() {
        return family_name;
    }

    public void setFamily_name(String family_name) {
        this.family_name = family_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGiven_name() {
        return given_name;
    }

    public void setGiven_name(String given_name) {
        this.given_name = given_name;
    }

    public URL getPicture() {
        return picture;
    }

    public void setPicture(URL picture) {
        this.picture = picture;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        if (_id != null ? !_id.equals(account._id) : account._id != null) return false;
        if (id != null ? !id.equals(account.id) : account.id != null) return false;
        if (email != null ? !email.equals(account.email) : account.email != null) return false;
        if (family_name != null ? !family_name.equals(account.family_name) : account.family_name != null)
            return false;
        if (gender != null ? !gender.equals(account.gender) : account.gender != null) return false;
        if (given_name != null ? !given_name.equals(account.given_name) : account.given_name != null)
            return false;
        return !(picture != null ? !picture.equals(account.picture) : account.picture != null);

    }

    @Override
    public int hashCode() {
        int result = _id != null ? _id.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (family_name != null ? family_name.hashCode() : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (given_name != null ? given_name.hashCode() : 0);
        result = 31 * result + (picture != null ? picture.hashCode() : 0);
        return result;
    }

    public String getName() {
        return String.format("%s %s", given_name, family_name);
    }
}
