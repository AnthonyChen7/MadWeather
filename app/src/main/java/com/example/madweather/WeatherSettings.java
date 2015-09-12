package com.example.madweather;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.AsyncTask;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;

//Initializes all the buttons on the Settings page

public class WeatherSettings extends Activity {

    public static final String WEATHER_SETTING = "weather settings";
    public static final String RAIN_KEY = "rain";
    public static final String SNOW_KEY = "snow";
    public static final String HAIL_KEY = "hail";
    public static final String LATITUDE_KEY = "lat";
    public static final String LONGITUDE_KEY = "long";
    public static final String LOCATION_KEY = "location";
    public static final String PROBABILITY_KEY = "probability";
    public static final String RESET_KEY = "reset";

    private static final String ADDRESS_ERROR = "Please input a valid address.";
    private static final String INPUT_ERROR = "Please enter a number.";
    private static final String PROBABILITY_ERROR = "A probability must be between 0 and 100%";
    private static final String RESET_START = "Starting reset alarm for every other day...";
    private static final String RESET_CANCEL = "Cancelling reset alarm for every other day...";

    private AlertDialog aDialog;
    private Boolean rain;
    private Boolean snow;
    private Boolean hail;
    private Boolean reset;
    private Intent intent;
    private PendingIntent pendingIntent;
    private SharedPreferences sharedPreferences;
    private String location;
    private int probability;

    public Location inputAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle("Settings");
        setContentView(R.layout.activity_weather_settings);

        sharedPreferences = getSharedPreferences(WEATHER_SETTING, 0);

