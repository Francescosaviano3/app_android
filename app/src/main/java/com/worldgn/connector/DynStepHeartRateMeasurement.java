package com.worldgn.connector;

/**
 * Created by Krishna Rao on 11/10/2017.
 */

class DynStepHeartRateMeasurement {

    private long timeStampInSec;
    private String stepCount;
    private String heartRate;

    public DynStepHeartRateMeasurement() {
    }

    public long getTimeStampInSec() {
        return timeStampInSec;
    }

    public void setTimeStampInSec(long timeStampInSec) {
        this.timeStampInSec = timeStampInSec;
    }

    public String getStepCount() {
        return stepCount;
    }

    public void setStepCount(String stepCount) {
        this.stepCount = stepCount;
    }

    public String getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
    }

}
