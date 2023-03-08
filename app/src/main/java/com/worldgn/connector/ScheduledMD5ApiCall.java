package com.worldgn.connector;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduledMD5ApiCall extends JobService {


    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        DebugLogger.i("JOBSER", "ScheduledMD5ApiCall onStartJob");
        checkMD5();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }


    public void checkMD5() {
        String app_key = "";
        String md5 =getMD5Checksum();
        try {
            app_key = SafePreferences.getInstance().getStringFromSecurePref(ConstantsImp.AP_KEY_PREF, "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*app_key = "150772884640619364";
        md5 = "212d29abc6e954fdf45303ebce495053";*/

        Call<Md5APIResponse> call = ApiUtils.getMD5Service().md5Checksum(md5, app_key);
        call.enqueue(new Callback<Md5APIResponse>() {

            @Override
            public void onResponse(Call<Md5APIResponse> call, Response<Md5APIResponse> response) {

                if(response.isSuccessful()) {

                    Md5APIResponse apiResponse = response.body();
                    DebugLogger.i("MD5API", apiResponse.getSuccess()+ " "+apiResponse.getMessage());
                    if(apiResponse.getSuccess() == 0) {
                        GlobalData.isDev = ConstantsImp.DEV_YES;
                        List<MacDevice> macList = apiResponse.getHelodevicedev().getMacdevicekey();
                        GlobalData.macDevices.clear();
                        if(macList != null) {
                            for (MacDevice macDevice : macList) {
                                GlobalData.macDevices.add(macDevice.getDeviceid());
                            }
                        }
                        SafePreferences.getInstance().saveStringToSecurePref(Constants.PREF_DEV, ConstantsImp.DEV_YES);
                        SafePreferences.getInstance().saveStringToSecurePref(Constants.JSON_KEY_MACDEVICE, new Gson().toJson(apiResponse.getHelodevicedev().getMacdevicekey()));
                    } else {
                        GlobalData.isDev = ConstantsImp.DEV_NO;
                        SafePreferences.getInstance().saveStringToSecurePref(Constants.PREF_DEV, ConstantsImp.DEV_NO);
                    }

                }
            }

            @Override
            public void onFailure(Call<Md5APIResponse> call, Throwable throwable) {
                DebugLogger.i("MD5API err", throwable.getMessage());
            }
        });
    }

    public String getMD5Checksum() {
        String path = getApkPath(ScheduledMD5ApiCall.this);
        String hash = "";
        if(!TextUtils.isEmpty(path)){
            try {
                hash = HashUtil.calculateMD5(new File(path));

            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return hash;
    }

    public static String getApkPath(Context context) {
        String packageName = context.getPackageName();
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
            String apk = appInfo.publicSourceDir;
            return apk;
        } catch (Exception ex) {
        }
        return "";
    }



}
