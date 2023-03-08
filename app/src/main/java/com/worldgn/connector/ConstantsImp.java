package com.worldgn.connector;

/**
 * Created by vijay on 06-11-2017.
 */

class ConstantsImp {

    //FOR CONSTANT PREFRENCES
    public static final String PREF_USERIDHELO="UserIDHelo";
    public static final String PREF_TOKENSESSION="tokensession";
    public static final String PREF_PERMISSION="permission";
    public static final String PREF_IS_LOGGED_IN="is_logged_in";
    public static final String PREF_HELODEVICEDEV="helodevicedev";
    public static final String PREF_DEVELOPER="developer";
    public static final String PREF_PRODUCTION="production";
    public static final String PREF_DEV_ENVIRONMENT="dev";
    public static final String PREF_FIRMWARE="firmware";
    public static final String PREF_MAC ="mac";
//    public static final String APP_KEY="3827984763984753827";
    public static final String PREF_SLEEP_LIGHT ="sleep_light";
    public static final String PREF_SLEEP_DEEP ="sleep_deep";
    public static final String PREF_WAKEUP_COUNT ="wakeup_count";
    public static final String PREF_SLEEP_LIGHT_DURATION ="sleep_light_dur";
    public static final String PREF_SLEEP_DEEP_DURATION ="sleep_deep_dur";
    public static final String PREF_START_SLEEP_HR = "start_sleep_hr";
    public static final String PREF_STOP_SLEEP_HR = "stop_sleep_hr";
    public static final String PREF_START_DEEP_SLEEP_HR = "start_deep_sleep_hr";
    public static final String PREF_STOP_DEEP_SLEEP_HR = "stop_deep_sleep_hr";
    public static final String PREF_WAKEUP_TIME = "wakeup_time";

    public static final String PREF_DECRY_TOKEN ="decry_token";


    //BROADCAST ACTIONS
    public static final String BROADCAST_ACTION__MEASUREMENT = "com.worldgn.connector_helo_lx_plus.HR_MEASUREMENT";

    public static final String BROADCAST_ACTION_HR_MEASUREMENT = "com.worldgn.connector_plus.HR_MEASUREMENT";
    public static final String BROADCAST_ACTION_DYNAMIC_HR_MEASUREMENT = "com.worldgn.connector_plus.DYNAMIC_HR_MEASUREMENT";
    public static final String BROADCAST_ACTION_DYNAMIC_STEPS_MEASUREMENT = "com.worldgn.connector_plus.DYNAMIC_STEPS_MEASUREMENT";
    public static final String BROADCAST_ACTION_BR_MEASUREMENT = "com.worldgn.connector_plus.BR_MEASUREMENT";
    public static final String BROADCAST_ACTION_SLEEP = "com.worldgn.connector_plus.SLEEP";
    public static final String BROADCAST_ACTION_BATTERY = "com.worldgn.connector_plus.BATTERY"; //
    public static final String BROADCAST_ACTION_MAIN_DATA_KLL = "com.worldgn.connector_plus.main_data_kll";
    //public static final String BROADCAST_ACTION_BP_MEASUREMENT = "com.worldgn.connector_plus.BP_MEASUREMENT";
    public static final String BROADCAST_ACTION_BP_MEASUREMENT = "com.worldgn.w22.ble.BluetoothLeService.ACTION_MAIN_DATA_BP";
    public static final String BROADCAST_ACTION_MOOD_MEASUREMENT = "com.worldgn.connector_plus.MOOD_MEASUREMENT";
    public static final String BROADCAST_ACTION_FATIGUE_MEASUREMENT = "com.worldgn.connector_plus.FATIGUE_MEASUREMENT";
    private static final String BROADCAST_ACTION_PW_MEASUREMENT = "com.worldgn.connector_plus.PW_MEASUREMENT";
    private static final String BROADCAST_ACTION_HEART_RATE_PACKAGE = "com.worldgn.connector_plus.HEART_RATE_PACKAGE";
    private static final String BROADCAST_ACTION_DEVICELOAD_MEASUREMENT = "com.worldgn.connector_plus.DEVICELOAD";
    public static final String BROADCAST_ACTION_STEPS_MEASUREMENT = "com.worldgn.connector_plus.STEPS_MEASUREMENT";
    public static final String BROADCAST_ACTION_UV_MEASUREMENT = "com.worldgn.connector_plus.UV_MEASUREMENT";
    //public static final String BROADCAST_ACTION_OXYGEN_MEASUREMENT = "com.worldgn.connector_plus.OXYGEN_MEASUREMENT";
    public static final String BROADCAST_ACTION_OXYGEN_MEASUREMENT = "com.worldgn.w22.ble.BluetoothLeService.ACTION_MAIN_DATA_OXYGEN";
    public static final String BROADCAST_ACTION_BUTTON = "com.worldgn.connector_plus.BUTTON";
    public static final String BROADCAST_ACTION_SOS = "com.worldgn.connector_plus.SOS";

