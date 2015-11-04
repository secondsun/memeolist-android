package org.feedhenry.apps.arthenry.vo;

import java.net.URL;

/**
 * Created by summers on 11/3/15.
 */
public class Account {
    private String _id;
    private Long id;
    private String email;
    private String familyName;
    private String gender;
    private String givenName;
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

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public URL getPicture() {
        return picture;
    }

    public void setPicture(URL picture) {
        this.picture = picture;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
        if (familyName != null ? !familyName.equals(account.familyName) : account.familyName != null)
            return false;
        if (gender != null ? !gender.equals(account.gender) : account.gender != null) return false;
        if (givenName != null ? !givenName.equals(account.givenName) : account.givenName != null)
            return false;
        return !(picture != null ? !picture.equals(account.picture) : account.picture != null);

    }

    @Override
    public int hashCode() {
        int result = _id != null ? _id.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (familyName != null ? familyName.hashCode() : 0);
        result = 31 * result + (gender != null ? gender.hashCode() : 0);
        result = 31 * result + (givenName != null ? givenName.hashCode() : 0);
        result = 31 * result + (picture != null ? picture.hashCode() : 0);
        return result;
    }

}
