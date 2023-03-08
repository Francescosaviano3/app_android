package com.worldgn.connector;

import android.bluetooth.BluetoothGatt;

/**
 * Created by WGN on 15-09-2017.
 */

interface ConnectCallback {

    public void onGattConnecting();
    public void onGattConencted(String mac);
    public void onGattDisconnected();
    public void onGattBinded();
    public void onGattObj(BluetoothGatt bluetoothGatt);

}
