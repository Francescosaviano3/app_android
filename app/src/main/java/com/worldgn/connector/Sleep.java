package com.worldgn.connector;

/**
 * Created by WGN on 27-09-2017.
 */

class Sleep extends Measurement {

    private String startSleep;
    private String stopSleep;
    private String timeSleep;
    private String deepSleep;
    private String lightSleep;
    private String wakeup;

    public String getStartSleep() {
        return startSleep;
    }

    public void setStartSleep(String startSleep) {
        this.startSleep = startSleep;
    }

    public String getStopSleep() {
        return stopSleep;
    }

    public void setStopSleep(String stopSleep) {
        this.stopSleep = stopSleep;
    }

    public String getTimeSleep() {
        return timeSleep;
    }

    public void setTimeSleep(String timeSleep) {
        this.timeSleep = timeSleep;
    }

    public String getDeepSleep() {
        return deepSleep;
    }

    public void setDeepSleep(String deepSleep) {
        this.deepSleep = deepSleep;
    }

    public String getLightSleep() {
        return lightSleep;
    }

    public void setLightSleep(String lightSleep) {
        this.lightSleep = lightSleep;
    }

    public String getWakeup() {
        return wakeup;
    }

    public void setWakeup(String wakeup) {
        this.wakeup = wakeup;
    }
}
