package com.worldgn.connector;



/**
 * Created by WGN on 15-09-2017.
 */

public interface ScanCallBack {

    public void onScanStarted();
    public void onScanFinished();
    public void onLedeviceFound(DeviceItem deviceItem);
    public void onPairedDeviceNotFound();
    public void onFailure(String desc);

}
