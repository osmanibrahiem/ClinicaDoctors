package com.clinica.doctors.Activities.Appointments;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.clinica.doctors.Activities.Base.BaseActivity;
import com.clinica.doctors.Activities.Base.BaseFragment;
import com.clinica.doctors.Activities.Booking.Add.AddNewBookingActivity;
import com.clinica.doctors.Activities.Booking.View.ViewBookingDetailsActivity;
import com.clinica.doctors.Adapters.BookingAdapter;
import com.clinica.doctors.Models.Booking;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Constants;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AppointmentsFragment extends BaseFragment implements AppointmentsView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.weekCalendar)
    MaterialCalendarView weekCalendar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.error_container)
    ConstraintLayout errorContainer;
    @BindView(R.id.error_img)
    ImageView errorImg;
    @BindView(R.id.error_text)
    TextView errorText;
    @BindView(R.id.add_btn)
    FloatingActionButton addBtn;

    private long selectedDay = -1;

    private AppointmentsPresenter presenter;
    private BookingAdapter adapter;

    public AppointmentsFragment() {
        // Required empty public constructor
    }

    @Override
    protected int setLayoutView() {
        return R.layout.fragment_appoinments;
    }

    @Override
    protected void initViews(View view) {
        ButterKnife.bind(this, view);
        if (getActivity() != null) {
            toolbar.setTitle(R.string.appointments_title);
            ((BaseActivity) getActivity()).setSupportActionBar(toolbar);
            ((BaseActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        presenter = new AppointmentsPresenter(this, this);
    }

    @Override
    protected void initActions() {
        weekCalendar.setOnDateChangedListener(listener);
    }

    private CalendarDay calDay;

    @Override
    public void onResume() {
        super.onResume();

        if (calDay == null) {
            long day = -1;
            if (getArguments() != null) {
                day = getArguments().getLong(Constants.Intents.DAY_APPOINTMENTS, -1);
            }
            if (day == -1) {
                calDay = CalendarDay.today();
            } else {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(day);
                calDay = CalendarDay.from(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE));
            }
        }
        weekCalendar.setSelectedDate(calDay);
        weekCalendar.setCurrentDate(calDay);
        listener.onDateSelected(weekCalendar, calDay, true);
    }

    @Override
    public void onPause() {
        super.onPause();
        calDay = weekCalendar.getSelectedDate();
    }

    private OnDateSelectedListener listener = new OnDateSelectedListener() {
        @Override
        public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
            if (selected) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, date.getYear());
                cal.set(Calendar.MONTH, date.getMonth() - 1);
                cal.set(Calendar.DATE, date.getDay());
                selectedDay = cal.getTimeInMillis();

                presenter.getBookings(selectedDay);
            }
        }
    };


    @Override
    public void onGetBookings(List<Booking> bookings) {
        recyclerView.setVisibility(View.VISIBLE);
        errorContainer.setVisibility(View.GONE);

        adapter = new BookingAdapter(getActivity(), bookings);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.setCallBack(new BookingAdapter.BookingsCallBack() {
            @Override
            public void onBookingClicked(Booking booking) {
                Intent intent = new Intent(getActivity(), ViewBookingDetailsActivity.class);
                intent.putExtra(Constants.Intents.BOOKING_ID, booking.getId());
                startActivity(intent);
            }

            @Override
            public void onCheckInClicked(Booking booking) {
                presenter.CheckInBooking(booking);
            }

            @Override
            public void onAcceptClicked(Booking booking) {
                presenter.acceptBooking(booking);
            }

            @Override
            public void onCanceledClicked(Booking booking) {
                presenter.CanceledBooking(booking);
            }
        });

    }

    @Override
    public void showMessage(int img, int message) {
        errorText.setText(message);
//        errorImg.setImageResource(img);
        recyclerView.setVisibility(View.GONE);
        errorContainer.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.VISIBLE);
        errorImg.setVisibility(View.GONE);
    }

    @Override
    public void updateAddBtn(boolean isVisible) {
        if (isVisible) {
            addBtn.show();
        } else addBtn.hide();
    }

    @OnClick(R.id.add_btn)
    void addBooking() {
        Intent intent = new Intent(getActivity(), AddNewBookingActivity.class);
        intent.putExtra(Constants.Intents.DAY_APPOINTMENTS, selectedDay);
        startActivity(intent);
    }
}
