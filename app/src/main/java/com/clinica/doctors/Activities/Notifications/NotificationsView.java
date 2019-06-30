package com.clinica.doctors.Activities.Notifications;

import com.clinica.doctors.Activities.Base.BaseView;
import com.clinica.doctors.Models.NotificationModel;

import java.util.List;

interface NotificationsView extends BaseView {

    void initNotifications(List<NotificationModel> notifications);

    void showMessage(int message);
}
