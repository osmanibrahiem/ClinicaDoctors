package com.clinica.doctors.Activities.Booking.View;

import com.clinica.doctors.Activities.Base.BaseView;
import com.clinica.doctors.Models.Booking;

interface BookingDetailsView extends BaseView {

    void showMessage(int message);

    void onGetBooking(Booking booking);
}
