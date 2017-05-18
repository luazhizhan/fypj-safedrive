package com.fypj.icreative.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class TripModel implements Parcelable {
    private String uid;
    private long dateCreated;
    private double avgSpeed;
    private double distanceTravelled;
    private int vigorousTurnCount;
    private int speedingCount;
    private double startLat;
    private double startLon;
    private double endLat;
    private double endLon;
    private String userUID;
    private int currentTripSafetyIndex;
    private int tripSafetyIndex;
    private byte[] googleMapSnapShot;

    public TripModel() {

    }

    public TripModel(double distanceTravelled, double startLat, double startLon, double endLat, double endLon, int vigorousTurnCount) {
        this.distanceTravelled = distanceTravelled;
        this.startLat = startLat;
        this.startLon = startLon;
        this.endLat = endLat;
        this.endLon = endLon;
        this.vigorousTurnCount = vigorousTurnCount;
    }

    public Map<String, Object> toMap(Map<String, String> serverTimeStamp) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("dateCreated", serverTimeStamp);
        result.put("avgSpeed", avgSpeed);
        result.put("distanceTravelled", distanceTravelled);
        result.put("vigorousTurnCount", vigorousTurnCount);
        result.put("distanceTravelled", distanceTravelled);
        result.put("speedingCount", speedingCount);
        result.put("startLat", startLat);
        result.put("startLon", startLon);
        result.put("endLat", endLat);
        result.put("endLon", endLon);
        result.put("currentTripSafetyIndex", currentTripSafetyIndex);
        result.put("tripSafetyIndex", tripSafetyIndex);
        return result;
    }

    public TripModel(Parcel in) {
        uid = in.readString();
        dateCreated = in.readLong();
        avgSpeed = in.readDouble();
        distanceTravelled = in.readDouble();
        vigorousTurnCount = in.readInt();
        speedingCount = in.readInt();
        startLat = in.readDouble();
        startLon = in.readDouble();
        endLat = in.readDouble();
        endLon = in.readDouble();
        currentTripSafetyIndex = in.readInt();
        tripSafetyIndex = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeLong(dateCreated);
        dest.writeDouble(avgSpeed);
        dest.writeDouble(distanceTravelled);
        dest.writeInt(vigorousTurnCount);
        dest.writeInt(speedingCount);
        dest.writeDouble(startLat);
        dest.writeDouble(startLon);
        dest.writeDouble(endLat);
        dest.writeDouble(endLon);
        dest.writeInt(currentTripSafetyIndex);
        dest.writeInt(tripSafetyIndex);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TripModel> CREATOR = new Creator<TripModel>() {
        @Override
        public TripModel createFromParcel(Parcel in) {
            return new TripModel(in);
        }

        @Override
        public TripModel[] newArray(int size) {
            return new TripModel[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public double getDistanceTravelled() {
        return distanceTravelled;
    }

    public void setDistanceTravelled(double distanceTravelled) {
        this.distanceTravelled = distanceTravelled;
    }

    public int getVigorousTurnCount() {
        return vigorousTurnCount;
    }

    public void setVigorousTurnCount(int vigorousTurnCount) {
        this.vigorousTurnCount = vigorousTurnCount;
    }

    public int getSpeedingCount() {
        return speedingCount;
    }

    public void setSpeedingCount(int speedingCount) {
        this.speedingCount = speedingCount;
    }

    public double getStartLat() {
        return startLat;
    }

    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }

    public double getStartLon() {
        return startLon;
    }

    public void setStartLon(double startLon) {
        this.startLon = startLon;
    }

    public double getEndLat() {
        return endLat;
    }

    public void setEndLat(double endLat) {
        this.endLat = endLat;
    }

    public double getEndLon() {
        return endLon;
    }

    public void setEndLon(double endLon) {
        this.endLon = endLon;
    }

    public int getCurrentTripSafetyIndex() {
        return currentTripSafetyIndex;
    }

    public void setCurrentTripSafetyIndex(int currentTripSafetyIndex) {
        this.currentTripSafetyIndex = currentTripSafetyIndex;
    }

    public int getTripSafetyIndex() {
        return tripSafetyIndex;
    }

    public void setTripSafetyIndex(int tripSafetyIndex) {
        this.tripSafetyIndex = tripSafetyIndex;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public byte[] getGoogleMapSnapShot() {
        return googleMapSnapShot;
    }

    public void setGoogleMapSnapShot(byte[] googleMapSnapShot) {
        this.googleMapSnapShot = googleMapSnapShot;
    }
}
