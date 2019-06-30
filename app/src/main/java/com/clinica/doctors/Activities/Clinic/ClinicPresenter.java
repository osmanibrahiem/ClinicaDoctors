package com.clinica.doctors.Activities.Clinic;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.clinica.doctors.Activities.Base.BasePresenter;
import com.clinica.doctors.Models.Doctor.ClinicInformation;
import com.clinica.doctors.Models.Doctor.Doctor;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

class ClinicPresenter extends BasePresenter implements OnFailureListener {

    ClinicInformation thisClinic;
    private Doctor thisDoctor;
    private ClinicActivity activity;
    private ClinicView view;
    private DatabaseReference mReference;


    ClinicPresenter(ClinicView view, ClinicActivity activity) {
        super(view, activity);
        this.activity = activity;
        this.view = view;
        this.mReference = FirebaseDatabase.getInstance().getReference();
    }

    private String getDoctorId() {
        return FirebaseAuth.getInstance().getUid();
    }

    void getClinic() {
        if (isNetworkAvailable()) {
            view.showLoading();
            mReference.child(Constants.DataBase.Doctors_NODE).child(getDoctorId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            thisDoctor = dataSnapshot.getValue(Doctor.class);
                            if (thisDoctor != null) {
                                thisClinic = thisDoctor.getClinicInformation();
                                if (thisClinic == null)
                                    thisClinic = new ClinicInformation();
                                view.setData(thisClinic);
                                view.hideLoading();
                            } else {
                                view.showError(R.drawable.ic_sad, R.string.data_not_found);
                                view.hideLoading();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            view.showError(R.drawable.ic_sad, R.string.data_not_found);
                            view.hideLoading();
                        }
                    });
        } else {
            view.showError(R.drawable.no_internet, R.string.message_no_internet);
        }
    }

    boolean isInputsValid() {
        boolean isInputsValid = true;
        String examination = activity.examinationEt.getText().toString().trim();
        if (TextUtils.isEmpty(examination)) {
            view.showExaminationError(R.string.required);
            isInputsValid = false;
        }

        for (int i = 0; i < activity.workingHours.size(); i++) {
            Date startDate = new Date(activity.workingHours.get(i).getStartTime());
            Date endDate = new Date(activity.workingHours.get(i).getEndTime());
            if (activity.workingHours.get(i).isWorking()) {
                if (!endDate.after(startDate)) {
                    isInputsValid = false;
                    activity.workingHours.get(i).setError(activity.getString(R.string.message_working_hours_error));
                    activity.workingAdapter.notifyItemChanged(i);
                }
                if (activity.workingHours.get(i).getDuration() == -1) {
                    isInputsValid = false;
                    activity.workingHours.get(i).setError(activity.getString(R.string.message_working_duration_required));
                    activity.workingAdapter.notifyItemChanged(i);
                }
            }
        }

        return isInputsValid;
    }

    void saveEdits() {
        String examination = activity.examinationEt.getText().toString().trim();
        thisClinic.setExamination(Double.parseDouble(examination));
        thisClinic.setWorkingHours(activity.workingHours);
        thisDoctor.setClinicInformation(thisClinic);

        view.showLoading();
        mReference.child(Constants.DataBase.Doctors_NODE).child(getDoctorId())
                .setValue(thisDoctor).addOnFailureListener(this)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (!TextUtils.isEmpty(thisDoctor.getAccountStatus())) {
                            switch (thisDoctor.getAccountStatus()) {
                                case Doctor.ACTIVE_STATUS:
                                    view.hideLoading();
                                    view.openMain();
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
                    }
                });
    }

    private void setAccountStatus() {
        mReference.child(Constants.DataBase.Doctors_NODE).child(getDoctorId())
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
        view.toastError(R.string.error_happened_2);
    }
}
