package it.rialtlas.healthmonitor.view;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldgn.connector.AsyncTaskHelper;
import com.worldgn.connector.Connector;

import org.json.JSONObject;

import java.util.HashMap;

import it.rialtlas.healthmonitor.R;
import it.rialtlas.healthmonitor.controller.impl.HeloLXPlusControllerState;
import it.rialtlas.healthmonitor.model.MeasurementsContextStrategy;
import it.rialtlas.healthmonitor.utils.MessagingUtils;

public class PatientMeasurementFragment extends Fragment {
    private Context _context;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.patient_measurement_fragment, container, false);
        ImageView startMeasuringCmd = view.findViewById(R.id.imageView);
        startMeasuringCmd.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     Handler mainHandler = new Handler(Looper.getMainLooper());
                     Runnable myRunnable = new Runnable() {
                         @Override
                         public void run() {
                             if (MeasurementsContextStrategy.getInstance().state().equals(HeloLXPlusControllerState.CONNECTED)) {
                                 Log.d("PATIENTMEAS", "measuring BP");
                                 if (Connector.getInstance().measureBP()) {
                                     try {
                                         Thread.sleep(15000);
                                     } catch (InterruptedException ie) {
                                     }
                                 } else {
                                     MessagingUtils.criticalError(R.string.ERR_MEASUREMENT_FAILED, false);
                                 }
                             } else {
                                 MessagingUtils.criticalError(R.string.ERR_PAIRED_DEVICE_NOT_FOUND, false);
                             }
                         }
                     };
                     mainHandler.post(myRunnable);
                 }
             });

        startMeasuringCmd = view.findViewById(R.id.ivTestECG);
        startMeasuringCmd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (MeasurementsContextStrategy.getInstance().state().equals(HeloLXPlusControllerState.CONNECTED)) {
                            Log.d("PATIENTMEAS", "measuring ECG");
                            if (Connector.getInstance().measureECG()) {
                                try {
                                    Thread.sleep(15000);
                                } catch (InterruptedException ie) {
                                }
                            }
                            else {
                                MessagingUtils.criticalError(R.string.ERR_MEASUREMENT_FAILED, false);
                            }
                        }
                        else {
                            MessagingUtils.criticalError(R.string.ERR_PAIRED_DEVICE_NOT_FOUND, false);
                        }
                    }
                };
                mainHandler.post(myRunnable);
            }
        });

        startMeasuringCmd = view.findViewById(R.id.imageView_22_2);
        startMeasuringCmd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (MeasurementsContextStrategy.getInstance().state().equals(HeloLXPlusControllerState.CONNECTED)) {
                            Log.d("PATIENTMEAS", "measuring Breath Rate");
                            if (Connector.getInstance().measureBr()) {
                                try {
                                    Thread.sleep(15000);
                                } catch (InterruptedException ie) {
                                }
                            }
                            else {
                                MessagingUtils.criticalError(R.string.ERR_MEASUREMENT_FAILED, false);
                            }
                        }
                        else {
                            MessagingUtils.criticalError(R.string.ERR_PAIRED_DEVICE_NOT_FOUND, false);
                        }
                    }
                };
                mainHandler.post(myRunnable);
            }
        });

        startMeasuringCmd = view.findViewById(R.id.imageView22);
        startMeasuringCmd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (MeasurementsContextStrategy.getInstance().state().equals(HeloLXPlusControllerState.CONNECTED)) {
                            Log.d("PATIENTMEAS", "measuring Heart Rate");
                            if (Connector.getInstance().measureHR()) {
                                try {
                                    Thread.sleep(15000);
                                } catch (InterruptedException ie) {
                                }
                            } else {
                                MessagingUtils.criticalError(R.string.ERR_MEASUREMENT_FAILED, false);
                            }
                        } else {
                            MessagingUtils.criticalError(R.string.ERR_PAIRED_DEVICE_NOT_FOUND, false);
                        }
                    }
                };
                mainHandler.post(myRunnable);
            }
        });

        startMeasuringCmd = view.findViewById(R.id.imageView_11);
        startMeasuringCmd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (MeasurementsContextStrategy.getInstance().state().equals(HeloLXPlusControllerState.CONNECTED)) {
                           Log.d("PATIENTMEAS", "measuring Oxygen");
                            if (Connector.getInstance().measureOxygen()) {
                               try { Thread.sleep(15000); } catch (InterruptedException ie) { }
                            }
                            else {
                                MessagingUtils.criticalError(R.string.ERR_MEASUREMENT_FAILED, false);
                            }
                        }
                        else {
                            MessagingUtils.criticalError(R.string.ERR_PAIRED_DEVICE_NOT_FOUND, false);
                        }
                    }
                };
                mainHandler.post(myRunnable);
            }
        });

        ImageView startMeasuringImg =  view.findViewById(R.id.ivStartMeasuring);
        startMeasuringImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 04-12-21 [Start]
