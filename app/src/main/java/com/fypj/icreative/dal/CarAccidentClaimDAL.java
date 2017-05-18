package com.fypj.icreative.dal;

import android.os.Environment;

import com.fypj.icreative.model.CarAccidentClaimModel;
import com.fypj.icreative.model.UserModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CarAccidentClaimDAL {
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    public Task insertCarAccidentClaimModel(CarAccidentClaimModel carAccidentClaimModel, UserModel userModel) {
        String path = "carAccidentClaims/" + userModel.getUid() + "/" + carAccidentClaimModel.getTripUID();
        Map<String, Object> carAccidentClaimsValue = carAccidentClaimModel.toMap(ServerValue.TIMESTAMP);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(path, carAccidentClaimsValue);
        return mDatabase.updateChildren(childUpdates);
    }

    public Task updateCarAccidentRepairShopInfo(String repairShopInfo, String tripUID, String userUID) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("carAccidentClaims/" + userUID + "/" + tripUID + "/" + "repairShopInfo", repairShopInfo);
        return mDatabase.updateChildren(childUpdates);
    }

    public UploadTask insertCarAccidentClaimFormToStorage(CarAccidentClaimModel carAccidentClaimModel) {
        StorageReference storageRef = mStorageRef.child("CarAccidentForms/" + carAccidentClaimModel.getFileName());
        return storageRef.putBytes(carAccidentClaimModel.getCarAccidentClaimForm());
    }

    public Query getCarAccidentClaimByTripUID(String tripUID) {
        String path = "carAccidentClaims/" + tripUID;
        return mDatabase.child(path);
    }

    public FileDownloadTask downloadCarAccidentClaimFrom(String fileName) {
        StorageReference storageRef = mStorageRef.child("CarAccidentForms/" + fileName);
        String pdfFilePath = String.valueOf(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS));
        File localFile = new File(pdfFilePath + "/" + fileName);
        return storageRef.getFile(localFile);
    }

}
