package com.example.test2;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

public class GpsActivity extends AppCompatActivity {

    private static final int PERMISSIONS_FINE_LOCATION = 99;
    Integer count = 0;
    TextView tv_1,tv_labelspeed,tv_labelacc, tv_acc, tv_lat, tv_speed, tv_labellat, tv_labellon, tv_lon, tv_labelaltitude, tv_altitude, tv_labelaccuracy, tv_accuracy, tv_labelupdates, tv_updates;
    SwitchCompat sw_locationsupdates;
    ArrayList<Location> locationList = new ArrayList<Location>();

    //declarations for location services
    FusedLocationProviderClient fusedLocationProviderClient;

    LocationRequest locationRequest;
    LocationCallback locationCallBack;


    // code to establish connection with DB and get user Id
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseUser user = mAuth.getCurrentUser();

    String UID = user.getUid();


    // function that rounds up nu,bers to a given decimal place
    public static BigDecimal round(double d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_labelspeed = findViewById(R.id.tv_labelspeed);
        tv_lat = findViewById(R.id.tv_lat);
        tv_speed = findViewById(R.id.tv_speed);
        tv_labellat = findViewById(R.id.tv_labellat);
        tv_labellon = findViewById(R.id.tv_labellon);
        tv_lon = findViewById(R.id.tv_lon);
        tv_labelaltitude = findViewById(R.id.tv_labelaltitude);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_labelaccuracy = findViewById(R.id.tv_labelaccuracy);
        sw_locationsupdates = findViewById(R.id.sw_locationsupdates);
        tv_labelupdates = findViewById(R.id.tv_labelupdates);
        tv_updates = findViewById(R.id.tv_updates);
        tv_labelacc = findViewById(R.id.tv_labelacc);
        tv_acc = findViewById(R.id.tv_acc);


        // function that gets called for location updates
        locationCallBack = new LocationCallback() {

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                // list with locations for the calculation of acceleration. it holds max two locations
                Location location = locationResult.getLastLocation();
                if(locationList.size() == 0){
                    locationList.add(location);
                }else if(locationList.size() == 1){
                    locationList.add(location);
                    accDecCompute(locationList);
                }else{
                    locationList.remove(0);
                    locationList.add(location);
                    accDecCompute(locationList);
                }

                updateUIValues(location);
            }
        };



        //specifies the gps mode and update interval. now set to 5 sec
        locationRequest = LocationRequest.create()
                .setInterval(5000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        //code for the switch that toggles on and off location updatesd
        sw_locationsupdates.setOnClickListener(v -> {
            if (sw_locationsupdates.isChecked()) {
                starLocationUpdates();

            } else {
                stopLocationUpdates();
            }
        });

        updateGPS();


    }

    //functio that calculates the acceleration in kmph/s. the acceleration is calculated betwwen two speeds with a time difference between them of 5 sec(THE UPDATE INTERVAL)
    private void accDecCompute( ArrayList<Location> locationList){
        float speed0 = locationList.get(0).getSpeed();
        float speed1 = locationList.get(1).getSpeed();

        float kmphSpeed0 = round((speed0*3.6f),3).floatValue();
        float kmphSpeed1 = round((speed1*3.6f),3).floatValue();


        boolean isAcc;
        float acc = (kmphSpeed1 -kmphSpeed0) / 5;
        tv_acc.setText(String.valueOf(acc));

        // updates the DB with the Accelerations if they are between -100 and -2 and 2 and 100
        if (acc >= 2 && acc < 100){
            isAcc = true;
            updateDb(isAcc, acc, locationList);
        }else if(acc <= -2 && acc > -100) {
            isAcc = false;
            updateDb(isAcc, acc, locationList);
        }
    }


    //functions that updates the db with the speed, latitude, longitude, altitude, acceleration, time and userID
    private void updateDb(boolean isAc, float acc, ArrayList<Location> locationList){


        boolean isAcc = isAc;
        String speed, alt, acceleration, lon, lat, time;

        acceleration = String.valueOf(acc);
        speed = String.valueOf(locationList.get(0).getSpeed());
        alt = String.valueOf(locationList.get(1).getAltitude());
        lon = String.valueOf(locationList.get(1).getLongitude());
        lat = String.valueOf(locationList.get(1).getLatitude());
        time = String.valueOf(locationList.get(1).getTime());


        // if the value is acceleration it adds entris under the acceleration subtree if it is not under the deceleration
        if(isAcc == true){

            HashMap<String, Object> map =new HashMap<>();
            map.put("Speed" , speed);
            map.put("acceleration" , acceleration);
            map.put("altitude" , alt);
            map.put("longitude" , lon);
            map.put("latitude" , lat);
            map.put("time" , time);
            map.put("userID" , UID);
            FirebaseDatabase.getInstance("https://ergasia-aleph-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("acceleration").push().updateChildren(map);

        }else {

            HashMap<String, Object> map =new HashMap<>();
            map.put("Speed" , speed);
            map.put("acceleration" , acceleration);
            map.put("altitude" , alt);
            map.put("longitude" , lon);
            map.put("latitude" , lat);
            map.put("time" , time);
            map.put("userID" , UID);
            FirebaseDatabase.getInstance("https://ergasia-aleph-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("deceleration").push().updateChildren(map);
        }

        Context context = getApplicationContext();
        CharSequence text = "DataBase Updated";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    //stops location updates
    private void stopLocationUpdates() {
        tv_updates.setText("Off");

        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }

    // starts location updates. suppressed the error because if the user had previously not given the permission he would have been redirected at the main screen
    @SuppressLint("MissingPermission")
    private void starLocationUpdates() {
        tv_updates.setText("On");
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, Looper.getMainLooper());
        updateGPS();
    }

    //request the fine location permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    updateGPS();
                }
                else{
                    Toast.makeText(this, "This app requires permissions to run.", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    //updates the location
    private void updateGPS(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    updateUIValues(location);
                }
            });
        }
        else {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION} , PERMISSIONS_FINE_LOCATION);
            }
        }
    }

    // updates the values af the ui fields with the values of the new location
    private void updateUIValues(Location location) {
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));
        tv_accuracy.setText(String.valueOf(location.getAccuracy()));

        if (location.hasAltitude()){
            double alt = location.getAltitude();
            BigDecimal newAlt = round(alt,7);
            tv_altitude.setText(newAlt.toString());
        } else {
            tv_altitude.setText("Not Available");
        }

        if (location.hasSpeed()){
            float speed = location.getSpeed();
            BigDecimal kmphSpeed = round((speed*3.6f),3);
            tv_speed.setText(kmphSpeed.toString());
        } else {
            tv_speed.setText("Not Available");
        }

    }

}