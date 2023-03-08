package it.rialtlas.healthmonitor.model.impl;

import com.worldgn.connector.Connector;

import it.rialtlas.healthmonitor.R;
import it.rialtlas.healthmonitor.model.MeasurementFunction;
import it.rialtlas.healthmonitor.utils.ConstantsImp;

public class MFMeasurementFunction extends MeasurementFunction {

    public MFMeasurementFunction() {
        super(R.string.MSG_MF, 60, ConstantsImp.BROADCAST_ACTION_FATIGUE_MEASUREMENT,   new Runnable() {
            @Override
            public void run() {
                Connector.getInstance().measureMF();
            }
        });
    }
}