    public static final String BROADCAST_ACTION_ECG_MEASUREMENT = "com.worldgn.w22.ble.BluetoothLeService.ACTION_MAIN_DATA_ECG";
    public static final String ACTION_MAIN_DATA_ECGALLDATA = "com.worldgn.w22.ble.BluetoothLeService.ACTION_MAIN_DATA_ECGALLDATA";
    //public static final String BROADCAST_ACTION_ECG_MEASUREMENT = "com.worldgn.connector_plus.ECG_MEASUREMENT";
    public static final String BROADCAST_ACTION_APPVERSION_MEASUREMENT = "com.worldgn.connector_plus.APPVERSION_MEASUREMENT";
    public static final String BROADCAST_ACTION_LED_SUCCESS = "com.worldgn.connector_plus.LED_SUCCESS";
    public static final String BROADCAST_ACTION_HELO_DEVICE_RESET = "com.worldgn.connector_plus.ACTION_HELO_DEVICE_RESET";
    public static final String BROADCAST_ACTION_HELO_CONNECTED = "com.worldgn.connector_plus.ACTION_HELO_CONNECTED";
    public static final String BROADCAST_ACTION_HELO_DISCONNECTED = "com.worldgn.connector_plus.ACTION_HELO_DISCONNECTED";
    public static final String BROADCAST_ACTION_HELO_BONDED = "com.worldgn.connector_plus.ACTION_HELO_BONDED";
    public static final String BROADCAST_ACTION_HELO_UNBONDED = "com.worldgn.connector_plus.ACTION_HELO_UNBONDED";
    public static final String BROADCAST_ACTION_HELO_FIRMWARE = "com.worldgn.connector_plus.ACTION_HELO_FIRMWARE";
    public static final String BROADCAST_ACTION_PWD = "com.worldgn.connector_plus.ACTION_PWD";
    public static final String BROADCAST_ACTION_MEASUREMENT_WRITE_FAILURE = "com.worldgn.connector_plus.MEASURE_WRITE_FAILURE";
    public static final String BROADCAST_ACTION_LX_PLUS_NOT_ALLOWED = "com.worldgn.connector_plus.LXPLUS";
    public static final String BROADCAST_ACTION_OXY_PPG = "com.worldgn.connector_plus.OXY_PPG";
    public static final String BROADCAST_ACTION_OXY_IRNIR = "com.worldgn.connector_plus.OXY_IRNIR";
    public static final String BROADCAST_ACTION_PPG_QTY = "com.worldgn.connector_plus.PPG_QUALITY";
    public final static String ACTION_HUMAN_DETECT_NOTAG = "com.worldgn.connector_plus.ACTION_HUMAN_DETECT_NOTAG";
    public final static String ACTION_HUMAN_DETECT_SUCCESS = "com.worldgn.connector_plus.ACTION_HUMAN_DETECT_SUCCESS";
    public final static String ACTION_HUMAN_DETECT_FAIL = "com.worldgn.connector_plus.ACTION_HUMAN_DETECT_FAIL";
    public static final String BROADCAST_ACTION_RRVALUE = "com.worldgn.connector_plus.RRVALUE";
    public static final String BROADCAST_ACTION_ACCEL = "com.worldgn.connector_plus.ACCEL";

    //INTENT KEY FOR BROADCAST ACTIONS
    public static final String INTENT_KEY_HR_MEASUREMENT = "HR_MEASUREMENT";
    public static final String INTENT_KEY_DYNAMIC_HR_MEASUREMENT = "DYNAMIC_HR_MEASUREMENT";

