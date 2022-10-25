package com.example.test2;

public class AccModel {

    private String userID, Speed, acceleration, longitude, latitude, altitude, time;



    // Model class to store and access the information drawn from the db
    public AccModel(){
    }

    public AccModel(String userID, String speed, String acceleration, String longitude, String latitude, String altitude, String time) {
        this.userID = userID;
        this.Speed = speed;
        this.acceleration = acceleration;
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
        this.time = time;
    }

    public String getUserID() {
        return userID;
    }


    // getters and setters for every field on the db
    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getSpeed() {
        return Speed;
    }

    public void setSpeed(String speed) {
        Speed = speed;
    }

    public String getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(String acceleration) {
        this.acceleration = acceleration;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

