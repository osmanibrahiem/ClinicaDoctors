package com.clinica.doctors.Activities.Selector;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.clinica.doctors.Activities.Base.BasePresenter;
import com.clinica.doctors.Tools.Localization;
import com.clinica.doctors.Tools.SharedTool.UserData;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

class SelectorPresenter extends BasePresenter {

    private SelectorView view;
    private SelectorActivity activity;
    private DatabaseReference mReference;

    SelectorPresenter(SelectorView view, SelectorActivity activity) {
        super(view, activity);
        this.view = view;
        this.activity = activity;

        mReference = FirebaseDatabase.getInstance().getReference();
    }

    void getAllData(String childNode) {
        view.showLoading();

        Query query;
        if (UserData.getLocalization(activity) == Localization.ARABIC_VALUE) {
            query = mReference.child(childNode).orderByChild("titleAr");
        } else query = mReference.child(childNode).orderByChild("titleEn");

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                view.onItemAdded(dataSnapshot);
                view.hideLoading();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                view.onItemUpdated(dataSnapshot);
                view.hideLoading();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                view.onItemRemoved(dataSnapshot);
                view.hideLoading();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                view.hideLoading();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("cities", "onCancelled: " + databaseError.getMessage());
                view.hideLoading();
                view.showEmptyMessage(databaseError.toException().getMessage());
            }
        });
    }
}
