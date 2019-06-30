package com.clinica.doctors.Activities.Clinic;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.clinica.doctors.Activities.Auth.Address.MapsActivity;
import com.clinica.doctors.Activities.Auth.MessageActivity;
import com.clinica.doctors.Activities.Base.BaseActivity;
import com.clinica.doctors.Activities.MainActivity;
import com.clinica.doctors.Adapters.WorkingHoursAdapter;
import com.clinica.doctors.Models.Doctor.Address;
import com.clinica.doctors.Models.Doctor.ClinicInformation;
import com.clinica.doctors.Models.Doctor.WorkingDayHours;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Constants;
import com.clinica.doctors.Tools.Localization;
import com.clinica.doctors.Tools.SharedTool.UserData;
import com.clinica.doctors.Tools.ToastTool;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClinicActivity extends BaseActivity implements ClinicView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.main_container)
    NestedScrollView mainContainer;

    @BindView(R.id.input_examination)
    TextInputLayout inputExamination;
    @BindView(R.id.examination_et)
    AppCompatEditText examinationEt;

    @BindView(R.id.input_address)
    TextInputLayout inputAddress;
    @BindView(R.id.address_et)
    AppCompatEditText addressEt;
    @BindView(R.id.working_hours_recycler)
    RecyclerView workingHoursRecycler;

    @BindView(R.id.error_container)
    ConstraintLayout errorContainer;
    @BindView(R.id.error_img)
    ImageView errorImg;
    @BindView(R.id.error_text)
    TextView errorText;

    private ClinicPresenter presenter;
    private int selectionAddressRequestCode = 86;
    WorkingHoursAdapter workingAdapter;
    List<WorkingDayHours> workingHours;

    @Override
    protected int setLayoutView() {
        return R.layout.activity_clinic;
    }

    @Override
    protected void initViews() {
        ButterKnife.bind(this);
        presenter = new ClinicPresenter(this, this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void initActions() {
        presenter.getClinic();
    }

    @Override
    public void setData(ClinicInformation clinic) {
        mainContainer.setVisibility(View.VISIBLE);
        errorContainer.setVisibility(View.GONE);

        if (clinic != null) {
            if (clinic.getExamination() != 0)
                examinationEt.setText(String.valueOf(clinic.getExamination()));
            Address addressAr = clinic.getAddressAr();
            Address addressEn = clinic.getAddressEn();
            if (UserData.getLocalization(this) == Localization.ARABIC_VALUE &&
                    addressAr != null &&
                    (!TextUtils.isEmpty(addressAr.getFullAddress(this, Localization.ARABIC_VALUE))))
                addressEt.setText(addressAr.getFullAddress(this, Localization.ARABIC_VALUE));
            if (UserData.getLocalization(this) == Localization.ENGLISH_VALUE &&
                    addressEn != null &&
                    (!TextUtils.isEmpty(addressEn.getFullAddress(this, Localization.ENGLISH_VALUE))))
                addressEt.setText(addressEn.getFullAddress(this, Localization.ENGLISH_VALUE));

            workingHours = clinic.getWorkingHours();
            if (workingHours == null || workingHours.size() == 0) {
                setDefaultWorkingHours();
            }
            initWorkingHoursRecycler(workingHours);
        }
    }

    private void initWorkingHoursRecycler(List<WorkingDayHours> workingHours) {
        workingAdapter = new WorkingHoursAdapter(this, workingHours);
        workingHoursRecycler.setHasFixedSize(false);
        workingHoursRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        workingHoursRecycler.setNestedScrollingEnabled(false);
        workingHoursRecycler.setItemAnimator(new DefaultItemAnimator());
        workingHoursRecycler.setAdapter(workingAdapter);
    }

    private void setDefaultWorkingHours() {
        List<Integer> days = Arrays.asList(
                Calendar.SATURDAY,
                Calendar.SUNDAY,
                Calendar.MONDAY,
                Calendar.TUESDAY,
                Calendar.WEDNESDAY,
                Calendar.THURSDAY,
                Calendar.FRIDAY);
        workingHours = new ArrayList<>();
        try {
            DateFormat df = new SimpleDateFormat("hh:mm aa", Locale.US);
            Date startDate = df.parse("06:00 PM");
            Date endDate = df.parse("09:00 PM");
            for (int i = 0; i < 7; i++) {
                WorkingDayHours dayHours = new WorkingDayHours();
                dayHours.setWorking(true);
                dayHours.setDayOfWeek(days.get(i));
                dayHours.setDuration(30);
                dayHours.setStartTime(startDate.getTime());
                dayHours.setEndTime(endDate.getTime());
                workingHours.add(dayHours);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        presenter.thisClinic.setWorkingHours(workingHours);
    }

    @Override
    public void showError(int errorImage, int errorMessage) {
        errorImg.setImageResource(errorImage);
        errorText.setText(errorMessage);
        mainContainer.setVisibility(View.GONE);
        errorContainer.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == selectionAddressRequestCode && resultCode == RESULT_OK) {
            Address addressAr = data.getParcelableExtra(Constants.Intents.ADDRESS_AR);
            Address addressEn = data.getParcelableExtra(Constants.Intents.ADDRESS_EN);
            presenter.thisClinic.setAddressAr(addressAr);
            presenter.thisClinic.setAddressEn(addressEn);
            if (UserData.getLocalization(this) == Localization.ARABIC_VALUE)
                addressEt.setText(addressAr.getAddress(this, Localization.ARABIC_VALUE));
            else addressEt.setText(addressEn.getAddress(this, Localization.ENGLISH_VALUE));
        }
    }

    @Override
    public void showExaminationError(int message) {
        inputExamination.setError(getText(message));
    }

    @Override
    public void showAddressError(int message) {
        inputAddress.setError(getText(message));
    }

    @Override
    public void toastError(int message) {
        ToastTool.with(this, message).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save_profile) {
            inputExamination.setError(null);
            for (int i = 0; i < workingHours.size(); i++) {
                workingHours.get(i).setError(null);
            }
            workingAdapter.notifyItemRangeChanged(0, workingHours.size());
            if (presenter.isNetworkAvailable() && presenter.isInputsValid()) {
                presenter.saveEdits();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.address_et)
    void openLocationSelector() {
        Intent intent = new Intent(new Intent(this, MapsActivity.class));
        startActivityForResult(intent, selectionAddressRequestCode);
    }

    @Override
    public void openMain() {
        Intent getIntent = getIntent();
        if (getIntent.hasExtra(Constants.Intents.EDIT_CLINIC)) {
            finish();
        } else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public void openMessageActivity(int message) {
        Intent open = new Intent(this, MessageActivity.class);
        open.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        open.putExtra(Constants.Intents.MESSAGE_INTENT, message);
        startActivity(open);
    }

}
