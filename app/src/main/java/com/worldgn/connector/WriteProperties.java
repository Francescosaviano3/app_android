package com.worldgn.connector;

/**
 * Created by WGN on 26-09-2017.
 */

class WriteProperties {

    private String serviceUuid;
    private String charUuid;
    private byte[] data;
    private String function;

    public String getServiceUuid() {
        return serviceUuid;
    }

    public void setServiceUuid(String serviceUuid) {
        this.serviceUuid = serviceUuid;
    }

    public String getCharUuid() {
        return charUuid;
    }

    public void setCharUuid(String charUuid) {
        this.charUuid = charUuid;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }




}
