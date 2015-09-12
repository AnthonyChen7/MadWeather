package com.example.madweather;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

/**
    The main page of the application.
 */
public class MainActivity extends Activity {

    public static final String MY_PREFS = "MyPrefs";
    public static final String HOUR = "hour";
    public static final String MINUTE = "minute";
    public static final int ONE_TIME = 999;
    public static final int REPEATING = 998;
    public static final int RESET_HOUR = 12;
    public static final int RESET_MINUTE = 0;

    private Button createNotification;
    private Button instructions;
    private Button weatherSettings;
    private Button alarmSettings;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences(MY_PREFS, MODE_WORLD_READABLE);
        addListenerOnButton();
    }

    public void addListenerOnButton() {
        createNotification = (Button) findViewById(R.id.createNotification);
        createNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                Intent intent = new Intent(MainActivity.this, AnotherAlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, ONE_TIME ,intent,
                        PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager am = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
            }
        });

        instructions = (Button) findViewById(R.id.instructions);
        instructions.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InstructionsActivity.class);
                startActivity(intent);
            }
        });

        weatherSettings = (Button) findViewById(R.id.settings);
        weatherSettings.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WeatherSettings.class);
                startActivity(intent);
            }
        });

        alarmSettings = (Button) findViewById(R.id.alarms);
        alarmSettings.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AlarmSettings.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onPause(){
        savePref(HOUR, RESET_HOUR);
        savePref(MINUTE, RESET_MINUTE);
        super.onPause();
    }

    private void savePref(String key, int value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * Adds a 0 before c if c is <10
     */
    public static String pad(int c) {
        if(c >= 10){
            return String.valueOf(c);
        }else{
            return "0" + String.valueOf(c);
        }
    }
}
