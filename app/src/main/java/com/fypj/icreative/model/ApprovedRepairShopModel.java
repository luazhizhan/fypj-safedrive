package com.fypj.icreative.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.HashMap;
import java.util.Map;

public class ApprovedRepairShopModel implements Parcelable {
    private String uid;
    private String companyName;
    private String address;
    private String telNo;
    private String region;
    private String remarks;
    private long createdTime;


    public ApprovedRepairShopModel() {

    }

    public ApprovedRepairShopModel(String companyName, String address, String telNo, String region, String remarks) {
        this.companyName = companyName;
        this.address = address;
        this.telNo = telNo;
        this.region = region;
        this.remarks = remarks;
    }

    public Map<String, Object> toMap(Map<String, String> serverTimeStamp) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("companyName", companyName);
        result.put("address", address);
        result.put("telNo", telNo);
        result.put("region", region);
        result.put("remarks", remarks);
        result.put("createdTime", serverTimeStamp);
        return result;
    }

    public ApprovedRepairShopModel(Parcel in) {
        uid = in.readString();
        companyName = in.readString();
        address = in.readString();
        telNo = in.readString();
        region = in.readString();
        remarks = in.readString();
        createdTime = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(companyName);
        dest.writeString(address);
        dest.writeString(telNo);
        dest.writeString(region);
        dest.writeString(remarks);
        dest.writeLong(createdTime);
    }

    public static final Creator<ApprovedRepairShopModel> CREATOR = new Creator<ApprovedRepairShopModel>() {
        @Override
        public ApprovedRepairShopModel createFromParcel(Parcel in) {
            return new ApprovedRepairShopModel(in);
        }

        @Override
        public ApprovedRepairShopModel[] newArray(int size) {
            return new ApprovedRepairShopModel[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelNo() {
        return telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }
}
