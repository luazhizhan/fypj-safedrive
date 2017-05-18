package com.fypj.icreative.model;

import java.util.HashMap;
import java.util.Map;

public class UserModel {
    private String uid;
    private String userName;
    private String email;
    private String password;
    private String insuranceCompany;
    private String fullName;
    private String NRIC;
    private String motorVehicleRegNo;
    private long contactNum;
    private int safetyIndex;

    public UserModel() {
    }

    public UserModel(String uid, String userName, String email) {
        this.uid = uid;
        this.userName = userName;
        this.email = email;
    }

    public UserModel(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserModel(String uid, String insuranceCompany, int safetyIndex) {
        this.uid = uid;
        this.insuranceCompany = insuranceCompany;
        this.safetyIndex = safetyIndex;
    }

    public UserModel(String uid, String fullName, String motorVehicleRegNo, String NRIC, long contactNum) {
        this.uid = uid;
        this.fullName = fullName;
        this.motorVehicleRegNo = motorVehicleRegNo;
        this.NRIC = NRIC;
        this.contactNum = contactNum;
    }

    public UserModel(String insuranceCompany,int safetyIndex, String fullName, String NRIC, String motorVehicleRegNo, long contactNum) {
        this.insuranceCompany = insuranceCompany;
        this.safetyIndex = safetyIndex;
        this.fullName = fullName;
        this.NRIC = NRIC;
        this.motorVehicleRegNo = motorVehicleRegNo;
        this.contactNum = contactNum;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userName", userName);
        result.put("insuranceCompany", insuranceCompany);
        result.put("safetyIndex", safetyIndex);
        result.put("surName", fullName);
        result.put("NRIC", NRIC);
        result.put("motorVehicleRegNo", motorVehicleRegNo);
        result.put("contactNum", contactNum);
        result.put("safetyIndex", safetyIndex);
        return result;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(String insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public int getSafetyIndex() {
        return safetyIndex;
    }

    public void setSafetyIndex(int safetyIndex) {
        this.safetyIndex = safetyIndex;
    }

    public String getNRIC() {
        return NRIC;
    }

    public void setNRIC(String NRIC) {
        this.NRIC = NRIC;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMotorVehicleRegNo() {
        return motorVehicleRegNo;
    }

    public void setMotorVehicleRegNo(String motorVehicleRegNo) {
        this.motorVehicleRegNo = motorVehicleRegNo;
    }

    public long getContactNum() {
        return contactNum;
    }

    public void setContactNum(long contactNum) {
        this.contactNum = contactNum;
    }
}
