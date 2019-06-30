package com.clinica.doctors.Models.Doctor;

import java.util.Collections;
import java.util.List;

public class DayAppointments {

    private String id;
    private long title;
    private List<Appointment> appointments = Collections.emptyList();
    private boolean isConfirmed = false;

    public DayAppointments() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTitle() {
        return title;
    }

    public void setTitle(long title) {
        this.title = title;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }
}
