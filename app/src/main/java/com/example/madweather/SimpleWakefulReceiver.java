package com.example.madweather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This receiver will call on SimpleWakefulService when the system time equals the user's specified time.
 * Then it will set the alarm to ring at that exact same time for next week.
 */
public class SimpleWakefulReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        int alarmID = intent.getIntExtra(AlarmSettings.ALARM_ID, 0);
        int startHour = intent.getIntExtra(AlarmSettings.START_HOUR, 0);
        int startMinute = intent.getIntExtra(AlarmSettings.START_MINUTE, 0);
        Intent service = new Intent(context, SimpleWakefulService.class);

        /**
         * Sunday = 1
         * Monday = 2
         * Tuesday = 3
         * Wednesday = 4
         * Thursday = 5
         * Friday = 6
         * Saturday = 7
         */
        if ((alarmID + 1) == Calendar.getInstance().get(Calendar.DAY_OF_WEEK) &&
                startHour == Calendar.getInstance().get(Calendar.HOUR_OF_DAY) &&
                startMinute == Calendar.getInstance().get(Calendar.MINUTE)) {

            startWakefulService(context, service);
        }

        Calendar c = Calendar.getInstance();

        c.set(Calendar.DAY_OF_WEEK, alarmID + 1);
        c.add(Calendar.WEEK_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, startHour);
        c.set(Calendar.MINUTE, startMinute);

        Intent someIntent = new Intent(context.getApplicationContext(), SimpleWakefulReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), alarmID, someIntent, 0);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }
}
