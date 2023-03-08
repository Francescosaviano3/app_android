package com.worldgn.connector;

/**
 * Created by WGN on 27-09-2017.
 */

class BloodPressure extends Measurement {

    private String highbp;
    private String lowbp;

    public String getHighbp() {
        return highbp;
    }

    public void setHighbp(String highbp) {
        this.highbp = highbp;
    }

    public String getLowbp() {
        return lowbp;
    }

    public void setLowbp(String lowbp) {
        this.lowbp = lowbp;
    }
}