    public static final String INTENT_KEY_BR_MEASUREMENT = "BR_MEASUREMENT";
    public static final String INTENT_KEY_DEEP_SLEEP = "DEEP_SLEEP";
    public static final String INTENT_KEY_LIGHT_SLEEP = "LIGHT_SLEEP";
    public static final String INTENT_KEY_SLEEP = "SLEEP";
    public static final String INTENT_KEY_START_TIME = "START_TIME";
    public static final String INTENT_KEY_STOP_TIME = "STOP_TIME";
    public static final String INTENT_KEY_WAKEUP_TIME = "WAKEUP_TIME";
    public static final String INTENT_KEY_BATTERY = "BATTERY";
    public static final String INTENT_KEY_KLL = "MAIN_DATA_KLL";
    public static final String INTENT_KEY_BP_MEASUREMENT_MAX = "BP_MEASUREMENT_MAX";
    public static final String INTENT_KEY_BP_MEASUREMENT_MIN = "BP_MEASUREMENT_MIN";
    public static final String INTENT_KEY_MOOD_MEASUREMENT = "MOOD_MEASUREMENT";
    public static final String INTENT_KEY_FATIGUE_MEASUREMENT = "FATIGUE_MEASUREMENT";
    public static final String INTENT_KEY_OXYGEN_MEASUREMENT = "OXYGEN_MEASUREMENT";
    public static final String INTENT_KEY_UV_MEASUREMENT = "UV_MEASUREMENT";

    public static final String INTENT_KEY_STEPS_MEASUREMENT = "STEPS_MEASUREMENT";
    public static final String INTENT_KEY_DYNAMIC_STEPS_MEASUREMENT = "DYNAMIC_STEPS_MEASUREMENT";
    public static final String INTENT_KEY_ECG_MEASUREMENT = "ECG_MEASUREMENT";
    public static final String INTENT_KEY_ECG_VDO = "ECG_VDO";
    public static final String INTENT_KEY_HELO_CONNECTED = "HELO_CONNECTED";
    public static final String INTENT_KEY_FIRMWARE = "HELO_FIRMWARE";
    public static final String INTENT_KEY_MAC = "HELO_MAC";
    public static final String INTENT_MEASUREMENT_WRITE_FAILURE = "MEASUREMENT_WRITE_FAILURE";
    public static final String INTENT_LX_PLUS = "LXPLUS_DATA";
    public static final String INTENT_KEY_PWD = "PWD";
    public static final String INTENT_KEY_OXY_PPG = "OXY_PPG";
    public static final String INTENT_OXY_IR = "OXY_IR";
    public static final String INTENT_OXY_NIR = "OXY_NIR";
    public static final String INTENT_PPG_QTY = "PPG_QTY";
    public static final String INTENT_RRVALUE = "RRVALUE";
    public static final String INTENT_XVAL = "XVAL";
    public static final String INTENT_YVAL = "YVAL";
    public static final String INTENT_ZVAL = "ZVAL";
    public static final String INTENT_GYRO_XVAL = "GYRO_X";
    public static final String INTENT_GYRO_YVAL = "GYRO_Y";
    public static final String INTENT_GYRO_ZVAL = "GYRO_Z";



    //Commands from app to device
    static final byte bindReqCmd = 0x13;
    static final byte initDeviceLoadCodeCmd = 0x1a;
    static final byte matchInfoCmd = 0x11;
    static final byte secondMachCmd = 0x1b;
    static final byte startSendDecryTokenCmd = 0x1d; //decry token Length
    static final byte sendDecryTokenContentCmd = 0x1E; //decry token content
    static final byte updateNewTimeCmd = 0x12; //datatime syncgronization
    static final byte sendReviseAutoTime = 0x17; //health plan


    //JSON CONSTANTS
    public static final String JSON_KEY_IT_EXIST="it_exist";

    public static final String CALLBACK_INVALID_HELOID="HeloId is null";
    public static final String CALLBACK_INVALID_CREDENTIALS="username or sponsor doesn`t exist";

    public static final String compatabilty_pref = "COMPATABILITY_PREF";
    public static final String MAC_PREF = "MAC";
    public static final String AP_KEY_PREF = "APP_KEY";

    //Dev mac ids list
    public static final String DEV_YES = "YES";
    public static final String DEV_NO = "NO";
    public static final String DEV_NOT_CALLED = "NOT YET CALLED";

}
