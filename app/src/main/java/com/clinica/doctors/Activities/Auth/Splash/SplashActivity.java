package com.clinica.doctors.Activities.Auth.Splash;

import android.content.Intent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clinica.doctors.Activities.Auth.DoctorProfile.ProfileActivity;
import com.clinica.doctors.Activities.Auth.Login.LoginActivity;
import com.clinica.doctors.Activities.Auth.MessageActivity;
import com.clinica.doctors.Activities.Auth.UploadPracticeLicenseIdPhotoActivity;
import com.clinica.doctors.Activities.Base.BaseActivity;
import com.clinica.doctors.Activities.Clinic.ClinicActivity;
import com.clinica.doctors.Activities.MainActivity;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Constants;
import com.clinica.doctors.Tools.Localization;
import com.clinica.doctors.Tools.ToastTool;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity implements SplashView {

    @BindView(R.id.splash_logo)
    LinearLayout logo;
    @BindView(R.id.logo_img)
    ImageView logoImg;
    @BindView(R.id.logo_txt)
    TextView logoTxt;

    private SplashPresenter presenter;

    @Override
    protected int setLayoutView() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initViews() {
        ButterKnife.bind(this);
        presenter = new SplashPresenter(this, this);
        presenter.setLanguage(Localization.ARABIC_VALUE);
    }

    @Override
    protected void initActions() {
        presenter.animateLogo(logo);
    }

    @Override
    public void openLoginActivity() {
        Intent open = new Intent(this, LoginActivity.class);
        open.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(open);
    }

    @Override
    public void openLicenseActivity() {
        Intent open = new Intent(this, UploadPracticeLicenseIdPhotoActivity.class);
        open.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(open);
    }

    @Override
    public void openMainActivity() {
        Intent open = new Intent(this, MainActivity.class);
        open.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(open);
    }

    @Override
    public void openProfileActivity() {
        Intent open = new Intent(this, ProfileActivity.class);
        open.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(open);
    }

    @Override
    public void showError(int errorImage, int errorMessage) {
        ToastTool.with(this, errorMessage).show();
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
