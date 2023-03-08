package it.rialtlas.healthmonitor.model;

import android.os.Environment;

import java.io.File;

import it.rialtlas.healthmonitor.BuildConfig;

public class Constants {
    public static final String APP_SP_NAME = "EMM-Health-Monitor";
    public static final String APP_KEY_DEV = "154265141669139954";
    public static final String APP_TOKEN = "76C38C6BA15699A4C31EC8488CC974B20C224FE2";
    public static final String EMAIL ="riatlas.onco@gmail.com";
    public static final String PIN = "66484";

    public static final String PIN_SP_KEY = "PIN";
    // public static final String WEB_BASE_URL = "http://192.168.1.36:8080/hr/";
    // public static final String  WEB_BASE_URL = "http://80.211.68.178:8080/hr/";
    public static final String WEB_BASE_URL = "https://80.19.188.140/hr/";
    public static final String UPDATE_PATIENTS_URL = WEB_BASE_URL+"api/hubData";
    public static final String GET_PATIENTS_URL = WEB_BASE_URL+"admin/patient?p=0&s=10000";

    public static File getDataDirectory() {
        return new File(BuildConfig.useSDCard ? Environment.getExternalStorageDirectory() : MeasurementsContextStrategy.getInstance().application().getFilesDir(), BuildConfig.targetFolder);
    }
}
