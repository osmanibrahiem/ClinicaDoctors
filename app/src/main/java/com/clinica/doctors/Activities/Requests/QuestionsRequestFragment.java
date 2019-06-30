package com.clinica.doctors.Activities.Requests;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.clinica.doctors.Activities.Base.BaseFragment;
import com.clinica.doctors.Adapters.ConsultationAdapter;
import com.clinica.doctors.Models.Consultation;
import com.clinica.doctors.Models.Doctor.Doctor;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuestionsRequestFragment extends BaseFragment {

    @BindView(R.id.recycler_view)
    RecyclerView consultationRecycler;
    @BindView(R.id.empty)
    AppCompatTextView empty;

    public QuestionsRequestFragment() {
    }

    @Override
    protected int setLayoutView() {
        return R.layout.recycler_only_layout;
    }

    @Override
    protected void initViews(View view) {
        ButterKnife.bind(this, view);
    }

    @Override
    protected void initActions() {
        getDoctor();
    }

    private void getDoctor() {
        showLoading();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.DataBase.Doctors_NODE).child(FirebaseAuth.getInstance().getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Doctor doctor = dataSnapshot.getValue(Doctor.class);
                if (doctor != null) {
                    doctor.setUid(dataSnapshot.getKey());
                    getQuestionsRequests(doctor.getSpecializationID());
                } else {
                    hideLoading();
                    showError(R.string.data_not_found);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideLoading();
                showError(R.string.data_not_found);
            }
        });
    }

    private void getQuestionsRequests(final String specializationID) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.DataBase.Consultation_NODE);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Consultation> consultationList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Consultation consultation = snapshot.getValue(Consultation.class);
                    if (consultation != null && TextUtils.isEmpty(consultation.getAnswerPublisherID())
                            && consultation.getSpecializationID().equals(specializationID)) {
                        consultation.setId(snapshot.getKey());
                        consultationList.add(consultation);
                    }
                }

                if (consultationList.size() > 0) {
                    initConsultations(consultationList);
                }else {
                    showError(R.string.data_not_found);
                }
                hideLoading();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showError(R.string.data_not_found);
                hideLoading();
            }
        });
    }

    private void initConsultations(List<Consultation> consultationList) {
        empty.setVisibility(View.GONE);
        consultationRecycler.setVisibility(View.VISIBLE);
        ConsultationAdapter consultationAdapter = new ConsultationAdapter(getActivity(), Constants.LIST_CONSULTATIONS_VIEW);
        consultationAdapter.appendData(consultationList);
        consultationRecycler.setHasFixedSize(false);
        consultationRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        consultationRecycler.setNestedScrollingEnabled(false);
        consultationRecycler.setItemAnimator(new DefaultItemAnimator());
        consultationRecycler.setAdapter(consultationAdapter);
    }

    private void showError(int message) {
        consultationRecycler.setVisibility(View.GONE);
        empty.setText(message);
        empty.setVisibility(View.VISIBLE);
    }
}
