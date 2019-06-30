package com.clinica.doctors.Activities.ConfirmAttendance;

import com.clinica.doctors.Activities.Base.BaseView;
import com.clinica.doctors.Models.Doctor.Doctor;

public interface ConfirmAttendanceView extends BaseView {

    void initData(Doctor doctor);

    void toastError(String message);
}
