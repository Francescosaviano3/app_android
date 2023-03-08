package it.rialtlas.healthmonitor.model.impl;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.worldgn.connector.DeviceItem;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import it.rialtlas.healthmonitor.R;
import it.rialtlas.healthmonitor.controller.HeloControllerState;
import it.rialtlas.healthmonitor.controller.impl.HeloLXPlusControllerState;
import it.rialtlas.healthmonitor.model.Constants;
import it.rialtlas.healthmonitor.model.MeasurementsContext;
import it.rialtlas.healthmonitor.model.MeasurementsContextStrategy;
import it.rialtlas.healthmonitor.model.PatientData;
import it.rialtlas.healthmonitor.utils.MessagingUtils;
import it.rialtlas.healthmonitor.utils.OkHttpClientUtils;
import it.rialtlas.healthmonitor.utils.ProcessingDialog;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MeasurementsContextImpl extends MeasurementsContext {
    private final String TAG = MeasurementsContextImpl.class.getSimpleName();

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "Pascale01";

    private final Application application;


    private Activity currentActivity;
    private HeloControllerState currentState;
    private long heloUserId;
    private int deviceFirmware = -1;
    private PatientData patient;

    private DeviceItem device;
    private String systolicPressure;
    private String diastolicPressure;
    private String heartRate;
    private String breathRate;
    private String spo2;
    private String steps;
    private String mood;
    private String fatigue;

    public String patientId() {
        return patient != null ? patient.getId() : null;
    }

    public PatientData selectedPatient() {
        return patient;
    }

    public String patientWrist() {
        return patient != null ? patient.getWrist() : null;
    }


    public MeasurementsContext patientId(PatientData patient) {
        this.patient = patient;
        return this;
    }

    private MeasurementsContextImpl(Application application, HeloControllerState startingState) {
        this.application = application;
        this.currentState = startingState;
    }

    @Override
    public HeloControllerState state() {
        return this.currentState;
    }

    @Override
    public MeasurementsContext state(HeloControllerState state) {
        this.currentState = state;
        ImageView ivSendMeasuresEnabled = ((ImageView) MeasurementsContextStrategy.getInstance().currentActivity().findViewById(R.id.ivSendMeasuresEnabled));
        if (ivSendMeasuresEnabled != null) {
            ivSendMeasuresEnabled.setVisibility(this.currentState!=HeloLXPlusControllerState.MUST_SEND ? View.VISIBLE : View.INVISIBLE);
        }
        return this;
    }

    @Override
    public Application application() {
        return this.application;
    }

    @Override
    public Activity currentActivity() {
        return this.currentActivity;
    }

    @Override
    public MeasurementsContext currentActivity(Activity currentActivity) {
        this.currentActivity = currentActivity;
        return this;
    }

    @Override
    public MeasurementsContext heloUserId(long heloUserId) {
        this.heloUserId = heloUserId;
        return this;
    }

    @Override
    public long heloUserId() {
        return this.heloUserId;
    }

    @Override
    public MeasurementsContext deviceFirmware(int firmware) {
        this.deviceFirmware = firmware;
        return this;
    }

    @Override
    public String deviceLabel() {
        StringBuilder sb = new StringBuilder();
        if (device()== null) {
            sb.append(MessagingUtils.translate(R.string.LBL_DISCONNECTED));
        }
        else {
            sb.append(device().getDeviceMac());
            sb.append(" ");
            sb.append("FW:");
            if (this.deviceFirmware > 0) {
                sb.append(Integer.toString(this.deviceFirmware));
            } else {
                sb.append("?");
            }
        }
        return sb.toString();
    }

    @Override
    public MeasurementsContext device(DeviceItem device) {
        this.device = device;
        return this;
    }

    @Override
    public DeviceItem device() {
        return this.device;
    }

    public static MeasurementsContext of(Application application, HeloControllerState startingState) {
        return new MeasurementsContextImpl(application, startingState);
    }

    @Override
    public MeasurementsContext systolicPressure(String bp) {
        this.systolicPressure = bp;
        return this;
    }

    @Override
    public String systolicPressure() {
        return systolicPressure;
    }

    @Override
    public MeasurementsContext diastolicPressure(String bp) {
        this.diastolicPressure = bp;
        return this;
    }

    @Override
    public String diastolicPressure() {
        return this.diastolicPressure;
    }

    @Override
    public MeasurementsContext heartRate(String rate) {
        this.heartRate = rate;
        return this;
    }

    @Override
    public String heartRate() {
        return this.heartRate;
    }

    @Override
    public MeasurementsContext breathRate(String rate) {
        this.breathRate = rate;
        return this;
    }

    @Override
    public String breathRate() {
        return this.breathRate;
    }

    @Override
    public MeasurementsContext spo2(String rate) {
        this.spo2 = rate;
        return this;
    }

    @Override
    public String spo2() {
        return this.spo2;
    }

    @Override
    public MeasurementsContext  steps(String steps) {
        this.steps = steps;
        return this;
    }

    @Override
    public String steps() { return this.steps; }

    @Override
    public MeasurementsContext mood(String mood) {
        this.mood = mood;
        return this;
    }

    @Override
    public String mood() { return this.mood; }

    @Override
    public MeasurementsContext fatigue(String fatigue) {
        this.fatigue = fatigue;
        return this;
    }

    @Override
    public String fatigue() { return this.fatigue; }

    private ProcessingDialog pd;

    @Override
    public MeasurementsContext stopWaitingForMeasurement() {
        if (this.pd!=null) {
            this.pd.completed();
            this.pd = null;
        }
        return this;
    }

    @Override
    public MeasurementsContext waitForMeasurement(Context context) {
        if (this.pd==null) {
            this.pd = ProcessingDialog.waitFor(context);
        }
        return this;
    }
    private String glucose;

    @Override
    public MeasurementsContext glucose(String value) {
        this.glucose = value;
        return this;
    }

    @Override
    public String glucose() {
        return this.glucose;
    }

    private String weight;

    @Override
    public MeasurementsContext weight(String value) {
        this.weight = value;
        return this;
    }

    @Override
    public String weight() {
        return this.weight;
    }


    @Override
    public void sendMeasurements() {
        if (patientId()!= null) {
            // if ( state()==HeloLXPlusControllerState.MUST_SEND) {
                new SendMeasurementsTask().execute(null, null, null);
            // }
            // else {
            //     MessagingUtils.criticalError(R.string.ERR_NO_WRIST_MEASURES, false);
            // }
        }
        else {
            MessagingUtils.criticalError(R.string.ERR_NO_PATIENT, false);
        }

    }

    public class SendMeasurementsTask extends AsyncTask<Void, Void, Void> {

        private String makeDdata() {
            boolean valueAdded = false;
            StringBuilder sb = new StringBuilder();
            if(systolicPressure()!=null) {
                if (valueAdded) sb.append("|");
                sb.append(String.format("SYS=%s",systolicPressure()));
                valueAdded = true;
            }
            if(diastolicPressure()!=null) {
                if (valueAdded) sb.append("|");
                sb.append(String.format("DIA=%s",diastolicPressure()));
                valueAdded = true;
            }
            if(breathRate()!=null) {
                if (valueAdded) sb.append("|");
                sb.append(String.format("BR=%s",breathRate()));
                valueAdded = true;
            }
            if(glucose()!=null) {
                if (valueAdded) sb.append("|");
                sb.append(String.format("G=%s",glucose()));
                valueAdded = true;
            }
            if(heartRate()!=null) {
                if (valueAdded) sb.append("|");
                sb.append(String.format("HR=%s",heartRate()));
                valueAdded = true;
            }
            if(spo2()!=null) {
                if (valueAdded) sb.append("|");
                sb.append(String.format("SpO2=%s",spo2()));
                valueAdded = true;
            }
            if(weight()!=null) {
                if (valueAdded) sb.append("|");
                sb.append(String.format("W=%s",weight()));
                valueAdded = true;
            }
            if(steps()!=null) {
                if (valueAdded) sb.append("|");
                sb.append(String.format("STP=%s",steps()));
                valueAdded = true;
            }
            if(mood()!=null) {
                if (valueAdded) sb.append("|");
                sb.append(String.format("MOOD=%s",mood()));
                valueAdded = true;
            }
            if(fatigue()!=null) {
                if (valueAdded) sb.append("|");
                sb.append(String.format("FAT=%s",fatigue()));
                valueAdded = true;
            }
            return sb.toString();

        }
        @Override
        protected Void doInBackground(Void... voids) {
            DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ITALY);
            String date = df.format(new Date());
            String ddata = makeDdata();
            if (ddata.trim().length()==0) {
                MessagingUtils.criticalError(R.string.ERR_NO_MEASURES, false);
            }
            FormBody formBody = new FormBody.Builder()
                    .add("patId", patientId())
                    .add("hubId", "0002")
                    .add("dtype", "MP")
                    .add("dclass", "000900")
                    .add("dvendor", "riatlas")
                    .add("dprotocol", "BT")
                    .add("dsource", "00-50-FC-A0-67-2C")
                    .add("rawdata", "empty")
                    .add("serialNumber", "0001")
                    .add("dtime", date)
                    .add("htime", date)
                    .add("ddata", makeDdata())
                    .build();

            OkHttpClient httpClient = new OkHttpClient();
            Request.Builder requestBuilder =  new Request.Builder().url(Constants.UPDATE_PATIENTS_URL)
                    .post(formBody)
                    .addHeader("content-type", "application/json; charset=utf-8")
                    .addHeader("Cache-Control", "no-cache")
                    .addHeader("Pragma", "no-cache")
                    .addHeader("Referer", Constants.WEB_BASE_URL)
                    .addHeader("Connection", "keep-alive")
                    .addHeader("Accept-Encoding", "gzip, deflate, br")
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");

            Request request = requestBuilder.build();
            try {
                OkHttpClient okHttpClient = OkHttpClientUtils.getUnsafeOkHttpClient();
                // new OkHttpClient.Builder().build();
                Response response = okHttpClient.newCall(request).execute();
                if (response.isSuccessful()) {
                   MessagingUtils.longToast(R.string.MSG_DATA_SENT_SUCCESSFUL);


                    MeasurementsContextStrategy.getInstance().currentActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
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
                            // MeasurementsContextStrategy.getInstance().state(HeloLXPlusControllerState.CONNECTED);
                        }
                    });
//                    Intent mStartActivity = new Intent(application().getApplicationContext(), OncoSupportMainActivity.class);
//                    int mPendingIntentId = 123456;
//                    PendingIntent mPendingIntent = PendingIntent.getActivity(application().getApplicationContext(), mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
//                    AlarmManager mgr = (AlarmManager) application().getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
//                    System.exit(0);
                   return null;
                }
            }
            catch (Exception e) {
                Log.w(TAG, "Unable to get patients", e);
            }
            MessagingUtils.longToast(R.string.MSG_DATA_SENDING_FAILURE);

            return null;
        }
    }
}
