package com.example.madweather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.Calendar;

/**
 * This class is called when device is rebooted.
 * It makes a call to a service to restart all alarms and restart reset alarm.
 */
public class ActionBootReceiver extends WakefulBroadcastReceiver {
    public ActionBootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){

            SharedPreferences sharedPreferences = context.getSharedPreferences(WeatherSettings.WEATHER_SETTING, 0);
            boolean reset = sharedPreferences.getBoolean(WeatherSettings.RESET_KEY, false);

            Intent service = new Intent(context, ActionBootService.class);
            startWakefulService(context, service);

            if(reset == true) {
                Calendar myCal = Calendar.getInstance();
                myCal.set(Calendar.HOUR_OF_DAY, MainActivity.RESET_HOUR);
                myCal.set(Calendar.MINUTE, MainActivity.RESET_MINUTE);

                Intent someIntent = new Intent(context.getApplicationContext(), ResetAlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), MainActivity.REPEATING,
                        someIntent, 0);

                AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP, myCal.getTimeInMillis(), pendingIntent);
            }
        }
    }
}
