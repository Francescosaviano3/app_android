package com.worldgn.connector;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;


/**
 * Created by WGN on 27-09-2017.
 */

class LocationValues implements LocationListener {


    private static String TAG = LocationValues.class.getSimpleName();
    private static LocationValues instance = null;
    private double latitude;
    private double longitude;
    private static String LOCK = "lock";
    private Context context;
    LocationManager locationManager;
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10.0f; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    public static LocationValues getInstance(Context context) {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new LocationValues(context);
                }
            }
        }
        return instance;
    }

    private LocationValues(Context context) {
        this.context = context;
    }


    public void registerLocation() {
        DebugLogger.i(TAG, "registerLocation");
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //Added By Abhijeet 6-3-18
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, LocationValues.this, Looper.getMainLooper());
        }
    }
    public void unRegisterLocation() {
        DebugLogger.i(TAG, "unregisterLocation");
        if(locationManager != null)
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        DebugLogger.i(TAG, "lat n long "+location.getLatitude()+" "+location.getLongitude());
        latitude    = location.getLatitude();
        longitude   = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public String getLatitude() {
        return Double.toString(latitude);
    }

    public String getLongitude() {
        return Double.toString(longitude);
    }


}
