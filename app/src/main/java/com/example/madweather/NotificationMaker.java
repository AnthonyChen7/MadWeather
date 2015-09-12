package com.example.madweather;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * A class tht creates a notification with a given message.
 */
public class NotificationMaker {
    public static final int notificationId = 1;
    private static final String CONTENT_TITLE = "MadWeather";
    private static final String CONTENT_TEXT = "Please swipe down to view message";
    private static final String DISMISS = "Dismiss";
    private Context context;
    private String message;

    public NotificationMaker(Context context, String message){
        this.context = context;
        this.message = message;
    }

    public void createNotification(){
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
        Intent dismissIntent = new Intent(context, DismissButtonReceiver.class);
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(context, 0, dismissIntent,0);

        /**
         * Notification.Builder provides builder interface to create
         * notification object use PendingIntent to specify the action which
         * should be performed once user selects notification
         */
        Notification noti = new Notification.Builder(context)
                .setContentTitle(CONTENT_TITLE)
                .setContentText(CONTENT_TEXT)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .addAction(0, DISMISS,dismissPendingIntent)
                .setStyle(new Notification.BigTextStyle().bigText(message))
                .build();

        /**
         * Notifications in android r represented by notification class
         * To create Notification, use notification manager class which is
         * recieved from Context (an activity or service) via get System
         * Service() method
         */
        NotificationManager notificationManager = (NotificationManager) context.
                getSystemService(context.NOTIFICATION_SERVICE);

        //Explicitly set lights to show
        noti.defaults = Notification.DEFAULT_ALL;
        noti.flags |= Notification.FLAG_SHOW_LIGHTS;
        noti.ledARGB = 0xff00ff00;
        noti.ledOnMS = 300;
        noti.ledOffMS = 1000;

        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        //allow text to be displayed when notification first pops up
        noti.tickerText = message;

        //Increase priority of notification so that it becomes first
        //notification in the tray
        //So that it's always expanded by default
        noti.priority = Notification.PRIORITY_MAX;

        notificationManager.notify(notificationId, noti);
    }
}
