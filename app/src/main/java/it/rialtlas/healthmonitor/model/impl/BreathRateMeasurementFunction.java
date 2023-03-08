package it.rialtlas.healthmonitor.model.impl;

import com.worldgn.connector.Connector;

import it.rialtlas.healthmonitor.R;
import it.rialtlas.healthmonitor.model.MeasurementFunction;
import it.rialtlas.healthmonitor.utils.ConstantsImp;

public class BreathRateMeasurementFunction extends MeasurementFunction {

    public BreathRateMeasurementFunction() {
        super(R.string.MSG_BREATH_RATE, 80, ConstantsImp.BROADCAST_ACTION_BR_MEASUREMENT, new Runnable() {
            @Override
            public void run() {
                Connector.getInstance().measureBr();
            }
        });
    }
}