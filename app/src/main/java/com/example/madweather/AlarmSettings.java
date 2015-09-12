package com.example.madweather;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.List;

/**
 * This is the activity where the alarm times are changed and set.
 */
public class AlarmSettings extends Activity {

    public static final String ALARM_ID = "alarmId";
    public static final String START_HOUR = "startHour";
    public static final String START_MINUTE = "startMinute";

    private static int sundayID = 0;
    private static int mondayID = 0;
    private static int tuesdayID = 0;
    private static int wednesdayID = 0;
    private static int thursdayID = 0;
    private static int fridayID = 0;

    private static int sunBoxID = 0;
    private static int monBoxID = 0;
    private static int tuesBoxID = 0;
    private static int wedBoxID = 0;
    private static int thursBoxID = 0;
    private static int friBoxID = 0;

    private String whichViewClicked = "";

    private CheckBox sundayBox;
    private TextView sundayTime;

    private CheckBox mondayBox;
    private TextView mondayTime;

    private CheckBox tuesdayBox;
    private TextView tuesdayTime;

    private CheckBox wednesdayBox;
    private TextView wednesdayTime;

    private CheckBox thursdayBox;
    private TextView thursdayTime;

    private CheckBox fridayBox;
    private TextView fridayTime;

    private CheckBox saturdayBox;
    private TextView saturdayTime;

    private TextView startTime;
    private TextView endTime;

    private TimeParser timeParser;
    private int startHour = 0;
    private int startMinute = 0;
    private int endHour = 0;
    private int endMinute = 0;

    private DatabaseHandler db;

