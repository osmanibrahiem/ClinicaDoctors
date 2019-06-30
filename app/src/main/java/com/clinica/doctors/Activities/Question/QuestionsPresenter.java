package com.clinica.doctors.Activities.Question;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.clinica.doctors.Activities.Base.BasePresenter;
import com.clinica.doctors.Models.Consultation;
import com.clinica.doctors.Models.Doctor.Doctor;
import com.clinica.doctors.Models.NotificationModel;
import com.clinica.doctors.Models.User;
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

class QuestionsPresenter extends BasePresenter {

    private QuestionActivity activity;
    private QuestionsView view;
    private DatabaseReference mReference;
    private Consultation thisConsultation;

    QuestionsPresenter(QuestionsView view, QuestionActivity activity) {
        super(view, activity);
        this.activity = activity;
        this.view = view;
        this.mReference = FirebaseDatabase.getInstance().getReference();
    }

    void getQuestion(String questionID) {
        if (isNetworkAvailable()) {
            view.showLoading();
            mReference.child(Constants.DataBase.Consultation_NODE).child(questionID)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            thisConsultation = dataSnapshot.getValue(Consultation.class);
                            if (thisConsultation != null) {
                                thisConsultation.setId(dataSnapshot.getKey());
                                view.initConsultation(thisConsultation);
                                if (!TextUtils.isEmpty(thisConsultation.getQuestionPublisherID())) {
                                    getUser(thisConsultation.getQuestionPublisherID());
                                }
                            } else {
                                view.showMessage(R.string.data_not_found);
                                view.hideLoading();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            view.showMessage(R.string.data_not_found);
                            view.hideLoading();
                        }
                    });
        } else {
            view.showMessage(R.string.message_no_internet);
        }
    }

    private void getUser(String userID) {
        mReference.child(Constants.DataBase.USERS_NODE).child(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            user.setUid(dataSnapshot.getKey());
                            view.initUser(user);
                            if (!TextUtils.isEmpty(thisConsultation.getAnswerPublisherID())) {
                                getDoctor(thisConsultation.getAnswerPublisherID());
                            } else {
                                view.initInputNewAnswer();
                                view.hideLoading();
                            }
                        } else {
                            view.showMessage(R.string.data_not_found);
                            view.hideLoading();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        view.showMessage(R.string.data_not_found);
                        view.hideLoading();
                    }
                });
    }

    private void getDoctor(String doctorID) {
        mReference.child(Constants.DataBase.Doctors_NODE).child(doctorID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Doctor doctor = dataSnapshot.getValue(Doctor.class);
                        if (doctor != null) {
                            doctor.setUid(dataSnapshot.getKey());
                            view.initDoctor(doctor);
                            view.hideLoading();
                        } else {
                            view.showMessage(R.string.data_not_found);
                            view.hideLoading();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        view.showMessage(R.string.data_not_found);
                        view.hideLoading();
                    }
                });
    }

    void sendAnswer() {
        if (isNetworkAvailable()) {
            view.showLoading();
            final String id = thisConsultation.getId();
            thisConsultation.setId(null);
            thisConsultation.setAnswer(activity.inputAnswer.getText().toString().trim());
            thisConsultation.setAnswerDate(new Date().getTime());
            thisConsultation.setAnswerPublisherID(FirebaseAuth.getInstance().getUid());
            thisConsultation.setStatus(Consultation.STATUS_ACTIVE);

            mReference.child(Constants.DataBase.Consultation_NODE).child(id).setValue(thisConsultation)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            view.hideLoading();
                            view.toastError(R.string.error_happened_2);
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            view.hideLoading();
                            sendNotificationToUser(thisConsultation.getQuestionPublisherID(),
                                    NotificationModel.DOCTOR_ANSWERED_QUESTION,
                                    Constants.DataBase.Consultation_NODE, id);
                            getQuestion(id);
                        }
                    });
        } else {
            view.toastError(R.string.message_no_internet);
        }
    }
}
