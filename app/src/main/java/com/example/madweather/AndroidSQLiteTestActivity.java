package com.example.madweather;

import android.app.Activity;
import android.os.Bundle;

import java.util.List;

/**
 * Activity to test if database methods work
 */
public class AndroidSQLiteTestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_sqlite_test);

        DatabaseHandler db = new DatabaseHandler(this);

        //Insert
        AlarmEntry alarmOne = new AlarmEntry(0,true,"lala");
        AlarmEntry alarmTwo = new AlarmEntry(1,true,"6:00pm-10:00pm");
        AlarmEntry alarmThree = new AlarmEntry(2,true,"12:00am");

        db.save(alarmOne);
        db.save(alarmTwo);
        db.save(alarmThree);


        //Reading all...
        List<AlarmEntry> result = db.getAllAlarms();

        for(AlarmEntry alarm : result){
            String log = "Id: " + alarm.getId()+ " ,Entry: " + alarm.getEntry() + " isOn: " + alarm.isOn();
        }

        db.save(new AlarmEntry(0,false, "yolo"));
        db.save(new AlarmEntry(1,false, "holo"));
        db.save(new AlarmEntry(4,false, "molo"));

        result = db.getAllAlarms();

        for(AlarmEntry alarm : result){
            String log = "Id: " + alarm.getId()+ " ,Entry: " + alarm.getEntry() + " isOn: " + alarm.isOn();
        }

        //Delete all
        db.deleteAll();
    }

}
