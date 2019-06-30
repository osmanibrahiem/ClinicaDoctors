package com.clinica.doctors.Activities.Auth;

import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.clinica.doctors.Activities.Auth.DoctorProfile.ProfileActivity;
import com.clinica.doctors.Activities.Auth.Login.LoginActivity;
import com.clinica.doctors.Activities.Base.BaseActivity;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Constants;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.img)
    ImageView image;
    @BindView(R.id.text)
    TextView text;

    @Override
    protected int setLayoutView() {
        return R.layout.activity_message;
    }

    @Override
    protected void initViews() {
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void initActions() {
        Intent intent = getIntent();
        int messageRes = intent.getIntExtra(Constants.Intents.MESSAGE_INTENT, -1);
        switch (messageRes) {
            case R.string.profile_under_review:
                image.setImageResource(R.drawable.under_review);
                text.setText(messageRes);
                image.setVisibility(View.VISIBLE);
                text.setVisibility(View.VISIBLE);
                break;
            case R.string.profile_blocked:
            case R.string.profile_deleted:
                image.setImageResource(R.drawable.ic_sad);
                text.setText(messageRes);
                image.setVisibility(View.VISIBLE);
                text.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.message_menu, menu);
        MenuItem item = menu.findItem(R.id.edit_profile);
        Intent intent = getIntent();
        int messageRes = intent.getIntExtra(Constants.Intents.MESSAGE_INTENT, -1);
        item.setVisible(messageRes == R.string.profile_under_review);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.edit_profile) {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            intent.putExtra(Constants.Intents.EDIT_PROFILE, true);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.log_out) {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.exit) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
