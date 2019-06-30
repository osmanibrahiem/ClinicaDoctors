package com.clinica.doctors.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clinica.doctors.Models.Booking;
import com.clinica.doctors.Models.User;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Constants;
import com.clinica.doctors.Tools.DateUtils;
import com.clinica.doctors.Tools.Localization;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingsViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<Booking> bookingList;
    private DatabaseReference mReference;
    private BookingsCallBack callBack;

    public BookingAdapter(Context context, List<Booking> bookingList) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.bookingList = bookingList;
        mReference = FirebaseDatabase.getInstance().getReference();
    }

    public void setCallBack(BookingsCallBack callBack) {
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public BookingsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.row_bookings_item, viewGroup, false);
        return new BookingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BookingsViewHolder bookingsViewHolder, int position) {
        final Booking booking = bookingList.get(position);
        if (!TextUtils.isEmpty(booking.getUserId())) {
            mReference.child(Constants.DataBase.USERS_NODE).child(booking.getUserId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User patient = dataSnapshot.getValue(User.class);
                            if (patient != null) {
                                patient.setUid(dataSnapshot.getKey());
                                booking.setPatient(patient);
                                bookingsViewHolder.bind(booking);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        } else bookingsViewHolder.bind(booking);
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    class BookingsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img_patient)
        CircularImageView imgPatient;
        @BindView(R.id.patient_name)
        TextView patientName;
        @BindView(R.id.booking_date)
        TextView bookingDate;
        @BindView(R.id.check_in_btn)
        AppCompatButton checkInBtn;
        @BindView(R.id.cancel_btn)
        AppCompatButton cancelBtn;

        BookingsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(final Booking booking) {

            User patient = booking.getPatient();
            if (patient != null) {
                if (!TextUtils.isEmpty(patient.getPhotoUrl())) {
                    Picasso.get()
                            .load(patient.getPhotoUrl())
                            .placeholder(R.drawable.profile_picture_blank_square)
                            .error(R.drawable.error_placeholder)
                            .into(imgPatient);
                }

                patientName.setText(patient.getDisplayName());
            }

            final DateFormat df = new SimpleDateFormat("hh:mm aa", Localization.getCurrentLocale(context));
            bookingDate.setText(df.format(new Date(booking.getDate())));

            if (!TextUtils.isEmpty(booking.getStatus())) {
                switch (booking.getStatus()) {
                    case Booking.REQUESTED_STATUS:
                        if (checkAppointment(booking)) {
                            checkInBtn.setText(R.string.accept);
                            checkInBtn.setEnabled(true);
                            checkInBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (callBack != null)
                                        callBack.onAcceptClicked(booking);
                                }
                            });
                        } else {
                            checkInBtn.setText(R.string.clinic_is_busy);
                            checkInBtn.setEnabled(false);
                        }
                        Log.i("mBooking", "bind: " + booking.getStatus());
                        break;
                    case Booking.PENDING_STATUS:
                        checkInBtn.setText(R.string.check_in);
                        if (DateUtils.isToday(new Date(booking.getDate())))
                            checkInBtn.setEnabled(true);
                        else checkInBtn.setEnabled(false);
                        checkInBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (callBack != null)
                                    callBack.onCheckInClicked(booking);
                            }
                        });
                        Log.i("mBooking", "bind: " + booking.getStatus() + " --- " + checkInBtn.getText().toString());
                        break;
                    case Booking.CANCELED_STATUS:
                    case Booking.REFUSED_STATUS:
                        checkInBtn.setText(R.string.examination_canceled);
                        cancelBtn.setVisibility(View.GONE);
                        checkInBtn.setEnabled(false);
                        Log.i("mBooking", "bind: " + booking.getStatus());
                        break;
                    case Booking.SUCCESS_STATUS:
                        checkInBtn.setText(R.string.examination_done);
                        cancelBtn.setVisibility(View.GONE);
                        checkInBtn.setEnabled(false);
                        Log.i("mBooking", "bind: " + booking.getStatus());
                        break;
                }

            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callBack != null)
                        callBack.onBookingClicked(booking);
                }
            });

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callBack != null)
                        callBack.onCanceledClicked(booking);
                }
            });
        }
    }

    private boolean checkAppointment(Booking booking) {
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

        for (int i = 0; i < booking.getDoctor().getDayAppointments().size(); i++) {
            if (DateUtils.isSameDay(new Date(booking.getDoctor().getDayAppointments().get(i).getTitle()),
                    new Date(bookingDate.getTimeInMillis()))) {
                for (int j = 0; j < booking.getDoctor().getDayAppointments().get(i).getAppointments().size(); j++) {
                    if (DateUtils.isSameTime(new Date(booking.getDoctor().getDayAppointments().get(i).getAppointments().get(j).getTime()),
                            new Date(bookingTime.getTimeInMillis()))) {
                        return TextUtils.isEmpty(booking.getDoctor().getDayAppointments().get(i).getAppointments().get(j).getUserId());
                    }
                }
                break;
            }
        }
        return false;
    }

    public interface BookingsCallBack {

        void onBookingClicked(Booking booking);

        void onCheckInClicked(Booking booking);

        void onAcceptClicked(Booking booking);

        void onCanceledClicked(Booking booking);
    }
}
