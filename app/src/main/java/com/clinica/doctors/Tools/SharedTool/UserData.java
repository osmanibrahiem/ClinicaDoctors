package com.clinica.doctors.Tools.SharedTool;

import android.content.Context;

import com.clinica.doctors.Tools.Localization;

public class UserData {

    public static String TAG_LOCALIZATION = "_localization";
    public static String TAG_NOTIFICATION_STATE = "_notification_state";


    public static String TAG_INSERT_TOKEN = "insert_token";
    public static Boolean TAG_TOKEN_INSERT_STATE = false;


    public static void saveLocalization(Context context, int value) {
        SharedPreferencesTool.setInt(context, TAG_LOCALIZATION, value);
    }

    public static int getLocalization(Context context) {
        return SharedPreferencesTool.getInt(context, TAG_LOCALIZATION);
    }

    public static String getLocalizationString(Context context) {
        if (getLocalization(context) == Localization.ARABIC_VALUE)
            return "ar";
        else return "en";
    }

    public static void saveNotificationState(Context context, boolean value) {
        SharedPreferencesTool.setBoolean(context, value, TAG_NOTIFICATION_STATE);
    }

    public static boolean getNotificationState(Context context) {
        return SharedPreferencesTool.getBooleanlang(context, TAG_NOTIFICATION_STATE);
    }

    public static void saveUserStateOfInsertToken(Context context, boolean state, String value) {
        SharedPreferencesTool.setBoolean(context, state, value);
    }

    public static Boolean getUserStateOfInsertToken(Context context) {
        return SharedPreferencesTool.getBoolean(context, TAG_INSERT_TOKEN);
    }

    public static void clearShared(Context context) {
        SharedPreferencesTool.clearObject(context);
    }
}
