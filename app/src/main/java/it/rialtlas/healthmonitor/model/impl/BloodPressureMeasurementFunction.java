package it.rialtlas.healthmonitor.model.impl;

import com.worldgn.connector.Connector;

import it.rialtlas.healthmonitor.R;
import it.rialtlas.healthmonitor.model.MeasurementFunction;
import it.rialtlas.healthmonitor.utils.ConstantsImp;

public class BloodPressureMeasurementFunction extends MeasurementFunction {

    public BloodPressureMeasurementFunction() {
        super(R.string.MSG_BLOOD_PRESSURE, 300, ConstantsImp.BROADCAST_ACTION_BP_MEASUREMENT, new Runnable() {
            @Override
            public void run() {
                //Connector.getInstance().stopMeasuring();
                try { Thread.sleep(1000); } catch(InterruptedException ie) {}
                Connector.getInstance().measureBP();
            }
        });
    }
}
