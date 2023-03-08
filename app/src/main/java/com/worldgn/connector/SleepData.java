package com.worldgn.connector;

/**
 * Created by WGN on 05-10-2017.
 */

class SleepData {

    private long   startSleep;
    private long   stopSleep;
    private long   duration;
    private String sleeptype;
    private String sleepDate;


    public long getStartSleep() {
        return startSleep;
    }

    public String getSleepDate() {
        return sleepDate;
    }

    public void setSleepDate(String sleepDate) {
        this.sleepDate = sleepDate;
    }

    public void setStartSleep(long startSleep) {
        this.startSleep = startSleep;
    }

    public long getStopSleep() {
        return stopSleep;
    }

    public void setStopSleep(long stopSleep) {
        this.stopSleep = stopSleep;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getSleeptype() {
        return sleeptype;
    }

    public void setSleeptype(String sleeptype) {
        this.sleeptype = sleeptype;
    }


}
