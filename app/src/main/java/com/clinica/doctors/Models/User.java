package com.clinica.doctors.Models;

public class User {

    private String uid, displayName, email, photoUrl, phoneNumber, gender, accountStatus, facebookID, googleID;
    private long creationTimestamp, lastSignInTimestamp, birthdayTimestamp;
    private int cityID;

    public static final String ACTIVE_STATUS = "active";
    public static final String BLOCKED_STATUS = "blocked";

    public static final String MALE_GENDER = "male";
    public static final String FEMALE_GENDER = "female";

    public User() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(long creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public long getLastSignInTimestamp() {
        return lastSignInTimestamp;
    }

    public void setLastSignInTimestamp(long lastSignInTimestamp) {
        this.lastSignInTimestamp = lastSignInTimestamp;
    }

    public long getBirthdayTimestamp() {
        return birthdayTimestamp;
    }

    public void setBirthdayTimestamp(long birthdayTimestamp) {
        this.birthdayTimestamp = birthdayTimestamp;
    }

    public int getCityID() {
        return cityID;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getFacebookID() {
        return facebookID;
    }

    public void setFacebookID(String facebookID) {
        this.facebookID = facebookID;
    }

    public String getGoogleID() {
        return googleID;
    }

    public void setGoogleID(String googleID) {
        this.googleID = googleID;
    }
}
