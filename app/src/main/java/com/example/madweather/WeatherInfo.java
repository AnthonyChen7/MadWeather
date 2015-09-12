package com.example.madweather;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Anthony on 2015-04-30.
 */
public class WeatherInfo extends AsyncTask<Void, Void, HashSet<WeatherData>> {

    private static final String GOOD_WEATHER_TODAY = "Good weather today!";
    private static final String INTERNET_CONNECTION_ERROR = "Internet connection was not detected. No weather notification " +
            "available for you today.";
    private static final String SERVER_ERROR = "Unable to retrieve data from server. No weather notification available for you " +
            "today.";

    private Context context;
    private boolean alertUser;
    private boolean exceptionCaught;
    private double probabilityThreshold;
    private int hoursToParse;
    private String aOrAn;
    private String latitude;
    private String longitude;
    private String msg;
    private String url;
    private SharedPreferences sharedPreferences;
    private WeatherData mainWeatherData;
    private HashSet<WeatherData> weatherData;
    private HashSet<String> favorableWeatherEvents;

    public WeatherInfo(Context context) {
        this.context = context;
        alertUser = false;
        exceptionCaught = false;
        aOrAn = "a(n)";
        msg = "An unexpected error has occured";
        sharedPreferences = context.getSharedPreferences("weather settings", 1);
        mainWeatherData = new WeatherData("", 0.00);
        favorableWeatherEvents = new HashSet<String>();
        weatherData = new HashSet<WeatherData>();
        loadPrefs();
        url = "https://api.forecast.io/forecast/" + APIKEY + "/" + latitude + "," + longitude;
        updateHourlyInterval();
    }

    private void updateHourlyInterval() {
        DatabaseHandler db = new DatabaseHandler(context);
        List<AlarmEntry> alarms = db.getAllAlarms();

//        Reader friendly version
//        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
//        String entry = alarms.get(dayOfWeek) - 1).getEntry();
//        TimeParser tp = new TimeParser(entry);
//        int startHour = tp.getStartHour();
//        int endHour = tp.getEndHour();
//        int startMin = tp.getStartMinute();
//        int endMin = tp.getEndMinute();
//        hoursToParse = endHour - startHour;
//
//        if(tp.getEndHour() - tp.getStartHour() < 0){
//            hoursToParse += 24;
//        }
//        if(endMin - startMin > 0){
//            hoursToParse++;
//        }

        TimeParser tp = new TimeParser(alarms.get(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1).getEntry());
        hoursToParse = tp.getEndHour() - tp.getStartHour();
        //If the end hour is before the start hour, assume is the the end hour
        // is for the next day
        if (tp.getEndHour() - tp.getStartHour() < 0) {
            hoursToParse += 24;
        }
        // If there is at least a 1 min gap between the start and end time, add
        // 1 more hour to check for to account for the extra minute time
        if (tp.getEndMinute() - tp.getStartMinute() > 0) {
            hoursToParse++;
        }
    }

    private void loadPrefs() {
        latitude = sharedPreferences.getString(WeatherSettings.LATITUDE_KEY, "49.2827");
        longitude = sharedPreferences.getString(WeatherSettings.LONGITUDE_KEY, "-123.1207");
        probabilityThreshold = sharedPreferences.getInt(WeatherSettings.PROBABILITY_KEY, 10)/100;

        if (sharedPreferences.getBoolean(WeatherSettings.RAIN_KEY, false)) {
            favorableWeatherEvents.add("rain");
        }
        if (sharedPreferences.getBoolean(WeatherSettings.SNOW_KEY, false)) {
            favorableWeatherEvents.add("snow");
        }
        if (sharedPreferences.getBoolean(WeatherSettings.HAIL_KEY, false)) {
            favorableWeatherEvents.add("sleet");
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return this.context;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected HashSet<WeatherData> doInBackground(Void... voids) {
        try {
            String routingCall = makeRoutingCall(url);
            JSONTokener jsonTokener = new JSONTokener(routingCall);
            JSONObject weatherInfo = new JSONObject(jsonTokener);

            //Seperated into 3 loops to prevent nesting if statements
            for (String i : favorableWeatherEvents) {
                // uncomment to debug
                // System.out.println(weatherInfo.getJSONObject("hourly").
                // get("summary").toString());
                if (weatherInfo.getJSONObject("hourly").get("summary").toString().toLowerCase().contains(i)) {
                    alertUser = true;
                    break;
                }
            }
            if (alertUser) {
                JSONArray hourlyInfo = weatherInfo.getJSONObject("hourly").getJSONArray("data");

                int i = 0;

                while(i < hoursToParse){
                    JSONObject jsonObject = hourlyInfo.getJSONObject(i);
                    Double precipProbability = jsonObject.getDouble("precipProbability");

                    i++;
                    try {
                        String precipType = jsonObject.getString("precipType");
                        if (precipProbability > probabilityThreshold && favorableWeatherEvents.contains(precipType)) {
                            mainWeatherData.setProbability(precipProbability);
                            mainWeatherData.setEvent(precipType);
                            break;
//                            weatherData.add(new WeatherData(precipType,
//                                    precipProbability));
//
//                            if(precipProbability > mainWeatherData.getProbability()){
//                                mainWeatherData.setProbability(precipProbability);
//                                mainWeatherData.setEvent(precipType);
//                            }
                        }
                    } catch (JSONException e) {}
                }

                while(i < hoursToParse){
                    JSONObject jsonObject = hourlyInfo.getJSONObject(i);
                    Double precipProbability = jsonObject.getDouble(
                            "precipProbability");

                    try {
                        String precipType = jsonObject.getString("precipType");
                        if(precipProbability > mainWeatherData.getProbability() && favorableWeatherEvents.contains(precipType)){
                            mainWeatherData.setProbability(precipProbability);
                            mainWeatherData.setEvent(precipType);
                        }
                    } catch (JSONException e) {}
                    i++;
                }
            }
        } catch (IOException e) {
            msg = INTERNET_CONNECTION_ERROR;
            exceptionCaught = true;
            e.printStackTrace();
        } catch (JSONException e) {
            msg = SERVER_ERROR;
            exceptionCaught = true;
            e.printStackTrace();
        }
        return weatherData;
    }

    @Override
    protected void onPostExecute(HashSet<WeatherData> weatherDatas) {
        if (exceptionCaught) {
            createNotification(this.context, msg);
            exceptionCaught = false;
        }
        else{
            // use else if you want to receive notifications only if there is bad weather
            // else if (alertUser){
            updateAOrAnd();
            updateMSG();
            createNotification(this.context, msg);
            alertUser = false;
        }
    }

    private String makeRoutingCall(String httpRequest) throws IOException {
        URL url = new URL(httpRequest);
        HttpURLConnection client = (HttpURLConnection) url.openConnection();
        InputStream in = client.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String returnString = br.readLine();
        client.disconnect();
        return returnString;
    }

    private void updateAOrAnd() {
        int percentage1 = (int) mainWeatherData.getProbability()*100;
        if (percentage1 == 8 || percentage1/10 == 8 || percentage1 == 18) {
            aOrAn = "an";
        } else {
            aOrAn = "a";
        }
    }

    private void updateMSG() {
        if (weatherData.size() != 0) {
            msg = "There is up to " + aOrAn + " " + mainWeatherData.getProbability() * 100 + "% chance of " +
                    mainWeatherData.getEvent() + " today.";
        } else {
            msg = GOOD_WEATHER_TODAY;
        }
    }

    private void createNotification(Context context, String message) {
        NotificationMaker notifMaker = new NotificationMaker(context, message);
        notifMaker.createNotification();
    }
}
