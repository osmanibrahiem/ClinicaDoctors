package com.clinica.doctors.Activities.Requests;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.clinica.doctors.Activities.Base.BaseFragment;
import com.clinica.doctors.Activities.Booking.View.ViewBookingDetailsActivity;
import com.clinica.doctors.Adapters.BookingAdapter;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookingsRequestFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.empty)
    AppCompatTextView empty;

    private DatabaseReference mReference;
    private Doctor thisDoctor;

    public BookingsRequestFragment() {
    }

    @Override
    protected int setLayoutView() {
        return R.layout.recycler_only_layout;
    }

    @Override
    protected void initViews(View view) {
        ButterKnife.bind(this, view);
        this.mReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void initActions() {
        getBookings();
    }

    private String getID() {
        return FirebaseAuth.getInstance().getUid();
    }

    void getBookings() {
        if (isNetworkAvailable()) {
            showLoading();
            mReference.child(Constants.DataBase.Doctors_NODE).child(getID())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            thisDoctor = dataSnapshot.getValue(Doctor.class);
                            if (thisDoctor != null) {
                                thisDoctor.setUid(dataSnapshot.getKey());
                                getBookingsList();


                            } else {
                                showMessage(R.string.data_not_found);
                                hideLoading();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            showMessage(R.string.data_not_found);
                            hideLoading();
                        }
                    });
        } else {
            showMessage(R.string.message_no_internet);
        }
    }


    private void getBookingsList() {
        mReference.child(Constants.DataBase.Bookings_NODE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Booking> bookingList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Booking booking = snapshot.getValue(Booking.class);
                            if (booking != null && booking.getDoctorId().equals(getID())
                                    && booking.getStatus().equals(Booking.REQUESTED_STATUS)) {
                                booking.setId(snapshot.getKey());
                                booking.setDoctor(thisDoctor);
                                bookingList.add(booking);
                            }

                        }
                        Collections.sort(bookingList, new Comparator<Booking>() {
                            @Override
                            public int compare(Booking o1, Booking o2) {
                                return Long.compare(o1.getDate(), o2.getDate());
                            }
                        });
                        if (bookingList.size() > 0)
                            onGetBookings(bookingList);
                        else showMessage(R.string.empty_list);
                        hideLoading();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        showMessage(R.string.data_not_found);
                        hideLoading();
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
                        showMessage(R.string.error_happened_2);
                        hideLoading();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showLoading();
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
                        updateDoctor();
                        break;
                    }
                }
                break;
            }
        }
    }

    private void updateDoctor() {
        thisDoctor.setUid(null);
        mReference.child(Constants.DataBase.Doctors_NODE).child(getID()).setValue(thisDoctor)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage(R.string.error_happened_2);
                        hideLoading();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        hideLoading();
                        getBookingsList();
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
                        showMessage(R.string.error_happened_2);
                        hideLoading();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showLoading();

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
                                        updateDoctor();
                                        break;
                                    }
                                }
                                break;
                            }
                        }

                    }
                });
    }

    public void onGetBookings(List<Booking> bookings) {
        recyclerView.setVisibility(View.VISIBLE);
        empty.setVisibility(View.GONE);

        BookingAdapter adapter = new BookingAdapter(getActivity(), bookings);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.setCallBack(new BookingAdapter.BookingsCallBack() {
            @Override
            public void onBookingClicked(Booking booking) {
                Intent intent = new Intent(getActivity(), ViewBookingDetailsActivity.class);
                intent.putExtra(Constants.Intents.BOOKING_ID, booking.getId());
                startActivity(intent);
            }

            @Override
            public void onCheckInClicked(Booking booking) {
            }

            @Override
            public void onAcceptClicked(Booking booking) {
                acceptBooking(booking);
            }

            @Override
            public void onCanceledClicked(Booking booking) {
                CanceledBooking(booking);
            }
        });

    }

    public void showMessage(int message) {
        empty.setText(message);
        recyclerView.setVisibility(View.GONE);
        empty.setVisibility(View.VISIBLE);
    }


    public boolean isNetworkAvailable() {
        ConnectivityManager mConnectivity =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivity.getActiveNetworkInfo();
        if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            showNetWorkError(R.string.message_no_internet);
            return false;
        }
    }

    public void sendNotificationToUser(String userId, String type, String catID, String destinationID) {
        NotificationModel notification = new NotificationModel();
        notification.setCatID(catID);
        notification.setDestinationID(destinationID);
        notification.setFrom(FirebaseAuth.getInstance().getUid());
        notification.setTo(userId);
        notification.setDate(new Date().getTime());
        notification.setType(type);

        FirebaseDatabase.getInstance().getReference().child(Constants.DataBase.Notifications_NODE)
                .push().setValue(notification);
    }
}
