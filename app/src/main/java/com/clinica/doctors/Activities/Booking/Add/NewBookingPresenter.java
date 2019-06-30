package com.clinica.doctors.Activities.Booking.Add;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Patterns;

import com.clinica.doctors.Activities.Base.BasePresenter;
import com.clinica.doctors.Models.Booking;
import com.clinica.doctors.Models.Doctor.Appointment;
import com.clinica.doctors.Models.Doctor.Doctor;
import com.clinica.doctors.Models.User;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Constants;
import com.clinica.doctors.Tools.DateUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

class NewBookingPresenter extends BasePresenter {

    private AddNewBookingActivity activity;
    private NewBookingView view;
    private DatabaseReference mReference;
    private Doctor thisDoctor;

    NewBookingPresenter(NewBookingView view, AddNewBookingActivity activity) {
        super(view, activity);
        this.activity = activity;
        this.view = view;
        this.mReference = FirebaseDatabase.getInstance().getReference();
    }

    private String getId() {
        return FirebaseAuth.getInstance().getUid();
    }

    void getAppointmentList(final long day) {
        if (isNetworkAvailable()) {
            view.showLoading();
            mReference.child(Constants.DataBase.Doctors_NODE).child(getId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            thisDoctor = dataSnapshot.getValue(Doctor.class);
                            if (thisDoctor != null) {
                                boolean isFound = false;
                                for (int i = 0; i < thisDoctor.getDayAppointments().size(); i++) {
                                    if (thisDoctor.getDayAppointments().get(i).isConfirmed() &&
                                            DateUtils.isSameDay(new Date(day),
                                                    new Date(thisDoctor.getDayAppointments().get(i).getTitle()))) {
                                        isFound = true;
                                        view.initData(thisDoctor.getDayAppointments().get(i));
                                        view.hideLoading();
                                        break;
                                    }
                                }
                                if (!isFound) {
                                    view.showAppointmentError(R.string.attendance_error);
                                    view.hideLoading();
                                }
                            } else {
                                view.toastError(R.string.data_not_found);
                                view.hideLoading();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            view.toastError(R.string.data_not_found);
                            view.hideLoading();
                        }
                    });
        } else {
            view.toastError(R.string.message_no_internet);
        }
    }

    boolean isInputsValid(Appointment appointment) {
        String name = activity.patientNameEt.getText().toString().trim();
        String email = activity.emailEt.getText().toString().trim();
        String phone = activity.phoneEt.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            view.showNameError(R.string.required);
            return false;
        }
        if ((!TextUtils.isEmpty(email)) && (!Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            view.showNameError(R.string.invalid_email);
            return false;
        }
        if (TextUtils.isEmpty(phone)) {
            view.showPhoneError(R.string.required);
            return false;
        }
        if (!Patterns.PHONE.matcher(phone).matches()) {
            view.showPhoneError(R.string.invalid_phone);
            return false;
        }
        if (appointment == null) {
            view.toastError(R.string.attendance_error);
            return false;
        }
        return true;
    }

    void saveBooking(final Appointment appointment, final long day) {
        String name = activity.patientNameEt.getText().toString().trim();
        String email = activity.emailEt.getText().toString().trim();
        String phone = activity.phoneEt.getText().toString().trim();
        final String notes = activity.notesEt.getText().toString().trim();

        User patient = new User();
        patient.setDisplayName(name);
        patient.setEmail(email);
        patient.setPhoneNumber(phone);

        Calendar bookingDate = GregorianCalendar.getInstance();
        bookingDate.setTimeInMillis(day);
        Calendar bookingTime = GregorianCalendar.getInstance();
        bookingTime.setTimeInMillis(appointment.getTime());

        final Calendar bookingDateTime = GregorianCalendar.getInstance();
        bookingDateTime.set(Calendar.YEAR, bookingDate.get(Calendar.YEAR));
        bookingDateTime.set(Calendar.MONTH, bookingDate.get(Calendar.MONTH));
        bookingDateTime.set(Calendar.DATE, bookingDate.get(Calendar.DATE));
        bookingDateTime.set(Calendar.HOUR, bookingTime.get(Calendar.HOUR));
        bookingDateTime.set(Calendar.HOUR_OF_DAY, bookingTime.get(Calendar.HOUR_OF_DAY));
        bookingDateTime.set(Calendar.MINUTE, bookingTime.get(Calendar.MINUTE));
        bookingDateTime.set(Calendar.SECOND, bookingTime.get(Calendar.SECOND));

        view.showLoading();
        final String patientID = mReference.child(Constants.DataBase.USERS_NODE).push().getKey();
        mReference.child(Constants.DataBase.USERS_NODE).child(patientID).setValue(patient)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        view.hideLoading();
                        view.toastError(R.string.error_happened_2);
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Booking booking = new Booking();
                        booking.setDoctorId(getId());
                        booking.setUserId(patientID);
                        booking.setStatus(Booking.PENDING_STATUS);
                        booking.setMessage(notes);
                        booking.setDate(bookingDateTime.getTimeInMillis());

                        mReference.child(Constants.DataBase.Bookings_NODE).push().setValue(booking);

                        appointment.setUserId(patientID);
                        for (int i = 0; i < thisDoctor.getDayAppointments().size(); i++) {
                            if (DateUtils.isSameDay(new Date(day),
                                    new Date(thisDoctor.getDayAppointments().get(i).getTitle()))) {
                                for (int j = 0; j < thisDoctor.getDayAppointments().get(i).getAppointments().size(); j++) {
                                    if (appointment.getTime() == thisDoctor.getDayAppointments().get(i).getAppointments().get(j).getTime()) {
                                        thisDoctor.getDayAppointments().get(i).getAppointments().set(j, appointment);
                                        break;
                                    }
                                }
                                break;
                            }
                        }

                        thisDoctor.setUid(null);
                        mReference.child(Constants.DataBase.Doctors_NODE).child(getId()).setValue(thisDoctor)
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        view.hideLoading();
                                        view.toastError(R.string.error_happened_2);
                                    }
                                })
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        view.hideLoading();
                                        view.toastSuccessMessage(R.string.booking_saved_success);
                                    }
                                });
                    }
                });
    }
}
