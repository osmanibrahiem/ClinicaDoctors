package com.clinica.doctors.Activities.Appointments;

import android.support.annotation.NonNull;

import com.clinica.doctors.Activities.Base.BasePresenter;
import com.clinica.doctors.Models.Booking;
import com.clinica.doctors.Models.Doctor.Doctor;
import com.clinica.doctors.Models.Doctor.WorkingDayHours;
import com.clinica.doctors.Models.NotificationModel;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

class AppointmentsPresenter extends BasePresenter {

    private AppointmentsFragment fragment;
    private AppointmentsView view;
    private DatabaseReference mReference;
    private Doctor thisDoctor;

    AppointmentsPresenter(AppointmentsView view, AppointmentsFragment fragment) {
        super(view, fragment);
        this.fragment = fragment;
        this.view = view;
        this.mReference = FirebaseDatabase.getInstance().getReference();

    }

    private String getId() {
        return FirebaseAuth.getInstance().getUid();
    }

    void getBookings(final long day) {
        if (isNetworkAvailable()) {
            view.showLoading();
            mReference.child(Constants.DataBase.Doctors_NODE).child(getId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            thisDoctor = dataSnapshot.getValue(Doctor.class);
                            if (thisDoctor != null) {
                                thisDoctor.setUid(dataSnapshot.getKey());
                                Calendar cal = Calendar.getInstance();
                                cal.setTimeInMillis(day);
                                WorkingDayHours dayHours = getWorkingDay(thisDoctor.getClinicInformation().getWorkingHours()
                                        , cal.get(Calendar.DAY_OF_WEEK));
                                if (dayHours != null) {
                                    view.updateAddBtn(dayHours.isWorking());
                                    if (dayHours.isWorking()) {
                                        getBookingsList(day);
                                    } else {
                                        view.showMessage(R.drawable.ic_sad, R.string.isnt_working_day);
                                        view.hideLoading();
                                    }
                                } else {
                                    view.showMessage(R.drawable.ic_sad, R.string.data_not_found);
                                    view.hideLoading();
                                }
                            } else {
                                view.showMessage(R.drawable.ic_sad, R.string.data_not_found);
                                view.hideLoading();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            view.showMessage(R.drawable.ic_sad, R.string.data_not_found);
                            view.hideLoading();
                        }
                    });
        } else {
            view.showMessage(R.drawable.no_internet, R.string.message_no_internet);
        }
    }

    private WorkingDayHours getWorkingDay(List<WorkingDayHours> workingDayHours, int day) {
        for (WorkingDayHours dayHours : workingDayHours) {
            if (dayHours.getDayOfWeek() == day)
                return dayHours;
        }
        return null;
    }

    private void getBookingsList(final long day) {
        mReference.child(Constants.DataBase.Bookings_NODE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Booking> bookingList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Booking booking = snapshot.getValue(Booking.class);
                            if (booking != null && booking.getDoctorId().equals(getId())) {
                                booking.setId(snapshot.getKey());
                                booking.setDoctor(thisDoctor);
                                if (DateUtils.isSameDay(new Date(day), new Date(booking.getDate()))) {
                                    bookingList.add(booking);
                                }
                            }

                        }
                        Collections.sort(bookingList, new Comparator<Booking>() {
                            @Override
                            public int compare(Booking o1, Booking o2) {
                                return Long.compare(o1.getDate(), o2.getDate());
                            }
                        });
                        if (bookingList.size() > 0)
                            view.onGetBookings(bookingList);
                        else view.showMessage(R.drawable.ic_sad, R.string.empty_list);
                        view.hideLoading();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        view.showMessage(R.drawable.ic_sad, R.string.data_not_found);
                        view.hideLoading();
                    }
                });
    }

    void CheckInBooking(final Booking booking) {
        String id = booking.getId();
        booking.setId(null);
        booking.setPatient(null);
        booking.setDoctor(null);
        booking.setStatus(Booking.SUCCESS_STATUS);
        mReference.child(Constants.DataBase.Bookings_NODE).child(id).setValue(booking)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        view.showMessage(R.drawable.ic_sad, R.string.error_happened_2);
                        view.hideLoading();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        view.showLoading();
                        getBookingsList(booking.getDate());
                    }
                });
    }

    void CanceledBooking(final Booking booking) {
        String id = booking.getId();
        sendNotificationToUser(booking.getUserId(), NotificationModel.DOCTOR_CANCELED_BOOKING,
                Constants.DataBase.Bookings_NODE, id);
        booking.setId(null);
        booking.setPatient(null);
        booking.setDoctor(null);
        booking.setStatus(Booking.CANCELED_STATUS);
        mReference.child(Constants.DataBase.Bookings_NODE).child(id).setValue(booking)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        view.showMessage(R.drawable.ic_sad, R.string.error_happened_2);
                        view.hideLoading();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        view.showLoading();
                        clearAppointment(booking.getDate());
                    }
                });
    }

    private void clearAppointment(long date) {
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
                        updateDoctor(date);
                        break;
                    }
                }
                break;
            }
        }
    }

    private void updateDoctor(final long day) {
        thisDoctor.setUid(null);
        mReference.child(Constants.DataBase.Doctors_NODE).child(getId()).setValue(thisDoctor)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        view.showMessage(R.drawable.ic_sad, R.string.error_happened_2);
                        view.hideLoading();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        view.hideLoading();
                        getBookingsList(day);
                    }
                });
    }

    void acceptBooking(final Booking booking) {
        sendNotificationToUser(booking.getUserId(), NotificationModel.DOCTOR_ACCEPTED_BOOKING,
                Constants.DataBase.Bookings_NODE, booking.getId());
        final String id = booking.getId();
        booking.setId(null);
        booking.setPatient(null);
        booking.setDoctor(null);
        booking.setStatus(Booking.PENDING_STATUS);
        mReference.child(Constants.DataBase.Bookings_NODE).child(id).setValue(booking)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        view.showMessage(R.drawable.ic_sad, R.string.error_happened_2);
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
                        bookingDateTime.setTimeInMillis(booking.getDate());

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
                                        thisDoctor.getDayAppointments().get(i).getAppointments().get(j).setUserId(booking.getUserId());
                                        updateDoctor(booking.getDate());
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
