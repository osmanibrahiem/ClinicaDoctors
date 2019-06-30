package com.clinica.doctors.Activities.Home;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.clinica.doctors.Activities.Base.BasePresenter;
import com.clinica.doctors.Models.Ads;
import com.clinica.doctors.Models.Doctor.Doctor;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

class HomePresenter extends BasePresenter {

    private HomeView view;
    private HomeFragment fragment;
    Doctor thisDoctor;
    private DatabaseReference mReference;

    HomePresenter(HomeView view, HomeFragment fragment) {
        super(view, fragment);
        this.view = view;
        this.fragment = fragment;
        this.mReference = FirebaseDatabase.getInstance().getReference();
    }

    private String getId() {
        return FirebaseAuth.getInstance().getUid();
    }

    void getDoctor() {
        if (isNetworkAvailable()) {
            view.showLoading();
            mReference.child(Constants.DataBase.Doctors_NODE).child(getId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            thisDoctor = dataSnapshot.getValue(Doctor.class);
                            thisDoctor.setUid(dataSnapshot.getKey());
                            view.hideLoading();
                            view.initData(thisDoctor);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            view.toastError(R.string.data_not_found);
                            view.hideLoading();
                        }
                    });
        } else {
            view.toastError(R.string.message_no_internet);
        }
    }

    void checkAds() {
        Query query = mReference.child(Constants.DataBase.ADS_NODE).orderByChild("publisherID").equalTo(getId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mReference.child(Constants.DataBase.ADS_NODE).removeEventListener(this);
                    view.initAdsRecycler(dataSnapshot.getChildrenCount());
                    getAllAds();
                } else {
                    view.hideAdsPager();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAllAds() {
        Query query = mReference.child(Constants.DataBase.ADS_NODE).orderByChild("publisherID").equalTo(getId());
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Ads ads = dataSnapshot.getValue(Ads.class);
                ads.setId(dataSnapshot.getKey());
                if (ads.getStatus().equals(Constants.ACTIVE_STATUS)) {
                    long startDate = ads.getPublicationDate();
                    long endDate = ads.getExpiryDate();
                    long timeNow = new Date().getTime();

                    if (timeNow >= startDate && timeNow <= endDate) {
                        view.addAds(ads);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Ads ads = dataSnapshot.getValue(Ads.class);
                ads.setId(dataSnapshot.getKey());
                view.updateAds(ads);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Ads ads = dataSnapshot.getValue(Ads.class);
                ads.setId(dataSnapshot.getKey());
                view.deleteAds(ads);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
