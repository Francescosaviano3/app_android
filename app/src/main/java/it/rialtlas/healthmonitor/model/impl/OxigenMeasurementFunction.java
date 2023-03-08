package it.rialtlas.healthmonitor.model.impl;

import com.worldgn.connector.Connector;

import it.rialtlas.healthmonitor.R;
import it.rialtlas.healthmonitor.model.MeasurementFunction;
import it.rialtlas.healthmonitor.utils.ConstantsImp;

public class OxigenMeasurementFunction extends MeasurementFunction {

    public OxigenMeasurementFunction() {
        super(R.string.MSG_OXIGEN, 300, ConstantsImp.BROADCAST_ACTION_OXYGEN_MEASUREMENT,   new Runnable() {
            @Override
            public void run() {
                Connector.getInstance().measureOxygen();
            }
        });
    }
}
