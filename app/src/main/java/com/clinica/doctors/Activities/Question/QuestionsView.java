package com.clinica.doctors.Activities.Question;

import com.clinica.doctors.Activities.Base.BaseView;
import com.clinica.doctors.Models.Consultation;
import com.clinica.doctors.Models.Doctor.Doctor;
import com.clinica.doctors.Models.User;

interface QuestionsView extends BaseView {

    void showMessage(int message);

    void initConsultation(Consultation consultation);

    void initUser(User user);

    void initInputNewAnswer();

    void initDoctor(Doctor doctor);

    void toastError(int message);
}
