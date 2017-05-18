package com.fypj.icreative.dal;

import com.fypj.icreative.model.TripModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class TripDAL {
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    // long unixTime = System.currentTimeMillis() / 1000L; //latest time have smaller value;

    public Task insertTripModel(TripModel tripModel) {
        String path = "trips/" + tripModel.getUserUID();
        String key = mDatabase.child(path).push().getKey();
        Map<String, Object> tripModelValues = tripModel.toMap(ServerValue.TIMESTAMP);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(path + "/" + key, tripModelValues);
        return mDatabase.updateChildren(childUpdates);
    }

    public Query getAllTripByUID(String UID) {
        String path = "trips/" + UID;
        return mDatabase.child(path);
    }

    public Query getTripByDateCreated(long startDate, long endDate, String UID){
        String path = "trips/" + UID;
        return mDatabase.child(path).orderByChild("dateCreated").startAt(startDate).endAt(endDate);
    }
}
