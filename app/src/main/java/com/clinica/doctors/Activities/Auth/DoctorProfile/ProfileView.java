package com.clinica.doctors.Activities.Auth.DoctorProfile;

import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.clinica.doctors.Activities.Base.BaseView;
import com.clinica.doctors.Models.Doctor.Doctor;

interface ProfileView extends BaseView {

    void setData(Doctor doctor);

    void showError(@DrawableRes int errorImage, @StringRes int errorMessage);

    void setProfileImage(Bitmap bitmap);

    void showNameArError(int message);

    void showNameEnError(int message);

    void showBirthdayError(int message);

    void showGenderError(int message);

    void showLicenseError(int message);

    void showProfessionalTitleArError(int message);

    void showProfessionalTitleEnError(int message);

    void showSpecialtyError(int message);

    void showCertificateError(int message);

    void toastError(int message);

    void setLicenseImage(Bitmap bitmap);

    void setCertificationImage(Bitmap bitmap);

    void openClinicActivity();

    void openMain();

    void openMessageActivity(int message);
}
