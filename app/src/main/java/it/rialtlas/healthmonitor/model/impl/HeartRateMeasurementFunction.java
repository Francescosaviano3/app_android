package it.rialtlas.healthmonitor.model.impl;

import com.worldgn.connector.Connector;

import it.rialtlas.healthmonitor.R;
import it.rialtlas.healthmonitor.model.MeasurementFunction;
import it.rialtlas.healthmonitor.utils.ConstantsImp;

public class HeartRateMeasurementFunction extends MeasurementFunction {

    public HeartRateMeasurementFunction() {
        super(R.string.MSG_HEART_RATE, 300, ConstantsImp.BROADCAST_ACTION_HR_MEASUREMENT, new Runnable() {
            @Override
            public void run() {
                Connector.getInstance().measureHR();
            }
        });
    }
}
