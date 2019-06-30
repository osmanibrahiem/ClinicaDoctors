package com.clinica.doctors.Activities.Notifications;

import android.content.Intent;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.clinica.doctors.Activities.Base.BaseActivity;
import com.clinica.doctors.Activities.Base.BaseFragment;
import com.clinica.doctors.Activities.Booking.View.ViewBookingDetailsActivity;
import com.clinica.doctors.Activities.Question.QuestionActivity;
import com.clinica.doctors.Adapters.NotificationsAdapter;
import com.clinica.doctors.Models.NotificationModel;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationsFragment extends BaseFragment implements NotificationsView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.empty)
    AppCompatTextView empty;

    private NotificationsPresenter presenter;
    private NotificationsAdapter adapter;

    public NotificationsFragment() {
    }

    @Override
    protected int setLayoutView() {
        return R.layout.layout_recycler;
    }

    @Override
    protected void initViews(View view) {
        ButterKnife.bind(this, view);
        if (getActivity() != null) {
            toolbar.setTitle(R.string.notifications_title);
            ((BaseActivity) getActivity()).setSupportActionBar(toolbar);
            ((BaseActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        presenter = new NotificationsPresenter(this, this);
    }

    @Override
    protected void initActions() {
        presenter.getNotifications();
    }

    @Override
    public void initNotifications(List<NotificationModel> notifications) {
        recycler.setVisibility(View.VISIBLE);
        empty.setVisibility(View.GONE);

        adapter = new NotificationsAdapter(getActivity(), notifications);
        recycler.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(llm);
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(adapter);
        adapter.setCallBack(new NotificationsAdapter.CallBack() {
            @Override
            public void onBookingNotification(String bookingID) {
                Intent intent = new Intent(getActivity(), ViewBookingDetailsActivity.class);
                intent.putExtra(Constants.Intents.BOOKING_ID, bookingID);
                startActivity(intent);
            }

            @Override
            public void onQuestionNotification(String questionID) {
                Intent intent = new Intent(getActivity(), QuestionActivity.class);
                intent.putExtra(Constants.Intents.QUESTION_ID, questionID);
                startActivity(intent);
            }
        });

    }

    @Override
    public void showMessage(int message) {
        empty.setText(message);
        recycler.setVisibility(View.GONE);
        empty.setVisibility(View.VISIBLE);
    }
}
