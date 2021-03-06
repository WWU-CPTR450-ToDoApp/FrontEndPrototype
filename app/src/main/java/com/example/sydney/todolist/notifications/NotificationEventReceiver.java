package com.example.sydney.todolist.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Calendar;


public class NotificationEventReceiver extends WakefulBroadcastReceiver {

    private static final String ACTION_START_NOTIFICATION_SERVICE = "ACTION_START_NOTIFICATION_SERVICE";
    private static final String ACTION_DELETE_NOTIFICATION = "ACTION_DELETE_NOTIFICATION";

    //Setup Alarm for notifications
    public static void setupAlarm(Context context, String name, String id, long date) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE); //Initialize alarm manager
        PendingIntent alarmIntent = getStartPendingIntent(context,name,id); //Setup a pending intent from the name and id of event
        alarmIntent.getIntentSender(); //Retrieve a IntentSender object that wraps the existing sender of the PendingIntent
        long timec= System.currentTimeMillis();// + 5000;
        long atime = date;
        //long time = calendar.getTimeInMillis(); //Convert calendar to milli seconds
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(timec);
        Log.d("Notifications",cl.get(Calendar.MONTH)+", "+Integer.toString(cl.get(Calendar.DATE)));
        Log.d("Notifications",cl.get(Calendar.HOUR_OF_DAY) + ":" + cl.get(Calendar.MINUTE) + ":" + cl.get(Calendar.SECOND));
        Calendar cl2 = Calendar.getInstance();
        /*cl2.setTimeInMillis(time);
        Log.d("Notifications",cl2.get(Calendar.MONTH)+", "+Integer.toString(cl2.get(Calendar.DATE)));
        Log.d("Notifications",cl2.get(Calendar.HOUR_OF_DAY) + ":" + cl2.get(Calendar.MINUTE) + ":" + cl2.get(Calendar.SECOND));*/
        Calendar cl3 = Calendar.getInstance();
        cl3.setTimeInMillis(date);
        Log.d("Notifications",cl3.get(Calendar.MONTH)+", "+Integer.toString(cl3.get(Calendar.DATE)));
        Log.d("Notifications",cl3.get(Calendar.HOUR_OF_DAY) + ":" + cl3.get(Calendar.MINUTE) + ":" + cl3.get(Calendar.SECOND));
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, atime, alarmIntent); //Setup alarm
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
