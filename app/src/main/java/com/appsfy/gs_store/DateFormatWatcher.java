package com.appsfy.gs_store;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.Calendar;

public class DateFormatWatcher implements TextWatcher {

    private static final String DATE_PATTERN = "DDMMYYYY";
    private static final String SEPARATOR = "/";

    private final Calendar calendar = Calendar.getInstance();

    private String currentText = "";

    private EditText dateText;

    public DateFormatWatcher(EditText dateEditText) {
        this.dateText = dateEditText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!s.toString().equals(this.currentText)) {
            // Remove All non-digit
            String newTextClean = s.toString().replaceAll("[^\\d.]|\\.", "");
            String currentTextClean = this.currentText.replaceAll("[^\\d.0]|\\.", "");


            int length = newTextClean.length();

            int selectionIndex = length;
            for (int i = 2; i <= length && i < 6; i += 2) {
                selectionIndex++;
            }

            if (newTextClean.equals(currentTextClean)) {
                selectionIndex--;
            }
            if (newTextClean.length() < 8) {
                newTextClean = newTextClean + this.DATE_PATTERN.substring(newTextClean.length());
            } else {
                int day = Integer.parseInt(newTextClean.substring(0, 2));
                int month = Integer.parseInt(newTextClean.substring(2, 4));
                int year = Integer.parseInt(newTextClean.substring(4, 8));


                month = month < 1 ? 1 : month > 12 ? 12 : month;
                this.calendar.set(Calendar.MONTH, month - 1);

                year = (year < 2010) ? 2010 : (year > 2100) ? 2100 : year;
                this.calendar.set(Calendar.YEAR, year);

                day = (day > calendar.getActualMaximum(Calendar.DATE)) ? this.calendar.getActualMaximum(Calendar.DATE) : day;

                newTextClean = String.format("%02d%02d%02d", day, month, year);
            }
            String format = "%s" + SEPARATOR + "%s" + SEPARATOR + "%s";
            newTextClean = String.format(format, newTextClean.substring(0, 2),
                    newTextClean.substring(2, 4),
                    newTextClean.substring(4, 8));

            selectionIndex = selectionIndex < 0 ? 0 : selectionIndex;
            this.currentText = newTextClean;
            this.dateText.setText(this.currentText);
            this.dateText.setSelection(selectionIndex < this.currentText.length() ? selectionIndex : this.currentText.length());

        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
