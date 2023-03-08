package com.worldgn.connector;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.support.annotation.NonNull;



import java.util.List;
import java.util.UUID;

/**
 * Created by WGN on 18-09-2017.
 */

class MeasurementParser {

    private static String TAG = MeasurementParser.class.getSimpleName();


    public static boolean measureRate(@NonNull BluetoothGatt bluetoothGatt, byte[] byte_hr, List<String> uuids, int attemptCount, String TAG) {
        int count = 0;
        boolean result = false;
        while (!result) {
            result = writeRXCharacteristic(uuids.get(0), uuids.get(1), byte_hr, bluetoothGatt);
            DebugLogger.i(TAG, "");
            if (!GlobalData.status_Connected)
                return false;
            if (count > attemptCount) {
                break;
            } else {
                count++;
            }
        }

        DebugLogger.i(TAG, ""+result);
        return result;
    }



    private static boolean writeRXCharacteristic(String serviceUUID, String charactersticUUID, byte[] value, BluetoothGatt mBluetoothGatt) {
        BluetoothGattService RxService = null;
        if (mBluetoothGatt != null) {
            try {
                RxService = mBluetoothGatt.getService(UUID.fromString(serviceUUID));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return false;
        }

        // "0aabcdef-1111-2222-0000-facebeadaaaa",
        // "facebead-ffff-eeee-0001-facebeadaaaa"
        if (RxService == null) {
            DebugLogger.i(TAG, "653 写入失败 GATT服务未找到 Rx service not found!");
            // broadcastUpdate(GlobalData.DEVICE_DOES_NOT_SUPPORT_UART);
            return false;
        }
        BluetoothGattCharacteristic RxChar = RxService.getCharacteristic(UUID.fromString(charactersticUUID));
        if (RxChar == null) {
            DebugLogger.i(TAG, "659 写入失败 GATT属性未找到 Rx charateristic not found!");
            // broadcastUpdate(GlobalData.DEVICE_DOES_NOT_SUPPORT_UART);
            return false;
        }

        RxChar.setValue(value);

        boolean status = false;
        if (mBluetoothGatt != null) {
            status = mBluetoothGatt.writeCharacteristic(RxChar);
        }
        // DebugLogger.i(TAG, "666 写入成功 返回响应值 :write TXchar - status=" + status);
        return status;
    }



}
