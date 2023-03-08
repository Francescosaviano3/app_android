package com.worldgn.connector;

/**
 * Created by WGN on 27-09-2017.
 */

class Mood extends Measurement {

    private String measureData;
    private String data_type;

    public String getMeasureData() {
        return measureData;
    }

    public void setMeasureData(String measureData) {
        this.measureData = measureData;
    }

    public String getData_type() {
        return data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }
}
