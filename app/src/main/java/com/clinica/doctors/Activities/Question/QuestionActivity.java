package com.clinica.doctors.Activities.Question;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.clinica.doctors.Activities.Base.BaseActivity;
import com.clinica.doctors.Models.Consultation;
import com.clinica.doctors.Models.Doctor.Doctor;
import com.clinica.doctors.Models.User;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Constants;
import com.clinica.doctors.Tools.Localization;
import com.clinica.doctors.Tools.SharedTool.UserData;
import com.clinica.doctors.Tools.ToastTool;
import com.clinica.doctors.Tools.Utils;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuestionActivity extends BaseActivity implements QuestionsView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.main_container)
    NestedScrollView mainContainer;
    @BindView(R.id.img_patient)
    CircularImageView imgPatient;
    @BindView(R.id.patient_name)
    TextView patientName;
    @BindView(R.id.question_date)
    TextView questionDate;
    @BindView(R.id.question_text)
    TextView questionText;
    @BindView(R.id.question_image)
    ImageView questionImage;
    @BindView(R.id.answer_section)
    ConstraintLayout answerSection;
    @BindView(R.id.img_doctor)
    CircularImageView imgDoctor;
    @BindView(R.id.doctor_name)
    TextView doctorName;
    @BindView(R.id.professional_title)
    TextView professionalTitle;
    @BindView(R.id.answer_date)
    TextView answerDate;
    @BindView(R.id.answer_text)
    TextView answerText;
    @BindView(R.id.input_answer_section)
    ConstraintLayout inputAnswerSection;
    @BindView(R.id.input_answer)
    AppCompatEditText inputAnswer;
    @BindView(R.id.send_btn)
    AppCompatButton sendBtn;
    @BindView(R.id.empty)
    TextView empty;

    private QuestionsPresenter presenter;

    @Override
    protected int setLayoutView() {
        return R.layout.activity_question;
    }

    @Override
    protected void initViews() {
        ButterKnife.bind(this);
        toolbar.setTitle(R.string.questions_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        presenter = new QuestionsPresenter(this, this);
    }

    @Override
    protected void initActions() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.Intents.QUESTION_ID)) {
            presenter.getQuestion(intent.getStringExtra(Constants.Intents.QUESTION_ID));
        }
    }

    @Override
    public void showMessage(int message) {
        mainContainer.setVisibility(View.GONE);
        empty.setText(message);
        empty.setVisibility(View.VISIBLE);
    }

    @Override
    public void initConsultation(Consultation consultation) {
        mainContainer.setVisibility(View.VISIBLE);
        empty.setVisibility(View.GONE);

        questionDate.setText(Utils.parceDateToStringAgo(consultation.getQuestionDate(), this));
        questionText.setText(consultation.getQuestion());
        if (!TextUtils.isEmpty(consultation.getImage())) {
            questionImage.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(consultation.getImage())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error_placeholder)
                    .into(questionImage);
        } else {
            questionImage.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(consultation.getAnswerPublisherID())) {
            answerSection.setVisibility(View.VISIBLE);
            inputAnswerSection.setVisibility(View.GONE);

            answerText.setText(consultation.getAnswer());
            answerDate.setText(Utils.parceDateToStringAgo(consultation.getAnswerDate(), this));
        } else {
            initInputNewAnswer();
        }
    }

    @Override
    public void initUser(User user) {
        if (!TextUtils.isEmpty(user.getPhotoUrl())) {
            Picasso.get()
                    .load(user.getPhotoUrl())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error_placeholder)
                    .into(imgPatient);
        }

        patientName.setText(user.getDisplayName());
    }

    @Override
    public void initInputNewAnswer() {
        answerSection.setVisibility(View.GONE);
        inputAnswerSection.setVisibility(View.VISIBLE);

        inputAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(inputAnswer.getText().toString().trim())) {
                    sendBtn.setEnabled(false);
                } else sendBtn.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.sendAnswer();
            }
        });
    }

    @Override
    public void initDoctor(Doctor doctor) {
        if (!TextUtils.isEmpty(doctor.getProfilePhotoUrl())) {
            Picasso.get()
                    .load(doctor.getProfilePhotoUrl())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error_placeholder)
                    .into(imgDoctor);
        }

        if (UserData.getLocalization(this) == Localization.ARABIC_VALUE) {
            doctorName.setText(doctor.getBasicInformationAr().getDisplayName());
            professionalTitle.setText(doctor.getBasicInformationAr().getProfessionalTitle());
        } else {
            doctorName.setText(doctor.getBasicInformationEN().getDisplayName());
            professionalTitle.setText(doctor.getBasicInformationEN().getProfessionalTitle());
        }
    }

    @Override
    public void toastError(int message) {
        ToastTool.with(this, message).show();
    }
}
