package com.clinica.doctors.Tools;

import android.content.Context;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.clinica.doctors.R;


public class ToastTool {

    private static Context mContext;
    private static String mText = "";
    private LayoutInflater inflater;
    private static int mGravity = Gravity.BOTTOM;
    private static Toast toast;

    public ToastTool(Context context) {
        mContext = context;
        inflater = LayoutInflater.from(context);
    }

    public static ToastTool with(Context context, String text) {
        mText = text;
        return new ToastTool(context);
    }

    public static ToastTool with(Context context, String text, int gravity) {
        setGravity(gravity);
        return with(context, text);
    }

    public static ToastTool with(Context context, @StringRes int text) {
        return with(context, context.getString(text));
    }

    public static ToastTool with(Context context, @StringRes int text, int gravity) {
        return with(context, context.getString(text), gravity);
    }

    public void setText(String text) {
        mText = text;
    }

    public void setText(@StringRes int text) {
        setText(mContext.getString(text));
    }

    public static void setGravity(int gravity) {
        mGravity = gravity;
    }

    public void show() {
        View layout = inflater.inflate(R.layout.layout_custom_toast, null);

        TextView text = layout.findViewById(R.id.text);
        text.setText(mText);
        if (toast != null)
            toast.cancel();

        toast = new Toast(mContext);
        toast.setGravity(mGravity, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();

        mText = "";
        mGravity = Gravity.BOTTOM;
    }
}
