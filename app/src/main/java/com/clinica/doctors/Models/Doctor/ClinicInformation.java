package com.clinica.doctors.Models.Doctor;

import java.util.Collections;
import java.util.List;

public class ClinicInformation {

    private String displayNameAr, displayNameEn, phoneNumber;
    private List<String> imagesURLs = Collections.emptyList();
    private double examination = 0;
    private Address addressAr, addressEn;
    private List<WorkingDayHours> workingHours;

    public ClinicInformation() {
    }

    public ClinicInformation(String displayNameAr, String displayNameEn, String phoneNumber,
                             List<String> imagesURLs, double examination, Address addressAr,
                             Address addressEn) {
        this.displayNameAr = displayNameAr;
        this.displayNameEn = displayNameEn;
        this.phoneNumber = phoneNumber;
        this.imagesURLs = imagesURLs;
        this.examination = examination;
        this.addressAr = addressAr;
        this.addressEn = addressEn;
    }

    public String getDisplayNameAr() {
        return displayNameAr;
    }

    public void setDisplayNameAr(String displayNameAr) {
        this.displayNameAr = displayNameAr;
    }

    public String getDisplayNameEn() {
        return displayNameEn;
    }

    public void setDisplayNameEn(String displayNameEn) {
        this.displayNameEn = displayNameEn;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getImagesURLs() {
        return imagesURLs;
    }

    public void setImagesURLs(List<String> imagesURLs) {
        this.imagesURLs = imagesURLs;
    }

    public double getExamination() {
        return examination;
    }

    public void setExamination(double examination) {
        this.examination = examination;
    }

    public Address getAddressAr() {
        return addressAr;
    }

    public void setAddressAr(Address addressAr) {
        this.addressAr = addressAr;
    }

    public Address getAddressEn() {
        return addressEn;
    }

    public void setAddressEn(Address addressEn) {
        this.addressEn = addressEn;
    }

    public List<WorkingDayHours> getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(List<WorkingDayHours> workingHours) {
        this.workingHours = workingHours;
    }
}
