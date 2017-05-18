package com.fypj.icreative.model;

import java.util.HashMap;
import java.util.Map;

public class PrivateSettlementModel {
    private String uid;
    private String ppFullName;
    private String ppNRIC;
    private long ppContactNum;
    private String ppMotorVehicleRegNo;
    private String ppInsuranceCompany;
    private double ppCompensationAmt;
    private String rpFullName;
    private String rpNRIC;
    private long rpContactNum;
    private String rpMotorVehicleRegNo;
    private String rpInsuranceCompany;
    private long dateTimeOfAccident;
    private long dateSubmitted;
    private String fileName;
    private String tripUID;
    private byte[] privateSettlementForm;

    public PrivateSettlementModel() {
    }

    public PrivateSettlementModel(String ppFullName, String ppNRIC, long ppContactNum, String ppMotorVehicleRegNo, String ppInsuranceCompany, double ppCompensationAmt) {
        this.ppFullName = ppFullName;
        this.ppNRIC = ppNRIC;
        this.ppContactNum = ppContactNum;
        this.ppMotorVehicleRegNo = ppMotorVehicleRegNo;
        this.ppInsuranceCompany = ppInsuranceCompany;
        this.ppCompensationAmt = ppCompensationAmt;
    }

    public Map<String, Object> toMap(Map<String, String> serverTimeStamp) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("ppFullName", ppFullName);
        result.put("ppNRIC", ppNRIC);
        result.put("ppContactNum", ppContactNum);
        result.put("ppMotorVehicleRegNo", ppMotorVehicleRegNo);
        result.put("ppInsuranceCompany", ppInsuranceCompany);
        result.put("ppCompensationAmt", ppCompensationAmt);
        result.put("rpFullName", rpFullName);
        result.put("rpNRIC", rpNRIC);
        result.put("rpContactNum", rpContactNum);
        result.put("rpMotorVehicleRegNo", rpMotorVehicleRegNo);
        result.put("rpInsuranceCompany", rpInsuranceCompany);
        result.put("dateTimeOfAccident", dateTimeOfAccident);
        result.put("dateSubmitted", serverTimeStamp);
        result.put("fileName", fileName);
        return result;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPpFullName() {
        return ppFullName;
    }

    public void setPpFullName(String ppFullName) {
        this.ppFullName = ppFullName;
    }

    public String getPpNRIC() {
        return ppNRIC;
    }

    public void setPpNRIC(String ppNRIC) {
        this.ppNRIC = ppNRIC;
    }

    public long getPpContactNum() {
        return ppContactNum;
    }

    public void setPpContactNum(long ppContactNum) {
        this.ppContactNum = ppContactNum;
    }

    public String getPpMotorVehicleRegNo() {
        return ppMotorVehicleRegNo;
    }

    public void setPpMotorVehicleRegNo(String ppMotorVehicleRegNo) {
        this.ppMotorVehicleRegNo = ppMotorVehicleRegNo;
    }

    public String getPpInsuranceCompany() {
        return ppInsuranceCompany;
    }

    public void setPpInsuranceCompany(String ppInsuranceCompany) {
        this.ppInsuranceCompany = ppInsuranceCompany;
    }

    public double getPpCompensationAmt() {
        return ppCompensationAmt;
    }

    public void setPpCompensationAmt(double ppCompensationAmt) {
        this.ppCompensationAmt = ppCompensationAmt;
    }

    public String getRpFullName() {
        return rpFullName;
    }

    public void setRpFullName(String rpFullName) {
        this.rpFullName = rpFullName;
    }

    public String getRpNRIC() {
        return rpNRIC;
    }

    public void setRpNRIC(String rpNRIC) {
        this.rpNRIC = rpNRIC;
    }

    public long getRpContactNum() {
        return rpContactNum;
    }

    public void setRpContactNum(long rpContactNum) {
        this.rpContactNum = rpContactNum;
    }

    public String getRpMotorVehicleRegNo() {
        return rpMotorVehicleRegNo;
    }

    public void setRpMotorVehicleRegNo(String rpMotorVehicleRegNo) {
        this.rpMotorVehicleRegNo = rpMotorVehicleRegNo;
    }

    public String getRpInsuranceCompany() {
        return rpInsuranceCompany;
    }

    public void setRpInsuranceCompany(String rpInsuranceCompany) {
        this.rpInsuranceCompany = rpInsuranceCompany;
    }

    public long getDateTimeOfAccident() {
        return dateTimeOfAccident;
    }

    public void setDateTimeOfAccident(long dateTimeOfAccident) {
        this.dateTimeOfAccident = dateTimeOfAccident;
    }

    public long getDateSubmitted() {
        return dateSubmitted;
    }

    public void setDateSubmitted(long dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTripUID() {
        return tripUID;
    }

    public void setTripUID(String tripUID) {
        this.tripUID = tripUID;
    }

    public byte[] getPrivateSettlementForm() {
        return privateSettlementForm;
    }

    public void setPrivateSettlementForm(byte[] privateSettlementForm) {
        this.privateSettlementForm = privateSettlementForm;
    }
}
