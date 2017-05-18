package com.fypj.icreative.model;

import android.graphics.Bitmap;
import android.net.Uri;

public class CarAccidentClaimPhotosModel {
    private Uri fileUri;
    private String dateTaken;
    private Bitmap photoBitmap;
    private String currentLocation;

    public CarAccidentClaimPhotosModel(Uri fileUri, String dateTaken, String currentLocation) {
        this.fileUri = fileUri;
        this.dateTaken = dateTaken;
        this.currentLocation = currentLocation;
    }

    public Uri getFileUri() {
        return fileUri;
    }

    public void setFileUri(Uri fileUri) {
        this.fileUri = fileUri;
    }

    public String getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(String dateTaken) {
        this.dateTaken = dateTaken;
    }

    public Bitmap getPhotoBitmap() {
        return photoBitmap;
    }

    public void setPhotoBitmap(Bitmap photoBitmap) {
        this.photoBitmap = photoBitmap;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }
}
