package com.clinica.doctors.Models;

import android.support.annotation.DrawableRes;

import com.clinica.doctors.R;

public class NotificationModel {

    private String id, from, to, catID, destinationID, type;
    private long date;

    public static final String DOCTOR_ACCEPTED_BOOKING = "Booking_Accepted_by_doctor";
    public static final String DOCTOR_CANCELED_BOOKING = "Booking_Canceled_by_doctor";
    public static final String DOCTOR_REFUSED_BOOKING = "Booking_Refused_by_doctor";

    public static final String USER_REQUSETED_BOOKING = "User_Requested_Booking";
    public static final String USER_CANCELED_BOOKING = "Booking_Canceled_by_User";

    public static final String USER_REQUESTED_QUESTION = "User_Requested_Question";
    public static final String DOCTOR_ANSWERED_QUESTION = "Doctor_Answered_Question";

    public NotificationModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCatID() {
        return catID;
    }

    public void setCatID(String catID) {
        this.catID = catID;
    }

    public String getDestinationID() {
        return destinationID;
    }

    public void setDestinationID(String destinationID) {
        this.destinationID = destinationID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @DrawableRes
    public int getImage() {
        switch (getType()) {
            case DOCTOR_ACCEPTED_BOOKING:
            case DOCTOR_CANCELED_BOOKING:
            case DOCTOR_REFUSED_BOOKING:
            case USER_REQUSETED_BOOKING:
            case USER_CANCELED_BOOKING:
                return R.drawable.ic_notification_appointment;
            case USER_REQUESTED_QUESTION:
            case DOCTOR_ANSWERED_QUESTION:
                return R.drawable.ic_notification_question;
            default:
                return R.drawable.notification_icon;
        }
    }
}
