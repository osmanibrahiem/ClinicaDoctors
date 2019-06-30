package com.clinica.doctors.Adapters;

import android.app.TimePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.clinica.doctors.Models.Doctor.WorkingDayHours;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Localization;
import com.clinica.doctors.Tools.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WorkingHoursAdapter extends RecyclerView.Adapter<WorkingHoursAdapter.WorkingHoursViewHolder> {

    private LayoutInflater inflater;
    private Context context;
    private List<WorkingDayHours> workingHours;

    public WorkingHoursAdapter(Context context, List<WorkingDayHours> workingHours) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.workingHours = workingHours;
    }

    @NonNull
    @Override
    public WorkingHoursViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = inflater.inflate(R.layout.row_working_day_hours, viewGroup, false);
        return new WorkingHoursViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkingHoursViewHolder workingHoursViewHolder, int i) {
        workingHoursViewHolder.bind(i);
    }

    @Override
    public int getItemCount() {
        return ((workingHours.size() > 7) ? 7 : workingHours.size());
    }

    class WorkingHoursViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.start_time)
        TextView startTime;
        @BindView(R.id.dil)
        TextView dil;
        @BindView(R.id.end_time)
        TextView endTime;
        @BindView(R.id.duration)
        AppCompatSpinner duration;
        @BindView(R.id.working)
        SwitchCompat working;
        @BindView(R.id.error)
        TextView error;

        WorkingHoursViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
//            this.setIsRecyclable(false);
        }

        void bind(final int position) {

            if (position == 0) {
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) itemView.getLayoutParams();
                params.setMargins(0, 0, 0, 0);
                itemView.setLayoutParams(params);
            }
            Calendar cal = Calendar.getInstance();
            cal.setFirstDayOfWeek(Calendar.SATURDAY);
            cal.set(Calendar.DAY_OF_WEEK, workingHours.get(position).getDayOfWeek());
            String dayName = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Localization.getCurrentLocale(context));
            working.setText(dayName);

            final DateFormat df = new SimpleDateFormat("hh:mm aa", Localization.getCurrentLocale(context));
            final String[] ITEMS = context.getResources().getStringArray(R.array.examination_duration);

            working.setChecked(workingHours.get(position).isWorking());
            if (workingHours.get(position).isWorking()) {
                startTime.setText(df.format(new Date(workingHours.get(position).getStartTime())));
                endTime.setText(df.format(new Date(workingHours.get(position).getEndTime())));
                duration.setSelection(Utils.findIndex(ITEMS, workingHours.get(position).getDuration()));
                startTime.setVisibility(View.VISIBLE);
                dil.setVisibility(View.VISIBLE);
                endTime.setVisibility(View.VISIBLE);
                duration.setVisibility(View.VISIBLE);
            } else {
                startTime.setVisibility(View.INVISIBLE);
                dil.setVisibility(View.INVISIBLE);
                endTime.setVisibility(View.INVISIBLE);
                duration.setVisibility(View.INVISIBLE);
            }

            startTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(workingHours.get(position).getStartTime());
                    int mHour = c.get(Calendar.HOUR_OF_DAY);
                    int mMinute = c.get(Calendar.MINUTE);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    c.set(Calendar.MINUTE, minute);
                                    workingHours.get(position).setStartTime(c.getTimeInMillis());
                                    startTime.setText(df.format(c.getTimeInMillis()));
                                }
                            }, mHour, mMinute, false);
                    timePickerDialog.show();
                }
            });

            endTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(workingHours.get(position).getEndTime());
                    int mHour = c.get(Calendar.HOUR_OF_DAY);
                    int mMinute = c.get(Calendar.MINUTE);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                    c.set(Calendar.MINUTE, minute);
                                    workingHours.get(position).setEndTime(c.getTimeInMillis());
                                    endTime.setText(df.format(c.getTimeInMillis()));
                                }
                            }, mHour, mMinute, false);
                    timePickerDialog.show();
                }
            });

            duration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int i, long id) {
                    String number = ITEMS[i].replaceAll("[^0-9]", "");
                    try {
                        workingHours.get(position).setDuration(Integer.parseInt(number));
                    } catch (NumberFormatException e) {
                        workingHours.get(position).setDuration(-1);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            working.setOnCheckedChangeListener(null);

            working.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    workingHours.get(position).setWorking(isChecked);

                    startTime.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
                    dil.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
                    endTime.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
                    duration.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
                }
            });

            if (!TextUtils.isEmpty(workingHours.get(position).getError())) {
                error.setText(workingHours.get(position).getError());
                error.setVisibility(View.VISIBLE);
            } else {
                error.setText("");
                error.setVisibility(View.GONE);
            }
        }
    }
}