//                if (!MeasurementsContextStrategy.getInstance().state().startMeasuring()) {
//                    if (MeasurementsContextStrategy.getInstance().isMeasuring())
//                        MessagingUtils.longToast(R.string.MSG_ALREADY_MEASURING);
//                }

                if (MeasurementsContextStrategy.getInstance().state().equals(HeloLXPlusControllerState.CONNECTED)) {
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            Connector.getInstance().listCharacteristics();
                            try { Thread.sleep(5000); } catch (InterruptedException ie) { }

                            Log.d("PATIENTMEAS", "updateNewTime");
                            Connector.getInstance().updateNewTime();
                            try { Thread.sleep(5000); } catch (InterruptedException ie) { }

                            Log.d("PATIENTMEAS", "deviceCalibration");
                            Connector.getInstance().deviceCalibration();
                            try { Thread.sleep(5000); } catch (InterruptedException ie) { }

                            Log.d("PATIENTMEAS", "skinCalibration");
                            Connector.getInstance().skinCalibration();
                            try { Thread.sleep(5000); } catch (InterruptedException ie) { }
                        }
                    };
                    mainHandler.post(myRunnable);
                    // MessagingUtils.criticalError(R.string.ERR_FIRMWARE_MIGRATION, false);



                    // Connector.getInstance().measureECG(); // OK
                    // Connector.getInstance().deviceCalibration();
                    // Connector.getInstance().getHeartRate();
                    // MeasurementsContextStrategy.getInstance().state().measureBloodPressure(MeasurementsContextStrategy.getInstance());
                }
                // 04-12-21 [Start]
            }
        });

        ImageView spo2Img = view.findViewById(R.id.imageView_11_h);
        spo2Img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setView(promptView);

                final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
                // setup a dialog window
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ((TextView) getView().findViewById(R.id.spo2)).setText(MeasurementsContextStrategy.getInstance().spo2(editText.getText().toString()).spo2());
                            }
                        })
                        .setNegativeButton("Annulla",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                // create an alert dialog
                AlertDialog alert = alertDialogBuilder.create();
                alert.show();
            }
        });

        ImageView heartRateImg =  view.findViewById(R.id.imageView22_h);
        heartRateImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setView(promptView);

                final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
                // setup a dialog window
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ((TextView) getView().findViewById(R.id.heartRate)).setText(MeasurementsContextStrategy.getInstance().heartRate(editText.getText().toString()).heartRate());
                            }
                        })
                        .setNegativeButton("Annulla",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                // create an alert dialog
                AlertDialog alert = alertDialogBuilder.create();
                alert.show();
            }
        });

        ImageView breathRateImg =  view.findViewById(R.id.imageView_22_h);
        breathRateImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setView(promptView);

                final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
                // setup a dialog window
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ((TextView) getView().findViewById(R.id.breath)).setText(MeasurementsContextStrategy.getInstance().breathRate(editText.getText().toString()).breathRate());
                            }
                        })
                        .setNegativeButton("Annulla",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                // create an alert dialog
                AlertDialog alert = alertDialogBuilder.create();
                alert.show();
            }
        });

        ImageView bloodPressureImg =  view.findViewById(R.id.imageView_h);
        bloodPressureImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View promptView = layoutInflater.inflate(R.layout.double_input_dialog, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setView(promptView);

                final EditText systolic = (EditText) promptView.findViewById(R.id.edittext1);
                final EditText diastolic = (EditText) promptView.findViewById(R.id.edittext2);
                // setup a dialog window
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ((TextView) getView().findViewById(R.id.systolic)).setText(MeasurementsContextStrategy.getInstance().systolicPressure(systolic.getText().toString()).systolicPressure());
                                ((TextView) getView().findViewById(R.id.diastolic)).setText(MeasurementsContextStrategy.getInstance().diastolicPressure(diastolic.getText().toString()).diastolicPressure());
                            }
                        })
                        .setNegativeButton("Annulla",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                // create an alert dialog
                AlertDialog alert = alertDialogBuilder.create();
                alert.show();
            }
        });

        ImageView bloodGlucoseImg =  view.findViewById(R.id.imageView_22);
        bloodGlucoseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setView(promptView);

                final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
                // setup a dialog window
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ((TextView) getView().findViewById(R.id.glucose)).setText(MeasurementsContextStrategy.getInstance().glucose(editText.getText().toString()).glucose());
                            }
                        })
                        .setNegativeButton("Annulla",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                // create an alert dialog
                AlertDialog alert = alertDialogBuilder.create();
                alert.show();
            }
        });

        ImageView weightImg =  view.findViewById(R.id.imageView_32);
        weightImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View promptView = layoutInflater.inflate(R.layout.input_dialog, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setView(promptView);

                final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                // setup a dialog window
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ((TextView) getView().findViewById(R.id.weight)).setText(MeasurementsContextStrategy.getInstance().weight(editText.getText().toString()).weight());
                            }
                        })
                        .setNegativeButton("Annulla",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                // create an alert dialog
                AlertDialog alert = alertDialogBuilder.create();
                alert.show();
            }
        });
        ImageView  sendImage = view.findViewById(R.id.ivSendMeasures);
        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeasurementsContextStrategy.getInstance().sendMeasurements();

            }
        });
        return view;

    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        _context = context;
    }




}
