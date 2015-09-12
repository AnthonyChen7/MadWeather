package com.example.madweather;

/**
 * Created by Anthony on 2015-05-01.
 * This is the alarm entity.
 *
 * id is ID of the alarm.
 * Entry contains the start alarm time and end alarm time.
 * isOn tells us whether the alarm is enabled or not.
 */
public class AlarmEntry {

    private int id;
    private String entry;
    private boolean isOn;

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public AlarmEntry(int id, boolean isOn, String entry){
        this.id = id;
        this.isOn = isOn;
        this.entry = entry;

    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean isOn) {
        this.isOn = isOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AlarmEntry that = (AlarmEntry) o;

        if (id != that.id) return false;
        if (isOn != that.isOn) return false;
        if (entry != null ? !entry.equals(that.entry) : that.entry != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (entry != null ? entry.hashCode() : 0);
        result = 31 * result + (isOn ? 1 : 0);
        return result;
    }
}
