package com.clinica.doctors.Activities.ConfirmAttendance;

import android.support.annotation.NonNull;

import com.clinica.doctors.Activities.Base.BasePresenter;
import com.clinica.doctors.Models.Booking;
import com.clinica.doctors.Models.Doctor.DayAppointments;
import com.clinica.doctors.Models.Doctor.Doctor;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Constants;
import com.clinica.doctors.Tools.DateUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class ConfirmAttendancePresenter extends BasePresenter {

    private ConfirmAttendanceActivity activity;
    private ConfirmAttendanceView view;
    private DatabaseReference mReference;
    private Doctor thisDoctor;

    ConfirmAttendancePresenter(ConfirmAttendanceView view, ConfirmAttendanceActivity activity) {
        super(view, activity);
        this.activity = activity;
        this.view = view;
        this.mReference = FirebaseDatabase.getInstance().getReference();
    }

    private String getId() {
        return FirebaseAuth.getInstance().getUid();
    }

    void getDoctor() {
        if (isNetworkAvailable()) {
            view.showLoading();
            mReference.child(Constants.DataBase.Doctors_NODE).child(getId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            thisDoctor = dataSnapshot.getValue(Doctor.class);
                            if (thisDoctor != null) {
                                thisDoctor.setUid(dataSnapshot.getKey());
                                view.initData(thisDoctor);
                            } else {
                                view.toastError(activity.getString(R.string.data_not_found));
                            }
                            view.hideLoading();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            view.toastError(activity.getString(R.string.data_not_found));
                            view.hideLoading();
                        }
                    });
        } else {
            view.toastError(activity.getString(R.string.message_no_internet));
        }
    }

    void cancelDayBookings(DayAppointments dayAppointments) {
        dayAppointments.setConfirmed(false);
        if (thisDoctor != null) {
            for (int i = 0; i < dayAppointments.getAppointments().size(); i++) {
                sentNotificationToUser(dayAppointments.getAppointments().get(i).getUserId());
                CanceledBooking(dayAppointments);
                dayAppointments.getAppointments().get(i).setStatus(Constants.ACTIVE_STATUS);
                dayAppointments.getAppointments().get(i).setUserId(null);
            }

            confirmDay(dayAppointments, false);

        }
    }

    private void CanceledBooking(final DayAppointments dayAppointments) {
        mReference.child(Constants.DataBase.Bookings_NODE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Booking booking = snapshot.getValue(Booking.class);
                            if (booking != null) {
                                if (DateUtils.isSameDay(new Date(dayAppointments.getTitle()), new Date(booking.getDate()))) {
                                    booking.setId(null);
                                    booking.setStatus(Booking.CANCELED_STATUS);
                                    mReference.child(Constants.DataBase.Bookings_NODE).child(snapshot.getKey()).setValue(booking);
                                }
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void sentNotificationToUser(String userId) {

    }

    void confirmDay(DayAppointments dayAppointments, boolean isConfirmed) {
        dayAppointments.setConfirmed(isConfirmed);
        if (thisDoctor != null) {
            if (thisDoctor.getDayAppointments() != null) {
                boolean isFound = false;
                for (int i = 0; i < thisDoctor.getDayAppointments().size(); i++) {
                    if (DateUtils.isSameDay(new Date(thisDoctor.getDayAppointments().get(i).getTitle()), new Date(dayAppointments.getTitle()))) {
                        isFound = true;
                        thisDoctor.getDayAppointments().set(i, dayAppointments);
                    }
                }
                if (!isFound) {
                    thisDoctor.getDayAppointments().add(dayAppointments);
                }
            } else {
                List<DayAppointments> dayAppointmentsList = new ArrayList<>();
                dayAppointmentsList.add(dayAppointments);
                thisDoctor.setDayAppointments(dayAppointmentsList);
            }

            thisDoctor.setUid(null);
            thisDoctor.setSpecialization(null);
            mReference.child(Constants.DataBase.Doctors_NODE).child(getId()).setValue(thisDoctor);

        }
    }
}
