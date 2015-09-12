package com.example.madweather;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anthony on 2015-05-01.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "alarmTimesManager";
    private static final String TABLE_ALARM_TIMES = "alarmTimes";

    //Column names
    private static final String KEY_ID = "id";
    private static final String ENTRY = "time_entry";
    private static final String IS_ON = "is_on";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Create tables
     * Write create table statements
     * When database is created
     */

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_ALARM_TIMES_TABLE = "CREATE TABLE " + TABLE_ALARM_TIMES + "(" + KEY_ID + " INTEGER PRIMARY KEY, " + IS_ON +
                " INTEGER, " + ENTRY + " TEXT)";
        sqLiteDatabase.execSQL(CREATE_ALARM_TIMES_TABLE);
    }

    /**
     * Upgrade database
     * if db version is increased in app code.
     * This method allows u to update existing db schema or to drop existing db
     * and re-create it via onCreate(...)
     */

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "
            + TABLE_ALARM_TIMES);
        onCreate(sqLiteDatabase);
    }

    /**
     * If alarm object doesn't exist in the database, then insert it
     * else update it
     */

    public void save(AlarmEntry alarmEntry){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, alarmEntry.getId());
        values.put(IS_ON, checkBooleanValue(alarmEntry.isOn()));
        values.put(ENTRY, alarmEntry.getEntry());

        int id = (int) db.insertWithOnConflict(TABLE_ALARM_TIMES,null,values, SQLiteDatabase.CONFLICT_IGNORE);

        //if id = -1, it means conflict has occurred
        if(id==-1){
            updateAlarmTime(alarmEntry);
        }
        db.close();
    }

    /**
     * Build ContentValues parameters using alarmTime object.
     * Once we insert into db, we close the db connection.
     */
//    public void addAlarmTime(AlarmEntry alarmEntry){
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_ID, alarmEntry.getId());
//        values.put(ENTRY, alarmEntry.getEntry());
//
//        db.insert(TABLE_ALARM_TIMES,null,values);
//        db.close();
//    }

    public AlarmEntry getAlarmTime(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ALARM_TIMES, new String[]{KEY_ID, IS_ON, ENTRY}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if(cursor != null)
            cursor.moveToFirst();

        AlarmEntry alarmEntry = new AlarmEntry(Integer.parseInt(cursor.getString(0)), checkBooleanValue(cursor.getString(1)),
                cursor.getString(2));


        return alarmEntry;
    }

    public List<AlarmEntry> getAllAlarms(){
        List<AlarmEntry> alarmEntries = new ArrayList<AlarmEntry>();

        //Select all query
        String selectQuery = "SELECT *FROM " + TABLE_ALARM_TIMES;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //loop thru all rows and add to list
        if(cursor.moveToFirst()){
            do{
                AlarmEntry alarmEntry = new AlarmEntry(Integer.parseInt(cursor.getString(0)),
                        checkBooleanValue(cursor.getString(1)), cursor.getString(2));
                alarmEntries.add(alarmEntry);
            }while(cursor.moveToNext());
        }
        return alarmEntries;
    }

    public int updateAlarmTime(AlarmEntry alarmEntry){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, alarmEntry.getId());
        values.put(IS_ON, checkBooleanValue(alarmEntry.isOn()));
        values.put(ENTRY, alarmEntry.getEntry());

        return db.update(TABLE_ALARM_TIMES, values, KEY_ID + " =?", new String[]{String.valueOf(alarmEntry.getId())});
    }

//    public void deleteAlarmTime(AlarmEntry alarmEntry){
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_ALARM_TIMES, KEY_ID + " =?", new String[]{String.valueOf(alarmEntry.getId())});
//        db.close();
//    }

    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ALARM_TIMES, null, null);
        db.close();
    }

    private int checkBooleanValue(boolean b){
        if(b==true){
            return 1;
        }
        return 0;
    }

    private boolean checkBooleanValue(String b){
       if(b.equalsIgnoreCase("1")){
           return true;
       }
        return false;
    }
}