        intent = new Intent(getApplicationContext(), ResetAlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), MainActivity.REPEATING, intent, 0);

    }

    @Override
    protected void onResume(){
        loadPreferences();
        addListenerOnButtons();
        initAlarm(reset, getApplicationContext());
        super.onResume();
    }

    @Override
    protected void onStop(){
        savePreferences();
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        savePreferences();
        super.onDestroy();
    }

    @Override
    protected void onPause(){
        savePreferences();
        super.onPause();
    }

    private void loadPreferences() {
        location = sharedPreferences.getString(LOCATION_KEY, "");
        rain = sharedPreferences.getBoolean(RAIN_KEY, false);
        snow = sharedPreferences.getBoolean(SNOW_KEY, false);
        hail = sharedPreferences.getBoolean(HAIL_KEY, false);
        reset = sharedPreferences.getBoolean(RESET_KEY, false);
        probability = sharedPreferences.getInt(PROBABILITY_KEY, 10);
    }

    private void savePreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(RAIN_KEY, rain);
        editor.putBoolean(SNOW_KEY, snow);
        editor.putBoolean(HAIL_KEY, hail);
        editor.putBoolean(RESET_KEY, reset);
        editor.putInt(PROBABILITY_KEY, probability);
        editor.apply();
    }

    public void addListenerOnButtons(){
        final EditText locationBox = (EditText) findViewById(R.id.locationBox);
        locationBox.setText(location);
        locationBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationBox.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int i, KeyEvent keyEvent) {
                        if (i == keyEvent.KEYCODE_ENTER &&
                                keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                            location = locationBox.getText().toString();

                            if(isTextValid(location)){
                                //Text is valid
                                // Must replace all white spaces or else IO
                                // exception occurs
                                String urlFormattedLocation = location.replaceAll("\\s","%20");
                                FindAddress anAddress = new FindAddress(urlFormattedLocation, WeatherSettings.this);
                                try {
                                    inputAddress = anAddress.execute().get();
                                    if(inputAddress != null){
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString(LATITUDE_KEY, inputAddress.getLat());
                                        editor.putString(LONGITUDE_KEY, inputAddress.getLng());
                                        editor.putString(LOCATION_KEY, inputAddress.getName());
                                        editor.apply();
                                        locationBox.setText(location);
                                    }else{
                                        AlertDialog someDialog =
                                                createSimpleDialog(ADDRESS_ERROR);
                                        someDialog.show();
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                            }else{
                                AlertDialog someDialog = createSimpleDialog(ADDRESS_ERROR);
                                someDialog.show();
                            }

                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
            }
        });

        CheckBox rainBox = (CheckBox) findViewById(R.id.rainCheck);
        rainBox.setChecked(rain);
        rainBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                rain = !rain;
            }
        });

        CheckBox snowBox = (CheckBox) findViewById(R.id.snowCheck);
        snowBox.setChecked(snow);
        snowBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                snow = !snow;
            }
        });

        CheckBox hailBox = (CheckBox) findViewById(R.id.hailCheck);
        hailBox.setChecked(hail);
        hailBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                hail = !hail;
            }
        });

        final EditText probabilityField =
                (EditText) findViewById(R.id.probability);
        probabilityField.setText(String.valueOf(probability));
        probabilityField.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                probabilityField.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int i, KeyEvent keyEvent) {

                        if (i == keyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                            if(probabilityField.getText().toString().equals("")){
                                aDialog = createSimpleDialog(INPUT_ERROR);
                                aDialog.show();
                                return false;
                            } else {
                                probability = Integer.parseInt(probabilityField.getText().toString());

                                if (probability > 100 || probability < 0) {
                                    aDialog = createSimpleDialog(PROBABILITY_ERROR);
                                    aDialog.show();
                                    probability = 100;

                                    probabilityField.setText("");
                                } else {
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                }
                                return true;
                            }
                        } else {
                            return false;
                        }
                    }
                });
            }
        });

        CheckBox dailyResetBox = (CheckBox) findViewById(R.id.dailyResetCheck);
        dailyResetBox.setChecked(reset);
        dailyResetBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                reset = !reset;
                savePreferences();
            }
        });

        dailyResetBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    createAlarm();
                    Toast.makeText(getApplicationContext(), RESET_START, Toast.LENGTH_LONG).show();
                }else{
                    AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    am.cancel(pendingIntent);
                    Toast.makeText(getApplicationContext(), RESET_CANCEL, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void createAlarm() {
        Calendar myCal = Calendar.getInstance();

        myCal.set(Calendar.HOUR_OF_DAY, MainActivity.RESET_HOUR);
        myCal.set(Calendar.MINUTE, MainActivity.RESET_MINUTE);

        AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        am.set(AlarmManager.RTC_WAKEUP, myCal.getTimeInMillis(), pendingIntent);
    }

    private void initAlarm(boolean bool, Context context){
        if(bool){
            createAlarm();
        }
    }

    private AlertDialog createSimpleDialog(String msg) {
        AlertDialog.Builder dialogBldr = new AlertDialog.Builder(WeatherSettings.this);
        dialogBldr.setTitle("Error!");
        dialogBldr.setMessage(msg);
        dialogBldr.setNeutralButton("Okay", null);
        return dialogBldr.create();
    }

    /**
     * Checks if specified text is valid
     * A text is invalid if it's null, empty, or contains pure whitespace.
     */
    public static boolean isTextValid(String text) {
        if (text == null || text.isEmpty() || text.matches("\\s+")) {
            return false;
        }
        return true;
    }


}

/**
 * A private class that will locate the lat and lng based on an address that is
 * of a string type.
 */
class FindAddress extends AsyncTask<Location,Location, Location>{

    private static final String LOCATION_SET = "Location has been set to ";

    private String address;
    private Context context;
    private String location;


    public FindAddress(String address,Context context){
        this.address = address;
        this.context = context;
    }

    @Override
    protected Location doInBackground(Location... locations) {
        String uri = "http://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=false";
        HttpGet httpGet = new HttpGet(uri);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject;
        Location myLocation = null;
        try {
            jsonObject = (new JSONObject(stringBuilder.toString())).getJSONArray("results").getJSONObject(0);
            location = jsonObject.getString("formatted_address");
            jsonObject = jsonObject.getJSONObject("geometry").getJSONObject("location");
            String lng = jsonObject.getString("lng");
            String lat = jsonObject.getString("lat");
            myLocation = new Location(lat, lng, location);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return myLocation;
    }

    @Override
    protected void onPostExecute(Location aLocation) {
        CharSequence text = LOCATION_SET + location;
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}