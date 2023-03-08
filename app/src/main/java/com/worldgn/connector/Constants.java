package com.worldgn.connector;

/**
 * Created by Krishna Rao on 15/09/2017.
 */

public class Constants {




    //CALLBACK CONSTANTS
    public static final String CALLBACK_INTILIZE_APPKEY_TOKEN="First call initialize with app key and token";
    public static final String CALLBACK_APPKEY_EMPTY="app key is empty";
    public static final String CALLBACK_TOKEN_EMPTY="app token is empty";
    public static final String CALLBACK_EMAIL_EMPTY="Email address is empty";
    public static final String CALLBACK_VALID_EMAIL="Enter valid email address";
    public static final String CALLBACK_PREFIX_EMPTY="Prefix is empty";
    public static final String CALLBACK_PHONENUMBER_EMPTY="Phone number is empty";
    public static final String CALLBACK_VERIFICATIONCODE_EMPTY=" verification code is empty";
    public static final String CALLBACK_INVALIDPIN_CODE="Invalid pin code";

    //ASYNC CONSTANTS
    public static final String SIMPLETASK_CONNECTION_ERROR="There is an error in connecting with cloud, try again";

    //JSON CONSTANTS
    public static final String JSON_KEY_DESCRIPTION="description";
    public static final String JSON_KEY_SUCCESS="success";
    public static final String JSON_KEY_UPDATE="update";
    public static final String JSON_KEY_DEVICEID="deviceid";
    public static final String JSON_KEY_MACDEVICE="macdevicekey";
    public static final String JSON_KEY_IS_ASSOCIATE="is_associate";
    public static final String JSON_KEY_PERMISSION="permission";

    //device names
    public static final String DEVICE_NAME = "Helo";
    public static final String DEVICE_SUBNAME1 = "seedmorn";
    public static final String DEVICE_SUBNAME2 = "HeloHL01";
    public static final String DEVICE_SUBNAME3 = "HelolX";
    public static final String DEVICE_SUBNAME4 = "HeloHL02";
    public static final String DEVICE_SUBNAME5="HeloLX";
    public static final String DEVICE_SUBNAME6="HeloLX+";

    //For tags in measurementrate api (writing characteristic)
    public static final String HEART_RATE = "HeartRate";
    public static final String IDENTIFY_CLIENT_STYLE = "HeartRate";
    public static final String UPDATE_NEW_TIME = "updnewtime";
    public static final String SET_STANDARD_BP = "setstandardbp";
    public static final String MATCH_INFO_USERID = "matchinfouserid";
    public static final String MATCH_INFO_MAC = "matchinfomac";
    public static final String ACK_FOR_BIND_REQUEST = "ackbindreq";
    public static final String UNBIND_DEVICE = "unbind";
    public static final String STOPMEASURING = "stopmeasuring";
    public static final String MEASHINE_DOWN = "meashinedown";
    public static final String MEASURE_HR = "measurehr";
    public static final String MEASURE_BP = "measurebps";
    public static final String BP_RESET = "bp_reset";
    public static final String MEASURE_ECG = "measure_ecg";
    public static final String MEASURE_BR = "measure_br";
    public static final String MEASURE_MF = "measure_mf";
    public static final String MEASURE_PW1 = "measure_pw1";
    public static final String MEASURE_PW2 = "measure_pw2";
    public static final String INIT_DEVICE_LOAD_CODE = "initdevloadcode";
    public static final String SECOND_MACH = "second_mach";
    public static final String APP_VERSION = "app_version";
    public static final String GET_STEPS = "get_steps";
    public static final String SET_LED_LIGHT = "set_led_light";
    public static final String START_SEND_DECRY_TOKEN = "senddecry_token";
    public static final String START_SEND_DECRY_TOKEN_CONTENT = "decry_token_content";
    public static final String SEND_REVISE_AUTO_TIME = "revise_auto_time";





    //Service measuretype
    static final String TYPE_BLOOD_PRESSURE = "Blood Pressure";
    static final String TYPE_BREATH_RATE = "Breath Rate";
    static final String TYPE_ECG = "ECG";
    static final String TYPE_FATIGUE = "Fatigue";
    static final String TYPE_HEART_RATE = "Heart Rate";
    static final String TYPE_MOOD = "Mood";
    static final String TYPE_STEP = "Steps";
    static final String TYPE_SLEEP = "Sleep";
    static final String TYPE_DEEP_SLEEP = "Deep sleep";
    static final String TYPE_WAKEUP = "Wakeup";


