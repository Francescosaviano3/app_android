package it.rialtlas.healthmonitor.model;

import android.app.Application;

import it.rialtlas.healthmonitor.controller.HeloControllerState;
import it.rialtlas.healthmonitor.model.impl.MeasurementsContextImpl;

public class MeasurementsContextStrategy {
    private static MeasurementsContext theInstance;

    public static synchronized MeasurementsContext getInstance(Application application, HeloControllerState startingState) {
        if (theInstance==null) {
            theInstance = MeasurementsContextImpl.of(application, startingState);
            return theInstance;
        }
        return theInstance;
    }

    public static synchronized MeasurementsContext getInstance() {
        return theInstance;
    }
}
