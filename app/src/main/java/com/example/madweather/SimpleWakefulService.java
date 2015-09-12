package com.example.madweather;

import android.app.IntentService;
import android.content.Intent;

/**
 * A service that will look query from the web server to look at weather results.
 */
public class SimpleWakefulService extends IntentService {

    public SimpleWakefulService(){
        super("SimpleWakefulService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        WeatherInfo weatherInfo = new WeatherInfo(getApplicationContext());
        weatherInfo.execute();
        SimpleWakefulReceiver.completeWakefulIntent(intent);
    }
}
