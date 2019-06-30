package com.clinica.doctors.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clinica.doctors.Models.Doctor.Appointment;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.SharedTool.UserData;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppointmentsChildAdapter extends RecyclerView.Adapter<AppointmentsChildAdapter.AppointmentsChildViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<Appointment> appointmentList;
    private int defaultChecked = 0;
    private int mCheckedPosition = defaultChecked;


    public AppointmentsChildAdapter(Context context, List<Appointment> appointmentList) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.appointmentList = appointmentList;
        sortList();
    }

    private void sortList() {
        Collections.sort(appointmentList, new Comparator<Appointment>() {
            @Override
            public int compare(Appointment appointment1, Appointment appointment2) {
                return Long.compare(appointment1.getTime(), appointment2.getTime());
            }
        });
        for (int i = 0; i < appointmentList.size(); i++) {
            if (TextUtils.isEmpty(appointmentList.get(i).getUserId())) {
                mCheckedPosition = i;
                defaultChecked = i;
                break;
            }
        }
    }

    @NonNull
    @Override
    public AppointmentsChildViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.row_appointments_child_item, viewGroup, false);
        return new AppointmentsChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AppointmentsChildViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    class AppointmentsChildViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.appointment)
        AppCompatCheckBox appointmentBtn;

        AppointmentsChildViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(final int position) {
            Appointment appointment = appointmentList.get(position);

            Locale locale = new Locale(UserData.getLocalizationString(context));
            SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", locale);
            appointmentBtn.setText(sdf.format(new Date(appointment.getTime())));

            final boolean isChecked = position == mCheckedPosition;
            appointmentBtn.setChecked(isChecked);
            appointmentBtn.setActivated(isChecked);
            appointmentBtn.setEnabled(TextUtils.isEmpty(appointment.getUserId()));

            appointmentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCheckedPosition = isChecked ? defaultChecked : position;
                    notifyDataSetChanged();
                }
            });
        }

    }

    public Appointment getSelectedAppointment() {
        if (appointmentList.size() > 0)
            return appointmentList.get(mCheckedPosition);
        return null;
    }
}
