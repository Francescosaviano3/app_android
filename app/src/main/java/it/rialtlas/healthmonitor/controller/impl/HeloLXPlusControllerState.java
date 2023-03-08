package it.rialtlas.healthmonitor.controller.impl;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.worldgn.connector.Connector;
import com.worldgn.connector.DeviceItem;
import com.worldgn.connector.ILoginCallback;
import com.worldgn.connector.IPinCallback;
import com.worldgn.connector.ScanCallBack;

import it.rialtlas.healthmonitor.R;
import it.rialtlas.healthmonitor.controller.HeloControllerState;
import it.rialtlas.healthmonitor.model.Constants;
import it.rialtlas.healthmonitor.model.MeasurementsContext;
import it.rialtlas.healthmonitor.model.MeasurementsContextStrategy;
import it.rialtlas.healthmonitor.model.PatientData;
import it.rialtlas.healthmonitor.utils.ConfirmDialogRunner;
import it.rialtlas.healthmonitor.utils.MessagingUtils;

public enum HeloLXPlusControllerState implements HeloControllerState {

    STARTING {
        @Override
        public boolean process(final MeasurementsContext context) {
            Connector.getInstance().initialize(context.application(), Constants.APP_KEY_DEV, Constants.APP_TOKEN);
            Connector.getInstance().login(Constants.EMAIL, new ILoginCallback() {
                @Override
                public void onSuccess(long heloUserId) {
                    /* call the required Activity for successful login */
                    Log.d(TAG, "logged in to Helo Connector API with userId "+heloUserId);
                    context.heloUserId(heloUserId);
                    context.state(INITIALIZED);
                    MessagingUtils.shortToast(R.string.MSG_WORLDGN_ACCESS_PERFORMED);
                    MeasurementsContextStrategy.getInstance().state().unbind(context);
                    // MeasurementsContextStrategy.getInstance().state().scan(context);
                }
                @Override
                public void onPinverification() {
                    /*call the Pin verification activity to validate pin */
                    Log.d(TAG, "onPinverification");
                    SharedPreferences sharedPreferences = MeasurementsContextStrategy.getInstance().currentActivity().getApplicationContext()
                            .getSharedPreferences(Constants.APP_SP_NAME, Context.MODE_PRIVATE);
                    String pin = sharedPreferences.getString(Constants.PIN_SP_KEY, Constants.PIN);
                    Connector.getInstance().verifyPin(pin, new IPinCallback() {
                        @Override
                        public void onSuccess(long heloUserId) {
                            context.heloUserId(heloUserId);
                            context.state(INITIALIZED);
                            MessagingUtils.shortToast(R.string.MSG_WORLDGN_ACCESS_PERFORMED);

                        }
                        @Override
                        public void onFailure(String description) {
                            MessagingUtils.oKDialog(R.string.MSG_WARNING, MessagingUtils.translate(R.string.ERR_PIN_FAILURE), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    LayoutInflater layoutInflater = LayoutInflater.from(MeasurementsContextStrategy.getInstance().currentActivity());
                                    View promptView = layoutInflater.inflate(R.layout.pin_dialog, null);
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MeasurementsContextStrategy.getInstance().currentActivity());
                                    alertDialogBuilder.setView(promptView);

                                    final EditText editText = (EditText) promptView.findViewById(R.id.tvPin);
                                    // setup a dialog window
                                    alertDialogBuilder.setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    SharedPreferences sharedPreferences = MeasurementsContextStrategy.getInstance().currentActivity().getApplicationContext()
                                                            .getSharedPreferences(Constants.APP_SP_NAME, Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putString(Constants.PIN_SP_KEY, editText.getText().toString());
                                                    editor.commit();
                                                    MeasurementsContextStrategy.getInstance().state().process(MeasurementsContextStrategy.getInstance());
                                                }
                                            })
                                            .setNegativeButton("Annulla",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            android.os.Process.killProcess(android.os.Process.myPid());
                                                            System.exit(1);
                                                        }
                                                    });
                                    // create an alert dialog
                                    AlertDialog alert = alertDialogBuilder.create();
                                    alert.show();
                                }
                            });
                        }
                    });
                }
                @Override
                public void onFailure(String description) {
                    /*show the shortToast msg for failure*/
                    MessagingUtils.criticalError(R.string.ERR_LOGIN_FAILURE, true);
                }

                @Override
                public void accountVerification() {

                }


            });
            return true;
        }

        public boolean scan(MeasurementsContext context) {
            MessagingUtils.longToast(R.string.ERR_INITIALIZATION_NEEDED);
            return false;
        }

        @Override
        public boolean unbind(MeasurementsContext context) { return false; }

        @Override
        public boolean measureBloodPressure(MeasurementsContext context) { return false; }

        @Override
        public boolean measureHeartRate(MeasurementsContext context) { return false; }

        @Override
        public boolean measureBreathRate(MeasurementsContext context) { return false; }

        @Override
        public boolean measureOxygen(MeasurementsContext context) { return false; }

        @Override
        public boolean startMeasuring() { return false; }

        @Override
        public boolean takeNextMeasure(boolean isMissing) { return false; }

        @Override
        public boolean retryMissingMeasurements() { return false; }
    },
    INITIALIZED {
        @Override
        public boolean process(MeasurementsContext context) {
            return false;
        }

        public boolean scan(final MeasurementsContext context) {
            if (MeasurementsContextStrategy.getInstance().selectedPatient() == null || MeasurementsContextStrategy.getInstance().selectedPatient() == PatientData.NULL) {
                MessagingUtils.criticalError(R.string.ERR_PATIENT_NEEDED, false);
                return false;
            }
            Connector.getInstance().scan(new ScanCallBack() {
                @Override
                public void onScanStarted() {
                    MessagingUtils.longToast(R.string.MSG_SCAN_STARTED);
                }

                @Override
                public void onScanFinished() {
                    Connector.getInstance().stopScan();
                    MessagingUtils.longToast(R.string.MSG_SCAN_COMPLETED);
                }

                @Override
                public void onLedeviceFound(DeviceItem deviceItem) {
                    String wrist = MeasurementsContextStrategy.getInstance().patientWrist();
                    if ( wrist == null || (wrist != null && (wrist.trim().length() == 0 || ("null".equalsIgnoreCase(wrist))))) {
                        context.currentActivity().runOnUiThread(ConfirmDialogRunner.of(context.currentActivity(),
                                MessagingUtils.translate(R.string.MSG_CONNECT_DEVICE) + " " + deviceItem.getDeviceName() + " (" + deviceItem.getDeviceMac() + ")?",
                                ConnectToDevice.of(context, deviceItem)));

                    }
                    else {
                        if (wrist.equalsIgnoreCase(deviceItem.getDeviceMac())) {
                            ImageView ivStartMeasuring = ((ImageView) MeasurementsContextStrategy.getInstance().currentActivity().findViewById(R.id.ivStartMeasuringEnabled));
                            if (Connector.getInstance().connect(deviceItem)) {
                                context.state(CONNECTED);
                                if (ivStartMeasuring!= null) {
                                    ivStartMeasuring.setVisibility(View.INVISIBLE);
                                }
                                MessagingUtils.longToast(R.string.MSG_CONNECTION_OCCURRED);
                            } else {
                                if (ivStartMeasuring!= null) {
                                    ivStartMeasuring.setVisibility(View.VISIBLE);
                                }
                                MessagingUtils.criticalError(R.string.ERR_UNABLE_TO_CONNECT_TO_DEVICE, false);
                            }
                        }
                    }

                }

                @Override
                public void onPairedDeviceNotFound() {
                    // MessagingUtils.criticalError(R.string.ERR_PAIRED_DEVICE_NOT_FOUND,false);
                    // context.state(INITIALIZED);
                    System.out.println("onPairedDeviceNotFound");
                }

                @Override
                public void onFailure(String desc) {
                    System.out.println(desc);
                }
            });
            return true;

        }

        @Override
        public boolean unbind(MeasurementsContext context) {
            Connector.getInstance().unbindDevice();
            context.state(INITIALIZED);
            return true;
        }

        @Override
        public boolean measureBloodPressure(MeasurementsContext context) { return false; }

        @Override
        public boolean measureHeartRate(MeasurementsContext context) { return false; }

        @Override
        public boolean measureBreathRate(MeasurementsContext context) { return false; }

        @Override
        public boolean measureOxygen(MeasurementsContext context) { return false; }

        @Override
        public boolean startMeasuring() {
            MessagingUtils.criticalError(R.string.MSG_CONNECT_DEVICE, false);
            return false;
        }

        @Override
        public boolean takeNextMeasure(boolean isMissing) { return false; }

        @Override
        public boolean retryMissingMeasurements() { return false; }

    },
    SCANNING {
        @Override
        public boolean process(MeasurementsContext context) {
            return false;
        }

        @Override
        public boolean scan(MeasurementsContext context) {
            return false;
        }

        @Override
        public boolean unbind(MeasurementsContext context) { return false; }

        @Override
        public boolean measureBloodPressure(MeasurementsContext context) { return false; }

        @Override
        public boolean measureHeartRate(MeasurementsContext context) { return false; }

        @Override
        public boolean measureBreathRate(MeasurementsContext context) { return false; }

        @Override
        public boolean measureOxygen(MeasurementsContext context) { return false; }

        @Override
        public boolean startMeasuring() {
            MessagingUtils.criticalError(R.string.MSG_CONNECT_DEVICE, false);
            return false;
        }

        @Override
        public boolean takeNextMeasure(boolean isMissing) { return false; }

        @Override
        public boolean retryMissingMeasurements() { return false; }

        },
    MUST_SEND {
        @Override
        public boolean process(MeasurementsContext context) {
            return false;
        }

        @Override
        public boolean scan(MeasurementsContext context) {
            return false;
        }

        @Override
        public boolean unbind(MeasurementsContext context) {
            return false;
        }
        @Override
        public boolean measureBloodPressure(MeasurementsContext context) { return false; }

        @Override
        public boolean measureHeartRate(MeasurementsContext context) { return false; }

        @Override
        public boolean measureBreathRate(MeasurementsContext context) { return false; }

        @Override
        public boolean measureOxygen(MeasurementsContext context) { return false; }

        @Override
        public boolean startMeasuring() {
            MessagingUtils.oKCancelDialog(R.string.MSG_WARNING, "Attenzione! Sono presenti misure non trasmesse.\nSi desidera annullare i dati correnti?",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (MeasurementsContextStrategy.getInstance().selectedPatient()!=PatientData.NULL) {
                                MeasurementsContextStrategy.getInstance().state(CONNECTED);
                                MeasurementsContextStrategy.getInstance().state().startMeasuring();
                            }
                        }
                    },
                    null
                    );
            return false;
        }

        @Override
        public boolean takeNextMeasure(boolean isMissing) { return false; }

        @Override
        public boolean retryMissingMeasurements() { return false; }
    },
    CONNECTED {
        @Override
        public boolean process(MeasurementsContext context) {
            return false;
        }

        @Override
        public boolean scan(MeasurementsContext context) {
            return false;
        }

        @Override
        public boolean unbind(MeasurementsContext context) {
            Connector.getInstance().unbindDevice();
            context.state(INITIALIZED);
            return true;
        }

        @Override
        public boolean measureBloodPressure(MeasurementsContext context) {
            return Connector.getInstance().measureBP();
        }

        @Override
        public boolean measureHeartRate(MeasurementsContext context) {
            return Connector.getInstance().measureHR();
        }

        @Override
        public boolean measureBreathRate(MeasurementsContext context) {
            return Connector.getInstance().measureBr();
        }
        @Override
        public boolean measureOxygen(MeasurementsContext context) {
            return Connector.getInstance().measureOxygen();
        }

        @Override
        public boolean startMeasuring() {
            if (!MeasurementsContextStrategy.getInstance().isMeasuring()) {
                if (MeasurementsContextStrategy.getInstance().device()==null) {
                    MessagingUtils.criticalError(R.string.MSG_CONNECT_DEVICE, false);
                    return false;
                }
                MeasurementsContextStrategy.getInstance().resetMeasurements();
                MeasurementsContextStrategy.getInstance().waitForMeasurement(MeasurementsContextStrategy.getInstance().currentActivity());
                MeasurementsContextStrategy.getInstance().performMeasurement();
                return true;
            }
            return false;
        }

        @Override
        public boolean takeNextMeasure(boolean isMissing) {
            if (MeasurementsContextStrategy.getInstance().isMeasuring()) {
                MeasurementsContextStrategy.getInstance().removeCurrentMeasurement(isMissing);
                if (MeasurementsContextStrategy.getInstance().isMeasuring()) {
                    MeasurementsContextStrategy.getInstance().performMeasurement();
                    return true;
                }
                // Connector.getInstance().disconnect();
                // Connector.getInstance().connect(MeasurementsContextStrategy.getInstance().device());
                MeasurementsContextStrategy.getInstance().stopWaitingForMeasurement();
                // MeasurementsContextStrategy.getInstance().state().unbind(MeasurementsContextStrategy.getInstance());
                MeasurementsContextStrategy.getInstance().state(MUST_SEND);
            }
            return false;
        }

        @Override
        public boolean retryMissingMeasurements() {
            if (MeasurementsContextStrategy.getInstance().isMeasuring()) {
                MeasurementsContextStrategy.getInstance().performMeasurement();
                return true;
            }
            return false;
        }
    };


    private static final String TAG = HeloLXPlusControllerState.class.getSimpleName();

    private static class ConnectToDevice implements DialogInterface.OnClickListener {

        final MeasurementsContext context;
        final DeviceItem deviceItem;

        private ConnectToDevice(MeasurementsContext context, DeviceItem deviceItem) {
            this.context = context;
            this.deviceItem = deviceItem;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            ImageView ivStartMeasuring = ((ImageView) MeasurementsContextStrategy.getInstance().currentActivity().findViewById(R.id.ivStartMeasuringEnabled));
            if (Connector.getInstance().connect(this.deviceItem)) {
                context.state(CONNECTED);
                MeasurementsContextStrategy.getInstance().selectedPatient().setWrist(this.deviceItem.getDeviceMac());
                MessagingUtils.longToast(R.string.MSG_CONNECTION_OCCURRED);
                if (ivStartMeasuring!= null) {
                    ivStartMeasuring.setVisibility(View.INVISIBLE);
                }
            }
            else {
                if (ivStartMeasuring!= null) {
                    ivStartMeasuring.setVisibility(View.VISIBLE);
                }
                MessagingUtils.criticalError(R.string.ERR_UNABLE_TO_CONNECT_TO_DEVICE, false);
            }
        }

        public static ConnectToDevice of(MeasurementsContext context, DeviceItem deviceItem) {
            return new ConnectToDevice(context, deviceItem);
        }
    }
}
