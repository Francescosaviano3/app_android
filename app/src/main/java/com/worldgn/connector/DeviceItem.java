package com.worldgn.connector;

/**
 * Created by WGN on 13-09-2017.
 */

public class DeviceItem {
    private String deviceMac;
    private String deviceName;
    private int rssi;
    /**
     * @return the deviceMac
     */
    public String getDeviceMac() {
        return deviceMac;
    }
    /**
     * @param deviceMac the deviceMac to set
     */
    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }
    /**
     * @return the rssi
     */
    public int getRssi() {
        return rssi;
    }
    /**
     * @param rssi the rssi to set
     */
    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
    public String getDeviceName() {
        return deviceName;
    }
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    @Override
    public boolean equals(Object obj) {
        DeviceItem deviceItem = (DeviceItem) obj;
        return deviceItem.getDeviceMac().equals(deviceMac);
    }


}
