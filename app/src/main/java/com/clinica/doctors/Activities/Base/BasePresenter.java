package com.clinica.doctors.Activities.Base;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.clinica.doctors.Models.Doctor.Address;
import com.clinica.doctors.Models.Doctor.BasicInformation;
import com.clinica.doctors.Models.Doctor.ClinicInformation;
import com.clinica.doctors.Models.Doctor.Doctor;
import com.clinica.doctors.Models.NotificationModel;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Constants;
import com.clinica.doctors.Tools.Localization;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class BasePresenter {

    private BaseView view;
    private BaseActivity activity;
    private BaseFragment fragment;

    public BasePresenter(BaseView view, BaseActivity activity) {
        this.view = view;
        this.activity = activity;
    }

    public BasePresenter(BaseView view, BaseFragment fragment) {
        this(view, (BaseActivity) fragment.getActivity());
        this.view = view;
        this.fragment = fragment;
    }

    public void hideKeypad() {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.setFocusableInTouchMode(false);
            view.setFocusable(false);
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager mConnectivity =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivity.getActiveNetworkInfo();
        if (mNetworkInfo != null && mNetworkInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            view.showNetWorkError(R.string.message_no_internet);
            return false;
        }
    }

    public boolean checkProfile(Doctor doctor) {
        boolean isCompleteProfile = true;
        BasicInformation informationAr = doctor.getBasicInformationAr();
        if (informationAr == null) {
            isCompleteProfile = false;
        } else {
            if (TextUtils.isEmpty(informationAr.getDisplayName()))
                isCompleteProfile = false;
            if (TextUtils.isEmpty(informationAr.getGender()))
                isCompleteProfile = false;
            if (TextUtils.isEmpty(informationAr.getProfessionalTitle()))
                isCompleteProfile = false;
        }
        BasicInformation informationEn = doctor.getBasicInformationEN();
        if (informationEn == null) {
            isCompleteProfile = false;
        } else {
            if (TextUtils.isEmpty(informationEn.getDisplayName()))
                isCompleteProfile = false;
            if (TextUtils.isEmpty(informationEn.getGender()))
                isCompleteProfile = false;
            if (TextUtils.isEmpty(informationEn.getProfessionalTitle()))
                isCompleteProfile = false;
        }
        if (TextUtils.isEmpty(doctor.getPracticeLicenseIdPhotoURL()))
            isCompleteProfile = false;
        if (TextUtils.isEmpty(doctor.getSpecializationID()))
            isCompleteProfile = false;
        if (TextUtils.isEmpty(doctor.getCertificatePhotoURL()))
            isCompleteProfile = false;
        return isCompleteProfile;
    }

    public boolean checkClinic(Doctor doctor) {
        boolean isCompleteClinic = true;
        ClinicInformation clinic = doctor.getClinicInformation();
        if (clinic == null) {
            isCompleteClinic = false;
        } else {
            if (clinic.getExamination() == 0)
                isCompleteClinic = false;
            Address addressAr = clinic.getAddressAr();
            if (addressAr == null)
                isCompleteClinic = false;
            else {
                if (TextUtils.isEmpty(addressAr.getFullAddress(activity, Localization.ARABIC_VALUE)))
                    isCompleteClinic = false;
            }
            Address addressEn = clinic.getAddressEn();
            if (addressEn == null)
                isCompleteClinic = false;
            else {
                if (TextUtils.isEmpty(addressEn.getFullAddress(activity, Localization.ENGLISH_VALUE)))
                    isCompleteClinic = false;
            }
        }
        return isCompleteClinic;
    }

    public void sendNotificationToUser(String userId, String type, String catID, String destinationID) {
        NotificationModel notification = new NotificationModel();
        notification.setCatID(catID);
        notification.setDestinationID(destinationID);
        notification.setFrom(FirebaseAuth.getInstance().getUid());
        notification.setTo(userId);
        notification.setDate(new Date().getTime());
        notification.setType(type);

        FirebaseDatabase.getInstance().getReference().child(Constants.DataBase.Notifications_NODE)
                .push().setValue(notification);
    }
}