    //FATIGUE
    static final String FATIGUE_NORMAL = "Normal";
    static final String FATIGUE_TIRED = "Tired";
    static final String FATIGUE_VERY_TIRED = "Very_Tired";
    static final String FATIGUE_INVALID = "Invalid";


    //MOOD
    static final String MOOD_DEPRESSION = "Depression";
    static final String MOOD_CALM = "Calm";
    static final String MOOD_EXCITEMENT = "Excitement";
    static final String MOOD_INVALID = "Invalid";

    //Sleep
    static final String SLEEP_TYPE_LIGHT = "01";
    static final String SLEEP_TYPE_DEEP = "02";
    static final String SLEEP_TYPE_WAKEUP = "03";


    //Internet connectivity checking
    static final String NO_INTERNET = "Internet connection not available";
    static final String SERVER_NOT_REACHABLE = "Server not reachable";

    //failure message
    static final String HR_REQ_FAIL = "Heart rate measurement request to Helo device failed. Please try again.";
    static final String BR_REQ_FAIL = "Breath rate measurement request to Helo device failed. Please try again.";
    static final String BP_REQ_FAIL = "Blood pressure measurement request to Helo device failed. Please try again.";
    static final String ECG_REQ_FAIL = "ECG measurement request to Helo device failed. Please try again.";
    static final String MF_REQ_FAIL = "Mood and Fatigue measurement request to Helo device failed. Please try again.";
    static final String STEPS_REQ_FAIL = "Steps measurement request to Helo device failed. Please try again.";
    static final String START_DYNAMIC_REQ_FAIL = "Start Dynamic measurement request to Helo device failed. Please try again.";
    static final String STOP_DYNAMIC_REQ_FAIL = "Stop Dynamic measurement request to Helo device failed. Please try again.";
    static final String UV_REQ_FAIL = "UV measurement request to Helo device failed. Please try again.";
    static final String OXY_REQ_FAIL = "Oxygen measurement request to Helo device failed. Please try again.";
    static final String LX_PLUS_MEASUREMENT = "This measurement supports only in LX+ device";

    static final String GREEN_REQ_FAIL = "Green PPG measurement request to Helo device failed. Please try again.";
    static final String RnIR_REQ_FAIL = "Green PPG measurement request to Helo device failed. Please try again.";
    static final String SKIN_CALIB_REQ_FAIL = "Skin calibration request to Helo device failed. Please try again.";
    static final String SCHEDULE_MEASURE_GRT_120 = "Scheduled measurement should not greater than 120 mins";
    static final String SCHEDULE_MEASURE_LESS_30 = "Scheduled measurement should not less than 30 mins";

    static final String START_GREEN_REQ_FAIL = "Start GREEN PPG measurement request to Helo device failed. Please try again.";
    static final String STOP_GREEN_REQ_FAIL = "Stop GREEN PPG measurement request to Helo device failed. Please try again.";

    static final String START_RNIR_REQ_FAIL = "Start RNIR PPG measurement request to Helo device failed. Please try again.";
    static final String STOP_RNIR_REQ_FAIL = "Stop RNIR PPG measurement request to Helo device failed. Please try again.";

    //LX+ not allowed
    static final String lxPlusNotALlowed = "XXX Measurement not allowed for LX+";
    static final String FIRMWARE_NOT_SUPPORTED_FOR_CALIB = "Current firmware does not support skin calibration";
    static final String FIRMWARE_NOT_SUPPORTED_FOR_ACCEL = "Current firmware does not support Accelerometer";
    static final String FIRMWARE_NOT_SUPPORTED_FOR_DYN = "Current firmware does not support Dynamic steps and heartrate measurement";
    static final String FIRMWARE_NOT_SUPPORTED_FOR_OXY = "Current firmware does not support Oxygen measurement";
    static final String FIRMWARE_NOT_SUPPORTED_FOR_UV = "Current firmware does not support UV measurement";

    static final String PREF_DEV = "PREF_DEV";


}
