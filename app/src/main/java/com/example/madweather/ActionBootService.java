package com.example.madweather;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;

import java.util.Calendar;
import java.util.List;

/**
 * This class is a service that is called from ActionBootReceiver.
 * It will restart all set alarms.
 */
public class ActionBootService extends IntentService {

    public ActionBootService() {
        super("Action Boot Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Get database....
        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        List<AlarmEntry> alarms = db.getAllAlarms();

        //re-enable alarms....
        for(AlarmEntry entry : alarms){

            if(entry.isOn() == true){
                TimeParser timeParser = new TimeParser(entry.getEntry());
                int startHour = timeParser.getStartHour();
                int startMinute = timeParser.getStartMinute();

                /**
                 * Sunday = 1
                 * Monday = 2
                 * Tuesday = 3
                 * Wednesday = 4
                 * Thursday = 5
                 * Friday = 6
                 * Saturday = 7
                 */
                Calendar myCal = Calendar.getInstance();
                myCal.set(Calendar.DAY_OF_WEEK, (entry.getId()+1));
                myCal.set(Calendar.HOUR_OF_DAY,startHour);
                myCal.set(Calendar.MINUTE,startMinute);

                Intent someIntent = new Intent(getApplicationContext(), SimpleWakefulReceiver.class);
                someIntent.putExtra(AlarmSettings.ALARM_ID, entry.getId());
                someIntent.putExtra(AlarmSettings.START_HOUR, startHour);
                someIntent.putExtra(AlarmSettings.START_MINUTE,startMinute);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), entry.getId(), someIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP, myCal.getTimeInMillis(), pendingIntent);
            }
        }
        ActionBootReceiver.completeWakefulIntent(intent);
    }
}
