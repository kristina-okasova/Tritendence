package com.example.tritendence.model;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePicker extends DialogFragment {
    private int hour = -1, minute = -1;

    public TimePicker() {}

    public TimePicker(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (hour == -1 || minute == -1) {
            Calendar calendar = Calendar.getInstance();
            this.hour = calendar.get(Calendar.HOUR_OF_DAY);
            this.minute = calendar.get(Calendar.MINUTE);
        }

        return new TimePickerDialog(getActivity(), AlertDialog.THEME_HOLO_DARK, (TimePickerDialog.OnTimeSetListener) getActivity(), this.hour, this.minute, DateFormat.is24HourFormat(getActivity()));
    }
}
