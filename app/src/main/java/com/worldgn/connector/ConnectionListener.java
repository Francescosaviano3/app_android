package com.worldgn.connector;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;


/**
 * Created by WGN on 13-09-2017.
 */

interface ConnectionListener {

    public void onDeviceFound(DeviceItem deviceItem);
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState);
    public void onServicesDiscovered(BluetoothGatt gatt, int status);
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);


}
