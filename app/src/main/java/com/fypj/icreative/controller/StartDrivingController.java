package com.fypj.icreative.controller;

import android.content.Context;
import android.location.Location;
import android.support.v7.app.AlertDialog;

import com.fypj.icreative.dal.TripDAL;
import com.fypj.icreative.model.SpeedCamLocationModel;
import com.fypj.icreative.model.TripModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class StartDrivingController {
    private final Context context;
    private int exceedSpeedLimitTimer;
    private int exceedSpeedLimitCount;
    private ArrayList<SpeedCamLocationModel> speedCamLocationModelArr = new ArrayList<SpeedCamLocationModel>();
    private ArrayList<SpeedCamLocationModel> speedCamLocationModelArrCopy;

    public StartDrivingController(Context context) {
        this.context = context;
    }

    public float fromMillisecondToSecond(float miliseconds) {
        return miliseconds / 1000;
    }

    public float fromMillisecondToHour(float miliseconds) {
        return miliseconds / 3600000;
    }

    public double fromKPHToMPS(double val) {
        return round((val * 0.28f), 1).doubleValue();
    }

    public BigDecimal round(double d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    public int distanceTravelledInMeter(double startLat, double startLon, double endLat, double endLon) {
        Location startLoc = new Location("point A");
        startLoc.setLatitude(startLat);
        startLoc.setLongitude(startLon);

        Location endLoc = new Location("point B");
        endLoc.setLatitude(endLat);
        endLoc.setLongitude(endLon);
        double distance = startLoc.distanceTo(endLoc);
        return round(distance, 0).intValue();
    }

    public void checkForSpeedCam(double currentLat, double currentLon) {
        Location currentLoc = new Location("point A");
        currentLoc.setLatitude(currentLat);
        currentLoc.setLongitude(currentLon);
        for (SpeedCamLocationModel speedCamLocationModel : speedCamLocationModelArr) {
            Location speedCamLoc = new Location("point B");
            speedCamLoc.setLatitude(Double.valueOf(speedCamLocationModel.getLat()));
            speedCamLoc.setLongitude(Double.valueOf(speedCamLocationModel.getLon()));
            double distance = currentLoc.distanceTo(speedCamLoc);
            if (distance <= 500f && distance >= 0f) {
                speedCamLocationModelArr.remove(speedCamLocationModel);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Speed Camera Alert");
                builder.setMessage("Speed camera within 500ms.\nPlease drive with care.");
                final AlertDialog alertdialog = builder.create();
                alertdialog.show();
                final Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        alertdialog.cancel();
                        timer.cancel();
                    }
                }, 5000);
                break;
            }
        }
    }

    public double getAvgSpeed(ArrayList<Double> speedArr) {
        double totalSpeed = 0f;
        if (!speedArr.isEmpty()) {
            for (double speed : speedArr) {
                totalSpeed += speed;
            }
            double avgSpeed = totalSpeed / speedArr.size();
            return round(avgSpeed, 1).doubleValue();
        }
        return 0.0;
    }


    public int speedCheck(double speed) {
        if (speed >= 101) {
            exceedSpeedLimitTimer += 1;
            if (exceedSpeedLimitTimer >= 5) {
                //Increase penalty cost
                exceedSpeedLimitTimer = 0;
                exceedSpeedLimitCount += 1;
            }
        }
        return exceedSpeedLimitCount;
    }


    public void getSpeedCamLocation() {
        try {
            InputStream ins = context.getResources().openRawResource(context.getResources().getIdentifier("spf_dsecs", "raw", context.getPackageName()));
            StringBuilder buf = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(ins));
            String str;
            while ((str = br.readLine()) != null) {
                buf.append(str);
            }
            br.close();
            String html = buf.toString();
            Document doc = Jsoup.parse(html, "", Parser.xmlParser());
            speedCamLocationModelArr = new ArrayList<SpeedCamLocationModel>();
            for (Element e : doc.select("coordinates")) {
                String[] sCoordinates = e.text().split(",");
                String cdata = e.parents().select("description").text();
                Document cdataDoc = Jsoup.parse(cdata);
                for (Element des : cdataDoc.select("td")) {
                    if (des.text().equals("DESCRIPTION")) {
                        String description = des.nextElementSibling().text();
                        SpeedCamLocationModel speedCamModel = new SpeedCamLocationModel(description, sCoordinates[1], sCoordinates[0]);
                        if (speedCamLocationModelArr == null) {
                            speedCamLocationModelArr = new ArrayList<SpeedCamLocationModel>();
                        }
                        speedCamLocationModelArr.add(speedCamModel);
                        break;
                    }
                }
            }
            speedCamLocationModelArrCopy = speedCamLocationModelArr;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void resetVariable() {
        exceedSpeedLimitTimer = 0;
        exceedSpeedLimitCount = 0;
        speedCamLocationModelArr = speedCamLocationModelArrCopy;

    }

    public int insertTripModelAndGetCurrentTripSafetyIndex(TripModel tripModel, int currentSafetyIndex) {
        TripDAL tripDAL = new TripDAL();
        SafetyIndexController safetyIndexController = new SafetyIndexController();
        int safetyTripIndex = safetyIndexController.tripSafetyIndexCalculation(tripModel,exceedSpeedLimitCount);
        currentSafetyIndex = (int) Math.ceil((currentSafetyIndex + safetyTripIndex) / 2);
        tripModel.setSpeedingCount(exceedSpeedLimitCount);
        tripModel.setTripSafetyIndex(safetyTripIndex);
        tripModel.setCurrentTripSafetyIndex(currentSafetyIndex);
        tripDAL.insertTripModel(tripModel);
        return currentSafetyIndex;
    }
}
