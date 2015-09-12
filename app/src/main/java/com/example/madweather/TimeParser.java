package com.example.madweather;


/**
 * A class that will parse a time entry.
 */
public class TimeParser {

    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private String toParse;
    private String startTime;
    private String endTime;

    public TimeParser(String s){
        this.toParse = s;
        parseTime();
    }

    private void parseTime(){
        String trimSpace = toParse.trim();

        //split at hyphen
        String[] parts = trimSpace.split("-");

         startTime = parts[0];
         endTime =  parts[1];

        String[] startTimeArray = startTime.split(":");

        String startHourString = startTimeArray[0];

        if(startTime.contains("pm") && startHourString.equals("12")){
            startHour = Integer.parseInt(startHourString);
        }else if(startTime.contains("pm")){
            startHour = Integer.parseInt(startHourString)+12;
        }else if(startTime.contains("am") && startHourString.equals("12")){
            startHour = 0;
        }else{
            startHour = Integer.parseInt(startHourString);
        }

        String startMinuteString = startTimeArray[1];
        //remove the am-pm stuff
        String startMinuteFiltered = startMinuteString.replaceAll("[^\\d.]", "");

        startMinute = Integer.parseInt(startMinuteFiltered);

        String[] endTimeArray = endTime.split(":");
        String endHourString = endTimeArray[0];

        if(endTime.contains("pm") && endHourString.equals("12")){
            endHour = Integer.parseInt(endHourString);
        }else if(endTime.contains("pm")){
            endHour = Integer.parseInt(endHourString) + 12;
        }
        else if(endTime.contains("am") && endHourString.equals("12")){
            endHour = 0;
        }else{
            endHour = Integer.parseInt(endHourString);
        }

        String endMinuteString = endTimeArray[1];
        //remove am-pm stuff
        String endMinuteFiltered = endMinuteString.replaceAll("[^\\d.]", "");
        endMinute = Integer.parseInt(endMinuteFiltered);
    }

    public int getStartMinute() {
        return startMinute;
    }

    public String getStartTime() {
        return startTime;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public String getToParse() {
        return toParse;
    }

    public int getStartHour() {
        return startHour;
    }

    public String getEndTime() {
        return endTime;
    }
}
