package com.blazingphoenix.iprepared;

import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Vaibhav on 11/11/13.
 */
public class DrillReminder extends Fragment implements DatePickerFragment.OnDatePickerDismissedListener, TimePickerFragment.OnTimeDismissedListener {

    TextView timeTextView;
    TextView dateTextView;
    Button timeButton;
    Button dateButton;
    Context context;
    static int year, month = 99, day, hour = 99, minute;

    public DrillReminder() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        View rootView = inflater.inflate(R.layout.drill_fragment, container, false);
        timeButton = (Button) rootView.findViewById(R.id.drillTimeButton);
        dateButton = (Button) rootView.findViewById(R.id.drillDateButton);
        timeTextView = (TextView) rootView.findViewById(R.id.drillTimeTextView);
        dateTextView = (TextView) rootView.findViewById(R.id.drillDateTextView);
        Button button = (Button) rootView.findViewById(R.id.drillSetButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAlarm();
            }
        });
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate();
            }
        });
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime();
            }
        });
        return rootView;
    }

    public void setAlarm() {

        if (hour != 99 && month != 99) {

            Calendar c = Calendar.getInstance();
            c.set(year, month, day, hour, minute, 0);

            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(context, NotifyReceiver.class);
            intent.putExtra("title", "Disaster Preparedness Reminder!");
            intent.putExtra("description", "Remember to have a practice disaster drill!");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            am.set(AlarmManager.RTC, c.getTimeInMillis(), pendingIntent);

            Toast.makeText(context, "Set reminder for: " + (month + 1) + "/" + day + "/" + year, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Date or Time is not selected!", Toast.LENGTH_SHORT).show();
        }

    }

    public void setDate() {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.setCancelable(false);
        datePicker.setTargetFragment(this, 0);
        datePicker.show(getFragmentManager(), "datePicker");
    }

    public void setTime() {
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.setCancelable(false);
        timePicker.setTargetFragment(this, 0);
        timePicker.show(getFragmentManager(), "timePicker");
    }

    public void onDatePickerDismissed(String string) {
        dateTextView.setText(string);
    }

    public void onTimePickerDismissed(String string) {
        timeTextView.setText(string);
    }

}