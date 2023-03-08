package com.worldgn.connector;

import android.app.job.JobParameters;
import android.app.job.JobService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleJobService extends JobService{

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        DebugLogger.i("JOBSER", "ScheduleJobService onStartJob");
        checkCompatible(jobParameters);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    private void checkCompatible(final JobParameters jobParameters) {
        String app_key = "";
        String mac = "";
        try {
            app_key = SafePreferences.getInstance().getStringFromSecurePref(ConstantsImp.AP_KEY_PREF, "");
            mac = SafePreferences.getInstance().getStringFromSecurePref(ConstantsImp.MAC_PREF, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        CompatabiltyRequest compatabiltyRequest = new CompatabiltyRequest();
        compatabiltyRequest.setAppKey(app_key);
        compatabiltyRequest.setMacAddress(mac);
        compatabiltyRequest.setAction("compatibilityCheck");
        Call<CompatibilityResponse> call = ApiUtils.getDevServcie().compatibiltycheck(compatabiltyRequest);
        call.enqueue(new Callback<CompatibilityResponse>() {
            @Override
            public void onResponse(Call<CompatibilityResponse> call, Response<CompatibilityResponse> response) {
                CompatibilityResponse compatibilityResponse = response.body();

                if(response.code() == 200) {
                    GlobalData.isCompatible = true;
                    SafePreferences.getInstance().saveBooleanToSecurePref(ConstantsImp.compatabilty_pref, true);
                } else {
                    GlobalData.isCompatible = false;
                    SafePreferences.getInstance().saveBooleanToSecurePref(ConstantsImp.compatabilty_pref, false);
                }
                /*if(response.isSuccessful() && compatibilityResponse != null) {
                    DebugLogger.i("APITEST", compatibilityResponse.getStatusMessage());
                } else {
                    ResponseBody responseBody = response.errorBody();
                    DebugLogger.i("APITEST", "Error "+responseBody.toString());
                }*/
                jobFinished(jobParameters, false);
            }

            @Override
            public void onFailure(Call<CompatibilityResponse> call, Throwable throwable) {
                DebugLogger.i("APITEST", "error "+throwable.getMessage());
                jobFinished(jobParameters, false);
            }
        });
    }

}
