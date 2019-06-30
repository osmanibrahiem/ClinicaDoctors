package com.clinica.doctors.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clinica.doctors.Activities.Question.QuestionActivity;
import com.clinica.doctors.Models.Consultation;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Constants;
import com.clinica.doctors.Tools.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConsultationAdapter extends RecyclerView.Adapter<ConsultationAdapter.ConsultationViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private int type;
    private List<Consultation> consultationList;

    public ConsultationAdapter(Context context, int type) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.type = type;
        this.consultationList = new ArrayList<>();
    }

    public void appendData(List<Consultation> consultationList) {
        this.consultationList.addAll(consultationList);
        sortList();
    }

    public void addConsultation(Consultation consultation) {
        if (consultation.getStatus().equals(Constants.ACTIVE_STATUS) && !TextUtils.isEmpty(consultation.getAnswer())) {
            consultationList.add(consultation);
            sortList();
        }
    }

    public void removeConsultation(Consultation consultation) {
        for (int i = 0; i < consultationList.size(); i++) {
            if (consultationList.get(i).getId().equals(consultation.getId())) {
                consultationList.remove(i);
                sortList();
            }
        }
    }

    public void updateConsultation(Consultation consultation) {
        if (consultation.getStatus().equals(Constants.ACTIVE_STATUS)) {
            boolean isInList = false;
            for (int i = 0; i < consultationList.size(); i++) {
                if (consultationList.get(i).getId().equals(consultation.getId())) {
                    isInList = true;
                    if (!TextUtils.isEmpty(consultation.getAnswer())) {
                        consultationList.set(i, consultation);
                        sortList();
                    } else removeConsultation(consultation);
                }
            }
            if (!isInList) {
                addConsultation(consultation);
            }
        } else {
            removeConsultation(consultation);
        }
    }

    private void sortList() {
        Collections.sort(consultationList, new Comparator<Consultation>() {
            @Override
            public int compare(Consultation consultation1, Consultation consultation2) {
                return Long.compare(consultation2.getAnswerDate(), consultation1.getAnswerDate());
            }
        });
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ConsultationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.row_question_item, viewGroup, false);
        return new ConsultationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ConsultationViewHolder holder, int position) {
        holder.setData(consultationList.get(position));
    }

    @Override
    public int getItemCount() {
        if (type == Constants.LIST_CONSULTATIONS_VIEW)
            return consultationList.size();
        else if (type == Constants.HOME_CONSULTATIONS_VIEW) {
            return (consultationList.size() >= Constants.LAST_QUESTIONS_COUNT ? Constants.LAST_QUESTIONS_COUNT : consultationList.size());
        }
        return 0;
    }

    class ConsultationViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.question_title)
        TextView questionTitleTV;
        //        @BindView(R.id.view_answer)
//        AppCompatButton viewAnswerBtn;
        @BindView(R.id.answer_date)
        TextView answerDateTV;

        ConsultationViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setData(final Consultation consultation) {
            questionTitleTV.setText(consultation.getQuestion());
            answerDateTV.setText(Utils.parceDateToStringAgo(consultation.getQuestionDate(), context));

//            viewAnswerBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(context, QuestionActivity.class);
//                    intent.putExtra(Constants.Intents.QUESTION_ID, consultation.getId());
//                    context.startActivity(intent);
//                }
//            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, QuestionActivity.class);
                    intent.putExtra(Constants.Intents.QUESTION_ID, consultation.getId());
                    context.startActivity(intent);
                }
            });
        }
    }

}
