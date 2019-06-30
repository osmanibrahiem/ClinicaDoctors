package com.clinica.doctors.Activities.Auth.Login;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.ImageView;

import com.clinica.doctors.Activities.Auth.DoctorProfile.ProfileActivity;
import com.clinica.doctors.Activities.Auth.ForgetPassword.ForgetPasswordActivity;
import com.clinica.doctors.Activities.Auth.MessageActivity;
import com.clinica.doctors.Activities.Auth.SignUp.SignupActivity;
import com.clinica.doctors.Activities.Auth.UploadPracticeLicenseIdPhotoActivity;
import com.clinica.doctors.Activities.Base.BaseActivity;
import com.clinica.doctors.Activities.Clinic.ClinicActivity;
import com.clinica.doctors.Activities.MainActivity;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Constants;
import com.clinica.doctors.Tools.ToastTool;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity implements LoginView {

    private LoginPresenter presenter;

    @BindView(R.id.input_email)
    TextInputLayout inputEmail;
    @BindView(R.id.email_et)
    AppCompatEditText emailET;
    @BindView(R.id.input_password)
    TextInputLayout inputPassword;
    @BindView(R.id.password_et)
    AppCompatEditText passwordET;
    @BindView(R.id.forget_password_btn)
    AppCompatButton forgetPasswordBtn;
    @BindView(R.id.login_btn)
    AppCompatButton loginBtn;
    @BindView(R.id.signup_btn)
    AppCompatButton signupBtn;
    @BindView(R.id.fb_btn)
    ImageView fbBtn;
    @BindView(R.id.google_btn)
    ImageView googleBtn;

    @Override
    protected int setLayoutView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initViews() {
        ButterKnife.bind(this);
        presenter = new LoginPresenter(this, this);

        Typeface typeface = emailET.getTypeface();
        if (typeface != null)
            inputPassword.setTypeface(typeface);
    }


    @Override
    protected void initActions() {
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.hideKeypad();
                if (presenter.isNetworkAvailable() &&
                        presenter.isValidEmail() &&
                        presenter.isValidPassword()) {
                    presenter.checkEmail();
                }
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.hideKeypad();
                inputEmail.setError(null);
                inputPassword.setError(null);
                openSignUp();
            }
        });

        forgetPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.hideKeypad();
                inputEmail.setError(null);
                inputPassword.setError(null);
                openForgetPassword();
            }
        });

        fbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.hideKeypad();
                inputEmail.setError(null);
                inputPassword.setError(null);
                if (presenter.isNetworkAvailable())
                    presenter.loginWithFacebook();
            }
        });

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.hideKeypad();
                inputEmail.setError(null);
                inputPassword.setError(null);
                if (presenter.isNetworkAvailable())
                    presenter.loginWithGoogle();
            }
        });

        emailET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    inputEmail.setError(null);
                } else {
                    presenter.isValidEmail();
                }
            }
        });

        passwordET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    inputPassword.setError(null);
                } else {
                    presenter.isValidPassword();
                }
            }
        });
    }

    @Override
    public void showEmailError(int message) {
        inputEmail.setError(getText(message));
    }

    @Override
    public void showPasswordError(int message) {
        inputPassword.setError(getText(message));
    }

    @Override
    public void openSignUp() {
        Intent intent = new Intent(this, SignupActivity.class);
        intent.putExtra(Constants.Intents.ACCOUNT_REGEITERATION, Constants.Intents.EMAIL_REGEITERATION);
        startActivity(intent);
    }

    @Override
    public void openMain() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void openForgetPassword() {
        startActivity(new Intent(this, ForgetPasswordActivity.class));
    }

    @Override
    public void openLicenseActivity() {
        Intent intent = new Intent(getApplicationContext(), UploadPracticeLicenseIdPhotoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void openProfileActivity() {
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void showError(int errorMessage) {
        ToastTool.with(this, errorMessage).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        presenter.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void openMessageActivity(int message) {
        Intent open = new Intent(this, MessageActivity.class);
        open.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        open.putExtra(Constants.Intents.MESSAGE_INTENT, message);
        startActivity(open);
    }

    @Override
    public void openClinicActivity() {
        Intent open = new Intent(this, ClinicActivity.class);
        open.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(open);
    }
}
