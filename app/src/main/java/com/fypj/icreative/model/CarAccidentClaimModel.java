package com.fypj.icreative.model;

import java.util.HashMap;
import java.util.Map;

public class CarAccidentClaimModel {
    private String uid;
    private String selfFullName;
    private String selfNRIC;
    private long selfContactNum;
    private String selfMotorVehicleRegNo;
    private String selfInsuranceCompany;
    private String opFullName;
    private String opNRIC;
    private long opContactNum;
    private String opMotorVehicleRegNo;
    private String opInsuranceCompany;
    private String remarks;
    private long dateTimeOfAccident;
    private long dateSubmitted;
    private String fileName;
    private String tripUID;
    private String repairShopInfo;
    private byte[] carAccidentClaimForm;

    public CarAccidentClaimModel() {
    }


    public CarAccidentClaimModel(String selfFullName, String selfNRIC, long selfContactNum, String selfMotorVehicleRegNo, String selfInsuranceCompany) {
        this.selfFullName = selfFullName;
        this.selfNRIC = selfNRIC;
        this.selfContactNum = selfContactNum;
        this.selfMotorVehicleRegNo = selfMotorVehicleRegNo;
        this.selfInsuranceCompany = selfInsuranceCompany;
    }

    public Map<String, Object> toMap(Map<String, String> serverTimeStamp) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("selfFullName", selfFullName);
        result.put("selfNRIC", selfNRIC);
        result.put("selfContactNum", selfContactNum);
        result.put("selfMotorVehicleRegNo", selfMotorVehicleRegNo);
        result.put("selfInsuranceCompany", selfInsuranceCompany);
        result.put("opFullName", opFullName);
        result.put("opNRIC", opNRIC);
        result.put("opContactNum", opContactNum);
        result.put("opMotorVehicleRegNo", opMotorVehicleRegNo);
        result.put("opInsuranceCompany", opInsuranceCompany);
        result.put("remarks", remarks);
        result.put("dateTimeOfAccident", dateTimeOfAccident);
        result.put("dateSubmitted", serverTimeStamp);
        result.put("fileName", fileName);
        result.put("repairShopInfo", repairShopInfo);
        return result;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSelfFullName() {
        return selfFullName;
    }

    public void setSelfFullName(String selfFullName) {
        this.selfFullName = selfFullName;
    }

    public String getSelfNRIC() {
        return selfNRIC;
    }

    public void setSelfNRIC(String selfNRIC) {
        this.selfNRIC = selfNRIC;
    }

    public long getSelfContactNum() {
        return selfContactNum;
    }

    public void setSelfContactNum(long selfContactNum) {
        this.selfContactNum = selfContactNum;
    }

    public String getSelfMotorVehicleRegNo() {
        return selfMotorVehicleRegNo;
    }

    public void setSelfMotorVehicleRegNo(String selfMotorVehicleRegNo) {
        this.selfMotorVehicleRegNo = selfMotorVehicleRegNo;
    }

    public String getSelfInsuranceCompany() {
        return selfInsuranceCompany;
    }

    public void setSelfInsuranceCompany(String selfInsuranceCompany) {
        this.selfInsuranceCompany = selfInsuranceCompany;
    }

    public String getOpFullName() {
        return opFullName;
    }

    public void setOpFullName(String opFullName) {
        this.opFullName = opFullName;
    }

    public String getOpNRIC() {
        return opNRIC;
    }

    public void setOpNRIC(String opNRIC) {
        this.opNRIC = opNRIC;
    }

    public long getOpContactNum() {
        return opContactNum;
    }

    public void setOpContactNum(long opContactNum) {
        this.opContactNum = opContactNum;
    }

    public String getOpMotorVehicleRegNo() {
        return opMotorVehicleRegNo;
    }

    public void setOpMotorVehicleRegNo(String opMotorVehicleRegNo) {
        this.opMotorVehicleRegNo = opMotorVehicleRegNo;
    }

    public String getOpInsuranceCompany() {
        return opInsuranceCompany;
    }

    public void setOpInsuranceCompany(String opInsuranceCompany) {
        this.opInsuranceCompany = opInsuranceCompany;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    public String getRepairShopInfo() {
        return repairShopInfo;
    }

    public void setRepairShopInfo(String repairShopInfo) {
        this.repairShopInfo = repairShopInfo;
    }

    public String getTripUID() {
        return tripUID;
    }

    public void setTripUID(String tripUID) {
        this.tripUID = tripUID;
    }

    public byte[] getCarAccidentClaimForm() {
        return carAccidentClaimForm;
    }

    public void setCarAccidentClaimForm(byte[] carAccidentClaimForm) {
        this.carAccidentClaimForm = carAccidentClaimForm;
    }
}
