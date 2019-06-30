package com.clinica.doctors.Activities.Clinic;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.clinica.doctors.Activities.Base.BaseView;
import com.clinica.doctors.Models.Doctor.ClinicInformation;

interface ClinicView extends BaseView {

    void setData(ClinicInformation clinic);

    void showError(@DrawableRes int errorImage, @StringRes int errorMessage);

    void showExaminationError(int message);

    void showAddressError(int message);

    void toastError(int message);

    void openMain();

    void openMessageActivity(int message);
}
