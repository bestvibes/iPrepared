package com.blazingphoenix.iprepared;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Vaibhav on 11/23/13.
 */
public class ExpiryFragmentDialog extends DialogFragment {

    OnExpiryDismissedListener mCallback;

    public interface OnExpiryDismissedListener {
        public Context getExpiryContext();
        public CheckBox getExpiryCheckBox();
        public ListItemClass getExpiryListItemClass();

    }

    public void setInterface(OnExpiryDismissedListener listener) {
        mCallback = listener;
    }

    ListItemClass listItem;
    Context context;
    CheckBox checkBox;
    SharedPreferences prefs;
    DatePickerDialog datePickerDialog;
    SharedPreferences.Editor editor;
    public final String PREFS_NOTIFICATION_ID = "prefsNotificationID";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        context = mCallback.getExpiryContext();
        listItem = mCallback.getExpiryListItemClass();
        checkBox = mCallback.getExpiryCheckBox();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        editor = prefs.edit();
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();

        // Create a new instance of DatePickerFragment and return it
        datePickerDialog = new DatePickerDialog(context, null, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    checkBox.setChecked(false);
                    listItem.setChecked(0);
                    new SQLiteHelper(context).updateItem(listItem, listItem.getList());
                    dialog.dismiss();
                }
            }
        });
        datePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "No Expiry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Set Expiry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int month = 0, day = 0, year = 0;
                DatePicker datePicker = datePickerDialog.getDatePicker();
                if (datePicker != null) {
                    month = datePicker.getMonth();
                    day = datePicker.getDayOfMonth();
                    year = datePicker.getYear();
                }
                String string = (month + 1) + "/" + day + "/" + year;
                listItem.setExpiry(string);

                Calendar c = Calendar.getInstance();
                c.set(year, month, day, 0, 0, 0);

                AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                int id = prefs.getInt(PREFS_NOTIFICATION_ID, 2) + 1;
                editor.putInt(PREFS_NOTIFICATION_ID, id);
                editor.commit();
                listItem.setNotificationID(id);

                Intent intent = new Intent(context, NotifyReceiver.class);
                intent.putExtra("title", "Expiry Reminder");
                intent.putExtra("description", "Expires Today: " + listItem.getName());
                intent.putExtra("list", listItem.getList());
                intent.putExtra("name", listItem.getName());
                Log.d("com.blazingphoenix.preperation.ExpiryFragmentDialog", "sent: " + id);
                intent.putExtra("id", id);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                am.set(AlarmManager.RTC, c.getTimeInMillis(), pendingIntent);

                new SQLiteHelper(context).updateItem(listItem, listItem.getList());
                Toast.makeText(context, listItem.getName() + " expires on: " + (month + 1) + "/" + day + "/" + year, Toast.LENGTH_LONG).show();
                context.sendBroadcast(new Intent(TaskList.INTENT_UPDATE));
            }
        });

        return datePickerDialog;
    }
}