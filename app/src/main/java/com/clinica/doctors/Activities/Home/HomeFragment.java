package com.clinica.doctors.Activities.Home;

import android.content.Intent;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.clinica.doctors.Activities.Base.BaseFragment;
import com.clinica.doctors.Activities.ConfirmAttendance.ConfirmAttendanceActivity;
import com.clinica.doctors.Adapters.AdsAdapter;
import com.clinica.doctors.Models.Ads;
import com.clinica.doctors.Models.Doctor.Doctor;
import com.clinica.doctors.Models.Doctor.WorkingDayHours;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Localization;
import com.clinica.doctors.Tools.SharedTool.UserData;
import com.clinica.doctors.Tools.ToastTool;
import com.rd.PageIndicatorView;
import com.rd.draw.data.RtlMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeFragment extends BaseFragment implements HomeView {

    public HomeFragment() {
        // Required empty public constructor
    }

    @BindView(R.id.header_ConstraintLayout)
    ConstraintLayout headerConstraintLayout;
    @BindView(R.id.ads_recycler)
    RecyclerView adsRecycler;
    @BindView(R.id.logo)
    AppCompatImageView logo;
    @BindView(R.id.pageIndicatorView)
    PageIndicatorView indicator;

    private HomePresenter presenter;
    private AdsAdapter adsAdapter;

    private int mCurrentPage = 0;
    private Handler handler;
    private int delay = 5000; //milliseconds
    private Runnable runnable = new Runnable() {
        public void run() {
            if (adsRecycler.getAdapter() != null && adsRecycler.getAdapter().getItemCount() != 0) {
                mCurrentPage++;
                mCurrentPage = mCurrentPage % adsRecycler.getAdapter().getItemCount();
                adsRecycler.smoothScrollToPosition(mCurrentPage);
                handler.postDelayed(this, delay);
            }
        }
    };

    @Override
    protected int setLayoutView() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initViews(View view) {
        ButterKnife.bind(this, view);
        presenter = new HomePresenter(this, this);
        handler = new Handler();
        if (UserData.getLocalization(getActivity()) == Localization.ARABIC_VALUE) {
            indicator.setRtlMode(RtlMode.On);
        }
    }

    @Override
    protected void initActions() {
        presenter.getDoctor();
        presenter.checkAds();

    }

    @Override
    public void initData(final Doctor doctor) {
        /*

        thisWeekAvailableSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Doctor mDoctor = doctor;
                mDoctor.setUid(null);
                mDoctor.setSpecialization(null);
                if (isChecked) {
                    List<DayAppointments> dayAppointmentsList = new ArrayList<>();
                    if (mDoctor.getDayAppointments() != null)
                        dayAppointmentsList.addAll(mDoctor.getDayAppointments());
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, -1);
                    for (int i = 0; i < 7; i++) {
                        cal.add(Calendar.DATE, 1);
                        WorkingDayHours dayHours = getWorkingDay(mDoctor.getClinicInformation().getWorkingHours(), cal.get(Calendar.DAY_OF_WEEK));

                        if (dayHours != null && dayHours.isWorking()) {
                            DayAppointments dayAppointments = new DayAppointments();
                            List<Appointment> appointmentList = new ArrayList<>();

                            Calendar startTimeCal = Calendar.getInstance();
                            startTimeCal.setTimeInMillis(dayHours.getStartTime());
                            Calendar endTimeCal = Calendar.getInstance();
                            endTimeCal.setTimeInMillis(dayHours.getEndTime());
                            while (startTimeCal.before(endTimeCal)) {
                                Appointment appointment = new Appointment();
                                appointment.setStatus(Constants.ACTIVE_STATUS);
                                appointment.setTime(startTimeCal.getTimeInMillis());
                                appointmentList.add(appointment);

                                startTimeCal.add(Calendar.MINUTE, dayHours.getDuration());
                            }
                            dayAppointments.setAppointments(appointmentList);
                            dayAppointments.setTitle(cal.getTimeInMillis());
                            dayAppointmentsList.add(dayAppointments);
                        }
                        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)
                            break;
                    }
                    mDoctor.setDayAppointments(dayAppointmentsList);
                    FirebaseDatabase
                            .getInstance()
                            .getReference()
                            .child(Constants.DataBase.Doctors_NODE)
                            .child(FirebaseAuth.getInstance().getUid())
                            .setValue(mDoctor);
                }
            }
        });

        nextWeekAvailableSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Doctor mDoctor = doctor;
                mDoctor.setUid(null);
                mDoctor.setSpecialization(null);
                List<Integer> days = Arrays.asList(
                        Calendar.SATURDAY,
                        Calendar.SUNDAY,
                        Calendar.MONDAY,
                        Calendar.TUESDAY,
                        Calendar.WEDNESDAY,
                        Calendar.THURSDAY,
                        Calendar.FRIDAY);
                if (isChecked) {
                    List<DayAppointments> dayAppointmentsList = new ArrayList<>();
                    if (mDoctor.getDayAppointments() != null)
                        dayAppointmentsList.addAll(mDoctor.getDayAppointments());
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.WEEK_OF_YEAR, 1);
                    cal.add(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                    cal.add(Calendar.DATE, -1);
                    for (int i = 0; i < 7; i++) {
                        cal.add(Calendar.DATE, 1);
                        WorkingDayHours dayHours = getWorkingDay(mDoctor.getClinicInformation().getWorkingHours(), cal.get(Calendar.DAY_OF_WEEK));

                        if (dayHours != null && dayHours.isWorking()) {
                            DayAppointments dayAppointments = new DayAppointments();
                            List<Appointment> appointmentList = new ArrayList<>();


                            Calendar startTimeCal = Calendar.getInstance();
                            startTimeCal.setTimeInMillis(dayHours.getStartTime());
                            Calendar endTimeCal = Calendar.getInstance();
                            endTimeCal.setTimeInMillis(dayHours.getEndTime());
                            while (startTimeCal.before(endTimeCal)) {
                                Appointment appointment = new Appointment();
                                appointment.setStatus(Constants.ACTIVE_STATUS);
                                appointment.setTime(startTimeCal.getTimeInMillis());
                                appointmentList.add(appointment);

                                startTimeCal.add(Calendar.MINUTE, dayHours.getDuration());
                            }
                            dayAppointments.setAppointments(appointmentList);
                            dayAppointments.setTitle(cal.getTimeInMillis());
                            dayAppointmentsList.add(dayAppointments);
                        }
                        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY)
                            break;
                    }
                    mDoctor.setDayAppointments(dayAppointmentsList);
                    FirebaseDatabase
                            .getInstance()
                            .getReference()
                            .child(Constants.DataBase.Doctors_NODE)
                            .child(FirebaseAuth.getInstance().getUid())
                            .setValue(mDoctor);
                }
            }
        });

        */
    }

    private WorkingDayHours getWorkingDay(List<WorkingDayHours> workingDayHours, int day) {
        for (WorkingDayHours dayHours : workingDayHours) {
            if (dayHours.getDayOfWeek() == day)
                return dayHours;
        }
        return null;
    }

    @Override
    public void initAdsRecycler(long adsCount) {
        hideLoading();
        adsAdapter = new AdsAdapter(getActivity());
        adsRecycler.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        adsRecycler.setLayoutManager(llm);
        adsRecycler.setItemAnimator(new DefaultItemAnimator());
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        adsRecycler.setOnFlingListener(null);
        snapHelper.attachToRecyclerView(adsRecycler);
        adsRecycler.setAdapter(adsAdapter);
        indicator.setCount(1);
        adsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int position = ((LinearLayoutManager) recyclerView.getLayoutManager())
                            .findFirstVisibleItemPosition();
                    indicator.setCount(recyclerView.getAdapter().getItemCount());
                    indicator.setSelection(position);
                    mCurrentPage = position;
                }
            }
        });
        showAdsPager();
    }

    private void showAdsPager() {
        adsRecycler.setVisibility(View.VISIBLE);
        indicator.setVisibility(View.VISIBLE);
        logo.setVisibility(View.GONE);
        handler.postDelayed(runnable, delay);
    }

    @Override
    public void hideAdsPager() {
        adsRecycler.setVisibility(View.GONE);
        indicator.setVisibility(View.GONE);
        logo.setVisibility(View.VISIBLE);
        handler.removeCallbacks(runnable);
    }


    @Override
    public void addAds(Ads ads) {
        adsAdapter.addAds(ads);
        indicator.setCount(adsAdapter.getItemCount());
    }

    @Override
    public void updateAds(Ads ads) {
        adsAdapter.updateAds(ads);
        indicator.setCount(adsAdapter.getItemCount());
    }

    @Override
    public void deleteAds(Ads ads) {
        adsAdapter.removeAds(ads);
        indicator.setCount(adsAdapter.getItemCount());
    }

    @Override
    public void toastError(int message) {
        ToastTool.with(getActivity(), message).show();
    }

    @OnClick({R.id.availability_section})
    void openAvailability() {
        startActivity(new Intent(getActivity(), ConfirmAttendanceActivity.class));
    }
}
