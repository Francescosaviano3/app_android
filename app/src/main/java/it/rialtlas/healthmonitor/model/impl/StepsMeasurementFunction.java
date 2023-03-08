package it.rialtlas.healthmonitor.model.impl;

import com.worldgn.connector.Connector;

import it.rialtlas.healthmonitor.R;
import it.rialtlas.healthmonitor.model.MeasurementFunction;
import it.rialtlas.healthmonitor.utils.ConstantsImp;

public class StepsMeasurementFunction extends MeasurementFunction {

    public StepsMeasurementFunction() {
        super(R.string.MSG_STEPS, 20, ConstantsImp.BROADCAST_ACTION_STEPS_MEASUREMENT,   new Runnable() {
            @Override
            public void run() {
                Connector.getInstance().getStepsData();
            }
        });
    }
}