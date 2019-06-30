package com.clinica.doctors.Activities.Auth.SignUp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.clinica.doctors.Activities.Base.BaseActivity;
import com.clinica.doctors.Activities.Selector.SelectorActivity;
import com.clinica.doctors.Models.Doctor.Specialization;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Constants;
import com.clinica.doctors.Tools.DateInputMask;
import com.clinica.doctors.Tools.Localization;
import com.clinica.doctors.Tools.SharedTool.UserData;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignupActivity extends BaseActivity implements SignupView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.input_img)
    CircularImageView inputImage;
    @BindView(R.id.img_plus)
    CircularImageView imgPlus;
    @BindView(R.id.input_email)
    TextInputLayout inputEmail;
    @BindView(R.id.email_et)
    AppCompatEditText emailEt;
    @BindView(R.id.input_name_ar)
    TextInputLayout inputNameAr;
    @BindView(R.id.name_ar_et)
    AppCompatEditText nameArET;
    @BindView(R.id.input_name_en)
    TextInputLayout inputNameEn;
    @BindView(R.id.name_en_et)
    AppCompatEditText nameEnET;
    @BindView(R.id.input_specialty)
    TextInputLayout inputSpecialty;
    @BindView(R.id.specialty_et)
    AppCompatEditText specialtyET;
    @BindView(R.id.input_birthday)
    TextInputLayout inputBirthday;
    @BindView(R.id.birthday_et)
    AppCompatEditText birthdayET;
    @BindView(R.id.input_phone)
    TextInputLayout inputPhone;
    @BindView(R.id.phone_et)
    AppCompatEditText phoneET;
    @BindView(R.id.input_password)
    TextInputLayout inputPassword;
    @BindView(R.id.password_et)
    AppCompatEditText passwordET;
    @BindView(R.id.input_gender)
    AppCompatSpinner inputGender;
    @BindView(R.id.signup_btn)
    AppCompatButton signupBtn;

    private SignupPresenter presenter;
    private int selectionSpecialtyRequestCode = 54;

    @Override
    protected int setLayoutView() {
        return R.layout.activity_signup;
    }

    @Override
    protected void initViews() {
        ButterKnife.bind(this);
        presenter = new SignupPresenter(this, this);
        toolbar.setTitle(getString(R.string.sign_up_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Typeface typeface = emailEt.getTypeface();
        if (typeface != null) {
            inputPassword.setTypeface(typeface);
        }
        String[] ITEMS = getResources().getStringArray(R.array.sex_items);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.gender_spinner_header, ITEMS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputGender.setAdapter(adapter);
        birthdayET.addTextChangedListener(new DateInputMask(birthdayET));
        inputImage.setFocusableInTouchMode(true);
        inputImage.setFocusable(true);
    }

    @Override
    protected void initActions() {
        Intent intent = getIntent();
        if (intent.hasExtra(Constants.Intents.ACCOUNT_REGEITERATION)) {
            if (!intent.getStringExtra(Constants.Intents.ACCOUNT_REGEITERATION)
                    .equals(Constants.Intents.EMAIL_REGEITERATION)) {
                presenter.setIntent(intent);

                if (intent.hasExtra(Constants.Intents.ACCOUNT_NAME)) {
                    if (UserData.getLocalization(this) == Localization.ARABIC_VALUE)
                        nameArET.setText(intent.getStringExtra(Constants.Intents.ACCOUNT_NAME));
                    else nameEnET.setText(intent.getStringExtra(Constants.Intents.ACCOUNT_NAME));
                }
                if (intent.hasExtra(Constants.Intents.ACCOUNT_EMAIL)) {
                    emailEt.setText(intent.getStringExtra(Constants.Intents.ACCOUNT_EMAIL));
                    inputEmail.setEnabled(false);
                    emailEt.setEnabled(false);
                }
                if (intent.hasExtra(Constants.Intents.ACCOUNT_PICTURE)) {
                    Picasso.get()
                            .load(intent.getStringExtra(Constants.Intents.ACCOUNT_PICTURE))
                            .placeholder(R.drawable.profile_picture_blank_square)
                            .error(R.drawable.error_placeholder)
                            .into(inputImage);
                }
                if (intent.hasExtra(Constants.Intents.ACCOUNT_BIRTH_DATE)) {
                    birthdayET.setText(intent.getStringExtra(Constants.Intents.ACCOUNT_BIRTH_DATE));
                }
                if (intent.hasExtra(Constants.Intents.ACCOUNT_GENDER)) {
                    String gender = intent.getStringExtra(Constants.Intents.ACCOUNT_GENDER);
                    if (gender.toLowerCase().equals("male"))
                        inputGender.setSelection(2);
                    else if (gender.toLowerCase().equals("female"))
                        inputGender.setSelection(1);
                }

                inputPassword.setVisibility(View.GONE);
            }
        }
    }

    @OnClick({R.id.img_plus, R.id.input_img})
    void onProfileImageClick() {
        presenter.importImage();
    }

    @OnClick(R.id.signup_btn)
    void createUser() {
        presenter.hideKeypad();
        if (presenter.isNetworkAvailable() &&
                presenter.isValidInput()) {
            presenter.signUpUserWithEmail();
        }
    }

    @OnClick(R.id.specialty_et)
    void openSpecialtySelector() {
        Intent intent = new Intent(new Intent(this, SelectorActivity.class));
        intent.putExtra(Constants.Intents.SELECTION_KEY, Constants.Intents.SELECT_SPECIALIZATION);
        startActivityForResult(intent, selectionSpecialtyRequestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == selectionSpecialtyRequestCode && resultCode == RESULT_OK) {
            Specialization specialization = data.getParcelableExtra(Constants.Intents.SPECIALIZATION_DATA);
            presenter.specialtyID = specialization.getId();
            if (UserData.getLocalization(this) == Localization.ARABIC_VALUE)
                specialtyET.setText(specialization.getTitleAr());
            else specialtyET.setText(specialization.getTitleEn());
        } else
            presenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void setProfileImage(Bitmap img) {
        inputImage.setImageBitmap(img);
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
    public void showEmailError(int message) {
        inputEmail.setError(getText(message));
    }

    @Override
    public void showPhoneError(int message) {
        inputPhone.setError(getText(message));
    }

    @Override
    public void showBirthdayError(int message) {
        inputBirthday.setError(getText(message));
    }

    @Override
    public void showPasswordError(int message) {
        inputPassword.setError(getText(message));
    }

    @Override
    public void showGenderError(int message) {
        ((TextView) inputGender.getSelectedView()).setError(getString(message));
    }

    @Override
    public void showSpecialtyError(int message) {
        inputSpecialty.setError(getText(message));
    }

}
