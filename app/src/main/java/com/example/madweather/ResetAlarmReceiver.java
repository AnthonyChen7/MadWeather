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

/**
 * This receiver will restart all the set alarms every 2 days
 */
public class ResetAlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent service = new Intent(context, ActionBootService.class);
        Calendar tempCal = Calendar.getInstance();

        if(tempCal.get(Calendar.HOUR_OF_DAY) == MainActivity.RESET_HOUR && tempCal.get(Calendar.MINUTE) ==
                MainActivity.RESET_MINUTE){
            startWakefulService(context, service);
        }

        Calendar myCal = Calendar.getInstance();
        myCal.set(Calendar.HOUR_OF_DAY, MainActivity.RESET_HOUR);
        myCal.set(Calendar.MINUTE, MainActivity.RESET_MINUTE);
        myCal.add(Calendar.DATE, 2);

        Intent someIntent = new Intent(context.getApplicationContext(), ResetAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), MainActivity.REPEATING,
                someIntent, 0);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, myCal.getTimeInMillis(), pendingIntent);

    }
}
