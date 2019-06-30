package com.clinica.doctors.Activities.Base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.clinica.doctors.Tools.ProgressDialogFragment;
import com.clinica.doctors.Tools.ToastTool;

public abstract class BaseActivity extends AppCompatActivity implements BaseView {

    private String PROGRESS_DIALOG = "ProgressDialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayoutView());

        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initActions();
    }

    @Override
    public void showLoading() {
        ProgressDialogFragment f = ProgressDialogFragment.getInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(f, PROGRESS_DIALOG)
                .commitAllowingStateLoss();
    }

    @Override
    public void hideLoading() {
        try {
            FragmentManager manager = getSupportFragmentManager();
            if (manager == null) return;
            ProgressDialogFragment f = (ProgressDialogFragment) manager.findFragmentByTag(PROGRESS_DIALOG);
            if (f != null) {
                manager.beginTransaction().remove(f).commitAllowingStateLoss();
            }
        } catch (Exception ignored) {

        }
    }

    @Override
    public void showNetWorkError(int message) {
        ToastTool.with(this, message).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @LayoutRes
    protected abstract int setLayoutView();

    protected abstract void initViews();

    protected abstract void initActions();
}
