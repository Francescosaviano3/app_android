package com.worldgn.connector;

/**
 * Created by WGN on 27-09-2017.
 */

class Ecg extends Measurement {

    private String data_type;
    private String ecgvideo;


    public String getData_type() {
        return data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }

    public String getEcgvideo() {
        return ecgvideo;
    }

    public void setEcgvideo(String ecgvideo) {
        this.ecgvideo = ecgvideo;
    }
}
