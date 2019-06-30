package com.clinica.doctors.Activities.Booking.View;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.clinica.doctors.Activities.Base.BasePresenter;
import com.clinica.doctors.Models.Booking;
import com.clinica.doctors.Models.Doctor.Doctor;
import com.clinica.doctors.Models.Doctor.WorkingDayHours;
import com.clinica.doctors.Models.NotificationModel;
import com.clinica.doctors.Models.User;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Constants;
import com.clinica.doctors.Tools.DateUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

class BookingDetailsPresenter extends BasePresenter {

    private BookingDetailsView view;
    private ViewBookingDetailsActivity activity;
    private DatabaseReference mReference;
    private Doctor thisDoctor;
    private Booking thisBooking;

    BookingDetailsPresenter(BookingDetailsView view, ViewBookingDetailsActivity activity) {
        super(view, activity);
        this.activity = activity;
        this.view = view;
        this.mReference = FirebaseDatabase.getInstance().getReference();
    }

    void getBooking(String bookingID) {
        if (isNetworkAvailable()) {
            view.showLoading();
            mReference.child(Constants.DataBase.Bookings_NODE).child(bookingID)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            thisBooking = dataSnapshot.getValue(Booking.class);
                            if (thisBooking != null) {
                                thisBooking.setId(dataSnapshot.getKey());
                                String doctorID = thisBooking.getDoctorId();
                                String patientID = thisBooking.getUserId();
                                getDoctor(doctorID, patientID);
                            } else {
                                view.showMessage(R.string.data_not_found);
                                view.hideLoading();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            view.showMessage(R.string.data_not_found);
                            view.hideLoading();
                        }
                    });

        } else {
            view.showMessage(R.string.message_no_internet);
        }
    }

    private void getDoctor(String doctorID, final String patientID) {
        mReference.child(Constants.DataBase.Doctors_NODE).child(doctorID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        thisDoctor = dataSnapshot.getValue(Doctor.class);
                        if (thisDoctor != null) {
                            thisBooking.setDoctor(thisDoctor);
                            if (!TextUtils.isEmpty(patientID))
                                getPatient(patientID);
                            else {
                                view.onGetBooking(thisBooking);
                                view.hideLoading();
                            }
                        } else {
                            view.showMessage(R.string.data_not_found);
                            view.hideLoading();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        view.showMessage(R.string.data_not_found);
                        view.hideLoading();
                    }
                });

    }

    private void getPatient(String patientID) {
        mReference.child(Constants.DataBase.USERS_NODE).child(patientID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User patient = dataSnapshot.getValue(User.class);
                        thisBooking.setPatient(patient);
                        view.onGetBooking(thisBooking);
                        view.hideLoading();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        view.showMessage(R.string.data_not_found);
                        view.hideLoading();
                    }
                });

    }

    void CheckInBooking() {
        final String id = thisBooking.getId();
        thisBooking.setId(null);
        thisBooking.setPatient(null);
        thisBooking.setDoctor(null);
        thisBooking.setStatus(Booking.SUCCESS_STATUS);
        mReference.child(Constants.DataBase.Bookings_NODE).child(id).setValue(thisBooking)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        view.showMessage(R.string.error_happened_2);
                        view.hideLoading();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        view.showLoading();
                        getBooking(id);
                    }
                });
    }

    void CanceledBooking() {
        final String id = thisBooking.getId();
        sendNotificationToUser(thisBooking.getUserId(), NotificationModel.DOCTOR_CANCELED_BOOKING,
                Constants.DataBase.Bookings_NODE, id);
        thisBooking.setId(null);
        thisBooking.setPatient(null);
        thisBooking.setDoctor(null);
        thisBooking.setStatus(Booking.CANCELED_STATUS);
        mReference.child(Constants.DataBase.Bookings_NODE).child(id).setValue(thisBooking)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        view.showMessage(R.string.error_happened_2);
                        view.hideLoading();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        view.showLoading();
                        clearAppointment(thisBooking.getDate(), id);
                    }
                });
    }

    private void clearAppointment(long date, String bookingID) {
        Calendar bookingDate = GregorianCalendar.getInstance();
        Calendar bookingTime = GregorianCalendar.getInstance();
        Calendar bookingDateTime = GregorianCalendar.getInstance();
        bookingDateTime.setTimeInMillis(date);

        bookingDate.set(Calendar.YEAR, bookingDateTime.get(Calendar.YEAR));
        bookingDate.set(Calendar.MONTH, bookingDateTime.get(Calendar.MONTH));
        bookingDate.set(Calendar.DATE, bookingDateTime.get(Calendar.DATE));
        bookingTime.set(Calendar.HOUR, bookingDateTime.get(Calendar.HOUR));
        bookingTime.set(Calendar.HOUR_OF_DAY, bookingDateTime.get(Calendar.HOUR_OF_DAY));
        bookingTime.set(Calendar.MINUTE, bookingDateTime.get(Calendar.MINUTE));
        bookingTime.set(Calendar.SECOND, bookingDateTime.get(Calendar.SECOND));

        for (int i = 0; i < thisDoctor.getDayAppointments().size(); i++) {
            if (DateUtils.isSameDay(new Date(thisDoctor.getDayAppointments().get(i).getTitle()),
                    new Date(bookingDate.getTimeInMillis()))) {
                for (int j = 0; j < thisDoctor.getDayAppointments().get(i).getAppointments().size(); j++) {
                    if (DateUtils.isSameTime(new Date(thisDoctor.getDayAppointments().get(i).getAppointments().get(j).getTime()),
                            new Date(bookingTime.getTimeInMillis()))) {
                        thisDoctor.getDayAppointments().get(i).getAppointments().get(j).setStatus(Constants.ACTIVE_STATUS);
                        thisDoctor.getDayAppointments().get(i).getAppointments().get(j).setUserId(null);
                        updateDoctor(bookingID);
                        break;
                    }
                }
                break;
            }
        }
    }

    private void updateDoctor(final String bookingID) {
        thisDoctor.setUid(null);
        mReference.child(Constants.DataBase.Doctors_NODE).child(thisBooking.getDoctorId()).setValue(thisDoctor)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        view.showMessage(R.string.error_happened_2);
                        view.hideLoading();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        view.hideLoading();
                        getBooking(bookingID);
                    }
                });
    }

    String getExaminationDuration() {
        Calendar bookingDateTime = GregorianCalendar.getInstance();
        bookingDateTime.setTimeInMillis(thisBooking.getDate());
        WorkingDayHours dayHours = getWorkingDay(bookingDateTime.get(Calendar.DAY_OF_WEEK));
        int duration = 0;
        if (dayHours != null) {
            duration = dayHours.getDuration();
        }
        return activity.getString(R.string.minute, duration);
    }

    private WorkingDayHours getWorkingDay(int day) {
        for (WorkingDayHours dayHours : thisDoctor.getClinicInformation().getWorkingHours()) {
            if (dayHours.getDayOfWeek() == day)
                return dayHours;
        }
        return null;
    }

    void acceptBooking() {
        sendNotificationToUser(thisBooking.getUserId(), NotificationModel.DOCTOR_ACCEPTED_BOOKING,
                Constants.DataBase.Bookings_NODE, thisBooking.getId());
        final String id = thisBooking.getId();
        thisBooking.setId(null);
        thisBooking.setPatient(null);
        thisBooking.setDoctor(null);
        thisBooking.setStatus(Booking.PENDING_STATUS);
        mReference.child(Constants.DataBase.Bookings_NODE).child(id).setValue(thisBooking)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        view.showMessage(R.string.error_happened_2);
                        view.hideLoading();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        view.showLoading();

                        Calendar bookingDate = GregorianCalendar.getInstance();
                        Calendar bookingTime = GregorianCalendar.getInstance();
                        Calendar bookingDateTime = GregorianCalendar.getInstance();
                        bookingDateTime.setTimeInMillis(thisBooking.getDate());

                        bookingDate.set(Calendar.YEAR, bookingDateTime.get(Calendar.YEAR));
                        bookingDate.set(Calendar.MONTH, bookingDateTime.get(Calendar.MONTH));
                        bookingDate.set(Calendar.DATE, bookingDateTime.get(Calendar.DATE));
                        bookingTime.set(Calendar.HOUR, bookingDateTime.get(Calendar.HOUR));
                        bookingTime.set(Calendar.HOUR_OF_DAY, bookingDateTime.get(Calendar.HOUR_OF_DAY));
                        bookingTime.set(Calendar.MINUTE, bookingDateTime.get(Calendar.MINUTE));
                        bookingTime.set(Calendar.SECOND, bookingDateTime.get(Calendar.SECOND));

                        for (int i = 0; i < thisDoctor.getDayAppointments().size(); i++) {
                            if (DateUtils.isSameDay(new Date(thisDoctor.getDayAppointments().get(i).getTitle()),
                                    new Date(bookingDate.getTimeInMillis()))) {
                                for (int j = 0; j < thisDoctor.getDayAppointments().get(i).getAppointments().size(); j++) {
                                    if (DateUtils.isSameTime(new Date(thisDoctor.getDayAppointments().get(i).getAppointments().get(j).getTime()),
                                            new Date(bookingTime.getTimeInMillis()))) {
                                        thisDoctor.getDayAppointments().get(i).getAppointments().get(j).setStatus(Constants.ACTIVE_STATUS);
                                        thisDoctor.getDayAppointments().get(i).getAppointments().get(j).setUserId(thisBooking.getUserId());
                                        updateDoctor(id);
                                        break;
                                    }
                                }
                                break;
                            }
                        }

                    }
                });
    }

}
