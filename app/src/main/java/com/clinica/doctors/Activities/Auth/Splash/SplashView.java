package com.clinica.doctors.Activities.Auth.Splash;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import com.clinica.doctors.Activities.Base.BaseView;

interface SplashView extends BaseView {

    void openLoginActivity();

    void openLicenseActivity();

    void openMainActivity();

    void openProfileActivity();

    void showError(@DrawableRes int errorImage, @StringRes int errorMessage);

    void openMessageActivity(@StringRes int message);

    void openClinicActivity();
}
