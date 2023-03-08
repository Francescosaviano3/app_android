package it.rialtlas.healthmonitor.utils;

import android.os.AsyncTask;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import it.rialtlas.healthmonitor.R;
import it.rialtlas.healthmonitor.model.Constants;
import it.rialtlas.healthmonitor.model.MeasurementsContextStrategy;
import it.rialtlas.healthmonitor.model.PatientData;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSource;

public class UpdatePatientsTask extends AsyncTask<Void, Void, Void> {
    private final List<PatientData> patientsData;
    private final ListView patientsListView;
    private final LinearLayout layoutToHide;
    private final Button okButton;
    private final Button cancelButton;

    public UpdatePatientsTask(ListView patientsListView, List<PatientData> patientsData, LinearLayout layoutToHide, Button okButton, Button cancelButton) {
        this.patientsData = patientsData;
        this.patientsListView = patientsListView;
        this.layoutToHide = layoutToHide;
        this.okButton = okButton;
        this.cancelButton = cancelButton;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        this.okButton.setEnabled(false);
        this.cancelButton.setEnabled(false);
        this.layoutToHide.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        this.cancelButton.setEnabled(true);
        this.layoutToHide.setVisibility(View.INVISIBLE);
        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        boolean errorOccurred = true;
        OkHttpClient okHttpClient = OkHttpClientUtils.getUnsafeOkHttpClient();
                // new OkHttpClient.Builder().build();
        Request.Builder requestBuilder =  new Request.Builder().url(Constants.GET_PATIENTS_URL)
                .get()
                .addHeader("Accept", "application/json, text/plain, */*")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Pragma", "no-cache")
                .addHeader("Referer", Constants.WEB_BASE_URL)
                .addHeader("Connection", "keep-alive")
                .addHeader("Accept-Encoding", "gzip, deflate, br")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
        Request request = requestBuilder.build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                final BufferedSource source = response.body().source();
                source.request(Integer.MAX_VALUE);
                final byte[] bytes = source.buffer().snapshot().toByteArray();
                String str = new String(bytes);
                try{
                    JSONObject myResponse = new JSONObject(str);
                    if (myResponse!= null) {
                        if (myResponse.has("content")) {
                            patientsData.clear();
                            patientsData.add(PatientData.NULL);
                            JSONArray patientsListArray = myResponse.getJSONArray("content");
                            for (int i=0; i<patientsListArray.length(); i++) {
                                JSONObject patientData = patientsListArray.getJSONObject(i);
                                patientsData.add(PatientData.of(patientData.getString("patientIdentifier"), patientData.getString("name"), patientData.getString("surname"), patientData.has("wrist") ? ("null".equalsIgnoreCase(patientData.getString("wrist")) ? "" : patientData.getString("wrist").toUpperCase()) : null, patientData.getString("id")));
                            }
                            MeasurementsContextStrategy.getInstance().currentActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((BaseAdapter) patientsListView.getAdapter()).notifyDataSetChanged();
                                    patientsListView.invalidate();
                                    patientsListView.setSelection(0);
                                    MessagingUtils.shortToast(R.string.MSG_PATIENTS_UPDATED);
                                }
                            });
                            errorOccurred = false;
                        }
                        else {
                        }
                    }
                    else {
                    }
                }
                catch (JSONException e) {
                }
            }
            else {
            }
        }
        catch (Exception e) {
        }
        if (errorOccurred) {
            MessagingUtils.criticalError(R.string.MSG_PATIENTS_UPDATE_FAILED, false);
        }
        return null;
    }
}
