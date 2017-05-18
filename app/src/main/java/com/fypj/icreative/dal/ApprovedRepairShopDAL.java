package com.fypj.icreative.dal;

import com.fypj.icreative.model.ApprovedRepairShopModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class ApprovedRepairShopDAL {
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public Task insertAvivaApprovedRepairShopModel(ApprovedRepairShopModel approvedRepairShopModel) {
        String path = "approvedRepairShops/aviva/" + approvedRepairShopModel.getRegion().toLowerCase();
        String key = mDatabase.child(path).push().getKey();
        Map<String, Object> approvedRepairShopsValue = approvedRepairShopModel.toMap(ServerValue.TIMESTAMP);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(path + "/" + key, approvedRepairShopsValue);
        return mDatabase.updateChildren(childUpdates);
    }

    public Query getApprovedRepairShopByRegion(String approvalCompany, String region) {
        String path = "approvedRepairShops/" + approvalCompany.toLowerCase() + "/" + region.toLowerCase();
        return mDatabase.child(path);
    }
}
