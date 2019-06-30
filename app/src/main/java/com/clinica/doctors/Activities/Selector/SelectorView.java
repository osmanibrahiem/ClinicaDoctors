package com.clinica.doctors.Activities.Selector;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.clinica.doctors.Activities.Base.BaseView;
import com.google.firebase.database.DataSnapshot;

public interface SelectorView extends BaseView {

    void setSearchError(String message);

    void showEmptyMessage(String message);

    void onItemAdded(@NonNull DataSnapshot dataSnapshot);

    void onItemUpdated(@NonNull DataSnapshot dataSnapshot);

    void onItemRemoved(@NonNull DataSnapshot dataSnapshot);

    void setResultOk(Intent data);

    void setResultCancelled();

}
