package com.clinica.doctors.Activities.Base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clinica.doctors.Tools.ProgressDialogFragment;
import com.clinica.doctors.Tools.ToastTool;

public abstract class BaseFragment extends Fragment implements BaseView {

    private String PROGRESS_DIALOG = "ProgressDialog";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(setLayoutView(), container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        initActions();
    }

    @Override
    public void showLoading() {
        ProgressDialogFragment f = ProgressDialogFragment.getInstance();
        getChildFragmentManager()
                .beginTransaction()
                .add(f, PROGRESS_DIALOG)
                .commitAllowingStateLoss();
    }

    @Override
    public void hideLoading() {
        try {
            FragmentManager manager = getChildFragmentManager();
            if (manager == null) return;
            ProgressDialogFragment f = (ProgressDialogFragment) manager.findFragmentByTag(PROGRESS_DIALOG);
            if (f != null) {
                manager.beginTransaction().remove(f).commitAllowingStateLoss();
            }
        } catch (Exception ex) {

        }
    }

    @Override
    public void showNetWorkError(int message) {
        ToastTool.with(getActivity(), message).show();
    }

    @LayoutRes
    protected abstract int setLayoutView();

    protected abstract void initViews(View view);

    protected abstract void initActions();
}
