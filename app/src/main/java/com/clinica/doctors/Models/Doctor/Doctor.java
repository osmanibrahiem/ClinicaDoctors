package com.clinica.doctors.Models.Doctor;

import java.util.List;

public class Doctor {

    private String uid, email, profilePhotoUrl, phoneNumber, accountStatus, facebookID, googleID;
    private BasicInformation basicInformationAr, basicInformationEN;
    private long creationTimestamp, lastSignInTimestamp, birthDateTimestamp;
    private String practiceLicenseIdPhotoURL, specializationID, certificatePhotoURL;
    private Specialization specialization;
    private ClinicInformation clinicInformation;
    private List<DayAppointments> dayAppointments ;

    public static final String ACTIVE_STATUS = "Active";
    public static final String UNDER_REVIEW_STATUS = "Under_Review";
    public static final String BLOCKED_STATUS = "Blocked";
    public static final String DELETED_STATUS = "Deleted";

    public Doctor() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public BasicInformation getBasicInformationAr() {
        return basicInformationAr;
    }

    public void setBasicInformationAr(BasicInformation basicInformationAr) {
        this.basicInformationAr = basicInformationAr;
    }

    public BasicInformation getBasicInformationEN() {
        return basicInformationEN;
    }

    public void setBasicInformationEN(BasicInformation basicInformationEN) {
        this.basicInformationEN = basicInformationEN;
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

    public long getBirthDateTimestamp() {
        return birthDateTimestamp;
    }

    public void setBirthDateTimestamp(long birthDateTimestamp) {
        this.birthDateTimestamp = birthDateTimestamp;
    }

    public String getPracticeLicenseIdPhotoURL() {
        return practiceLicenseIdPhotoURL;
    }

    public void setPracticeLicenseIdPhotoURL(String practiceLicenseIdPhotoURL) {
        this.practiceLicenseIdPhotoURL = practiceLicenseIdPhotoURL;
    }

    public String getSpecializationID() {
        return specializationID;
    }

    public void setSpecializationID(String specializationID) {
        this.specializationID = specializationID;
    }

    public String getCertificatePhotoURL() {
        return certificatePhotoURL;
    }

    public void setCertificatePhotoURL(String certificatePhotoURL) {
        this.certificatePhotoURL = certificatePhotoURL;
    }

    public Specialization getSpecialization() {
        return specialization;
    }

    public void setSpecialization(Specialization specialization) {
        this.specialization = specialization;
    }

    public ClinicInformation getClinicInformation() {
        return clinicInformation;
    }

    public void setClinicInformation(ClinicInformation clinicInformation) {
        this.clinicInformation = clinicInformation;
    }

    public List<DayAppointments> getDayAppointments() {
        return dayAppointments;
    }

    public void setDayAppointments(List<DayAppointments> dayAppointments) {
        this.dayAppointments = dayAppointments;
    }

}
