package com.example.sociallibrary;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.content.ContextCompat;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by shiva on 8/4/17.
 */

public class GpsTracker implements LocationListener {

    Context context;

    public GpsTracker(Context context) {
        super();
        this.context = context;
    }

    public Location getLocation(){
        if (ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            Log.d("gps","error1");
            return null;
        }
        try {
            LocationManager lm = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            boolean isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)||lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (isGPSEnabled){
                Log.d("gps","error2");
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000,10,this);

                Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Log.d("gps",  loc.toString());
                return loc;
            }else{
                Log.d("gps","error3");
            }
        }catch (Exception e){
            Log.d("gps","error4");
            e.printStackTrace();
        }
        Log.d("gps","error5");
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}