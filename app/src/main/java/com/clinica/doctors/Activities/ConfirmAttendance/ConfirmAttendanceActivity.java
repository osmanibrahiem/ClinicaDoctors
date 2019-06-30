package com.clinica.doctors.Activities.ConfirmAttendance;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import com.clinica.doctors.Activities.Base.BaseActivity;
import com.clinica.doctors.Activities.MainActivity;
import com.clinica.doctors.Adapters.AvailabilityAdapter;
import com.clinica.doctors.Models.Doctor.Appointment;
import com.clinica.doctors.Models.Doctor.DayAppointments;
import com.clinica.doctors.Models.Doctor.Doctor;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Constants;
import com.clinica.doctors.Tools.RecyclerDividerItemDecoration;
import com.clinica.doctors.Tools.ToastTool;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConfirmAttendanceActivity extends BaseActivity implements ConfirmAttendanceView {

    private ConfirmAttendancePresenter presenter;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private AvailabilityAdapter adapter;
    private List<Long> daysAttendanceList;
    private int weekToAdd = 0;

    @Override
    protected int setLayoutView() {
        return R.layout.recycler_layout;
    }

    @Override
    protected void initViews() {
        ButterKnife.bind(this);
        toolbar.setTitle(R.string.confirm_attendance_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        presenter = new ConfirmAttendancePresenter(this, this);
        presenter.getDoctor();
    }

    @Override
    protected void initActions() {

    }

    @Override
    public void initData(final Doctor doctor) {
        adapter = new AvailabilityAdapter(this, doctor.getDayAppointments(), doctor.getClinicInformation().getWorkingHours());
        daysAttendanceList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new RecyclerDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 20));
        recyclerView.setAdapter(adapter);
        adapter.setDayCallBack(new AvailabilityAdapter.DayCallBack() {
            @Override
            public void onCheckChange(DayAppointments dayAppointments, boolean isConfirmed) {
                if (isConfirmed) {
                    presenter.confirmDay(dayAppointments, true);
                } else {
                    int count = 0;
                    if (dayAppointments.getAppointments() != null) {
                        for (Appointment appointment : dayAppointments.getAppointments()) {
                            if (!TextUtils.isEmpty(appointment.getUserId())) {
                                count++;
                            }
                        }
                    }
                    if (count != 0) {
                        displayConfirmationDialog(dayAppointments);
                    } else presenter.confirmDay(dayAppointments, false);
                }

            }

            @Override
            public void onDayClicked(DayAppointments dayAppointments) {
                Intent intent = new Intent(ConfirmAttendanceActivity.this, MainActivity.class);
                intent.putExtra(Constants.Intents.DAY_APPOINTMENTS, dayAppointments.getTitle());
                startActivity(intent);
            }
        });
        addDaysToList();

        weekToAdd++;
        addDaysToList();

        if (adapter.getItemCount() < 14) {
            weekToAdd++;
            addDaysToList();
        }

        /*
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) {
                    // Recycle view scrolling down...
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                        weekToAdd++;
                        addDaysToList();
                    }

                }
            }
        });
        */
    }

    private void displayConfirmationDialog(final DayAppointments dayAppointments) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.dialog_confrimation_message));
        builder.setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                presenter.cancelDayBookings(dayAppointments);
            }
        });
        builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void addDaysToList() {
        daysAttendanceList.clear();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_YEAR, weekToAdd);
        int firstDay = cal.getActualMinimum(Calendar.DAY_OF_WEEK);
        cal.set(Calendar.DAY_OF_WEEK, firstDay);
        cal.add(Calendar.DATE, -1);
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_WEEK);
        while (firstDay != lastDay) {

            daysAttendanceList.add(cal.getTimeInMillis());

            cal.add(Calendar.DATE, 1);
            firstDay = cal.get(Calendar.DAY_OF_WEEK);
        }
        adapter.addDays(daysAttendanceList);
    }

    @Override
    public void toastError(String message) {
        ToastTool.with(this, message).show();
    }
}
