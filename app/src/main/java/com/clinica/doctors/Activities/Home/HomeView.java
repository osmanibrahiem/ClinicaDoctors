package com.clinica.doctors.Activities.Home;

import com.clinica.doctors.Activities.Base.BaseView;
import com.clinica.doctors.Models.Ads;
import com.clinica.doctors.Models.Doctor.Doctor;

interface HomeView extends BaseView {

    void initData(Doctor doctor);

    void toastError(int message);

    void initAdsRecycler(long adsCount);

    void hideAdsPager();

    void addAds(Ads ads);

    void updateAds(Ads ads);

    void deleteAds(Ads ads);
}
