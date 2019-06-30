package com.clinica.doctors.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.clinica.doctors.Activities.Appointments.AppointmentsFragment;
import com.clinica.doctors.Activities.Base.BaseActivity;
import com.clinica.doctors.Activities.Home.HomeFragment;
import com.clinica.doctors.Activities.Notifications.NotificationsFragment;
import com.clinica.doctors.Activities.Requests.RequestsFragment;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.navigation)
    BottomNavigationView navigation;

    private Fragment active = null;

    @Override
    protected int setLayoutView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        ButterKnife.bind(this);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (getIntent() != null && getIntent().hasExtra(Constants.Intents.DAY_APPOINTMENTS)) {
            navigation.setSelectedItemId(R.id.nav_appoints);
        } else {
            navigation.setSelectedItemId(R.id.nav_home);
        }
    }

    @Override
    protected void initActions() {

    }

    @OnClick(R.id.questions_btn)
    void onQuestionsSelected() {
        navigation.setSelectedItemId(R.id.nav_questions);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    openHome();
                    return true;
                case R.id.nav_appoints:
                    if (getIntent() != null && getIntent().hasExtra(Constants.Intents.DAY_APPOINTMENTS)) {
                        Bundle bundle = new Bundle();
                        bundle.putLong(Constants.Intents.DAY_APPOINTMENTS, getIntent().getLongExtra(Constants.Intents.DAY_APPOINTMENTS, -1));
                        openAppoints(bundle);
                    } else {
                        openAppoints(null);
                    }
                    return true;
                case R.id.nav_questions:
                    openAsk();
                    return true;
                case R.id.nav_notifications:
                    openNotifications();
                    return true;
                case R.id.nav_profile:
//                    openProfile();
                    return true;
            }
            return false;
        }
    };

    private void openHome() {
        if (active == null || (!active.getClass().getSimpleName().equals(HomeFragment.class.getSimpleName()))) {
            active = new HomeFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.home_frame_container, active).commit();
        }
    }

    private void openAppoints(Bundle bundle) {
        if (active != null && (!active.getClass().getSimpleName().equals(AppointmentsFragment.class.getSimpleName()))) {
            active = new AppointmentsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.home_frame_container, active).commit();
        } else {
            active = new AppointmentsFragment();
            active.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.home_frame_container, active).commit();
        }
    }

    private void openAsk() {
        if (active != null && (!active.getClass().getSimpleName().equals(RequestsFragment.class.getSimpleName()))) {
            active = new RequestsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.home_frame_container, active).commit();
        }
    }

    private void openNotifications() {
        if (active != null && (!active.getClass().getSimpleName().equals(NotificationsFragment.class.getSimpleName()))) {
            active = new NotificationsFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.home_frame_container, active).commit();
        }
    }

    private void openProfile() {

    }

    @Override
    public void onBackPressed() {
        if (active != null && active.getClass().getSimpleName().equals(AppointmentsFragment.class.getSimpleName()) &&
                getIntent() != null && getIntent().hasExtra(Constants.Intents.DAY_APPOINTMENTS)) {
            super.onBackPressed();
        } else if (active == null || (!active.getClass().getSimpleName().equals(HomeFragment.class.getSimpleName())))
            navigation.setSelectedItemId(R.id.nav_home);
        else
            super.onBackPressed();

    }
}
