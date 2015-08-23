package com.blazingphoenix.iprepared;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class NotifyReceiver extends BroadcastReceiver {

    public String PREFS_NOTIFICATION_ID = "prefsNotificationID";
    // The system notification manager
    private NotificationManager mNM;
    //Context
    private Context context;
    private String title;
    private String description;
    private String list;
    private String name;

    @Override
    public void onReceive(Context ctx, Intent intent) {
        mNM = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            title = bundle.getString("title");
            description = bundle.getString("description");
            list = bundle.getString("list");
            name = bundle.getString("name");
            context = ctx;
            int id = bundle.getInt("id", 0);
            if (id == 0) {
                id = 123098;
            } else {
                uncheckItem();
            }
            showNotification(id);
        }
    }

    /**
     * Creates a notification and shows it in the OS drag-down status bar
     */
    private void showNotification(int id) {

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);

        Notification noti = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(description)
                .setStyle(new Notification.BigTextStyle().bigText(description))
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(contentIntent).build();

        // Clear the notification when it is pressed
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        // Send the notification to the system.
        Log.d("com.blazingphoenix.preperation.NotifyReceiver", "New Notification ID: " + id);
        mNM.notify(id, noti);
    }

    private void uncheckItem() {
        SQLiteHelper sqLiteHelper = new SQLiteHelper(context);
        ListItemClass item = sqLiteHelper.getItemByName(name, list);
        item.setChecked(0);
        item.setExpiry(null);
        sqLiteHelper.updateItem(item, list);
        context.sendBroadcast(new Intent(TaskList.INTENT_UPDATE));
    }
}