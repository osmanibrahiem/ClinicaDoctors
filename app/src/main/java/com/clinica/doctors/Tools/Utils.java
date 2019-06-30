package com.clinica.doctors.Tools;

import android.content.Context;
import android.text.format.DateUtils;

import com.clinica.doctors.R;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public final class Utils {

    public static String formatNumbers(String source) {
        return source
                .replaceAll("٠", "0")
                .replaceAll("١", "1")
                .replaceAll("٢", "2")
                .replaceAll("٣", "3")
                .replaceAll("٤", "4")
                .replaceAll("٥", "5")
                .replaceAll("٦", "6")
                .replaceAll("٧", "7")
                .replaceAll("٨", "8")
                .replaceAll("٩", "9");
    }

    public static String parceDateToStringAgo(long input, Context context) {
        Date past = new Date(input);
        Date now = new Date();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
        long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
        long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
        long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());

        if (seconds < 60) {
            return context.getString(R.string.jsut_now);
        } else if (minutes < 60) {
            if (minutes <= 1) {
                return context.getString(R.string._1_minute_ago);
            } else if (minutes <= 2) {
                return context.getString(R.string._2_minute_ago);
            } else if (minutes <= 10) {
                return context.getString(R.string._3__10_minute_ago, minutes);
            } else {
                return context.getString(R.string._11_minute_ago, minutes);
            }
        } else if (hours < 24) {
            if (hours <= 1) {
                return context.getString(R.string._1_hours_ago);
            } else if (hours <= 2) {
                return context.getString(R.string._2_hours_ago);
            } else if (hours <= 10) {
                return context.getString(R.string._3_10_hours_ago, hours);
            } else {
                return context.getString(R.string._11_hours_ago, hours);
            }
        } else if (days <= 6) {
            if (days <= 1) {
                return context.getString(R.string._1_days_ago);
            } else if (days <= 2) {
                return context.getString(R.string._2_days_ago);
            } else {
                return context.getString(R.string._3_10_days_ago, days);
            }
        } else {
            return DateUtils.getRelativeTimeSpanString(past.getTime()).toString();
        }
    }

    public static int findIndex(String[] a, int target) {
        for (int i = 0; i < a.length; i++)
            if (a[i].contains(String.valueOf(target)))
                return i;
        return 0;
    }
}
