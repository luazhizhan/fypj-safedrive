package com.fypj.icreative.dal;

import com.fypj.icreative.model.UserModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 6/21/2016.
 */
public class UserDAL {
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    public Query getUserByUID(String UID) {
        return mDatabase.child("users").child(UID);
    }

    public Task updateSafetyIndex(UserModel userModel){
        String path = "/users/" + userModel.getUid() + "/safetyIndex";
        return mDatabase.child(path).setValue(userModel.getSafetyIndex());
    }

    public Task updateUser(UserModel userModel) {
        Map<String, Object> userModelValues = userModel.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + userModel.getUid(), userModelValues);
        return mDatabase.updateChildren(childUpdates);
    }
}