    private boolean isCheckBoxChanged = false;
    private boolean isTimeChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle("Alarms");
        setContentView(R.layout.activity_alarm_settings);
        db = new DatabaseHandler(this);
        initCheckBoxes();
        initTextViews();
    }

    @Override
    protected void onResume(){
        isCheckBoxChanged = false;
        isTimeChanged = false;
        loadEverything();
        super.onResume();
    }

    @Override
    protected void onPause(){
        saveEverything();
        super.onPause();
    }

    /**
     * Load all AlarmEntry from the database
     */
    private void loadEverything(){

        List<AlarmEntry> alarms = db.getAllAlarms();
        if(!alarms.isEmpty()) {
            AlarmEntry sunday = db.getAlarmTime(0);
            AlarmEntry monday = db.getAlarmTime(1);
            AlarmEntry tuesday = db.getAlarmTime(2);
            AlarmEntry wednesday = db.getAlarmTime(3);
            AlarmEntry thursday = db.getAlarmTime(4);
            AlarmEntry friday = db.getAlarmTime(5);
            AlarmEntry saturday = db.getAlarmTime(6);

            sundayBox.setChecked(sunday.isOn());
            sundayTime.setText(sunday.getEntry());

            mondayBox.setChecked(monday.isOn());
            mondayTime.setText(monday.getEntry());

            tuesdayBox.setChecked(tuesday.isOn());
            tuesdayTime.setText(tuesday.getEntry());

            wednesdayBox.setChecked(wednesday.isOn());
            wednesdayTime.setText(wednesday.getEntry());

            thursdayBox.setChecked(thursday.isOn());
            thursdayTime.setText(thursday.getEntry());

            fridayBox.setChecked(friday.isOn());
            fridayTime.setText(friday.getEntry());

            saturdayBox.setChecked(saturday.isOn());
            saturdayTime.setText(saturday.getEntry());
        }else{
            //Nothing is stored in db
            //Hack: Force it to save everything to db or else error occurs
            isCheckBoxChanged = true;
            saveEverything();
            isCheckBoxChanged = false;
        }
    }

    /**
     * Save all AlarmEntry to database
     */
    private void saveEverything(){

        if(isCheckBoxChanged == true || isTimeChanged == true) {
            db.save(new AlarmEntry(TextViewConstants.Sunday, sundayBox.isChecked(), sundayTime.getText().toString()));
            db.save(new AlarmEntry(TextViewConstants.Saturday, saturdayBox.isChecked(), saturdayTime.getText().toString()));
            db.save(new AlarmEntry(TextViewConstants.Monday, mondayBox.isChecked(), mondayTime.getText().toString()));
            db.save(new AlarmEntry(TextViewConstants.Tuesday, tuesdayBox.isChecked(), tuesdayTime.getText().toString()));
            db.save(new AlarmEntry(TextViewConstants.Wednesday, wednesdayBox.isChecked(), wednesdayTime.getText().toString()));
            db.save(new AlarmEntry(TextViewConstants.Thursday, thursdayBox.isChecked(), thursdayTime.getText().toString()));
            db.save(new AlarmEntry(TextViewConstants.Friday, fridayBox.isChecked(), fridayTime.getText().toString()));
        }
    }

    private void initTextViews(){
        sundayTime = (TextView) findViewById(R.id.SundayTime);
        mondayTime = (TextView) findViewById(R.id.MondayTime);
        tuesdayTime = (TextView) findViewById(R.id.TuesdayTime);
        wednesdayTime = (TextView) findViewById(R.id.WednesdayTime);
        thursdayTime = (TextView) findViewById(R.id.ThursdayTime);
        fridayTime = (TextView) findViewById(R.id.FridayTime);
        saturdayTime = (TextView) findViewById(R.id.SaturdayTime);

        sundayID = sundayTime.getId();
        mondayID = mondayTime.getId();
        tuesdayID = tuesdayTime.getId();
        wednesdayID = wednesdayTime.getId();
        thursdayID = thursdayTime.getId();
        fridayID = fridayTime.getId();

        initTextViewListener(sundayTime);
        initTextViewListener(mondayTime);
        initTextViewListener(tuesdayTime);
        initTextViewListener(wednesdayTime);
        initTextViewListener(thursdayTime);
        initTextViewListener(fridayTime);
        initTextViewListener(saturdayTime);
    }

    private void initCheckBoxes(){
        sundayBox = (CheckBox) findViewById(R.id.sundayBox);
        mondayBox = (CheckBox) findViewById(R.id.mondayBox);
        tuesdayBox = (CheckBox) findViewById(R.id.tuesdayBox);
        wednesdayBox = (CheckBox) findViewById(R.id.wednesdayBox);
        thursdayBox = (CheckBox) findViewById(R.id.thursdayBox);
        fridayBox = (CheckBox) findViewById(R.id.fridayBox);
        saturdayBox = (CheckBox) findViewById(R.id.saturdayBox);

        sunBoxID = sundayBox.getId();
        monBoxID = mondayBox.getId();
        tuesBoxID = tuesdayBox.getId();
        wedBoxID = wednesdayBox.getId();
        thursBoxID = thursdayBox.getId();
        friBoxID = fridayBox.getId();

        initCheckBoxListener(sundayBox);
        initCheckBoxListener(mondayBox);
        initCheckBoxListener(tuesdayBox);
        initCheckBoxListener(wednesdayBox);
        initCheckBoxListener(thursdayBox);
        initCheckBoxListener(fridayBox);
        initCheckBoxListener(saturdayBox);

    }

    private void initCheckBoxListener(final CheckBox checkBox){

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkChanges(b,checkBox.getId());

                // Find appropriate checkbox id....
                int alarmId = getCheckBoxId(checkBox.getId());

                Intent intent = new Intent(getApplicationContext(), SimpleWakefulReceiver.class);
                intent.putExtra(ALARM_ID, alarmId);

                AlarmEntry entry = db.getAlarmTime(alarmId);
                String startTime = entry.getEntry();
                timeParser = new TimeParser(startTime);
                startHour = timeParser.getStartHour();
                startMinute = timeParser.getStartMinute();

                intent.putExtra(START_HOUR, startHour);
                intent.putExtra(START_MINUTE,startMinute);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), alarmId ,intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                if(b == true){


                    Calendar myCal = Calendar.getInstance();
                    /**
                     * Sunday = 1
                     * Monday = 2
                     * Tuesday = 3
                     * Wednesday = 4
                     * Thursday = 5
                     * Friday = 6
                     * Saturday = 7
                     */
                    if(alarmId == TextViewConstants.Sunday){
                        myCal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

                    }
                    else if(alarmId == TextViewConstants.Monday){
                        myCal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    }
                    else if(alarmId == TextViewConstants.Tuesday){
                        myCal.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                    }
                    else if(alarmId == TextViewConstants.Wednesday){
                        myCal.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                    }
                    else if(alarmId == TextViewConstants.Thursday){
                        myCal.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                    }
                    else if(alarmId == TextViewConstants.Friday){
                        myCal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                    }
                    else{
                        myCal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                    }
                    myCal.set(Calendar.HOUR_OF_DAY, startHour);
                    myCal.set(Calendar.MINUTE, startMinute);

                    am.set(AlarmManager.RTC_WAKEUP, myCal.getTimeInMillis(), pendingIntent);
                }else{
                    am.cancel(pendingIntent);
                }

            }
        });
    }

    /**
     * Returns correct day number based on checkbox id
     */
    private int getCheckBoxId(int checkBoxId){

        if(checkBoxId == sunBoxID){
            return TextViewConstants.Sunday;
        }
        else if(checkBoxId == monBoxID){
            return TextViewConstants.Monday;
        }
        else if(checkBoxId == tuesBoxID){
            return TextViewConstants.Tuesday;
        }
        else if(checkBoxId == wedBoxID){
            return TextViewConstants.Wednesday;
        }
        else if(checkBoxId == thursBoxID){
            return TextViewConstants.Thursday;
        }
        else if(checkBoxId == friBoxID){
            return TextViewConstants.Friday;
        }
        else {
            return TextViewConstants.Saturday;
        }
    }

    /**
     * Compares the database value of selected checkbox along with
     * current value to see if checkbox has changed
     */
    private void checkChanges(boolean checkBoxValue, int checkBoxId){
        int id;

        if(checkBoxId == sunBoxID){
            id = TextViewConstants.Sunday;
        }
        else if(checkBoxId == monBoxID){
            id = TextViewConstants.Monday;
        }
        else if(checkBoxId == tuesBoxID){
            id = TextViewConstants.Tuesday;
        }
        else if(checkBoxId == wedBoxID){
            id = TextViewConstants.Wednesday;
        }
        else if(checkBoxId == thursBoxID){
            id = TextViewConstants.Thursday;
        }
        else if(checkBoxId == friBoxID){
            id = TextViewConstants.Friday;
        }
        else {
            id = TextViewConstants.Saturday;
        }

        AlarmEntry temp = db.getAlarmTime(id);

        boolean isChecked = temp.isOn();

        if(isChecked != checkBoxValue){
            isCheckBoxChanged = true;
        }else{
            isCheckBoxChanged = false;
        }
    }

    /**
     * Initialize TimePicker dialog for start time and end time
     */
    private void initListenerTime(){

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(AlarmSettings.this, startTimePickerListener, startHour,
                        startMinute, false);
                timePickerDialog.setTitle("Set Time");
                timePickerDialog.show();
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(AlarmSettings.this, endTimePickerListener, endHour,
                        endMinute, false);
                timePickerDialog.setTitle("Set Time");
                timePickerDialog.show();
            }
        });
    }


    private void initTextViewListener(final TextView textView){

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final TextView clickedTextView = (TextView) view;
                whichViewClicked = clickedTextView.getText().toString();
                timeParser = new TimeParser(whichViewClicked);
                final Dialog dialog = new Dialog(AlarmSettings.this);

                dialog.setContentView(R.layout.selecting_time);

                Button saveChanges = (Button) dialog.findViewById((R.id.save));
                Button cancel = (Button) dialog.findViewById(R.id.cancel);

                startTime = (TextView) dialog.findViewById(R.id.startTime);
                endTime = (TextView) dialog.findViewById(R.id.endTime);

                startTime.setText(timeParser.getStartTime());
                endTime.setText(timeParser.getEndTime());

                startHour = timeParser.getStartHour();
                startMinute = timeParser.getStartMinute();

                endHour = timeParser.getEndHour();
                endMinute = timeParser.getEndMinute();

                initListenerTime();

                saveChanges.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String resultText = startTime.getText() + "-" + endTime.getText();
                        checkTimeChange(resultText,clickedTextView.getId());
                        textView.setText(resultText);
                        dialog.dismiss();
                        saveEverything();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    /**
     * Compares specified time with that of in the database to see if the time
     * has changed
     */
    private void checkTimeChange(String time, int id){
        int dbId = 0;
        CheckBox aBox;

        if(id == sundayID){
            dbId = TextViewConstants.Sunday;
            aBox = sundayBox;
        }
        else if(id == mondayID){
            dbId = TextViewConstants.Monday;
            aBox = mondayBox;
        }
        else if(id == tuesdayID){
            dbId = TextViewConstants.Tuesday;
            aBox = tuesdayBox;
        }
        else if(id == wednesdayID){
            dbId = TextViewConstants.Wednesday;
            aBox = wednesdayBox;
        }
        else if(id == thursdayID){
            dbId = TextViewConstants.Thursday;
            aBox = thursdayBox;
        }
        else if(id == fridayID){
            dbId = TextViewConstants.Friday;
            aBox = fridayBox;
        }
        else{
            dbId = TextViewConstants.Saturday;
            aBox = saturdayBox;
        }

        AlarmEntry temp = db.getAlarmTime(dbId);
        String tempTime = temp.getEntry();

        if(!tempTime.equals(time)){
            isTimeChanged = true;
            //If checkbox was already checked, disable it
            TimeParser tOne = new TimeParser(tempTime);
            TimeParser tTwo = new TimeParser(time);
            if(aBox.isChecked() == true && (!tOne.getStartTime().equals(tTwo.getStartTime()))){
                aBox.setChecked(false);
            }
        }else{
            isTimeChanged = false;
        }
    }

    private TimePickerDialog.OnTimeSetListener startTimePickerListener =
            new TimePickerDialog.OnTimeSetListener(){
                /**
                 * Once the time  is set this will invoke ,
                 * "onTimeSet(TimePicker view, int hourOfDay, int minute)" method to perform user define functionality
                 * where we have assigned the time value to editText.
                 */

                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                    /**
                     * This is being ran twice.
                     * Make it only execute once.
                     * It will only return true once
                     */
                    if(timePicker.isShown()){
                        startHour = selectedHour;
                        startMinute = selectedMinute;
                        String result = convertTime(startHour,startMinute);
                        startTime.setText(result);
                    }
                }
            };

    private TimePickerDialog.OnTimeSetListener endTimePickerListener =
            new TimePickerDialog.OnTimeSetListener(){
                /**
                 * Once the time  is set this will invoke ,
                 * "onTimeSet(TimePicker view, int hourOfDay, int minute)"
                 * method to perform user define functionality where we have
                 * assigned the time value to editText.
                 */
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour,
                                      int selectedMinute) {
                    /**
                     * This is being ran twice.
                     * Make it only execute once.
                     * It will only return true once
                     */
                    if(timePicker.isShown()){
                        endHour = selectedHour;
                        endMinute = selectedMinute;
                        String result = convertTime(endHour,endMinute);
                        endTime.setText(result);
                    }
                }
            };

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

    /**
     * Converts a hour and minute to appropriate time format
     */
    private String convertTime(int hour, int minute){
        String result = "";

        if(hour == 0){
            result = "12:" + pad(minute) + "am";
        }
        else if(hour > 12){
            int temp = hour - 12;
            result = temp + ":" + pad(minute) + "pm";
        }else if(hour == 12){
            result = hour + ":" + pad(minute) + "pm";
        }
        else{
            result = hour + ":" + pad(minute) + "am";
        }
        return result;
    }
}
