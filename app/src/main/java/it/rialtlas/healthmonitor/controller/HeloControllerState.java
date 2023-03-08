package it.rialtlas.healthmonitor.controller;

import it.rialtlas.healthmonitor.model.MeasurementsContext;

public interface HeloControllerState {
    boolean process (MeasurementsContext context);
    boolean scan(MeasurementsContext context);
    boolean unbind(MeasurementsContext context);


    boolean measureBloodPressure(MeasurementsContext context);

    boolean measureHeartRate(MeasurementsContext context);

    boolean measureBreathRate(MeasurementsContext context);

    boolean measureOxygen(MeasurementsContext context);

    boolean startMeasuring();

    boolean takeNextMeasure(boolean isMissing);

    boolean retryMissingMeasurements();
}
