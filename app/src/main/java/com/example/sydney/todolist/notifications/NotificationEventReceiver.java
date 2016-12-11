package com.example.sydney.todolist.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class NotificationEventReceiver extends WakefulBroadcastReceiver {

    private static final String ACTION_START_NOTIFICATION_SERVICE = "ACTION_START_NOTIFICATION_SERVICE";
    private static final String ACTION_DELETE_NOTIFICATION = "ACTION_DELETE_NOTIFICATION";

    //Setup Alarm for notifications
    public static void setupAlarm(Context context, String name, String id, Calendar calendar) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE); //Initialize alarm manager
        PendingIntent alarmIntent = getStartPendingIntent(context,name,id); //Setup a pending intent from the name and id of event
        alarmIntent.getIntentSender(); //Retrieve a IntentSender object that wraps the existing sender of the PendingIntent
        //long time= System.currentTimeMillis() + 5000;
        long time = calendar.getTimeInMillis(); //Convert calendar to milli seconds
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, alarmIntent); //Setup alarm
    }

    //Cancel Alarm for notifications
    public static void cancelAlarm(Context context, String name, String id) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE); //Initialize alarm manager
        PendingIntent alarmIntent = getStartPendingIntent(context,name,id); //Setup a pending intent from the name and id of event
        alarmManager.cancel(alarmIntent); //Cancel specficied alarm
    }

    private static PendingIntent getStartPendingIntent(Context context, String name, String id) {
        Intent intent = new Intent(context, NotificationEventReceiver.class); //Create intent
        intent.setAction(ACTION_START_NOTIFICATION_SERVICE); //Set intent to start notifications
        intent.putExtra("Name",name); //Set name of intent
        intent.putExtra("id",id); //Set id of intent
        return PendingIntent.getBroadcast(context, Integer.parseInt(id), intent, PendingIntent.FLAG_UPDATE_CURRENT); //Return pending intent
    }

    public static PendingIntent getDeleteIntent(Context context, Integer id) {
        Intent intent = new Intent(context, NotificationEventReceiver.class); //Create intent
        intent.setAction(ACTION_DELETE_NOTIFICATION); //Set intent to delete notification
        return PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Intent serviceIntent = null;
        if (ACTION_START_NOTIFICATION_SERVICE.equals(action)) {
            serviceIntent = NotificationIntentService.createIntentStartNotificationService(context);
            serviceIntent.putExtra("Name",intent.getStringExtra("Name"));
            serviceIntent.putExtra("id",intent.getStringExtra("id"));
        } else if (ACTION_DELETE_NOTIFICATION.equals(action)) {
            serviceIntent = NotificationIntentService.createIntentDeleteNotification(context);
            serviceIntent.putExtra("Name",intent.getStringExtra("Name"));
            serviceIntent.putExtra("id",intent.getStringExtra("id"));
        }

        if (serviceIntent != null) {
            // Start the service, keeping the device awake while it is launching.
            startWakefulService(context, serviceIntent);
        }
    }
}
