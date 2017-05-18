package com.fypj.icreative.fragment;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
import com.fypj.icreative.R;
import com.fypj.icreative.activity.MainActivity;
import com.fypj.icreative.controller.StartDrivingController;
import com.fypj.icreative.dal.UserDAL;
import com.fypj.icreative.model.TripModel;
import com.fypj.icreative.model.UserModel;
import com.fypj.icreative.service.GPSService;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class StartDrivingFragment extends Fragment implements SensorEventListener {
    private GPSService gps;
    private StartDrivingController startDrivingController;
    private SpeedoMeterActivityReceiver speedoMeterActivityReceiver;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ArrayList<Double> kphSpeedArr;
    private double startLatitude;
    private double startLongitude;
    private double previousLatitude;
    private double previousLongitude;
    private double currentLatitude;
    private double currentLongitude;
    private double totalDistTravelled;
    private int vigorousTurnCount;
    private boolean hasStartLocation = false;
    private UserDAL userDAL;
    private UserModel userModel;
    private String userUID;
    private MainActivity mainActivity;
    private boolean firstLoad = true;
    private BeaconManager beaconManager;
    private Region region;
    private AlertDialog alertExitBeaconDialog;
    private boolean exitBeaconRegion = true;
    private boolean jusDetectedSharpTurn = false;
    private TextView startDrivingSafetyIndexTxtView;
    private TextView startDrivingSpeedTxtView;
    private TextView startDrivingDistTxtView;
    private TextView startDrivingSharpTurnTxtView;
    private TextView startDrivingSpeedingTxtView;
    private boolean isStartClick = false;
    private Button startDrivingButton, startDrivingAccidentBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainActivity = ((MainActivity) getActivity());
        mainActivity.getSupportActionBar().show();
        mainActivity.setToolBarTitle("Start Driving");
        mainActivity.setTripFragmentToolbarControlsVisibility(false);
        View layout = inflater.inflate(R.layout.fragment_start_driving, container, false);
        userDAL = new UserDAL();
        userUID = mainActivity.checkUserAuth();
        kphSpeedArr = new ArrayList<Double>();
        startDrivingController = new StartDrivingController(getActivity());
        startDrivingSafetyIndexTxtView = (TextView) layout.findViewById(R.id.startDrivingSafetyIndexTxtView);
        startDrivingSpeedTxtView = (TextView) layout.findViewById(R.id.startDrivingSpeedTxtView);
        startDrivingDistTxtView = (TextView) layout.findViewById(R.id.startDrivingDistTxtView);
        startDrivingSharpTurnTxtView = (TextView) layout.findViewById(R.id.startDrivingSharpTurnTxtView);
        startDrivingSpeedingTxtView = (TextView) layout.findViewById(R.id.startDrivingSpeedingTxtView);
        startDrivingButton = (Button) layout.findViewById(R.id.startDrivingButton);
        startDrivingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isStartClick) {
                    setUpBeaconManager();
                    setAlertExitBeaconDialog();
                    startBeaconMonitoring();
                }
            }
        });
        startDrivingAccidentBtn = (Button) layout.findViewById(R.id.startDrivingAccidentBtn);
        startDrivingAccidentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Stop Recording?");
                builder.setMessage("Do you want to report an accident? ");
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TripFragment tripFragment = new TripFragment();
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.main_container, tripFragment, "tripFragment");
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                });
                builder.show();
            }
        });
        return layout;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (isStartClick) {
            startBeaconMonitoring();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        try {
            if (totalDistTravelled >= 500f) {
                if (!exitBeaconRegion) {
                    TripModel tripModel = getCurrentTripData();
                    int currentTripSafetyIndex = startDrivingController.insertTripModelAndGetCurrentTripSafetyIndex(tripModel, userModel.getSafetyIndex());
                    userModel.setUid(userUID);
                    userModel.setSafetyIndex(currentTripSafetyIndex);
                    userDAL.updateSafetyIndex(userModel);
                }
            }
            reset();
            getActivity().unregisterReceiver(speedoMeterActivityReceiver);
            sensorManager.unregisterListener(this);
            beaconManager.stopMonitoring(region);
            gps.stopUsingGPS();
        } catch (IllegalArgumentException ignored) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            reset();
            sensorManager.unregisterListener(this);
            getActivity().unregisterReceiver(speedoMeterActivityReceiver);
            gps.stopUsingGPS();
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values[0] >= 2) {
            if (!jusDetectedSharpTurn) {
                vigorousTurnCount += 1;
                startDrivingSharpTurnTxtView.setText(String.valueOf(vigorousTurnCount) + "(Counts)");
                jusDetectedSharpTurn = true;
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        jusDetectedSharpTurn = false;
                    }
                }, 1000);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void startBeaconMonitoring() {
        SystemRequirementsChecker.checkWithDefaultDialogs(getActivity());
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                isStartClick = true;
                beaconManager.startMonitoring(region);
                startDrivingButton.setVisibility(View.GONE);
                startDrivingAccidentBtn.setVisibility(View.VISIBLE);
                alertExitBeaconDialog.show();
            }
        });
    }

    private void setUpAccelerometer() {
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void setUpBeaconManager() {
        beaconManager = new BeaconManager(getActivity().getApplicationContext());
        region = new Region("monitored region", UUID.fromString("b9407f30-f5f8-466e-aff9-25556b57fe6d"), 51282, 24396);
        beaconManager.setBackgroundScanPeriod(200, 0);
        beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                try {
                    mainActivity.showProgressDialog("Beacon Detected...").show();
                    if (firstLoad) {
                        getUserData();
                        Toast.makeText(getActivity(), "Driving is now being recorded",
                                Toast.LENGTH_LONG).show();
                        setUpAccelerometer();
                    }
                    GetSpeedCamLocAsync getSpeedCamLocAsync = new GetSpeedCamLocAsync();
                    getSpeedCamLocAsync.execute();
                    exitBeaconRegion = false;
                    alertExitBeaconDialog.cancel();
                } catch (Exception ignored) {

                }
            }

            @Override
            public void onExitedRegion(Region region) {
                reset();
                exitBeaconRegion = true;
                alertExitBeaconDialog.show();
            }
        });
    }

    private void getUserData() {
        userDAL.getUserByUID(userUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    userModel = dataSnapshot.getValue(UserModel.class);
                    setSafetyIndex(userModel.getSafetyIndex());
                    firstLoad = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setSafetyIndex(int tripSafetyIndex) {
        String safetyIndexWBending = null;
        if (tripSafetyIndex >= 80) {
            safetyIndexWBending = String.valueOf(tripSafetyIndex + "(Band 1)");
            startDrivingSafetyIndexTxtView.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.trip_safety_index_band_1));
        } else if (tripSafetyIndex >= 60) {
            safetyIndexWBending = String.valueOf(tripSafetyIndex + "(Band 2)");
            startDrivingSafetyIndexTxtView.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.trip_safety_index_band_2));
        } else if (tripSafetyIndex >= 40) {
            safetyIndexWBending = String.valueOf(tripSafetyIndex + "(Band 3)");
            startDrivingSafetyIndexTxtView.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.trip_safety_index_band_3));
        } else {
            safetyIndexWBending = String.valueOf(tripSafetyIndex + "(Band 4)");
            startDrivingSafetyIndexTxtView.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.trip_safety_index_band_4));
        }
        startDrivingSafetyIndexTxtView.setText(safetyIndexWBending);
    }

    private void setAlertExitBeaconDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Alert Dialog");
        builder.setMessage("Beacon not found.");
        alertExitBeaconDialog = builder.create();
    }

    private void reset() {
        startLatitude = 0.0;
        startLongitude = 0.0;
        currentLongitude = 0.0;
        currentLatitude = 0.0;
        previousLatitude = 0.0;
        previousLongitude = 0.0;
        totalDistTravelled = 0;
        vigorousTurnCount = 0;
        hasStartLocation = false;
        exitBeaconRegion = true;
        kphSpeedArr.clear();
        isStartClick = false;
        startDrivingController.resetVariable();
        startDrivingSpeedTxtView.setText("0km/h");
        startDrivingDistTxtView.setText("0m");
        startDrivingSharpTurnTxtView.setText(String.valueOf(vigorousTurnCount) + "(Counts)");
        startDrivingSpeedingTxtView.setText("0 (Counts)");
        startDrivingButton.setBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.primary_dark));
        startDrivingButton.setVisibility(View.VISIBLE);
        startDrivingAccidentBtn.setVisibility(View.GONE);
        startDrivingSafetyIndexTxtView.setText("Not Available");
        startDrivingSafetyIndexTxtView.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.primary_text));
        if (gps != null) {
            gps.resetVariables();
        }
    }

    private void startGPS() {
        gps = new GPSService(getActivity());
        if (!gps.canGetLocation()) {
            gps.showSettingsAlert();
        } else {
            speedoMeterActivityReceiver = new SpeedoMeterActivityReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(GPSService.GPS_TRACKER_ACTION);
            getActivity().registerReceiver(speedoMeterActivityReceiver, intentFilter);
            Intent intent = new Intent(getActivity(),
                    GPSService.class);
            getActivity().startService(intent);
        }
        mainActivity.hideProgressDialog();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void distanceCalculation() {
        totalDistTravelled += startDrivingController.distanceTravelledInMeter(previousLatitude, previousLongitude, currentLatitude, currentLongitude);
        startDrivingDistTxtView.setText(String.valueOf(totalDistTravelled + "m"));
        startDrivingController.checkForSpeedCam(currentLatitude, currentLongitude);
    }

    public TripModel getCurrentTripData() {
        TripModel tripModel = new TripModel(totalDistTravelled, startLatitude, startLongitude, currentLatitude, currentLongitude, vigorousTurnCount);
        tripModel.setAvgSpeed(startDrivingController.getAvgSpeed(kphSpeedArr));
        tripModel.setUserUID(((MainActivity) getActivity()).checkUserAuth());
        return tripModel;
    }

    private class SpeedoMeterActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            float speed = 0f;
            if (currentLatitude != arg1.getDoubleExtra("latitude", 0.0)) {
                if (!hasStartLocation) {
                    startLatitude = arg1.getDoubleExtra("latitude", 0.0);
                    startLongitude = arg1.getDoubleExtra("longitude", 0.0);
                    previousLatitude = arg1.getDoubleExtra("latitude", 0.0);
                    previousLongitude = arg1.getDoubleExtra("longitude", 0.0);
                    totalDistTravelled = 0f;
                    hasStartLocation = true;
                }
                currentLatitude = arg1.getDoubleExtra("latitude", 0.0);
                currentLongitude = arg1.getDoubleExtra("longitude", 0.0);
                speed = arg1.getFloatExtra("Speed", 0f);
                distanceCalculation();
                kphSpeedArr.add((double) speed);
                previousLatitude = arg1.getDoubleExtra("latitude", 0.0);
                previousLongitude = arg1.getDoubleExtra("longitude", 0.0);
            }
            double formattedSpeed = startDrivingController.round(speed, 1).doubleValue();
            startDrivingSpeedTxtView.setText((String.valueOf(formattedSpeed + "km/h")));
            startDrivingSpeedingTxtView.setText((String.valueOf(startDrivingController.speedCheck(formattedSpeed) + " (Counts)")));
        }
    }

    private class GetSpeedCamLocAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            startDrivingController.getSpeedCamLocation();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            startGPS();
        }
    }
}
