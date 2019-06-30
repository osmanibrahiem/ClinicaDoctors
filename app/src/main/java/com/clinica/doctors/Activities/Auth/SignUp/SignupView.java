package com.clinica.doctors.Activities.Auth.SignUp;

import android.graphics.Bitmap;

import com.clinica.doctors.Activities.Base.BaseView;

interface SignupView extends BaseView {

    void setProfileImage(Bitmap img);

    void showNameArError(int message);

    void showNameEnError(int message);

    void showEmailError(int message);

    void showPhoneError(int message);

    void showBirthdayError(int message);

    void showPasswordError(int message);

    void showGenderError(int message);

    void showSpecialtyError(int message);
}
