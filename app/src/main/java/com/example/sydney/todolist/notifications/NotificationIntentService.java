package com.example.sydney.todolist.notifications;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.example.sydney.todolist.MainActivity;
import com.example.sydney.todolist.R;

public class NotificationIntentService extends IntentService {

    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";

    public NotificationIntentService() {
        super(NotificationIntentService.class.getSimpleName());
    }

    public static Intent createIntentStartNotificationService(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    public static Intent createIntentDeleteNotification(Context context) {
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_DELETE);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(getClass().getSimpleName(), "onHandleIntent, started handling a notification event");
        try {
            String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                processStartNotification(intent.getStringExtra("Name"),intent.getStringExtra("id"));
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void processStartNotification(String name, String id) {
        // Do something. For example, fetch fresh data from backend to create a rich notification?

        Log.d("N",id);
        final NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this);

        mBuilder.setContentTitle("To Do List");
        mBuilder.setContentText(name);
        mBuilder.setAutoCancel(true);
        mBuilder.setSmallIcon(R.drawable.ic_edit_white); //set drawable to whatever we end up using

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, Integer.parseInt(id), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(this, Integer.parseInt(id)));

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);
        mBuilder.setLights(Color.GREEN, 3000, 3000);
        mBuilder.setVibrate(new long[] { 0, 100, 100, 100, 1000, 1000, 1000 });

        final NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(Integer.parseInt(id), mBuilder.build());

    }
}
