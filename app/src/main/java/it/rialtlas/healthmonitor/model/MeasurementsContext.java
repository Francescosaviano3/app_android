package it.rialtlas.healthmonitor.model;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import com.worldgn.connector.DeviceItem;

import java.util.ArrayList;
import java.util.List;

import it.rialtlas.healthmonitor.R;
import it.rialtlas.healthmonitor.controller.HeloControllerState;
import it.rialtlas.healthmonitor.model.impl.BloodPressureMeasurementFunction;
import it.rialtlas.healthmonitor.model.impl.HeartRateMeasurementFunction;
import it.rialtlas.healthmonitor.model.impl.OxigenMeasurementFunction;

public abstract class MeasurementsContext {
    private static final long COMMANDS_INTTIME_IN_MILLIS = 3000;

    public abstract String patientId();

    public abstract PatientData selectedPatient();

    public abstract String patientWrist();

    public abstract MeasurementsContext patientId(PatientData patient);

    public abstract HeloControllerState state();

    public abstract MeasurementsContext state(HeloControllerState state);

    public abstract Application application();

    public abstract Activity currentActivity();

    public abstract MeasurementsContext currentActivity(Activity currentActivity);

    public abstract MeasurementsContext heloUserId(long heloUserId);

    public abstract long heloUserId();

    public abstract MeasurementsContext device(DeviceItem device);

    public abstract DeviceItem device();

    public abstract MeasurementsContext deviceFirmware(int firmware);

    public abstract String deviceLabel();

    public abstract MeasurementsContext systolicPressure(String bp);

    public abstract String systolicPressure();

    public abstract MeasurementsContext  diastolicPressure(String bp);

    public abstract String diastolicPressure();

    public abstract MeasurementsContext  heartRate(String rate);

    public abstract String heartRate();

    public abstract MeasurementsContext  breathRate(String rate);

    public abstract String breathRate();

    public abstract MeasurementsContext  spo2(String rate);

    public abstract String spo2();

    public abstract MeasurementsContext  steps(String steps);

    public abstract String steps();

    public abstract MeasurementsContext  mood(String mood);

    public abstract String mood();

    public abstract MeasurementsContext  fatigue(String fatigue);

    public abstract String fatigue();

    public abstract MeasurementsContext glucose(String value);

    public abstract String glucose();

    public abstract MeasurementsContext weight(String value);

    public abstract String weight();


    public abstract MeasurementsContext waitForMeasurement(Context context);

    public abstract MeasurementsContext stopWaitingForMeasurement();

    public abstract void sendMeasurements();


    private List<MeasurementFunction> measurementFunctions = new ArrayList<>();

    public MeasurementsContext resetMeasurements() {

        // azzera tutti i dati
        MeasurementsContextStrategy.getInstance().systolicPressure(null);
        MeasurementsContextStrategy.getInstance().diastolicPressure(null);
        MeasurementsContextStrategy.getInstance().heartRate(null);
        MeasurementsContextStrategy.getInstance().breathRate(null);
        MeasurementsContextStrategy.getInstance().spo2(null);
        MeasurementsContextStrategy.getInstance().steps(null);
        MeasurementsContextStrategy.getInstance().mood(null);
        MeasurementsContextStrategy.getInstance().fatigue(null);
        MeasurementsContextStrategy.getInstance().glucose(null);
        MeasurementsContextStrategy.getInstance().weight(null);
        MeasurementsContextStrategy.getInstance().currentActivity().runOnUiThread(new Runnable() {
            public void run() {
                ((TextView) MeasurementsContextStrategy.getInstance().currentActivity().findViewById(R.id.systolic)).setText("--");
                ((TextView) MeasurementsContextStrategy.getInstance().currentActivity().findViewById(R.id.diastolic)).setText("--");
                ((TextView) MeasurementsContextStrategy.getInstance().currentActivity().findViewById(R.id.heartRate)).setText("--");
                ((TextView) MeasurementsContextStrategy.getInstance().currentActivity().findViewById(R.id.breath)).setText("--");
                ((TextView) MeasurementsContextStrategy.getInstance().currentActivity().findViewById(R.id.spo2)).setText("--");
                ((TextView) MeasurementsContextStrategy.getInstance().currentActivity().findViewById(R.id.glucose)).setText("--");
                ((TextView) MeasurementsContextStrategy.getInstance().currentActivity().findViewById(R.id.weight)).setText("--");
            }
        });

        measurementFunctions.clear();
        measurementFunctions.add(new BloodPressureMeasurementFunction());
        measurementFunctions.add(new HeartRateMeasurementFunction());
        measurementFunctions.add(new OxigenMeasurementFunction());



        return this;
    }

    public int currentMeasurement() {
        if (isMeasuring()) {
            return measurementFunctions.get(0).measure();
        }
        return R.string.MSG_EMPTY;
    }

    public int currentMeasurementElapsedTime(int seconds ) {
        int timeToRecover = 0;
        if (isMeasuring()) {
            timeToRecover = measurementFunctions.get(0).elapsedTime(seconds);
            if(timeToRecover>0) {
                final Intent intent = new Intent(measurementFunctions.get(0).actionKey());
                MeasurementsContextStrategy.getInstance().application().sendBroadcast(intent);
            }
        }
        return timeToRecover;
    }

    public int currentMeasurementForeseenSeconds() {
        if (isMeasuring()) {
            return measurementFunctions.get(0).foreseenSeconds();
        }
        return R.string.MSG_EMPTY;
    }

    public MeasurementsContext clearMeasurements() {
        measurementFunctions.clear();
        return this;
    }

    public int getMaxMeasurementsTime() {
        int total = 0;
        for (MeasurementFunction f:measurementFunctions) {
            total += f.foreseenSeconds();
        }
        return total;
    }
    public MeasurementsContext performMeasurement() {
        if (isMeasuring()) {
            try {
                Thread.sleep(COMMANDS_INTTIME_IN_MILLIS);
                measurementFunctions.get(0).perform();
            } catch(InterruptedException ie) {
                /* do nothing on this */
            }

        }
        return this;
    }

    public MeasurementsContext removeCurrentMeasurement(boolean isMissing) {
        if (isMeasuring()) {
            MeasurementFunction f = measurementFunctions.remove(0);
            //if (isMissing) {
            //    f.resetTime();
            //    measurementFunctions.add(f);
            // }
        }
        return this;
    }

    public boolean isMeasuring() {
        return this.measurementFunctions.size()>0;
    }

}
