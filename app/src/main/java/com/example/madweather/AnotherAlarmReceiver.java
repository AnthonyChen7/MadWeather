package com.example.madweather;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * This receiver is called when "Create notification" is pressed.
 */
public class AnotherAlarmReceiver extends WakefulBroadcastReceiver {

    public AnotherAlarmReceiver() {}

    @Override
    public void onReceive(Context context, Intent intent) {
        WeatherInfo weatherInfo = new WeatherInfo(context);
        weatherInfo.execute();
    }
}
