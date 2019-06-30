package com.clinica.doctors.Tools;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.Calendar;

public class DateInputMask implements TextWatcher {

    private String current = "";
    private String ddmmyyyy = "DDMMYYYY";
    private EditText input;

    public DateInputMask(EditText input) {
        this.input = input;
//        this.input.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().equals(current)) {
            return;
        }

        String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
        String cleanC = current.replaceAll("[^\\d.]|\\.", "");

        int cl = clean.length();
        int sel = cl;
        for (int i = 2; i <= cl && i < 6; i += 2) {
            sel++;
        }
        //Fix for pressing delete next to a forward slash
        if (clean.equals(cleanC)) sel--;

        if (clean.length() < 8) {
            clean = clean + ddmmyyyy.substring(clean.length());
        } else {
            //This part makes sure that when we finish entering numbers
            //the date is correct, fixing it otherwise
            Calendar cal = Calendar.getInstance();
            int day = Integer.parseInt(clean.substring(0, 2));
            int mon = Integer.parseInt(clean.substring(2, 4));
            int year = Integer.parseInt(clean.substring(4, 8));


            year = (year < 1900) ? 1900 : (year > cal.get(Calendar.YEAR) - 5) ? cal.get(Calendar.YEAR) - 5 : year;
//            mon = (mon < 1) ? 1 : ((mon > 12) ? 12 : (((year == todayCal.get(Calendar.YEAR)) ? (todayCal.get(Calendar.MONTH) + 1) : mon)));
            mon = (mon < 1) ? 1 : ((mon > 12) ? 12 : mon);
            cal.set(Calendar.MONTH, mon - 1);
            cal.set(Calendar.YEAR, year);

//            day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : ((day < cal.getActualMinimum(Calendar.DATE)) ? cal.getActualMinimum(Calendar.DATE) :
//                    (((year == todayCal.get(Calendar.YEAR)) && (mon == todayCal.get(Calendar.MONTH) + 1)) ? (todayCal.get(Calendar.DATE)) : day));
            day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : ((day < cal.getActualMinimum(Calendar.DATE)) ? cal.getActualMinimum(Calendar.DATE) : day);
            clean = String.format("%02d%02d%02d", day, mon, year);
        }

        clean = String.format("%s/%s/%s", clean.substring(0, 2),
                clean.substring(2, 4),
                clean.substring(4, 8));

        sel = sel < 0 ? 0 : sel;
        current = Utils.formatNumbers(clean);
        input.setText(current);
        input.setSelection(sel < current.length() ? sel : current.length());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}