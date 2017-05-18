package com.fypj.icreative.service;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.app.Service;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import com.fypj.icreative.utils.Utils;

/**
 * Created by Zhi Zhan on 19/6/2016.
 */
public class GPSService extends Service implements LocationListener {
    private final Context mContext;
    public final static String GPS_TRACKER_ACTION = "GPS_TRACKER_ACTION";

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    static boolean canGetLocation = false;

    Location location;
    static double latitude;
    static double longitude;
    static float speed;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;
    private static final long MIN_TIME_BW_UPDATES = 0;

    protected LocationManager locationManager;
    private GetLocationDetailsThread getLocationDetailsThread;

    public GPSService() {
        this.mContext = null;
    }


    public GPSService(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (isGPSEnabled || isNetworkEnabled) {
                canGetLocation = true;
                if (isNetworkEnabled) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Utils.MY_PERMISSIONS_REQUEST_LOCATION);
                        } else {
                            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        }
                    } else {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
                if (isGPSEnabled) {
                    if (location == null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            } else {
                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            }
                        } else {
                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return location;
    }

    public void stopUsingGPS() {
        if (locationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    locationManager.removeUpdates(GPSService.this);
                }
            } else {
                locationManager.removeUpdates(GPSService.this);
            }
        }
        canGetLocation = false;
    }

    public boolean canGetLocation() {
        return canGetLocation;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestLocationUpdate() {
        if (mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        }
    }


    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("GPS is settings");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    public Float getSpeed() {
        if (location != null) {
            speed = location.getSpeed();
        }
        return speed;
    }


    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    public void resetVariables(){
        latitude = 0.0;
        longitude = 0.0;
        speed = 0.0f;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getLocationDetailsThread = new GetLocationDetailsThread();
        getLocationDetailsThread.start();
        return START_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        speed = location.getSpeed();
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class GetLocationDetailsThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (canGetLocation) {
                try {
                    Thread.sleep(1500);
                    Intent intent = new Intent();
                    intent.setAction(GPS_TRACKER_ACTION);
                    //(location.getSpeed()*2.2369); convert to mph
                    intent.putExtra("Speed", ((getSpeed() * 3600) / 1000));
                    intent.putExtra("latitude", getLatitude());
                    intent.putExtra("longitude", getLongitude());
                    sendBroadcast(intent);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
