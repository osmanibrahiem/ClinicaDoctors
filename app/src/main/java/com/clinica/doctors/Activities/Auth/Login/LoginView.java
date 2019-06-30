package com.clinica.doctors.Activities.Auth.Login;

import android.support.annotation.StringRes;

import com.clinica.doctors.Activities.Base.BaseView;

interface LoginView extends BaseView {

    void showEmailError(int message);

    void showPasswordError(int message);

    void openSignUp();

    void openMain();

    void openForgetPassword();

    void openLicenseActivity();

    void openProfileActivity();

    void showError(@StringRes int errorMessage);

    void openMessageActivity(int message);

    void openClinicActivity();
}
