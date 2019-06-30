package com.clinica.doctors.Activities.Booking.Add;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.clinica.doctors.Activities.Base.BaseActivity;
import com.clinica.doctors.Adapters.AppointmentsChildAdapter;
import com.clinica.doctors.Models.Doctor.DayAppointments;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Constants;
import com.clinica.doctors.Tools.SharedTool.UserData;
import com.clinica.doctors.Tools.ToastTool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddNewBookingActivity extends BaseActivity implements NewBookingView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.day_title)
    TextView dayTitle;
    @BindView(R.id.appointments_recycler)
    RecyclerView appointmentsRecycler;
    @BindView(R.id.appointments_error)
    TextView appointmentsError;
    @BindView(R.id.input_patient_name)
    TextInputLayout inputPatientName;
    @BindView(R.id.patient_name_et)
    AppCompatEditText patientNameEt;
    @BindView(R.id.input_email)
    TextInputLayout inputEmail;
    @BindView(R.id.email_et)
    AppCompatEditText emailEt;
    @BindView(R.id.input_phone)
    TextInputLayout inputPhone;
    @BindView(R.id.phone_et)
    AppCompatEditText phoneEt;
    @BindView(R.id.input_notes)
    TextInputLayout inputNotes;
    @BindView(R.id.notes_et)
    AppCompatEditText notesEt;
    @BindView(R.id.save_btn)
    AppCompatButton saveBtn;

    private NewBookingPresenter presenter;
    private AppointmentsChildAdapter adapter;

    @Override
    protected int setLayoutView() {
        return R.layout.activity_add_new_booking;
    }

    @Override
    protected void initViews() {
        ButterKnife.bind(this);
        presenter = new NewBookingPresenter(this, this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void initActions() {
        Intent intent = getIntent();
        if (intent.hasExtra(Constants.Intents.DAY_APPOINTMENTS))
            presenter.getAppointmentList(intent.getLongExtra(Constants.Intents.DAY_APPOINTMENTS, -1));
        else toastError(R.string.data_not_found);
    }

    @Override
    public void initData(DayAppointments dayAppointments) {
        appointmentsRecycler.setVisibility(View.VISIBLE);
        appointmentsError.setVisibility(View.GONE);
        Locale locale = new Locale(UserData.getLocalizationString(this));
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMMM", locale);
        dayTitle.setText(sdf.format(new Date(dayAppointments.getTitle())));
        adapter = new AppointmentsChildAdapter(this, dayAppointments.getAppointments());
        appointmentsRecycler.setHasFixedSize(true);
        appointmentsRecycler.setLayoutManager(new GridLayoutManager(this, 3));
        appointmentsRecycler.setItemAnimator(new DefaultItemAnimator());
        appointmentsRecycler.setAdapter(adapter);
    }

    @Override
    public void showAppointmentError(int message) {
        appointmentsError.setText(message);
        appointmentsRecycler.setVisibility(View.GONE);
        appointmentsError.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNameError(int message) {
        inputPatientName.setError(getText(message));
    }

    @Override
    public void showEmailError(int message) {
        inputEmail.setError(getText(message));
    }

    @Override
    public void showPhoneError(int message) {
        inputPhone.setError(getText(message));
    }

    @Override
    public void toastError(int message) {
        ToastTool.with(this, message).show();
    }

    @Override
    public void toastSuccessMessage(int message) {
        ToastTool.with(this, message).show();
        finish();
    }

    @OnClick(R.id.save_btn)
    void saveBooking() {
        inputPatientName.setError(null);
        inputEmail.setError(null);
        inputPhone.setError(null);

        if (adapter != null) {
            if (presenter.isNetworkAvailable() && presenter.isInputsValid(adapter.getSelectedAppointment())) {
                presenter.saveBooking(adapter.getSelectedAppointment(), getIntent().getLongExtra(Constants.Intents.DAY_APPOINTMENTS, -1));
            }
        } else {
            toastError(R.string.attendance_error);
        }
    }
}
