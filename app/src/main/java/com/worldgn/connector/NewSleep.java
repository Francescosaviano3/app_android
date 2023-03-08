package com.worldgn.connector;

import java.io.Serializable;

/**
 * Created by ZAB on 2017/9/19.
 */

class NewSleep implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 6634617108185671753L;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**

     *
     */
    public String userId;//20161227
    public String groupIndex;//20161227
    public String beginSleepTimestamp;//
    public String endSleepTimestamp;//
    public String sleepType;// 0 浅睡 1 深睡 2翻身
    private String mac;
    private Double longitude;
    private Double latitude;
    private long time ;


    public String getUserId() {
        return userId;
    }

    public NewSleep() {
        super();
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public NewSleep(String userId, String groupIndex, String beginSleepTimestamp, String endSleepTimestamp, String sleepType, String mac) {
        this.userId = userId;
        this.groupIndex = groupIndex;
        this.beginSleepTimestamp = beginSleepTimestamp;
        this.endSleepTimestamp = endSleepTimestamp;
        this.sleepType = sleepType;
        this.mac = mac;

    }

    public String getGroupIndex() {
        return groupIndex;
    }

    public void setGroupIndex(String groupIndex) {
        this.groupIndex = groupIndex;
    }

    public String getBeginSleepTimestamp() {
        return beginSleepTimestamp;
    }

    public void setBeginSleepTimestamp(String beginSleepTimestamp) {
        this.beginSleepTimestamp = beginSleepTimestamp;
    }

    public String getEndSleepTimestamp() {
        return endSleepTimestamp;
    }

    public void setEndSleepTimestamp(String endSleepTimestamp) {
        this.endSleepTimestamp = endSleepTimestamp;
    }

    public String getSleepType() {
        return sleepType;
    }

    public void setSleepType(String sleepType) {
        this.sleepType = sleepType;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
