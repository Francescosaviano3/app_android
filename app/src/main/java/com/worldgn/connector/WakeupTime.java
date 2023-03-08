package com.worldgn.connector;

/**
 * Created by WGN on 27-09-2017.
 */

class WakeupTime extends Measurement {

    private String wakeuptime;
    private String type;
    private String groupNumber;

    public String getWakeuptime() {
        return wakeuptime;
    }

    public void setWakeuptime(String wakeuptime) {
        this.wakeuptime = wakeuptime;
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
