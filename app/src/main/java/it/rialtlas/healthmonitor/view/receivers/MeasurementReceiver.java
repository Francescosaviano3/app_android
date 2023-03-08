package it.rialtlas.healthmonitor.view.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.worldgn.connector.DeviceItem;

import it.rialtlas.healthmonitor.R;
import it.rialtlas.healthmonitor.controller.impl.HeloLXPlusControllerState;
import it.rialtlas.healthmonitor.model.MeasurementsContextStrategy;
import it.rialtlas.healthmonitor.utils.ConstantsImp;
import it.rialtlas.healthmonitor.utils.MessagingUtils;

public class MeasurementReceiver  extends BroadcastReceiver {
    private AppCompatActivity mainApp;

    private MeasurementReceiver(AppCompatActivity mainApp) {
        this.mainApp = mainApp;
    }
    public static MeasurementReceiver of(AppCompatActivity mainApp) {
        return new MeasurementReceiver(mainApp);
    }

    public void onReceive(Context context, final Intent intent) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            if (ConstantsImp.BROADCAST_ACTION_MEASUREMENT_WRITE_FAILURE.equalsIgnoreCase(action)) {
                boolean isMissing = !intent.hasExtra(ConstantsImp.INTENT_KEY_BP_MEASUREMENT_MAX);
                if (!isMissing) {
                    final String errorMessage = intent.getStringExtra(ConstantsImp.INTENT_MEASUREMENT_WRITE_FAILURE);
                    mainApp.runOnUiThread(new Runnable() {
                        public void run() {
                            MessagingUtils.criticalError(errorMessage, false);
                        }
                    });
                }
            }
            else if (ConstantsImp.BROADCAST_ACTION_BP_MEASUREMENT.equalsIgnoreCase(action)) {
                if (intent.hasExtra(ConstantsImp.INTENT_KEY_BP_MEASUREMENT_MAX)) {
                    final String max = intent.getStringExtra(ConstantsImp.INTENT_KEY_BP_MEASUREMENT_MAX);
                    mainApp.runOnUiThread(new Runnable() {
                        public void run() {
                            ((TextView) mainApp.findViewById(R.id.systolic)).setText(MeasurementsContextStrategy.getInstance().systolicPressure(max).systolicPressure());
                        }
                    });
                }
                if (intent.hasExtra(ConstantsImp.INTENT_KEY_BP_MEASUREMENT_MIN)) {
                    final String min = intent.getStringExtra(ConstantsImp.INTENT_KEY_BP_MEASUREMENT_MIN);
                    mainApp.runOnUiThread(new Runnable() {
                        public void run() {
                            ((TextView) mainApp.findViewById(R.id.diastolic)).setText(MeasurementsContextStrategy.getInstance().diastolicPressure(min).diastolicPressure());
                        }
                    });
                }
                if (!intent.hasExtra(ConstantsImp.INTENT_KEY_BP_MEASUREMENT_MAX) || !intent.hasExtra(ConstantsImp.INTENT_KEY_BP_MEASUREMENT_MIN) ) {
                    mainApp.runOnUiThread(new Runnable() {
                        public void run() {
                            MessagingUtils.criticalError(R.string.ERR_MEASUREMENT_ERROR, false);
                        }
                    });
                }
                // MeasurementsContextStrategy.getInstance().state().takeNextMeasure(isMissing);
            }
            else if (ConstantsImp.BROADCAST_ACTION_OXYGEN_MEASUREMENT.equalsIgnoreCase(action)) {
                boolean isMissing = !intent.hasExtra(ConstantsImp.INTENT_KEY_OXYGEN_MEASUREMENT);
                if (!isMissing) {
                    final String hr = intent.getStringExtra(ConstantsImp.INTENT_KEY_OXYGEN_MEASUREMENT);
                    mainApp.runOnUiThread(new Runnable() {
                        public void run() {
                            ((TextView) mainApp.findViewById(R.id.spo2)).setText(MeasurementsContextStrategy.getInstance().spo2(hr).spo2());
                        }
                    });
                }
                else {
                    mainApp.runOnUiThread(new Runnable() {
                        public void run() {
                            MessagingUtils.criticalError(R.string.ERR_MEASUREMENT_ERROR, false);
                        }
                    });
                }
                 // MeasurementsContextStrategy.getInstance().state().takeNextMeasure(isMissing);
            }
            else if (!ConstantsImp.BROADCAST_ACTION_APPVERSION_MEASUREMENT.equalsIgnoreCase(action)) {
                if (ConstantsImp.BROADCAST_ACTION_HELO_UNBONDED.equalsIgnoreCase(action)){
                    // MeasurementsContextStrategy.getInstance().device(null);
                }
                else if (ConstantsImp.BROADCAST_ACTION_HELO_BONDED.equalsIgnoreCase(action)){
                    // MeasurementsContextStrategy.getInstance().device(null);
                    // MessagingUtils.criticalError(R.string.MSG_BONDED, false);
                }
                else if (ConstantsImp.BROADCAST_ACTION_HELO_CONNECTED.equalsIgnoreCase(action)){ //  || ConstantsImp.BROADCAST_ACTION_HELO_FIRMWARE.equalsIgnoreCase(action)) {
                    final String mac = intent.getStringExtra(ConstantsImp.INTENT_KEY_MAC);
                    DeviceItem deviceItem = new DeviceItem();
                    deviceItem.setDeviceMac(mac);
                    MeasurementsContextStrategy.getInstance().device(deviceItem);
                    MessagingUtils.longToast(R.string.MSG_DEVICE_CONNECTED);
                    MeasurementsContextStrategy.getInstance().state(HeloLXPlusControllerState.CONNECTED);
                    mainApp.runOnUiThread(new Runnable() {
                        public void run() {
                            ((TextView) mainApp.findViewById(R.id.wristData)).setText(MeasurementsContextStrategy.getInstance().deviceLabel());
                        }
                    });
                }
                else if (ConstantsImp.BROADCAST_ACTION_HELO_DISCONNECTED.equalsIgnoreCase(action)){
                    if (MeasurementsContextStrategy.getInstance().isMeasuring()) {
                        MeasurementsContextStrategy.getInstance().clearMeasurements();
                        MeasurementsContextStrategy.getInstance().stopWaitingForMeasurement();
                        MeasurementsContextStrategy.getInstance().state(HeloLXPlusControllerState.INITIALIZED);
                        MessagingUtils.criticalError(R.string.ERR_PAIRED_DEVICE_NOT_FOUND, false);
                    }
                    MeasurementsContextStrategy.getInstance().device(null);
                    MeasurementsContextStrategy.getInstance().deviceFirmware(-1);
                    mainApp.runOnUiThread(new Runnable() {
                        public void run() {
                            ((TextView) mainApp.findViewById(R.id.wristData)).setText(MeasurementsContextStrategy.getInstance().deviceLabel());
                        }
                    });
                }
                else if (ConstantsImp.BROADCAST_ACTION_HELO_FIRMWARE.equalsIgnoreCase(action)){ //  || ConstantsImp.BROADCAST_ACTION_HELO_FIRMWARE.equalsIgnoreCase(action)) {
                    MeasurementsContextStrategy.getInstance().deviceFirmware(intent.getIntExtra(ConstantsImp.INTENT_KEY_FIRMWARE, -1));
                    mainApp.runOnUiThread(new Runnable() {
                        public void run() {
                            ((TextView) mainApp.findViewById(R.id.wristData)).setText(MeasurementsContextStrategy.getInstance().deviceLabel());
                        }
                    });

                }
               else if (ConstantsImp.BROADCAST_ACTION_HR_MEASUREMENT.equalsIgnoreCase(action)) {
                    boolean isMissing = !intent.hasExtra(ConstantsImp.INTENT_KEY_HR_MEASUREMENT);
                    if (!isMissing) {
                        final String hr = intent.getStringExtra(ConstantsImp.INTENT_KEY_HR_MEASUREMENT);
                        mainApp.runOnUiThread(new Runnable() {
                            public void run() {
                                ((TextView) mainApp.findViewById(R.id.heartRate)).setText(MeasurementsContextStrategy.getInstance().heartRate(hr).heartRate());
                            }
                        });
                    }
                    else {
                        mainApp.runOnUiThread(new Runnable() {
                            public void run() {
                                MessagingUtils.criticalError(R.string.ERR_MEASUREMENT_ERROR, false);
                            }
                        });
                    }
                    //  MeasurementsContextStrategy.getInstance().state().takeNextMeasure(isMissing);
                }
                else if (ConstantsImp.BROADCAST_ACTION_BR_MEASUREMENT.equalsIgnoreCase(action)) {
                    boolean isMissing = !intent.hasExtra(ConstantsImp.INTENT_KEY_BR_MEASUREMENT);
                    if (!isMissing) {
                        final String hr = intent.getStringExtra(ConstantsImp.INTENT_KEY_BR_MEASUREMENT);
                        mainApp.runOnUiThread(new Runnable() {
                            public void run() {
                                ((TextView) mainApp.findViewById(R.id.breath)).setText(MeasurementsContextStrategy.getInstance().breathRate(hr).breathRate());
                            }
                        });
                    }
                    else {
                        mainApp.runOnUiThread(new Runnable() {
                            public void run() {
                                MessagingUtils.criticalError(R.string.ERR_MEASUREMENT_ERROR, false);
                            }
                        });
                    }
                    //  MeasurementsContextStrategy.getInstance().state().takeNextMeasure(isMissing);
                }
            }
        }
    }


    public static final IntentFilter wristIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConstantsImp.BROADCAST_ACTION_BP_MEASUREMENT);
        intentFilter.addAction(ConstantsImp.BROADCAST_ACTION_APPVERSION_MEASUREMENT);
        intentFilter.addAction(ConstantsImp.BROADCAST_ACTION_BATTERY);
        intentFilter.addAction(ConstantsImp.BROADCAST_ACTION_ECG_MEASUREMENT);
        intentFilter.addAction(ConstantsImp.ACTION_MAIN_DATA_ECGALLDATA);
        intentFilter.addAction(ConstantsImp.BROADCAST_ACTION_BR_MEASUREMENT);
        intentFilter.addAction(ConstantsImp.BROADCAST_ACTION_FATIGUE_MEASUREMENT);
        intentFilter.addAction(ConstantsImp.BROADCAST_ACTION_MOOD_MEASUREMENT);
        intentFilter.addAction(ConstantsImp.BROADCAST_ACTION_STEPS_MEASUREMENT);
        intentFilter.addAction(ConstantsImp.BROADCAST_ACTION_HR_MEASUREMENT);
        intentFilter.addAction(ConstantsImp.BROADCAST_ACTION_SLEEP);
        intentFilter.addAction(ConstantsImp.BROADCAST_ACTION_HELO_CONNECTED);
        intentFilter.addAction(ConstantsImp.BROADCAST_ACTION_HELO_DISCONNECTED);
        intentFilter.addAction(ConstantsImp.BROADCAST_ACTION_HELO_DEVICE_RESET);
        intentFilter.addAction(ConstantsImp.BROADCAST_ACTION_HELO_BONDED);
        intentFilter.addAction(ConstantsImp.BROADCAST_ACTION_HELO_UNBONDED);
        intentFilter.addAction(ConstantsImp.BROADCAST_ACTION_HELO_FIRMWARE);
        intentFilter.addAction(ConstantsImp.BROADCAST_ACTION_DYNAMIC_HR_MEASUREMENT);
        intentFilter.addAction(ConstantsImp.BROADCAST_ACTION_DYNAMIC_STEPS_MEASUREMENT);
        intentFilter.addAction(ConstantsImp.BROADCAST_ACTION_HELO_DEVICE_RESET);
        intentFilter.addAction(ConstantsImp.BROADCAST_ACTION_MEASUREMENT_WRITE_FAILURE);
        intentFilter.addAction(ConstantsImp.BROADCAST_ACTION_LX_PLUS_NOT_ALLOWED);
        intentFilter.addAction(ConstantsImp.BROADCAST_ACTION_PWD);
        intentFilter.addAction(ConstantsImp.BROADCAST_ACTION_BUTTON);
        intentFilter.addAction(ConstantsImp.BROADCAST_ACTION_SOS);
        intentFilter.addAction(ConstantsImp.BROADCAST_ACTION_UV_MEASUREMENT);
        intentFilter.addAction(ConstantsImp.BROADCAST_ACTION_OXYGEN_MEASUREMENT);
        return intentFilter;
    }
}
