package com.worldgn.connector;

public class Md5APIResponse {

    private int success;
    private String message;
    private HeloDeviceDev helodevicedev;

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HeloDeviceDev getHelodevicedev() {
        return helodevicedev;
    }

    public void setHelodevicedev(HeloDeviceDev helodevicedev) {
        this.helodevicedev = helodevicedev;
    }

}
