package com.clinica.doctors.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clinica.doctors.Models.Booking;
import com.clinica.doctors.Models.Consultation;
import com.clinica.doctors.Models.NotificationModel;
import com.clinica.doctors.Models.User;
import com.clinica.doctors.R;
import com.clinica.doctors.Tools.Constants;
import com.clinica.doctors.Tools.Localization;
import com.clinica.doctors.Tools.SharedTool.UserData;
import com.clinica.doctors.Tools.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationsViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<NotificationModel> notificationList;
    private DatabaseReference reference;
    private CallBack callBack;

    public NotificationsAdapter(Context context, List<NotificationModel> notificationList) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.notificationList = notificationList;
        this.reference = FirebaseDatabase.getInstance().getReference();

        Collections.sort(notificationList, new Comparator<NotificationModel>() {
            @Override
            public int compare(NotificationModel o1, NotificationModel o2) {
                return Long.compare(o2.getDate(), o1.getDate());
            }
        });
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public NotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.row_notifications_item, viewGroup, false);
        return new NotificationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationsViewHolder notificationsViewHolder, final int i) {
        final NotificationModel model = notificationList.get(i);
        String userID = model.getFrom();
        reference.child(Constants.DataBase.USERS_NODE).child(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final User user = dataSnapshot.getValue(User.class);

                        reference.child(model.getCatID()).child(model.getDestinationID())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        switch (model.getType()) {
                                            case NotificationModel.USER_REQUSETED_BOOKING:
                                            case NotificationModel.USER_CANCELED_BOOKING:
                                                Booking booking = dataSnapshot.getValue(Booking.class);
                                                notificationsViewHolder.bind(i, user, booking, null);
                                                break;
                                            case NotificationModel.USER_REQUESTED_QUESTION:
                                                Consultation consultation = dataSnapshot.getValue(Consultation.class);
                                                notificationsViewHolder.bind(i, user, null, consultation);
                                                break;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    class NotificationsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img)
        CircularImageView img;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.date)
        TextView date;

        NotificationsViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(int position, User user, final Booking booking, final Consultation consultation) {
            final NotificationModel notification = notificationList.get(position);
            String bookingDate = "", userName = "";
            if (booking != null) {
                DateFormat dfEN = new SimpleDateFormat("EEEE, dd MMMM 'at' hh:mm aa", new Locale("en"));
                DateFormat dfAR = new SimpleDateFormat("EEEE dd MMMM 'في تمام الساعة' hh:mm aa", new Locale("ar"));
                if (UserData.getLocalization(context) == Localization.ARABIC_VALUE) {
                    bookingDate = dfAR.format(new Date(booking.getDate()));
                } else {
                    bookingDate = dfEN.format(new Date(booking.getDate()));
                }
            }
            if (user != null) {
                userName = user.getDisplayName();
            }
            switch (notification.getType()) {
                case NotificationModel.USER_REQUSETED_BOOKING:
                    title.setText(context.getString(R.string.user_requested_booking, userName, bookingDate));
                    break;
                case NotificationModel.USER_CANCELED_BOOKING:
                    title.setText(context.getString(R.string.user_canceled_booking, userName));
                    break;
                case NotificationModel.USER_REQUESTED_QUESTION:
                    title.setText(context.getString(R.string.user_requested_question, userName));
                    break;
            }

            img.setImageResource(notification.getImage());
            date.setText(Utils.parceDateToStringAgo(notification.getDate(), context));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (notification.getType()) {
                        case NotificationModel.USER_REQUSETED_BOOKING:
                        case NotificationModel.USER_CANCELED_BOOKING:
                            if (callBack != null && booking != null)
                                callBack.onBookingNotification(notification.getDestinationID());
                            break;
                        case NotificationModel.USER_REQUESTED_QUESTION:
                            if (callBack != null && consultation != null)
                                callBack.onQuestionNotification(notification.getDestinationID());
                            break;
                    }
                }
            });
        }
    }

    public interface CallBack {

        void onBookingNotification(String bookingID);

        void onQuestionNotification(String questionID);
    }
}
