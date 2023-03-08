package com.worldgn.connector;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by vijay on 08-11-2017.
 */

interface HeloGattCallBackListenter {

    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState);
    public void onServicesDiscovered(BluetoothGatt gatt, int status);
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);


}
