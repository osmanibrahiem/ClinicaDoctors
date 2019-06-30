package com.clinica.doctors.Activities.Booking.Add;

import com.clinica.doctors.Activities.Base.BaseView;
import com.clinica.doctors.Models.Doctor.DayAppointments;

interface NewBookingView extends BaseView {

    void initData(DayAppointments dayAppointments);

    void showAppointmentError(int message);

    void showNameError(int message);

    void showEmailError(int message);

    void showPhoneError(int message);

    void toastError(int message);

    void toastSuccessMessage(int message);

}
