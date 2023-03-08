package com.worldgn.connector;

/**
 * Created by WGN on 14-09-2017.
 */

interface BleCallback {
    public void onGattConnected();
    public void onGattDisConnected();
    public void onServiceGattDiscovered();

}
