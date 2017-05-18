package com.fypj.icreative.dal;

import android.os.Environment;

import com.fypj.icreative.model.PrivateSettlementModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 6/28/2016.
 */
public class PrivateSettlementDAL {
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    public Task insertPrivateSettlementModel(PrivateSettlementModel privateSettlementModel) {
        String path = "privateSettlements/" + privateSettlementModel.getTripUID();
        Map<String, Object> privateSettlementValue = privateSettlementModel.toMap(ServerValue.TIMESTAMP);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(path, privateSettlementValue);
        return mDatabase.updateChildren(childUpdates);
    }

    public UploadTask insertPrivateSettlementFormToStorage(PrivateSettlementModel privateSettlementModel) {
        StorageReference storageRef = mStorageRef.child("PrivateSettlementForms/" + privateSettlementModel.getFileName());
        return storageRef.putBytes(privateSettlementModel.getPrivateSettlementForm());
    }

    public Query getPrivateSettlementByTripUID(String tripUID) {
        String path = "privateSettlements/" + tripUID;
        return mDatabase.child(path);
    }

    public FileDownloadTask downloadPrivateSettlementFrom(String fileName) {
        StorageReference storageRef = mStorageRef.child("PrivateSettlementForms/" + fileName);
        String pdfFilePath = String.valueOf(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS));
        File localFile = new File(pdfFilePath + "/" + fileName);
        return storageRef.getFile(localFile);
    }
}
