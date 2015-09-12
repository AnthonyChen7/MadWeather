package com.example.madweather;

/**
 * Created by Noman on 4/24/2015.
 */
public class WeatherData {

    private String event;
    private double probability;

    public WeatherData(String event, double probability){
        this.event = event;
        this.probability = probability;
    }

    public String getEvent(){
        return this.event;
    }

    public double getProbability(){
        return this.probability;
    }

    public void setProbability(Double probability) {
        this.probability = probability;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
