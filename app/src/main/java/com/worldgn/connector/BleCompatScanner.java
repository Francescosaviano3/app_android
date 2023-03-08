package com.worldgn.connector;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;


/**
 * Created by vijay on 06-12-2017.
 */

public class BleCompatScanner implements BluetoothAdapter.LeScanCallback {
    private long scanTime = 10 * 1000;

    private BluetoothAdapter btAdapter;
    private MScanCallback mScanCallback;
    private Handler handler;
    BluetoothLeScanner bleScanner;
    BleCompatScannerCallback bleCompatScannerCallback;

    public BleCompatScanner(BleCompatScannerCallback bleCompatScannerCallback) {
        handler = new Handler(Looper.myLooper());
        this.bleCompatScannerCallback = bleCompatScannerCallback;
    }

    public void startScan(BluetoothAdapter btAdapter) {
        this.btAdapter = btAdapter;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            marshMallowScan();
        } else {
            preMarshMallowScan();
        }
    }

    public void stopScan(BluetoothAdapter btAdapter) {
        this.btAdapter = btAdapter;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            stopMarshmallowScan();
        } else {
            btAdapter.stopLeScan(this);
        }
    }

    private void stopMarshmallowScan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bleScanner.stopScan(mScanCallback);
        }
    }

    private Runnable preMScanRunnable = new Runnable() {
        @Override
        public void run() {
            btAdapter.stopLeScan(BleCompatScanner.this);
            bleCompatScannerCallback.onScanFinished();
        }
    };

    private void preMarshMallowScan() {

            DebugLogger.i(BleCompatScanner.class.getName(), "SCANNING PRE MSCAN");
            btAdapter.stopLeScan(BleCompatScanner.this);
            boolean isScanStarted = btAdapter.startLeScan(this);
            if (isScanStarted) {
                GlobalData.canSearchNewDevice = false;
                bleCompatScannerCallback.onScanStarted();
                handler.postDelayed(preMScanRunnable, scanTime);
            }

    }

    private Runnable mScanRunnable = new Runnable() {
        @Override
        public void run() {
            bleScanner.stopScan(mScanCallback);
            bleCompatScannerCallback.onScanFinished();
        }
    };


    private void marshMallowScan() {

            DebugLogger.i(BleCompatScanner.class.getName(), "SCANNING MSCAN");
            bleScanner = btAdapter.getBluetoothLeScanner();

            if (mScanCallback == null) {
                mScanCallback = new MScanCallback();
            }

            bleScanner.stopScan(mScanCallback);

            ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
            ArrayList<ScanFilter> filters = new ArrayList<ScanFilter>();

            bleScanner.startScan(filters, settings, mScanCallback);
            GlobalData.canSearchNewDevice = false;
            bleCompatScannerCallback.onScanStarted();

            handler.postDelayed(mScanRunnable, scanTime);

    }

    @Override
    public void onLeScan(BluetoothDevice bluetoothDevice, int rssi, byte[] bytes) {
        bleCompatScannerCallback.onLedeviceFound(bluetoothDevice, rssi);
    }


    private class MScanCallback extends ScanCallback {


        @Override
        public void onScanResult(int callbackType, ScanResult result) {

//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if(result != null) {
                BluetoothDevice bluetoothDevice = result.getDevice();
                bleCompatScannerCallback.onLedeviceFound(bluetoothDevice, result.getRssi());
            }

        }


    }

    public void removeCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            handler.removeCallbacks(mScanRunnable);
        } else {
            handler.removeCallbacks(preMScanRunnable);
        }
    }
}
