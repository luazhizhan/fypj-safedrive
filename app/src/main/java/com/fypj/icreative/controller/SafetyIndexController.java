package com.fypj.icreative.controller;

import com.fypj.icreative.model.TripModel;

public class SafetyIndexController {

    public String checkSafetyIndexBanding(int tripSafetyIndexBanding) {
        if (tripSafetyIndexBanding >= 80) {
            return String.valueOf(tripSafetyIndexBanding + "(Band 1)");
        } else if (tripSafetyIndexBanding >= 60) {
            return String.valueOf(tripSafetyIndexBanding + "(Band 2)");
        } else if (tripSafetyIndexBanding >= 40) {
            return String.valueOf(tripSafetyIndexBanding + "(Band 3)");
        } else {
            return String.valueOf(tripSafetyIndexBanding + "(Band 4)");
        }
    }

    public String checkSafetyIndexBandingWithoutIndex(int tripSafetyIndexBanding) {
        if (tripSafetyIndexBanding >= 80) {
            return "(Band 1)";
        } else if (tripSafetyIndexBanding >= 60) {
            return "(Band 2)";
        } else if (tripSafetyIndexBanding >= 40) {
            return "(Band 3)";
        } else {
            return "(Band 4)";
        }
    }

    public int distanceRiskCal(double distanceTravelled) {
        int distI = (int) (distanceTravelled / 500);
        return distI * 4;
    }

    public int avgSpeedRiskCal(double avgSpeed) {
        int avgSpeedRisk = 0;
        if (avgSpeed >= 101) {
            avgSpeedRisk += 15;
        } else if (avgSpeed >= 91) {
            avgSpeedRisk += 10;
        } else if (avgSpeed >= 81) {
            avgSpeedRisk += 5;
        }
        return avgSpeedRisk;
    }

    public int speedingRiskCal(int exceedSpeedLimitCount) {
        return (exceedSpeedLimitCount * 4);
    }

    public int sharpTurnCal(int sharpTurnCount) {
        return (sharpTurnCount * 4);
    }

    public int tripSafetyIndexCalculation(TripModel tripModel, int exceedSpeedLimitCount) {
        int distanceRisk = distanceRiskCal(tripModel.getDistanceTravelled());
        int avgSpeedRisk = avgSpeedRiskCal(tripModel.getAvgSpeed());
        int speedingRisk = speedingRiskCal(exceedSpeedLimitCount);
        int sharpTurnRisk = sharpTurnCal(tripModel.getVigorousTurnCount());
        int safetyTripIndex = 100 - distanceRisk - avgSpeedRisk - speedingRisk - sharpTurnRisk;
        if(safetyTripIndex < 0){
            safetyTripIndex = 0;

        }
        return safetyTripIndex;
    }
}
