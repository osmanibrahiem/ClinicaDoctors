package com.clinica.doctors.Activities.Appointments;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.clinica.doctors.Activities.Base.BaseView;
import com.clinica.doctors.Models.Booking;

import java.util.List;

public interface AppointmentsView extends BaseView {

    void onGetBookings(List<Booking> bookings);

    void showMessage(@DrawableRes int img, @StringRes int message);

    void updateAddBtn(boolean isVisible);
}
