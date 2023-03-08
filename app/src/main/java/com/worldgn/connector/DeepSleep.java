package com.worldgn.connector;

/**
 * Created by WGN on 27-09-2017.
 */

class DeepSleep extends Measurement {

    private String startdeepsleep;
    private String stopdeepsleep;
    private String type;
    private String groupNumber;


    public String getStartdeepsleep() {
        return startdeepsleep;
    }

    public void setStartdeepsleep(String startdeepsleep) {
        this.startdeepsleep = startdeepsleep;
    }

    public String getStopdeepsleep() {
        return stopdeepsleep;
    }

    public void setStopdeepsleep(String stopdeepsleep) {
        this.stopdeepsleep = stopdeepsleep;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }
}
