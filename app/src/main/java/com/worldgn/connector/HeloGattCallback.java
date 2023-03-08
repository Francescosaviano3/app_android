package com.worldgn.connector;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;

/**
 * Created by vijay on 08-11-2017.
 */

class HeloGattCallback extends BluetoothGattCallback {

    HeloGattCallBackListenter heloGattCallBackListenter;

    public HeloGattCallback(HeloGattCallBackListenter heloGattCallBackListenter) {
        this.heloGattCallBackListenter = heloGattCallBackListenter;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        heloGattCallBackListenter.onConnectionStateChange(gatt, status, newState);
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        heloGattCallBackListenter.onServicesDiscovered(gatt, status);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        heloGattCallBackListenter.onCharacteristicWrite(gatt, characteristic, status);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        heloGattCallBackListenter.onCharacteristicChanged(gatt, characteristic);
    }


}
