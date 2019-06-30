package com.clinica.doctors.Activities.Auth.DoctorProfile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.clinica.doctors.Activities.Auth.MessageActivity;
import com.clinica.doctors.Activities.Base.BaseActivity;
import com.clinica.doctors.Activities.Clinic.ClinicActivity;
import com.clinica.doctors.Activities.MainActivity;
import com.clinica.doctors.Activities.Selector.SelectorActivity;
import com.clinica.doctors.Models.Doctor.BasicInformation;
import com.clinica.doctors.Models.Doctor.ClinicInformation;
import com.clinica.doctors.Models.Doctor.Doctor;
import com.clinica.doctors.Models.Doctor.Specialization;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Constants;
import com.clinica.doctors.Tools.DateInputMask;
import com.clinica.doctors.Tools.Localization;
import com.clinica.doctors.Tools.SharedTool.UserData;
import com.clinica.doctors.Tools.ToastTool;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends BaseActivity implements ProfileView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.main_container)
    NestedScrollView mainContainer;
    @BindView(R.id.input_img)
    CircularImageView inputImg;

    @BindView(R.id.input_name_ar)
    TextInputLayout inputNameAr;
    @BindView(R.id.name_ar_et)
    AppCompatEditText nameArEt;
    @BindView(R.id.input_name_en)
    TextInputLayout inputNameEn;
    @BindView(R.id.name_en_et)
    AppCompatEditText nameEnEt;
    @BindView(R.id.input_birthday)
    TextInputLayout inputBirthday;
    @BindView(R.id.birthday_et)
    AppCompatEditText birthdayEt;
    @BindView(R.id.input_gender)
    AppCompatSpinner inputGender;
    @BindView(R.id.license_img)
    ImageView licenseImg;
    @BindView(R.id.license_text)
    TextInputLayout inputLicense;
    @BindView(R.id.license_container)
    ConstraintLayout licenseContainer;

    @BindView(R.id.input_professional_title_ar)
    TextInputLayout inputProfessionalTitleAr;
    @BindView(R.id.professional_title_ar_et)
    AppCompatEditText professionalTitleArEt;
    @BindView(R.id.input_professional_title_en)
    TextInputLayout inputProfessionalTitleEn;
    @BindView(R.id.professional_title_en_et)
    AppCompatEditText professionalTitleEnEt;

    @BindView(R.id.input_about_ar)
    TextInputLayout inputAboutAr;
    @BindView(R.id.about_ar_et)
    AppCompatEditText aboutArEt;
    @BindView(R.id.input_about_en)
    TextInputLayout inputAboutEn;
    @BindView(R.id.about_en_et)
    AppCompatEditText aboutEnEt;

    @BindView(R.id.input_specialty)
    TextInputLayout inputSpecialty;
    @BindView(R.id.specialty_et)
    AppCompatEditText specialtyEt;
    @BindView(R.id.certificate_container)
    ConstraintLayout certificateContainer;
    @BindView(R.id.certificate_text)
    TextInputLayout inputCertificate;
    @BindView(R.id.certificate_img)
    ImageView certificateImg;

    @BindView(R.id.error_container)
    ConstraintLayout errorContainer;
    @BindView(R.id.error_img)
    ImageView errorImg;
    @BindView(R.id.error_text)
    TextView errorText;

    private ProfilePresenter presenter;
    private int selectionSpecialtyRequestCode = 84;

    @Override
    protected int setLayoutView() {
        return R.layout.activity_profile;
    }

    @Override
    protected void initViews() {
        ButterKnife.bind(this);
        presenter = new ProfilePresenter(this, this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String[] ITEMS = getResources().getStringArray(R.array.sex_items);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ITEMS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputGender.setAdapter(adapter);
        birthdayEt.addTextChangedListener(new DateInputMask(birthdayEt));
        inputImg.setFocusableInTouchMode(true);
        inputImg.setFocusable(true);
    }

    @Override
    protected void initActions() {
        presenter.getDoctor();
    }

    @Override
    public void setData(Doctor doctor) {
        mainContainer.setVisibility(View.VISIBLE);
        errorContainer.setVisibility(View.GONE);

        if (doctor != null) {
            if (!TextUtils.isEmpty(doctor.getProfilePhotoUrl())) {
                Picasso.get()
                        .load(doctor.getProfilePhotoUrl())
                        .placeholder(R.drawable.profile_picture_blank_square)
                        .error(R.drawable.error_placeholder)
                        .into(inputImg);
            }
            BasicInformation informationAr = doctor.getBasicInformationAr();
            if (informationAr != null) {
                if (!TextUtils.isEmpty(informationAr.getDisplayName())) {
                    nameArEt.setText(informationAr.getDisplayName());
                }
                if ((!TextUtils.isEmpty(informationAr.getGender())) && UserData.getLocalization(this) == Localization.ARABIC_VALUE) {
                    String gender = informationAr.getGender();
                    if (gender.toLowerCase().equals("ذكر"))
                        inputGender.setSelection(2);
                    else if (gender.toLowerCase().equals("أنثى"))
                        inputGender.setSelection(1);
                }
                if (!TextUtils.isEmpty(informationAr.getProfessionalTitle())) {
                    professionalTitleArEt.setText(informationAr.getProfessionalTitle());
                }
                if (!TextUtils.isEmpty(informationAr.getAbout())) {
                    aboutArEt.setText(informationAr.getAbout());
                }
            }
            BasicInformation informationEn = doctor.getBasicInformationEN();
            if (informationEn != null) {
                if (!TextUtils.isEmpty(informationEn.getDisplayName())) {
                    nameEnEt.setText(informationEn.getDisplayName());
                }
                if ((!TextUtils.isEmpty(informationEn.getGender())) && UserData.getLocalization(this) == Localization.ENGLISH_VALUE) {
                    String gender = informationEn.getGender();
                    if (gender.toLowerCase().equals("male"))
                        inputGender.setSelection(2);
                    else if (gender.toLowerCase().equals("female"))
                        inputGender.setSelection(1);
                }
                if (!TextUtils.isEmpty(informationEn.getProfessionalTitle())) {
                    professionalTitleEnEt.setText(informationEn.getProfessionalTitle());
                }
                if (!TextUtils.isEmpty(informationEn.getAbout())) {
                    aboutEnEt.setText(informationEn.getAbout());
                }
            }
            if (doctor.getBirthDateTimestamp() != 0) {
                Date birthDate = new Date(doctor.getBirthDateTimestamp());
                DateFormat ClinicaDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                birthdayEt.setText(ClinicaDateFormat.format(birthDate));
            }
            if (!TextUtils.isEmpty(doctor.getPracticeLicenseIdPhotoURL())) {
                Picasso.get()
                        .load(doctor.getPracticeLicenseIdPhotoURL())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.error_placeholder)
                        .into(licenseImg);
            }
            if (doctor.getSpecialization() != null) {
                if (UserData.getLocalization(this) == Localization.ARABIC_VALUE)
                    specialtyEt.setText(doctor.getSpecialization().getTitleAr());
                else specialtyEt.setText(doctor.getSpecialization().getTitleEn());
            }
            if (!TextUtils.isEmpty(doctor.getCertificatePhotoURL())) {
                Picasso.get()
                        .load(doctor.getCertificatePhotoURL())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.error_placeholder)
                        .into(certificateImg);
            }
            ClinicInformation clinic = doctor.getClinicInformation();

        } else {
            showError(R.drawable.ic_sad, R.string.data_not_found);
        }
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
        if (requestCode == selectionSpecialtyRequestCode && resultCode == RESULT_OK) {
            Specialization specialization = data.getParcelableExtra(Constants.Intents.SPECIALIZATION_DATA);
            presenter.specialtyID = specialization.getId();
            if (UserData.getLocalization(this) == Localization.ARABIC_VALUE)
                specialtyEt.setText(specialization.getTitleAr());
            else specialtyEt.setText(specialization.getTitleEn());
        } else
            presenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setProfileImage(Bitmap img) {
        inputImg.setImageBitmap(img);
    }

    @Override
    public void showNameArError(int message) {
        inputNameAr.setError(getText(message));
    }

    @Override
    public void showNameEnError(int message) {
        inputNameEn.setError(getText(message));
    }

    @Override
    public void showBirthdayError(int message) {
        inputBirthday.setError(getText(message));
    }

    @Override
    public void showGenderError(int message) {
        ((TextView) inputGender.getSelectedView()).setError(getString(message));
    }

    @Override
    public void showLicenseError(int message) {
        inputLicense.setError(getText(message));
    }

    @Override
    public void showProfessionalTitleArError(int message) {
        inputProfessionalTitleAr.setError(getText(message));
    }

    @Override
    public void showProfessionalTitleEnError(int message) {
        inputProfessionalTitleEn.setError(getText(message));
    }

    @Override
    public void showSpecialtyError(int message) {
        inputSpecialty.setError(getText(message));
    }

    @Override
    public void showCertificateError(int message) {
        inputCertificate.setError(getText(message));
    }

    @Override
    public void toastError(int message) {
        ToastTool.with(this, message).show();
    }

    @Override
    public void setLicenseImage(Bitmap bitmap) {
        licenseImg.setImageBitmap(bitmap);
    }

    @Override
    public void setCertificationImage(Bitmap bitmap) {
        certificateImg.setImageBitmap(bitmap);
    }

    @Override
    public void openClinicActivity() {
        Intent open = new Intent(this, ClinicActivity.class);
        open.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(open);
    }

    @Override
    public void openMain() {
        Intent getIntent = getIntent();
        if (getIntent.hasExtra(Constants.Intents.EDIT_PROFILE)) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save_profile) {
            inputNameAr.setError(null);
            inputNameEn.setError(null);
            inputBirthday.setError(null);
            ((TextView) inputGender.getSelectedView()).setError(null);
            inputLicense.setError(null);
            inputProfessionalTitleEn.setError(null);
            inputProfessionalTitleAr.setError(null);
            inputSpecialty.setError(null);
            inputCertificate.setError(null);
            if (presenter.isNetworkAvailable() && presenter.isInputsValid()) {
                presenter.saveEdits();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.img_plus, R.id.input_img})
    void onProfileImageClick() {
        presenter.importImage(ProfilePresenter.ImageOptions.PROFILE_IMAGE);
    }

    @OnClick(R.id.specialty_et)
    void openSpecialtySelector() {
        Intent intent = new Intent(new Intent(this, SelectorActivity.class));
        intent.putExtra(Constants.Intents.SELECTION_KEY, Constants.Intents.SELECT_SPECIALIZATION);
        startActivityForResult(intent, selectionSpecialtyRequestCode);
    }

    @OnClick({R.id.license_container, R.id.license_img, R.id.license_text})
    void onLicenseClick() {
        if (!TextUtils.isEmpty(presenter.thisDoctor.getPracticeLicenseIdPhotoURL())) {
            ToastTool.with(this, R.string.message_perform_to_change_license).show();
        } else {
            presenter.importImage(ProfilePresenter.ImageOptions.LICENSE_IMAGE);
        }
    }

    @OnClick({R.id.certificate_container, R.id.certificate_img, R.id.certificate_text})
    void onCertificationClick() {
        if (!TextUtils.isEmpty(presenter.thisDoctor.getCertificatePhotoURL())) {
            ToastTool.with(this, R.string.message_perform_to_change_certification).show();
        } else {
            presenter.importImage(ProfilePresenter.ImageOptions.CERTIFICATION_IMAGE);
        }
    }
}
