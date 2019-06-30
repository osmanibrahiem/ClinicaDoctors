package com.clinica.doctors.Models;

import com.clinica.doctors.Models.Doctor.Doctor;

public class Booking {

    private String id, status, userId, message, doctorId;
    private long date;
    private User patient;
    private Doctor doctor;

    public static final String SUCCESS_STATUS = "Success";
    public static final String PENDING_STATUS = "Pending";
    public static final String CANCELED_STATUS = "Canceled";
    public static final String REQUESTED_STATUS = "Requested";
    public static final String REFUSED_STATUS = "Refused";

    public Booking() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public User getPatient() {
        return patient;
    }

    public void setPatient(User patient) {
        this.patient = patient;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }
}
