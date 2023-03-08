package com.worldgn.connector;

import android.bluetooth.BluetoothDevice;

/**
 * Created by vijay on 06-12-2017.
 */

public interface BleCompatScannerCallback {

    public void onScanStarted();
    public void onScanFinished();
    public void onLedeviceFound(BluetoothDevice bluetoothDevice, int rssi);

}
