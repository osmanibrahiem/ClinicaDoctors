package com.clinica.doctors.Models.Doctor;

public class BasicInformation {

    private String displayName, gender, professionalTitle, about;

    public BasicInformation() {
    }

    public BasicInformation(String displayName, String gender, String professionalTitle, String about, long birthDate) {
        this.displayName = displayName;
        this.gender = gender;
        this.professionalTitle = professionalTitle;
        this.about = about;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfessionalTitle() {
        return professionalTitle;
    }

    public void setProfessionalTitle(String professionalTitle) {
        this.professionalTitle = professionalTitle;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

}
