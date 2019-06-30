package com.clinica.doctors.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clinica.doctors.Models.Doctor.Appointment;
import com.clinica.doctors.Models.Doctor.DayAppointments;
import com.clinica.doctors.Models.Doctor.WorkingDayHours;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Constants;
import com.clinica.doctors.Tools.DateUtils;
import com.clinica.doctors.Tools.Localization;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AvailabilityAdapter extends RecyclerView.Adapter<AvailabilityAdapter.AvailabilityViewHolder> {

    private LayoutInflater inflater;
    private Context context;
    private List<DayAppointments> dayList;
    private List<WorkingDayHours> workingDayHours;
    private DayCallBack dayCallBack;

    public AvailabilityAdapter(Context context, List<DayAppointments> dayList, List<WorkingDayHours> workingDayHours) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.dayList = new ArrayList<>();
        this.workingDayHours = workingDayHours;
        if (dayList != null)
            addToList(dayList);
    }

    public void setDayCallBack(DayCallBack dayCallBack) {
        this.dayCallBack = dayCallBack;
    }

    private void addToList(List<DayAppointments> dayList) {
        for (DayAppointments dayAppointments : dayList) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(dayAppointments.getTitle());
            if ((!DateUtils.isBeforeDay(cal, Calendar.getInstance()))) {
                this.dayList.add(dayAppointments);
            }
        }
        notifyItemRangeInserted(0, this.dayList.size());
        sortList();
    }

    public void addDays(List<Long> newDayList) {
        int index = this.dayList.size();
        for (int i = 0; i < newDayList.size(); i++) {
            if (!isDayInList(newDayList.get(i))) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(newDayList.get(i));
                WorkingDayHours dayHours = getWorkingDay(cal.get(Calendar.DAY_OF_WEEK));

                if (dayHours != null && dayHours.isWorking() && (!DateUtils.isBeforeDay(cal, Calendar.getInstance()))) {
                    DayAppointments dayAppointments = new DayAppointments();
                    List<Appointment> appointmentList = new ArrayList<>();

                    Calendar startTimeCal = Calendar.getInstance();
                    startTimeCal.setTimeInMillis(dayHours.getStartTime());
                    Calendar endTimeCal = Calendar.getInstance();
                    endTimeCal.setTimeInMillis(dayHours.getEndTime());
                    while (startTimeCal.before(endTimeCal)) {
                        Appointment appointment = new Appointment();
                        appointment.setStatus(Constants.ACTIVE_STATUS);
                        appointment.setTime(startTimeCal.getTimeInMillis());
                        appointmentList.add(appointment);

                        startTimeCal.add(Calendar.MINUTE, dayHours.getDuration());
                    }
                    dayAppointments.setAppointments(appointmentList);
                    dayAppointments.setTitle(cal.getTimeInMillis());
                    dayList.add(dayAppointments);
                }
            }
        }
        notifyItemRangeInserted(index, this.dayList.size());
        sortList();
    }

    private void sortList() {
        Collections.sort(dayList, new Comparator<DayAppointments>() {
            @Override
            public int compare(DayAppointments o1, DayAppointments o2) {
                return Long.compare(o1.getTitle(), o2.getTitle());
            }
        });
        notifyItemRangeChanged(0, dayList.size());
    }

    private boolean isDayInList(Long day) {
        for (DayAppointments dayAppointments : dayList) {
            if (DateUtils.isSameDay(new Date(dayAppointments.getTitle()), new Date(day)))
                return true;
        }
        return false;
    }

    @NonNull
    @Override
    public AvailabilityViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = inflater.inflate(R.layout.row_availability_item, viewGroup, false);
        return new AvailabilityViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AvailabilityViewHolder availabilityViewHolder, int position) {
        availabilityViewHolder.bind(position);
    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }

    class AvailabilityViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.day_title)
        TextView dayTitle;
        @BindView(R.id.sw_container)
        LinearLayout swContainer;
        @BindView(R.id.appointments_count)
        TextView appointmentsCount;
        @BindView(R.id.day_sw)
        SwitchCompat daySw;
        @BindView(R.id.loading)
        FrameLayout loading;

        AvailabilityViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(final int position) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(dayList.get(position).getTitle());

            daySw.setChecked(dayList.get(position).isConfirmed());

            if (dayList.get(position).isConfirmed() && dayList.get(position).getAppointments() != null) {
                int count = 0;
                for (Appointment appointment : dayList.get(position).getAppointments()) {
                    if (!TextUtils.isEmpty(appointment.getUserId())) {
                        count++;
                    }
                }
                appointmentsCount.setText(context.getString(R.string.booking_count, count));
            } else appointmentsCount.setText(context.getString(R.string.booking_count, 0));

            DateFormat df = new SimpleDateFormat("EEEE'\n'dd MMMM", Localization.getCurrentLocale(context));
            DateFormat dfDay = new SimpleDateFormat("'\n'dd MMMM", Localization.getCurrentLocale(context));
            if (DateUtils.isToday(cal)) {
                dayTitle.setText(String.format("%1$s%2$s", context.getString(R.string.today), dfDay.format(cal.getTime())));
            } else if (DateUtils.isTomorrow(cal)) {
                dayTitle.setText(String.format("%1$s%2$s", context.getString(R.string.tomorrow), dfDay.format(cal.getTime())));
            } else {
                dayTitle.setText(df.format(cal.getTime()));
            }

            daySw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    dayList.get(position).setConfirmed(isChecked);
                    if (dayCallBack != null)
                        dayCallBack.onCheckChange(dayList.get(position), isChecked);
                }
            });

            appointmentsCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dayCallBack != null)
                        dayCallBack.onDayClicked(dayList.get(position));
                }
            });
        }
    }

    private WorkingDayHours getWorkingDay(int day) {
        for (WorkingDayHours dayHours : workingDayHours) {
            if (dayHours.getDayOfWeek() == day)
                return dayHours;
        }
        return null;
    }

    public interface DayCallBack {

        void onCheckChange(DayAppointments dayAppointments, boolean isConfirmed);

        void onDayClicked(DayAppointments dayAppointments);
    }

    public void clear() {
        dayList.clear();
        notifyDataSetChanged();
    }
}
