package com.clinica.doctors.Activities.Booking.View;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.clinica.doctors.Activities.Base.BaseActivity;
import com.clinica.doctors.Models.Booking;
import com.clinica.doctors.Models.User;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Constants;
import com.clinica.doctors.Tools.DateUtils;
import com.clinica.doctors.Tools.Localization;
import com.clinica.doctors.Tools.SharedTool.UserData;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewBookingDetailsActivity extends BaseActivity implements BookingDetailsView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.main_container)
    NestedScrollView mainContainer;
    @BindView(R.id.img_patient)
    CircularImageView imgPatient;
    @BindView(R.id.patient_name)
    TextView patientName;
    @BindView(R.id.examination_date)
    TextView examinationDate;
    @BindView(R.id.examination_duration)
    TextView examinationDuration;
    @BindView(R.id.doctor_price)
    TextView doctorPrice;
    @BindView(R.id.notes)
    TextView notes;
    @BindView(R.id.check_in_btn)
    AppCompatButton checkInBtn;
    @BindView(R.id.cancel_btn)
    AppCompatButton cancelBtn;
    @BindView(R.id.empty)
    TextView empty;
    @BindView(R.id.call_btn)
    FloatingActionButton callBtn;

    private BookingDetailsPresenter presenter;

    @Override
    protected int setLayoutView() {
        return R.layout.activity_view_booking_details;
    }

    @Override
    protected void initViews() {
        ButterKnife.bind(this);
        toolbar.setTitle(R.string.booking_details_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        presenter = new BookingDetailsPresenter(this, this);
    }

    @Override
    protected void initActions() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.Intents.BOOKING_ID))
            presenter.getBooking(intent.getStringExtra(Constants.Intents.BOOKING_ID));
    }

    @Override
    public void showMessage(int message) {
        empty.setText(message);
        mainContainer.setVisibility(View.GONE);
        callBtn.hide();
        empty.setVisibility(View.VISIBLE);
    }

    @Override
    public void onGetBooking(Booking booking) {
        final User patient = booking.getPatient();
        if (patient != null) {
            if (!TextUtils.isEmpty(patient.getPhotoUrl())) {
                Picasso.get()
                        .load(patient.getPhotoUrl())
                        .placeholder(R.drawable.profile_picture_blank_square)
                        .error(R.drawable.error_placeholder)
                        .into(imgPatient);
            }

            patientName.setText(patient.getDisplayName());
            callBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + patient.getPhoneNumber()));
                    startActivity(intent);
                }
            });
            callBtn.show();
        }


        DateFormat dfEN = new SimpleDateFormat("EEEE, d MMMM 'at' hh:mm aa", new Locale("en"));
        DateFormat dfAR = new SimpleDateFormat("EEEE d MMMM 'في تمام الساعة' hh:mm aa", new Locale("ar"));
        DateFormat dfDayEN = new SimpleDateFormat(", d MMMM 'at' hh:mm aa", new Locale("en"));
        DateFormat dfDayAR = new SimpleDateFormat(" d MMMM 'في تمام الساعة' hh:mm aa", new Locale("ar"));
        if (UserData.getLocalization(this) == Localization.ARABIC_VALUE) {
            if (DateUtils.isToday(new Date(booking.getDate()))) {
                examinationDate.setText(String.format("%1$s%2$s", getString(R.string.today), dfDayAR.format(new Date(booking.getDate()))));
            } else if (DateUtils.isTomorrow(new Date(booking.getDate()))) {
                examinationDate.setText(String.format("%1$s%2$s", getString(R.string.tomorrow), dfDayAR.format(new Date(booking.getDate()))));
            } else {
                examinationDate.setText(dfAR.format(new Date(booking.getDate())));
            }
        } else {
            if (DateUtils.isToday(new Date(booking.getDate()))) {
                examinationDate.setText(String.format("%1$s%2$s", getString(R.string.today), dfDayEN.format(new Date(booking.getDate()))));
            } else if (DateUtils.isTomorrow(new Date(booking.getDate()))) {
                examinationDate.setText(String.format("%1$s%2$s", getString(R.string.tomorrow), dfDayEN.format(new Date(booking.getDate()))));
            } else {
                examinationDate.setText(dfEN.format(new Date(booking.getDate())));
            }
        }

        examinationDuration.setText(presenter.getExaminationDuration());
        doctorPrice.setText(getString(R.string.price, booking.getDoctor().getClinicInformation().getExamination()));

        if (!TextUtils.isEmpty(booking.getStatus())) {
            switch (booking.getStatus()) {
                case Booking.REQUESTED_STATUS:
                    if (checkAppointment(booking)) {
                        checkInBtn.setText(R.string.accept);
                        checkInBtn.setEnabled(true);

                        checkInBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                presenter.acceptBooking();
                            }
                        });
                    } else {
                        checkInBtn.setText(R.string.clinic_is_busy);
                        checkInBtn.setEnabled(false);
                    }
                    break;
                case Booking.PENDING_STATUS:
                    checkInBtn.setText(R.string.check_in);
                    if (DateUtils.isToday(new Date(booking.getDate())))
                        checkInBtn.setEnabled(true);
                    else checkInBtn.setEnabled(false);
                    checkInBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            presenter.CheckInBooking();
                        }
                    });
                    break;
                case Booking.CANCELED_STATUS:
                case Booking.REFUSED_STATUS:
                    checkInBtn.setText(R.string.examination_canceled);
                    cancelBtn.setVisibility(View.GONE);
                    checkInBtn.setEnabled(false);
                    break;
                case Booking.SUCCESS_STATUS:
                    checkInBtn.setText(R.string.examination_done);
                    cancelBtn.setVisibility(View.GONE);
                    checkInBtn.setEnabled(false);
                    break;
            }

        }

        notes.setText(booking.getMessage());

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.CanceledBooking();
            }
        });

        mainContainer.setVisibility(View.VISIBLE);
        empty.setVisibility(View.GONE);
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
}
