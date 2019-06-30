package com.clinica.doctors.Activities.Auth.Splash;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;

import com.clinica.doctors.Activities.Base.BasePresenter;
import com.clinica.doctors.Models.Doctor.Doctor;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Constants;
import com.clinica.doctors.Tools.Localization;
import com.clinica.doctors.Tools.SharedTool.UserData;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

class SplashPresenter extends BasePresenter implements OnFailureListener {

    private SplashActivity activity;
    private SplashView view;
    private FirebaseAuth mAuth;
    private DatabaseReference mReference;

    private static final int TIME_SPLASH_CLOSE = 10000;//millis
    private static final int TIME_ANIMATION_DURATION = 3000;//millis
    private static final int TIME_ANIMATION_START = 500;//millis

    SplashPresenter(SplashView view, SplashActivity activity) {
        super(view, activity);
        this.activity = activity;
        this.view = view;
        this.mAuth = FirebaseAuth.getInstance();
        this.mReference = FirebaseDatabase.getInstance().getReference();
    }

    void animateLogo(View imgv) {
        imgv.setVisibility(View.VISIBLE);
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setStartOffset(TIME_ANIMATION_START);
        fadeIn.setDuration(TIME_ANIMATION_DURATION);
        AnimationSet animation = new AnimationSet(false); //change to false
        animation.addAnimation(fadeIn);
        imgv.setAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (checkLogin()) {
                    view.openLoginActivity();
                } else {
                    checkDoctor();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    void setLanguage() {
        if (UserData.getLocalization(activity) == -1) { // no found lang before .. first set up application
            if (Localization.getDefaultLocal(activity) == Localization.ARABIC_VALUE) { //RTL
                UserData.saveLocalization(activity, Localization.ARABIC_VALUE);
            } else {
                UserData.saveLocalization(activity, Localization.ENGLISH_VALUE);
            }
        }
        setLanguage(UserData.getLocalization(activity));
    }

    void setLanguage(int language) {
        Localization.setLanguage(activity, language);
    }

    private boolean checkLogin() {
        return FirebaseAuth.getInstance().getCurrentUser() == null;
    }

    private void checkDoctor() {
        if (isNetworkAvailable())
            mReference.child(Constants.DataBase.Doctors_NODE).child(mAuth.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Doctor doctor = dataSnapshot.getValue(Doctor.class);
                                if (doctor != null) {
                                    if (TextUtils.isEmpty(doctor.getPracticeLicenseIdPhotoURL())) {
                                        view.openLicenseActivity();
                                    } else {
                                        if (checkProfile(doctor)) {
                                            if (checkClinic(doctor)) {
                                                if (!TextUtils.isEmpty(doctor.getAccountStatus())) {
                                                    switch (doctor.getAccountStatus()) {
                                                        case Doctor.ACTIVE_STATUS:
                                                            view.hideLoading();
                                                            view.openMainActivity();
                                                            break;
                                                        case Doctor.UNDER_REVIEW_STATUS:
                                                            view.hideLoading();
                                                            view.openMessageActivity(R.string.profile_under_review);
                                                            break;
                                                        case Doctor.BLOCKED_STATUS:
                                                            view.hideLoading();
                                                            view.openMessageActivity(R.string.profile_blocked);
                                                            break;
                                                        case Doctor.DELETED_STATUS:
                                                            view.hideLoading();
                                                            view.openMessageActivity(R.string.profile_deleted);
                                                            break;
                                                        default:
                                                            setAccountStatus();
                                                    }
                                                } else {
                                                    setAccountStatus();
                                                }
                                            } else {
                                                view.openClinicActivity();
                                            }
                                        } else {
                                            view.openProfileActivity();
                                        }
                                    }
                                } else view.showError(R.drawable.ic_sad, R.string.data_not_found);
                            } else {
                                view.showError(R.drawable.ic_sad, R.string.data_not_found);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            view.showError(R.drawable.ic_sad, R.string.data_not_found);
                        }
                    });
        else view.showError(R.drawable.no_internet, R.string.message_no_internet);
    }

    private void setAccountStatus() {
        mReference.child(Constants.DataBase.Doctors_NODE).child(mAuth.getUid())
                .child(Constants.DataBase.Doctors_Statues_NODE).setValue(Doctor.UNDER_REVIEW_STATUS)
                .addOnFailureListener(this).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                view.hideLoading();
                view.openMessageActivity(R.string.profile_under_review);
            }
        });
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        view.hideLoading();
        view.showError(R.drawable.ic_sad, R.string.error_happened_2);
    }
}
