package com.worldgn.connector;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import it.rialtlas.healthmonitor.R;
import it.rialtlas.healthmonitor.utils.MessagingUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**\
 * Created by WGN on 20-09-2017.
 */

public class DataParser {

    private static String TAG = DataParser.class.getSimpleName();
    private final static String SleepLight = "SLEEP LIGHT";
    private final static String SleepDeep = "SLEEP DEEP";
    private final static String TurnOver = "TURN OVER";
    private static DataParser instance;
    private static String LOCK = "lock";
    private String lastOxygenItem;
    private long lastDateTime;
//    private HashMap<String, HashMap<String, ArrayList<SleepData>>> sleepClassifier = new HashMap<>();
    private HashMap<String, HashMap<String, Long>> sleepClassifier = new HashMap<>();
    private final static String Sleepcmd = "Sleep";

    private long lastGreenPPgtime = 0;
    private String greenPpgFileName = "";
    private long lastredPPgtime = 0;
    private String redPpgFileName = "";
    private String irpgFileName = "";


    private DataParser() {

    }

    int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    ThreadPoolExecutor executor = new ThreadPoolExecutor(
            NUMBER_OF_CORES*2,
            NUMBER_OF_CORES*2,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>()
    );



    public static DataParser getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new DataParser();
                }
            }
        }
        return instance;
    }


    public void oxyMeasurementParser(ArrayList<String> arrayList, Context context) {
        String oxygenMeasurement;
        ArrayList<String> parsedList = new ArrayList<>();
        for(int i=0;i<arrayList.size();i++) {
            oxygenMeasurement = parseOxygenSingeData(arrayList.get(i));
            parsedList.add(oxygenMeasurement);
        }
        DebugLogger.e("Parsed List",parsedList.toString());
        if( parsedList!=null && parsedList.size()>0 ) {
            final Intent intent = new Intent();
            intent.setAction(ConstantsImp.BROADCAST_ACTION_OXYGEN_MEASUREMENT);
            intent.putExtra(ConstantsImp.INTENT_KEY_OXYGEN_MEASUREMENT, parsedList.get(parsedList.size()-1));
            context.sendBroadcast(intent);
        }
    }

    public void dataMeasurementParser(final String data, Context context) {
        try {
            String dataType = data.substring(9, 11);
            String result;
            final Intent intent = new Intent();
            String dataStr;

            switch (dataType) {

                case "24":
                    //SOS - Double click
                    intent.setAction(ConstantsImp.BROADCAST_ACTION_SOS);
                    break;

                case "26":
                    //Button cmd - Single click
                    intent.setAction(ConstantsImp.BROADCAST_ACTION_BUTTON);
                    break;

                case "31":
                    if (GlobalData.stepsMeasure) {
                        String steps = parseSingeData(data);
                        int stepsCount = Integer.parseInt(steps);
                        int calorie = (int) (4.5 * stepsCount * 1.75 / 4 / 2 * 75 / 1800);
                        float distance = (float) (stepsCount * 0.762);
                        double miles = distance / 1000 * 0.621;
                /*measurement.setStep("100");
                measurement.setDistance("20");
                measurement.setUm("2");
                measurement.setCalorie("10");*/
                        int parseInt = Integer.parseInt(steps);
                        intent.setAction(ConstantsImp.BROADCAST_ACTION_STEPS_MEASUREMENT);
                        intent.putExtra(ConstantsImp.INTENT_KEY_STEPS_MEASUREMENT, Integer.toString(parseInt));
//                sendMeasurement(context, Constants.TYPE_STEP, Integer.toString(stepsCount), Float.toString(distance), "5", Integer.toString(calorie));
                        GlobalData.stepsMeasure = false;
                    }
                    break;
                case "32":
                case "0C":
                    result = parseSingeData(data);
                    intent.setAction(ConstantsImp.BROADCAST_ACTION_HR_MEASUREMENT);
                    intent.putExtra(ConstantsImp.INTENT_KEY_HR_MEASUREMENT, result);
//                sendMeasurement(context, Constants.TYPE_HEART_RATE, result);
                    break;

                case "3B":
                    dataStr = data.substring(27, 29);
                    result = parseMoodIntData(dataStr);
                    DebugLogger.i(TAG, "MOOD " + result);
                    intent.setAction(ConstantsImp.BROADCAST_ACTION_MOOD_MEASUREMENT);
                    intent.putExtra(ConstantsImp.INTENT_KEY_MOOD_MEASUREMENT, result);

                    // sendMeasurement(context, Constants.TYPE_MOOD, result, dataStr);
                    break;
                case "3C":
                    dataStr = data.substring(27, 29);
                    result = getFatigueValue(dataStr);
                    intent.setAction(ConstantsImp.BROADCAST_ACTION_FATIGUE_MEASUREMENT);
                    intent.putExtra(ConstantsImp.INTENT_KEY_FATIGUE_MEASUREMENT, result);
                    DebugLogger.i(TAG, "FATIGUE  " + result);
                    //sendMeasurement(context, Constants.TYPE_FATIGUE, result, dataStr);
                    break;
                case "3D":
                    result = parseBRData(data);
                    intent.setAction(ConstantsImp.BROADCAST_ACTION_BR_MEASUREMENT);
                    intent.putExtra(ConstantsImp.INTENT_KEY_BR_MEASUREMENT, result);
                    DebugLogger.i(TAG, "BR " + result);
                    // sendMeasurement(context, Constants.TYPE_BREATH_RATE, result);
                    break;
                case "34":
                    result = parseSingeData(data);
                    intent.setAction(ConstantsImp.BROADCAST_ACTION_MAIN_DATA_KLL);
                    intent.putExtra(ConstantsImp.INTENT_KEY_KLL, result);
                    DebugLogger.i(TAG, "MAIN DATA KLL  " + result);
                    break;
            /*case "35":
                long sleepData = parseSleepDatatoLong(data);
                intent.setAction(Constants.BROADCAST_ACTION_SLEEP);
                intent.putExtra(Constants.INTENT_KEY_SLEEP, sleepData);
                DebugLogger.i(TAG, "SLEEP  " + sleepData);
//                WriteToDevice.stopMeasuring(bluetoothGatt);
                break;*/
                case "35":
                    String uvMeasurement = parseNewUVSingeData(data);
                    intent.setAction(ConstantsImp.BROADCAST_ACTION_UV_MEASUREMENT);
                    intent.putExtra(ConstantsImp.INTENT_KEY_UV_MEASUREMENT, uvMeasurement);
                    GlobalData.isUVMEasurement=false;
                    break;

                case "36":
                    if (data != null) {
                        String oxygenMeasurement = parseOxygenSingeData(data);
                        intent.setAction(ConstantsImp.BROADCAST_ACTION_OXYGEN_MEASUREMENT);
                        intent.putExtra(ConstantsImp.INTENT_KEY_OXYGEN_MEASUREMENT, oxygenMeasurement);
                    }
                    break;

                case "41":
                    String[] results = parseBpData(data);
                    intent.setAction(ConstantsImp.BROADCAST_ACTION_BP_MEASUREMENT);
                    intent.putExtra(ConstantsImp.INTENT_KEY_BP_MEASUREMENT_MAX, results[0]);
                    intent.putExtra(ConstantsImp.INTENT_KEY_BP_MEASUREMENT_MIN, results[1]);
                    DebugLogger.i(TAG, "BP  " + results[0] + " " + results[1]);
                    // MessagingUtils.criticalError("BP  " + results[0] + " " + results[1], false);
                    // sendMeasurement(context, Constants.TYPE_BLOOD_PRESSURE, parseBpDataForSer(data));
                    break;
//            case "42":
//                result = parseEcgData(data);
//                intent.setAction(Constants.BROADCAST_ACTION_ECG_MEASUREMENT);
//                intent.putExtra(Constants.INTENT_KEY_ECG_MEASUREMENT, result);
//                DebugLogger.i(TAG, "ECG  "+result);
//                WriteToDevice.stopMeasuring(bluetoothGatt);
//                sendMeasurement(context, Constants.TYPE_ECG, result);
//                break;
                case "43":
                    int battery = pareseBatteryData(data);
                    intent.setAction(ConstantsImp.BROADCAST_ACTION_BATTERY);
                    intent.putExtra(ConstantsImp.INTENT_KEY_BATTERY, battery);
                    DebugLogger.i(TAG, "BATTERY  " + battery);
                    break;


                case "45":
                    if (data != null) {
                        String substring = data.substring(15, 17);
                        DebugLogger.d("sqs", "è®¾å¤‡å‘æ¥ LED substring = " + substring);
                        if ("01".equals(substring)) {
                            DebugLogger.i(TAG, "LED RESULT SUCCESS ");
                            intent.setAction(ConstantsImp.BROADCAST_ACTION_LED_SUCCESS);
                        } else {
                            DebugLogger.i(TAG, "LED RESULT FAILURE ");
                        }

                    }
                    //check data for suucess or failure
                    break;
                case "47":
//                intent.setAction(Constants.BROADCAST_ACTION_HELO_DEVICE_RESET);
                    DebugLogger.i(TAG, "DEVICE RESET  "); //Device reset function need to be add parse further on data
                    break;
                case "49":
                    parsePPGQuality(context, data);
                    break;


            }
            context.sendBroadcast(intent);
        }catch (Exception e){
            DebugLogger.e("Exception",e.toString());
        }
        //WriteToDevice.stopMeasuring(bluetoothGatt);
    }

    private void parsePPGQuality(Context context, String data) {
        DebugLogger.d(TAG, "parsePPGQuality: -----" + data);
        String timeStamp = data.substring(15, 26);
        String ppgQuality = data.substring(27, 38);
        String dataTimeStamp = timeStamp.substring(9, 11) + timeStamp.substring(6, 8)
                + timeStamp.substring(3, 5) + timeStamp.substring(0, 2);
        String dataPPGQuality = ppgQuality.substring(9, 11) + ppgQuality.substring(6, 8)
                + ppgQuality.substring(3, 5) + ppgQuality.substring(0, 2);
        long parseTimeStamp = Long.parseLong(dataTimeStamp, 16);
        int parsePPGQuality = Integer.parseInt(dataPPGQuality, 16);
        DebugLogger.d(TAG, "parsePPGQuality value1: -----" + parseTimeStamp);
        //broadcastUpdate(GlobalData.ACTION_MAIN_DATA_OXYGEN_PPG_TIMESTAMP, parseTimeStamp);
        DebugLogger.d(TAG, "parsePPGQuality value2: -----" + parsePPGQuality);
        broadcastValues(context, ConstantsImp.BROADCAST_ACTION_PPG_QTY, ConstantsImp.INTENT_PPG_QTY, parsePPGQuality);
    }

    private void broadcastValues(Context context, String action, String key, int value) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(key, value);
        context.sendBroadcast(intent);
    }


    public void sendEcg(Context context, String data, String ecgVedio) {
        if(data!=null && !data.equalsIgnoreCase("")) {
            Intent intent = new Intent();
            String result = parseEcgData(data);
            intent.setAction(ConstantsImp.BROADCAST_ACTION_ECG_MEASUREMENT);
            intent.putExtra(ConstantsImp.INTENT_KEY_ECG_MEASUREMENT, result);
            intent.putExtra(ConstantsImp.INTENT_KEY_ECG_VDO, "[" + ecgVedio + "]");
            context.sendBroadcast(intent);
            DebugLogger.i(TAG, "ECG  " + result);
            // sendMeasurement(context, Constants.TYPE_ECG, result, ecgVedio);
        }
    }

    // [04-12-2021] Metodo scollegato dal connettore. Impedisco che i dati vengano trasmessi in rete
    public void sendMeasurement(Context context, String type, String... datas) {

        Measurement measurement = getMeasurement();
        String firmware = "";
        try {
            firmware = SafePreferences.getInstance().getStringFromSecurePref(ConstantsImp.PREF_FIRMWARE, "");
        }catch (Exception e){
            e.printStackTrace();
        }
        measurement.setFirmware(firmware);
        measurement.setMeasuretype(type);
        switch (type) {
            case Constants.TYPE_BLOOD_PRESSURE:
                measurement.setIdtable("1");
                measurement.setHighbp(datas[0]);
                measurement.setLowbp(datas[1]);
                break;

            case Constants.TYPE_BREATH_RATE:
                measurement.setIdtable("2");
                measurement.setData_value(datas[0]);
                break;

            case Constants.TYPE_ECG:
                measurement.setIdtable("3");
                measurement.setData_type(datas[0]);
                measurement.setEcgvideo(datas[1]);
                break;

            case Constants.TYPE_FATIGUE:
                measurement.setIdtable("4");
                measurement.setMeasureData(datas[0]); //Normal or depression
                measurement.setData_type(datas[1]);    // 00 or 01

                break;

            case Constants.TYPE_HEART_RATE:
                measurement.setIdtable("5");
                measurement.setData_value(datas[0]);
                break;

            case Constants.TYPE_MOOD:
                measurement.setIdtable("6");
                measurement.setMeasureData(datas[0]); //Normal or dep
                measurement.setData_type(datas[1]); //00 or 01
                break;

            case Constants.TYPE_STEP:
                measurement.setIdtable("7");
                measurement.setStep(datas[0]);
                measurement.setDistance(datas[1]);
                measurement.setUm(datas[2]);
                measurement.setCalorie(datas[3]);
                break;

            case Constants.TYPE_SLEEP:
                measurement.setIdtable("8");
                measurement.setStartSleep(datas[0]);
                measurement.setStopSleep(datas[1]);
                measurement.setStartdeepsleep(datas[2]);
                measurement.setStopdeepsleep(datas[3]);
                measurement.setTimesleep(datas[4]);
                measurement.setDeepSleep(datas[5]);
                measurement.setLightSleep(datas[6]);
                measurement.setGroupNumber("0");
                measurement.setWakeup(datas[7]);
                measurement.setTimestamp(datas[8]);
                break;

            case Constants.TYPE_DEEP_SLEEP:
                measurement.setIdtable("9");
                measurement.setStartdeepsleep(datas[0]);
                measurement.setStopdeepsleep(datas[1]);
                measurement.setType("0");
                measurement.setGroupNumber("0");
                measurement.setTimestamp(datas[2]);
                break;

            case Constants.TYPE_WAKEUP:
                measurement.setIdtable("10");
                measurement.setWakeuptime(datas[0]);
                measurement.setType("0");
                measurement.setGroupNumber("0");
                measurement.setTimestamp(datas[1]);
                break;


        }
        DebugLogger.i(TAG, "type " + type);
        DebugLogger.i(TAG, new Gson().toJson(measurement));
        String token = "";
        try {
            token = SafePreferences.getInstance().getStringFromSecurePref(ConstantsImp.PREF_TOKENSESSION, "");
        }catch (Exception e){
            e.printStackTrace();
        }

        DebugLogger.i("APICALL", token);
        RequestBody tokenrb = RequestBody.create(MediaType.parse("text/plain"), token);
        RequestBody deviceidrb = RequestBody.create(MediaType.parse("text/plain"), Util.getIMEI(context));
        RequestBody bprb = RequestBody.create(MediaType.parse("text/plain"), new Gson().toJson(measurement));
        DebugLogger.i("APICALL", "measurement obj  " + new Gson().toJson(measurement));
        Call<ApiResponse> call = ApiUtils.getAPIService().saveMeasurement(tokenrb, deviceidrb, bprb);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                ApiResponse apiResponse = response.body();
                if (apiResponse != null)
                    DebugLogger.i("APICALL", "onResponse " + apiResponse.getSuccess() + "  " + apiResponse.getTable() + " " + apiResponse.getDescription());

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                DebugLogger.i("APICALL", "onFailure " + t.toString());
            }
        });

    }

    private Measurement getMeasurement() {

        Measurement measurement = new Measurement();
        String userIdHelo = "";
        String mac = "";
        String firmware = "";
        try {
            userIdHelo = SafePreferences.getInstance().getStringFromSecurePref(ConstantsImp.PREF_USERIDHELO, "");
            mac = SafePreferences.getInstance().getStringFromSecurePref(ConstantsImp.PREF_MAC, "");
            firmware = SafePreferences.getInstance().getStringFromSecurePref(ConstantsImp.PREF_FIRMWARE, "");
        }catch (Exception e) {
            e.printStackTrace();
        }
        measurement.setUserid(userIdHelo);
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        measurement.setDate(sdf.format(today));
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        measurement.setDatatime(sdf2.format(today));
        measurement.setMac(mac);
        measurement.setPpgVedio("");
        measurement.setRating("2");
        measurement.setVip("1");
        measurement.setLatitude(LocationValues.getInstance(null).getLatitude());
        measurement.setLongitude(LocationValues.getInstance(null).getLongitude());
        measurement.setTimestamp(Long.toString(System.currentTimeMillis()));
//        TimeZone.getDefault()
//        Calendar.getInstance().getTimeZone().getDisplayName()
        measurement.setTimezone("0");
        measurement.setScheduled("1");
        measurement.setFirmware(firmware);
        measurement.setAppversion("1");
        measurement.setApptype("1");
        measurement.setAppkey(GlobalData.app_key);
        return measurement;

    }


/*    public void parseSleepData(String data) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // HH:mm:ss

        String sleepType = data.substring(15, 17);
        String startSleepData = data.substring(18, 29);
        long startSleepTS = Long.parseLong(startSleepData.substring(9) + startSleepData.substring(6, 8) + startSleepData.substring(3, 5) + startSleepData.substring(0, 2), 16) - (Integer.parseInt(TimeUtils.offSetTimeZone()) / 1000);

        String durationTimeData = data.substring(30, 41);
        long durationinMillis = Long.parseLong(durationTimeData.substring(9) + durationTimeData.substring(6, 8) + durationTimeData.substring(3, 5) + durationTimeData.substring(0, 2), 16);
        long stopSleepTS = startSleepTS + (durationinMillis * 60);
        DebugLogger.i("sleep  ", "start " + startSleepTS + " stop " + stopSleepTS + " duration " + durationinMillis);
        String dataDate = sdf.format(new Date(startSleepTS * 1000));

        SleepData sleepData = new SleepData();
        sleepData.setStartSleep(startSleepTS);
        sleepData.setStopSleep(stopSleepTS);
        sleepData.setSleeptype(sleepType);
        sleepData.setDuration(durationinMillis * 60);
        sleepData.setSleepDate(dataDate);

        Gson gson = new Gson();
        String sleepJson = gson.toJson(sleepData);
        String prefJsonArr = "[]";
        TypeToken listType = new TypeToken<List<SleepData>>() {
        };
        List<SleepData> sleepDatas;

        switch (sleepType) {

            case Constants.SLEEP_TYPE_LIGHT:
                long lightSleepDuration = 0;
                try {
                    prefJsonArr = SafePreferences.getInstance().getStringFromSecurePref(ConstantsImp.PREF_SLEEP_LIGHT, "[]");
                    lightSleepDuration = SafePreferences.getInstance().getLongFromSecurePref(ConstantsImp.PREF_SLEEP_LIGHT_DURATION, 0L);
                }catch (Exception e){
                    e.printStackTrace();
                }

                sleepDatas = gson.fromJson(prefJsonArr, listType.getType());
                sleepDatas.add(sleepData);
                SafePreferences.getInstance().saveStringToSecurePref(ConstantsImp.PREF_SLEEP_LIGHT, gson.toJson(sleepDatas));

                long lightDuration = lightSleepDuration + sleepData.getDuration();
                SafePreferences.getInstance().saveLongToSecurePref(ConstantsImp.PREF_SLEEP_LIGHT_DURATION, lightDuration);
                break;

            case Constants.SLEEP_TYPE_DEEP:
                prefJsonArr = "[]";
                long deepSleepDuration = 0;
                try {
                    prefJsonArr = SafePreferences.getInstance().getStringFromSecurePref(ConstantsImp.PREF_SLEEP_DEEP, "[]");
                    deepSleepDuration = SafePreferences.getInstance().getLongFromSecurePref(ConstantsImp.PREF_SLEEP_DEEP_DURATION, 0L);

                }catch (Exception e){
                    e.printStackTrace();
                }

                sleepDatas = gson.fromJson(prefJsonArr, listType.getType());
                sleepDatas.add(sleepData);
                SafePreferences.getInstance().saveStringToSecurePref(ConstantsImp.PREF_SLEEP_DEEP, gson.toJson(sleepDatas));

                long duration = deepSleepDuration + sleepData.getDuration();
                SafePreferences.getInstance().saveLongToSecurePref(ConstantsImp.PREF_SLEEP_DEEP_DURATION, duration);
                break;

            case Constants.SLEEP_TYPE_WAKEUP:
                prefJsonArr = "[]";
                try {
                    prefJsonArr = SafePreferences.getInstance().getStringFromSecurePref(ConstantsImp.PREF_WAKEUP_COUNT, "[]");
                }catch (Exception e){

                }

                sleepDatas = gson.fromJson(prefJsonArr, listType.getType());
                sleepDatas.add(sleepData);
                SafePreferences.getInstance().saveStringToSecurePref(ConstantsImp.PREF_WAKEUP_COUNT, gson.toJson(sleepDatas));
                break;

        }
    }*/

    public void parseSleepData(String data, Context context) {
        writetosleepFile(data);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // HH:mm:ss

        String sleepType = data.substring(15, 17);
        String startSleepData = data.substring(18, 29);
        long startSleepTS = Long.parseLong(startSleepData.substring(9) + startSleepData.substring(6, 8) + startSleepData.substring(3, 5) + startSleepData.substring(0, 2), 16) - (Integer.parseInt(TimeUtils.offSetTimeZone()) / 1000);

        String durationTimeData = data.substring(30, 41);
        long durationinMillis = Long.parseLong(durationTimeData.substring(9) + durationTimeData.substring(6, 8) + durationTimeData.substring(3, 5) + durationTimeData.substring(0, 2), 16);
        long stopSleepTS = startSleepTS + (durationinMillis * 60);
        DebugLogger.i("sleep  ", "start " + startSleepTS + " stop " + stopSleepTS + " duration " + durationinMillis);
        String dataDate = sdf.format(new Date(startSleepTS * 1000));

        SleepData sleepData = new SleepData();
        sleepData.setStartSleep(startSleepTS);
        sleepData.setStopSleep(stopSleepTS);
        sleepData.setSleeptype(sleepType);
        sleepData.setDuration(durationinMillis * 60);
        sleepData.setSleepDate(dataDate);

       switch (sleepType) {

            case Constants.SLEEP_TYPE_LIGHT:
                addSleepData(dataDate, sleepData, ConstantsImp.PREF_SLEEP_LIGHT_DURATION);
                break;

            case Constants.SLEEP_TYPE_DEEP:
                addSleepData(dataDate, sleepData, ConstantsImp.PREF_SLEEP_DEEP_DURATION);

                break;

            case Constants.SLEEP_TYPE_WAKEUP:
                addSleepData(dataDate, sleepData, ConstantsImp.PREF_WAKEUP_TIME);
                break;

        }
        String timeStr = getDatadate(data);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        java.util.Date parse = null;
        try {
            parse = dateFormat.parse(dataDate); // 睡眠起始时间
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long dateTime = parse.getTime();
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

        String groupIndex = dateFormat1.format(new java.util.Date(stopSleepTS*1000));
        updateExistingSleep(groupIndex);
        String userIdStr = "";
        String macId = "";
        try {
            userIdStr = SafePreferences.getInstance().getStringFromSecurePref(ConstantsImp.PREF_USERIDHELO, "");
        }catch (Exception e){
            e.printStackTrace();
        }
        int USER_ID;
        if (!userIdStr.equals("")) {
            USER_ID = Integer.parseInt(userIdStr);
        } else {
            DebugLogger.i(TAG, "userIdStr =ä¸ºç©º ç¦»çº¿æ•°æ®æ— æ•ˆ ");
            return;
        }
        String mac = "";
        try {
            mac = SafePreferences.getInstance().getStringFromSecurePref(ConstantsImp.PREF_MAC, "");
        }catch (Exception e){
            e.printStackTrace();
        }
//        NewSleep newSleep = new NewSleep(Sleepcmd, dateTime, userIdStr, groupIndex, startSleepTS + "", stopSleepTS + "", sleepType, macId ,1);// userId,  groupIndex,  beginSleepTimestamp,  endSleepTimestamp,  sleepType,  mac
//        NewSleep newSleep = new NewSleep(USER_ID + "", "", startSleepTS + "", stopSleepTS+ "", sleepType, mac);// userId,  groupIndex,  beginSleepTimestamp,  endSleepTimestamp,  sleepType,  mac
//        updateDeviceDataJsonSleep(newSleep, AppUtil.getScheduledExtra(context));

    }


    private static void updateExistingSleep(String groupIndex){
        JSONObject jsonObj;
        try {
            String outLineJsonString = SafePreferences.getInstance().getStringFromSecurePref("offline_data_json_string", "");
            JSONArray array2 = new JSONArray(outLineJsonString);
            if (array2 != null) {
                for (int i = 0; i < array2.length(); i++) {
                    jsonObj = array2.getJSONObject(i);
                    if (jsonObj.has("cmd")) {
                        String cmd = jsonObj.getString("cmd");
                        if (Sleepcmd.equals(cmd)) {
                            JSONArray jsonArray1 = jsonObj.getJSONArray("sleepDetailNewVos");
                            for (int j = 0; j < jsonArray1.length(); j++) {
                                jsonObj = jsonArray1.getJSONObject(j);
                                if(jsonObj.has("groupIndex")){
                                    jsonObj.remove("groupIndex");
                                    jsonObj.put("groupIndex",groupIndex);
                                }
                            }
                        }
                    }
                }
                if(array2.length() > 0){
                    SafePreferences.getInstance().saveStringToSecurePref("offline_data_json_string", array2 + "");
                }
            }
        } catch (Exception e) {

        }

    }


    private static synchronized void updateDeviceDataJsonSleep(NewSleep newSleep, String extra) {
        try {


        JSONArray jsonArray = new JSONArray();
        String outLineJsonString = SafePreferences.getInstance().getStringFromSecurePref("offline_data_json_string", "");
        if (!TextUtils.isEmpty(outLineJsonString)) {
            try {
                jsonArray = new JSONArray(outLineJsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONObject jsonObj;
        JSONObject foundJson = null;
        for (int i = 0; i < jsonArray.length(); i++) {
            jsonObj = jsonArray.getJSONObject(i);
            if(jsonObj.has("cmd")){
                String cmd = jsonObj.getString("cmd");
                if(Sleepcmd.equals(cmd)){
                    foundJson = jsonObj;
                    break;
                }
            }
        }
        JSONArray jsonArray1;
        if(foundJson == null){
            foundJson = new JSONObject();
            jsonArray1 = new JSONArray();
            foundJson.put("cmd",Sleepcmd);
            foundJson.put("time",newSleep.getTime());
            foundJson.put("sleepDetailNewVos",jsonArray1);
            jsonArray.put(foundJson);
        }else{
            jsonArray1 = foundJson.getJSONArray("sleepDetailNewVos");
        }
        jsonArray1.put(newSleep);

        String jsonData = jsonArray.toString();
        SafePreferences.getInstance().saveStringToSecurePref("offline_data_json_string",
                jsonData);
//        MyApplication.RE_UPLOAD_DURATION = MyApplication.RE_UPLOAD_SUCCESS_TIME_TWO_MIN;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void writetosleepFile(String data) {
        File file = new File(Environment.getExternalStorageDirectory()+File.separator+"connector");
        if(!file.exists()) {
            file.mkdirs();
        }
        try {
            File gpxfile = new File(file, "sleep.txt");
            if(!file.exists()) {
                gpxfile.createNewFile();

            }
            FileOutputStream fos = new FileOutputStream(gpxfile, true);
            PrintStream printstream = new PrintStream(fos);
            printstream.print(data+"\n");
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addSleepData(String dataDate, SleepData sleepData, String sleepType) {
        HashMap<String, Long> sleephm = null;
        long sleepDuration = 0;
        if(sleepClassifier.containsKey(dataDate)) {
            sleephm = sleepClassifier.get(dataDate);
            if(sleephm.containsKey(sleepType)) {
                sleepDuration = sleephm.get(sleepType);
                switch (sleepType) {
                    case ConstantsImp.PREF_WAKEUP_TIME:
                        sleepDuration += 1;
                        break;
                    default:
                        sleepDuration += sleepData.getDuration();
                        break;
                }

            } else {
                switch (sleepType) {
                    case ConstantsImp.PREF_WAKEUP_TIME:
                        sleepDuration = 1;
                        break;
                    default:
                        sleepDuration = sleepData.getDuration();
                        break;
                }

            }
        } else {
            sleephm = new HashMap<>();

        }

        //for start and stop deep sleep
        if(sleepType.equals(ConstantsImp.PREF_SLEEP_DEEP_DURATION)) {
            if (sleephm.containsKey(ConstantsImp.PREF_START_DEEP_SLEEP_HR)) {
                long lastStartDeepSleepHr = sleephm.get(ConstantsImp.PREF_START_DEEP_SLEEP_HR);
                if (sleepData.getStartSleep() < lastStartDeepSleepHr) {
                    sleephm.put(ConstantsImp.PREF_START_DEEP_SLEEP_HR, lastStartDeepSleepHr);
                }
            } else {
                sleephm.put(ConstantsImp.PREF_START_DEEP_SLEEP_HR, sleepData.getStartSleep());
            }

            if (sleephm.containsKey(ConstantsImp.PREF_STOP_DEEP_SLEEP_HR)) {
                long lastStopDeepSleepHr = sleephm.get(ConstantsImp.PREF_STOP_DEEP_SLEEP_HR);
                if (sleepData.getStopSleep() > lastStopDeepSleepHr) {
                    sleephm.put(ConstantsImp.PREF_STOP_DEEP_SLEEP_HR, lastStopDeepSleepHr);
                }
            } else {
                sleephm.put(ConstantsImp.PREF_STOP_DEEP_SLEEP_HR, sleepData.getStopSleep());
            }
        }

        if (sleephm.containsKey(ConstantsImp.PREF_START_SLEEP_HR)) {
            long lastStartDeepSleepHr = sleephm.get(ConstantsImp.PREF_START_SLEEP_HR);
            if (sleepData.getStartSleep() < lastStartDeepSleepHr) {
                sleephm.put(ConstantsImp.PREF_START_SLEEP_HR, lastStartDeepSleepHr);
            }
        } else {
            sleephm.put(ConstantsImp.PREF_START_SLEEP_HR, sleepData.getStartSleep());
        }

        if (sleephm.containsKey(ConstantsImp.PREF_STOP_SLEEP_HR)) {
            long lastStopDeepSleepHr = sleephm.get(ConstantsImp.PREF_STOP_SLEEP_HR);
            if (sleepData.getStopSleep() > lastStopDeepSleepHr) {
                sleephm.put(ConstantsImp.PREF_STOP_SLEEP_HR, lastStopDeepSleepHr);
            }
        } else {
            sleephm.put(ConstantsImp.PREF_STOP_SLEEP_HR, sleepData.getStopSleep());
        }

        if(sleepType.equals(ConstantsImp.PREF_WAKEUP_TIME)) {
            if (sleephm.containsKey(ConstantsImp.PREF_WAKEUP_TIME)) {
                long lastWakeuptime = sleephm.get(ConstantsImp.PREF_WAKEUP_TIME);
                if (sleepData.getStartSleep() > lastWakeuptime) {
                    sleephm.put(ConstantsImp.PREF_WAKEUP_TIME, lastWakeuptime);
                }
            } else {
                sleephm.put(ConstantsImp.PREF_WAKEUP_TIME, sleepData.getStartSleep());
            }

            if (sleephm.containsKey(ConstantsImp.PREF_WAKEUP_COUNT)) {
                long wakeupcount = sleephm.get(ConstantsImp.PREF_WAKEUP_COUNT);
                sleephm.put(ConstantsImp.PREF_WAKEUP_COUNT, wakeupcount + 1);
            } else {
                sleephm.put(ConstantsImp.PREF_WAKEUP_COUNT, 1L);
            }
        }


        sleephm.put(sleepType, sleepDuration);
        sleepClassifier.put(dataDate, sleephm);
    }



   /* public void sendBpData(String data, Context context) {
        Intent intent = new Intent();
        String[] results = parseBpData(data);
        intent.setAction(Constants.BROADCAST_ACTION_BP_MEASUREMENT);
        intent.putExtra(Constants.INTENT_KEY_BP_MEASUREMENT_MAX, results[0]);
        intent.putExtra(Constants.INTENT_KEY_BP_MEASUREMENT_MIN, results[1]);
        context.sendBroadcast(intent);

        sendMeasurement(context, Constants.TYPE_BLOOD_PRESSURE, results[0], results[1]);
    }

    public void sendHrData(String data, Context context) {
        Intent intent = new Intent();
        String result = parseSingeData(data);

        intent.setAction(Constants.BROADCAST_ACTION_HR_MEASUREMENT);
        intent.putExtra(Constants.INTENT_KEY_HR_MEASUREMENT, result);
        context.sendBroadcast(intent);

        sendMeasurement(context, Constants.TYPE_HEART_RATE, result);
    }*/

    /*public void sendSleepData(Context context) {
        String deepSleepArr = "[]", lighSleepArr = "[]", wakeupArr = "[]";
        Long deepsleepDurtion = -1L, lightsleepDurtion = -1L;
        try {
            deepSleepArr = SafePreferences.getInstance().getStringFromSecurePref(ConstantsImp.PREF_SLEEP_DEEP, "[]");
            wakeupArr = SafePreferences.getInstance().getStringFromSecurePref(ConstantsImp.PREF_WAKEUP_COUNT, "[]");
            lighSleepArr = SafePreferences.getInstance().getStringFromSecurePref(ConstantsImp.PREF_SLEEP_LIGHT, "[]");
            deepsleepDurtion = SafePreferences.getInstance().getLongFromSecurePref(ConstantsImp.PREF_SLEEP_DEEP_DURATION, -1L);
            lightsleepDurtion = SafePreferences.getInstance().getLongFromSecurePref(ConstantsImp.PREF_SLEEP_LIGHT_DURATION, -1L);
        }catch (Exception e){

        }
        Long totalSleepDuration = lightsleepDurtion + deepsleepDurtion;
        DebugLogger.i(TAG, deepsleepDurtion + "  " + lightsleepDurtion);
        long hours = deepsleepDurtion / 3600;
        long minutes = (deepsleepDurtion % 3600) / 60;
        long seconds = deepsleepDurtion % 60;
        String deepSleepHours = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        hours = lightsleepDurtion / 3600;
        minutes = (lightsleepDurtion % 3600) / 60;
        seconds = lightsleepDurtion % 60;
        String lightSleepHours = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        hours = totalSleepDuration / 3600;
        minutes = (totalSleepDuration % 3600) / 60;
        seconds = totalSleepDuration % 60;
        String totalSleepHours = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);

        DebugLogger.i("sleep ", deepSleepHours);
        DebugLogger.i("sleep ", lightSleepHours);
        TypeToken listType = new TypeToken<List<SleepData>>() {
        };
        List<SleepData> deepSleepDatas;
        List<SleepData> lightSleepDatas;
        List<SleepData> wakeupSleepDatas;
        List<SleepData> allSleepDats = new ArrayList<>();

        Gson gson = new Gson();
        deepSleepDatas = gson.fromJson(deepSleepArr, listType.getType());
        lightSleepDatas = gson.fromJson(lighSleepArr, listType.getType());
        wakeupSleepDatas = gson.fromJson(wakeupArr, listType.getType());
        allSleepDats.addAll(deepSleepDatas);
        allSleepDats.addAll(lightSleepDatas);
        allSleepDats.addAll(wakeupSleepDatas);


        Collections.sort(deepSleepDatas, SleepComparator.startSleepComparator);
        long startDeepSleep = deepSleepDatas.get(0).getStartSleep();
        Collections.sort(deepSleepDatas, SleepComparator.stopSleepComparator);
        long stopDeepSleep = deepSleepDatas.get(0).getStopSleep();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startDeepSleep * 1000);
        String startDeepSleepStr = sdf.format(calendar.getTime());
        calendar.setTimeInMillis(stopDeepSleep * 1000);
        String stopDeepSleepStr = sdf.format(calendar.getTime());
        DebugLogger.i("sleep  ", "start " + startDeepSleepStr + " stop " + stopDeepSleepStr);

        Collections.sort(allSleepDats, SleepComparator.startSleepComparator);
        long startSleep = allSleepDats.get(0).getStartSleep();
        Collections.sort(allSleepDats, SleepComparator.stopSleepComparator);
        long stopSleep = allSleepDats.get(0).getStopSleep();
        calendar.setTimeInMillis(startSleep * 1000);
        String startSleepStr = sdf.format(calendar.getTime());
        calendar.setTimeInMillis(stopSleep * 1000);
        String stopSleepStr = sdf.format(calendar.getTime());

        Collections.sort(wakeupSleepDatas, SleepComparator.startSleepComparator);
        long wakeupTime = wakeupSleepDatas.get(wakeupSleepDatas.size() - 1).getStartSleep();
        calendar.setTimeInMillis(wakeupTime * 1000);
        String wakeupSleepStr = sdf.format(calendar.getTime());
        String timeStamp = Long.toString(System.currentTimeMillis());

        sendMeasurement(context, Constants.TYPE_SLEEP, startSleepStr, stopSleepStr, startDeepSleepStr, stopDeepSleepStr, totalSleepHours, deepSleepHours, lightSleepHours, Integer.toString(wakeupSleepDatas.size()), timeStamp);
        sendMeasurement(context, Constants.TYPE_DEEP_SLEEP, startDeepSleepStr, stopDeepSleepStr, timeStamp);
        sendMeasurement(context, Constants.TYPE_WAKEUP, wakeupSleepStr, timeStamp);

        Intent intent = new Intent();
        intent.setAction(ConstantsImp.BROADCAST_ACTION_SLEEP);
        intent.putExtra(ConstantsImp.INTENT_KEY_DEEP_SLEEP, deepSleepHours);
        intent.putExtra(ConstantsImp.INTENT_KEY_LIGHT_SLEEP, lightSleepHours);
        intent.putExtra(ConstantsImp.INTENT_KEY_SLEEP, totalSleepHours);
        intent.putExtra(ConstantsImp.INTENT_KEY_START_TIME, startSleepStr);
        intent.putExtra(ConstantsImp.INTENT_KEY_STOP_TIME, stopSleepStr);
        intent.putExtra(ConstantsImp.INTENT_KEY_WAKEUP_TIME, wakeupSleepDatas.size()+"");

        //com.wgn.SLEEP_ALL_DATA this receiver Only for testing not for release
        intent.putExtra("com.wgn.SLEEP_ALL_DATA", deepSleepHours +" "+ lightSleepHours+" "+totalSleepHours+" "+startSleepStr+" "+stopSleepStr+" "+wakeupSleepDatas.size());

        context.sendBroadcast(intent);
    }*/


    public void sendSleepData(Context context) {
        Set entrySet = sleepClassifier.entrySet();
        Iterator it = entrySet.iterator();
        Long deepsleepDurtion = 0L, lightsleepDurtion = 0L, wakeupcount = 0L;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        while (it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();
            String date = (String) me.getKey();
            HashMap<String, Long> sleepData = (HashMap<String, Long>) me.getValue();
            if(sleepData.containsKey(ConstantsImp.PREF_SLEEP_DEEP_DURATION)) {
                deepsleepDurtion = sleepData.get(ConstantsImp.PREF_SLEEP_DEEP_DURATION);
            }
            if(sleepData.containsKey(ConstantsImp.PREF_SLEEP_LIGHT_DURATION)) {
                lightsleepDurtion = sleepData.get(ConstantsImp.PREF_SLEEP_LIGHT_DURATION);
            }
            Long totalSleepDuration = deepsleepDurtion + lightsleepDurtion;
            String timeStamp = Long.toString(System.currentTimeMillis());
            String totalSleepHours = getSleepHourString(totalSleepDuration);
            String deepSleepHours = getSleepHourString(deepsleepDurtion);
            String lightSleepHours = getSleepHourString(lightsleepDurtion);
            String startSleepStr = sdf.format(new Date(sleepData.get(ConstantsImp.PREF_START_SLEEP_HR) * 1000));
            String stopSleepStr = sdf.format(new Date(sleepData.get(ConstantsImp.PREF_STOP_SLEEP_HR) * 1000));
            String startDeepSleepStr = sdf.format(new Date(sleepData.get(ConstantsImp.PREF_START_DEEP_SLEEP_HR) * 1000));
            String stopDeepSleepStr = sdf.format(new Date(sleepData.get(ConstantsImp.PREF_STOP_DEEP_SLEEP_HR) * 1000));
            if(sleepData.containsKey(ConstantsImp.PREF_WAKEUP_COUNT)) {
                wakeupcount = sleepData.get(ConstantsImp.PREF_WAKEUP_COUNT);
            }
            String wakeupSleepStr =  sdf.format(new Date(sleepData.get(ConstantsImp.PREF_WAKEUP_TIME)));

            // sendMeasurement(context, Constants.TYPE_SLEEP, startSleepStr, stopSleepStr, startDeepSleepStr, stopDeepSleepStr, totalSleepHours, deepSleepHours, lightSleepHours, Long.toString(wakeupcount), timeStamp);
            // sendMeasurement(context, Constants.TYPE_DEEP_SLEEP, startDeepSleepStr, stopDeepSleepStr, timeStamp);
            // sendMeasurement(context, Constants.TYPE_WAKEUP, wakeupSleepStr, timeStamp);

            Intent intent = new Intent();
            intent.setAction(ConstantsImp.BROADCAST_ACTION_SLEEP);
            intent.putExtra(ConstantsImp.INTENT_KEY_DEEP_SLEEP, deepSleepHours);
            intent.putExtra(ConstantsImp.INTENT_KEY_LIGHT_SLEEP, lightSleepHours);
            intent.putExtra(ConstantsImp.INTENT_KEY_SLEEP, totalSleepHours);
            intent.putExtra(ConstantsImp.INTENT_KEY_START_TIME, startSleepStr);
            intent.putExtra(ConstantsImp.INTENT_KEY_STOP_TIME, stopSleepStr);
            intent.putExtra(ConstantsImp.INTENT_KEY_WAKEUP_TIME, Long.toString(wakeupcount));

        }

        sleepClassifier.clear();
    }

    private String getSleepHourString(Long sleepduration) {
        long hours = sleepduration / 3600;
        long minutes = (sleepduration % 3600) / 60;
        long seconds = sleepduration % 60;
        String sleepHours = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        return sleepHours;
    }


    public void sleepParser(String data, Context context) {
        DebugLogger.i("sleepparser --- ", data);
        String datatype = data.substring(15, 17);// 1-3
        String dataType = "", dataSource = "", dataDate = "";// æ•°æ®å­˜å‚¨
        dataDate = getDatadate(data);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // HH:mm:ss
        java.util.Date parse;
        long time = 0;
        try {
            parse = dateFormat.parse(dataDate);
            time = parse.getTime();
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (datatype.equals("01")) {
            dataType = SleepLight;
            String dataTime = data.substring(18, 29);
            String dataSleep = data.substring(30, 41);
            long datacountTime = Long.parseLong(dataTime.substring(9) + dataTime.substring(6, 8) + dataTime.substring(3, 5) + dataTime.substring(0, 2), 16);
            long datacountSleep = Long.parseLong(dataSleep.substring(9) + dataSleep.substring(6, 8) + dataSleep.substring(3, 5) + dataSleep.substring(0, 2), 16);
            dataSource = datacountSleep + "";
            DebugLogger.d("sleepparser", "datacountTime=" + datacountTime);
            DebugLogger.d("sleepparser", "SleepLight=" + dataSource);
            // logä¿å­˜
            // SaveLog.WriteData2SD("AllOffData.txt",
            // dataType+"/"+dataDate+"/"+dataSource);
//            LoggingManager.getMeasurementsInstance().log("æµ…ç¡ --ã€‹"+time+"-----"+datacountSleep);
//            NewDBHelper.getInstance(context).insert(new NewEntity(USER_ID, NewDBHelper.CMD_SLEEP_LIGHT, time, datacountSleep, 0));
        } else if (datatype.equals("02")) { // æ·±ç¡

            dataType = SleepDeep;
            String dataSleep = data.substring(30, 41);// æ•°æ®
            long datacountSleep = Long.parseLong(dataSleep.substring(9) + dataSleep.substring(6, 8) + dataSleep.substring(3, 5) + dataSleep.substring(0, 2), 16);
            dataSource = datacountSleep + "";
            DebugLogger.d("sleepparser", "Sleepdeep=" + dataSource);

//            NewDBHelper.getInstance(context).insert(new NewEntity(USER_ID, NewDBHelper.CMD_SLEEP_DEEP, time, datacountSleep, 0));

        } else if (datatype.equals("03")) { // ç¿»èº«
            dataType = TurnOver;
            String dataTime = data.substring(18, 29);// æ—¶é—´æˆ³
            long datacountTime = Long.parseLong(dataTime.substring(9) + dataTime.substring(6, 8) + dataTime.substring(3, 5) + dataTime.substring(0, 2), 16);
            dataSource = dataDate;// å…¼å®¹
//            NewDBHelper.getInstance(context).insert(new NewEntity(USER_ID, NewDBHelper.CMD_SLEEP_WAKE, time, time, 0));
        }
        loadDeviceData(dataType, dataDate, dataSource, context);//type time data
        // å–æ¶ˆåŽŸæœ‰è®¡æ—¶ä»»åŠ¡


        Intent intent = new Intent();
        intent.setAction(ConstantsImp.BROADCAST_ACTION_SLEEP);
        String sleep = loadSleepData(data);
        intent.putExtra(ConstantsImp.INTENT_KEY_SLEEP, sleep);
        DebugLogger.i("sleepparser", "SLEEP  " + sleep);
        context.sendBroadcast(intent);

    }


    public void loadScheduleData(String data, Context context) {
        Intent intent = new Intent();
        String datatype = data.substring(15, 17);
        DebugLogger.i(TAG, "loadScheduleData datatype = " + datatype + "///");
        String dataStr = "";
        if (datatype.equals("01")) {
            //STEPS
            if(GlobalData.stepsMeasure) {
                dataStr = data.substring(30, 41);
                long stepsCount = Long.parseLong(dataStr.substring(9) + dataStr.substring(6, 8) + dataStr.substring(3, 5) + dataStr.substring(0, 2), 16);
                intent.setAction(ConstantsImp.BROADCAST_ACTION_STEPS_MEASUREMENT);
                intent.putExtra(ConstantsImp.INTENT_KEY_STEPS_MEASUREMENT, Long.toString(stepsCount));


//            String steps = parseSingeData(data);
//            int stepsCount = Integer.parseInt(steps);
                int calorie = (int) (4.5 * stepsCount * 1.75 / 4 / 2 * 75 / 1800);
                float distance = (float) (stepsCount * 0.762);
                double miles = distance / 1000 * 0.621;
                DebugLogger.i(TAG + " steps", stepsCount + " " + distance + " " + miles + " " + calorie);

                // sendMeasurement(context, Constants.TYPE_STEP, Long.toString(stepsCount), Double.toString(miles), "5", Integer.toString(calorie));
                GlobalData.stepsMeasure = false;
            }
        } else if (datatype.equals("02")) {
            //HR
            dataStr = data.substring(30, 41);
            long datacount = Long.parseLong(dataStr.substring(9) + dataStr.substring(6, 8) + dataStr.substring(3, 5) + dataStr.substring(0, 2), 16);
            intent.setAction(ConstantsImp.BROADCAST_ACTION_HR_MEASUREMENT);
            intent.putExtra(ConstantsImp.INTENT_KEY_HR_MEASUREMENT, Long.toString(datacount));
            // sendMeasurement(context, Constants.TYPE_HEART_RATE, Long.toString(datacount));
        } else if (datatype.equals("03")) {
            //Calories //disabled as of now
            dataStr = data.substring(30, 41);

        } else if (datatype.equals("04")) {
            //sleep
            sleepParser(data, context);

            dataStr = data.substring(30, 41);
            intent.setAction(ConstantsImp.BROADCAST_ACTION_SLEEP);
            intent.putExtra(ConstantsImp.INTENT_KEY_SLEEP, dataStr);
        } else if (datatype.equals("05")) {
            //mood
            String result = data.substring(30, 32);
            dataStr = parseMoodIntData(result);
            intent.setAction(ConstantsImp.BROADCAST_ACTION_MOOD_MEASUREMENT);
            intent.putExtra(ConstantsImp.INTENT_KEY_MOOD_MEASUREMENT, dataStr);
            // sendMeasurement(context, Constants.TYPE_MOOD, result, dataStr);
        } else if (datatype.equals("06")) {
            //fatigue
            String result = data.substring(30, 32);
            dataStr = getFatigueValue(result);
            intent.setAction(ConstantsImp.BROADCAST_ACTION_FATIGUE_MEASUREMENT);
            intent.putExtra(ConstantsImp.INTENT_KEY_FATIGUE_MEASUREMENT, dataStr);
            // sendMeasurement(context, Constants.TYPE_FATIGUE, result, dataStr);
        } else if (datatype.equals("07")) {
            //BR
            dataStr = data.substring(30, 41);
            long datacount = Long.parseLong(dataStr.substring(0, 2), 16);
            intent.setAction(ConstantsImp.BROADCAST_ACTION_BR_MEASUREMENT);
            intent.putExtra(ConstantsImp.INTENT_KEY_BR_MEASUREMENT, Long.toString(datacount));
            // sendMeasurement(context, Constants.TYPE_BREATH_RATE, Long.toString(datacount));
        } else if (datatype.equals("08")) {
            //BP
            dataStr = data.substring(30, 41);
            long max = Long.parseLong(dataStr.substring(0, 2), 16);
            long min = Long.parseLong(dataStr.substring(3, 5), 16);

            intent.setAction(ConstantsImp.BROADCAST_ACTION_BP_MEASUREMENT);
            intent.putExtra(ConstantsImp.INTENT_KEY_BP_MEASUREMENT_MAX, Long.toString(max));
            intent.putExtra(ConstantsImp.INTENT_KEY_BP_MEASUREMENT_MIN, Long.toString(min));
            // sendMeasurement(context, Constants.TYPE_BLOOD_PRESSURE, Long.toString(max), Long.toString(min));
        } else if (datatype.equals("09")) {
            //ECG
            dataStr = data.substring(30, 32);
            intent.setAction(ConstantsImp.BROADCAST_ACTION_ECG_MEASUREMENT);
            intent.putExtra(ConstantsImp.INTENT_KEY_ECG_MEASUREMENT, dataStr);
            // sendMeasurement(context, Constants.TYPE_ECG, dataStr, "[]");
        } else if(datatype.equals("0B")) {
            parsePPGQuality(context, data);


        }
        context.sendBroadcast(intent);
    }


        public void sendPwDataAll(final Context context, final String pw) {
            // F9 01 00 00 FF 01 00 00 01 02 00 00 01 02 00 00 01 02 00 00
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    DebugLogger.d(TAG, "pw  " + pw);
                    if (pw.length() != 0) {
                        ArrayList<Integer> pwdataAllList = new ArrayList<>();
                        pwdataAllList.add(parsePwdata(pw.substring(0, 11)));
                        pwdataAllList.add(parsePwdata(pw.substring(12, 23)));
                        pwdataAllList.add(parsePwdata(pw.substring(24, 35)));
                        pwdataAllList.add(parsePwdata(pw.substring(36, 47)));
                        pwdataAllList.add(parsePwdata(pw.substring(48)));
                        Intent intent = new Intent();

                        intent.setAction(ConstantsImp.BROADCAST_ACTION_PWD);
                        intent.putExtra(ConstantsImp.INTENT_KEY_PWD, pwdataAllList);
                        context.sendBroadcast(intent);

                        long currTime = System.currentTimeMillis();
                        if(lastGreenPPgtime == 0 || (currTime - lastGreenPPgtime) > 1000) {
//                            if(TextUtils.isEmpty(greenPpgFileName)) {
                                greenPpgFileName = "GreenPPG"+getPPGFileName()+".txt";
//                            }
                        } else {
                            if(TextUtils.isEmpty(greenPpgFileName)) {
                                greenPpgFileName = "GreenPPG"+getPPGFileName()+".txt";
                            }
                        }
                        lastGreenPPgtime = currTime;

                        if(!TextUtils.isEmpty(greenPpgFileName)) {
                            File logfile = new File(Environment.getExternalStorageDirectory()+"/"+"connector"+"/");
                            if(!logfile.exists()) {
                                logfile.mkdirs();
                            }
                            logfile = new File(logfile, greenPpgFileName); //GlobalData.greenPpgFile
                            if(!logfile.exists()) {
                                try {
                                    logfile.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            try {
                                FileOutputStream fileOutputStream = new FileOutputStream(logfile, true);
                                OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
                                writer.append(pwdataAllList.get(0)+" "+pwdataAllList.get(1)+" "+pwdataAllList.get(2)+" "+pwdataAllList.get(3)+" "+pwdataAllList.get(4)+"\n");
                                writer.close();
                                fileOutputStream.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }


                        pwdataAllList.clear();
                    }
                }
            });


        }




    private String parseSingeData(String data) {
        String dataStr;
        // 12 34 0B 32 08 61 FF 46 57 4F 00 00 00 91 02 00 00 43 21
        // 12 34 0B 32 08 AE 1E 4C 57 00 00 00 00 B4 01 00 00 43 21
        dataStr = data.substring(27, 38);
        String datastring = dataStr.substring(9, 11) + dataStr.substring(6, 8)
                + dataStr.substring(3, 5) + dataStr.substring(0, 2);
        long dataf = Integer.parseInt(datastring, 16);
        return dataf + "";
    }

    private String parseMoodIntData(String dataStr) {

        switch (dataStr) {
            case "00":
                return "Depression";

            case "01":
                return "Calm";

            case "02":
                return "Excitement";

            case "03":
                return "Invalid";


        }
        // 12 34 0B 3B 05 19 39 E4 56 01 D8 01 00 00 43 21
        // String datastring = dataStr.substring(9, 11) + dataStr.substring(6,
        // 8) + dataStr.substring(3, 5) + dataStr.substring(0, 2);
        return dataStr;
    }


    private String getMoodValue(String data) {
        switch (data) {
            case "00":
                return Constants.MOOD_DEPRESSION;

            case "01":
                return Constants.MOOD_CALM;

            case "02":
                return Constants.MOOD_EXCITEMENT;

            default:
                return "";

        }
    }

    private String getFatigueValue(String data) {
        switch (data) {
            case "00":
                return Constants.FATIGUE_NORMAL;

            case "01":
                return Constants.FATIGUE_TIRED;

            case "02":
                return Constants.FATIGUE_VERY_TIRED;

            case "03":
                return Constants.FATIGUE_INVALID;

            default:
                return "";

        }
    }


    private String parseBRData(String data) {
        String dataStr;
        dataStr = data.substring(27, 29);
        DebugLogger.v(TAG, "BR DATA = " + dataStr);
        // String datastring = dataStr.substring(9, 11) + dataStr.substring(6,
        // 8) + dataStr.substring(3, 5) + dataStr.substring(0, 2);
        // 12-34-b-3d-5-73-c0-e4-56-10-ca-2-0-0-43-21
        long dataf = Integer.parseInt(dataStr, 16);
        return dataf + "";
    }

    private long parseSleepDatatoLong(String data) {
        @SuppressWarnings("unused")
        String dateStr; // æ—¥æœŸ
        String dataStr; // æ—¶é—´
        dataStr = data.substring(27, 38);
        dateStr = data.substring(15, 26);
        String datastring = dataStr.substring(9, 11) + dataStr.substring(6, 8)
                + dataStr.substring(3, 5) + dataStr.substring(0, 2);
        long dataf = Integer.parseInt(datastring, 16);
        return dataf;
    }

    private String[] parseBpData(String data) {
        DebugLogger.v(TAG, "bp data = " + data);
        String dataStr;
        dataStr = data.substring(27, 32);
        String[] values = new String[2];
        DebugLogger.v(TAG, "bp dataStr = " + dataStr);
        if (dataStr != null) {
            int max, min;
            max = Integer.valueOf(dataStr.substring(0, 2), 16);
            min = Integer.valueOf(dataStr.substring(3), 16);
            values[0] = Long.toString(max);
            values[1] = Long.toString(min);

            // bpminData = bp.substring(3);
            // bpmaxData = bp.substring(0, 2);
            return values;

        }
        // String datastring = Integer.parseInt(dataStr.substring(0,2), 16)+
        // Integer.parseInt(dataStr.substring(2), 16)+"";
        // Log.i(" datastring", datastring);
        // long dataf = Integer.parseInt(datastring);
        return values;
    }

    private String[] parseBpDataForSer(String data) {
        DebugLogger.v(TAG, "bp data = " + data);
        String dataStr;
        dataStr = data.substring(27, 32);
        String[] bpDatas = new String[2];
        DebugLogger.v(TAG, "bp dataStr = " + dataStr);
        if (dataStr != null) {
            int max, min;
            max = Integer.valueOf(dataStr.substring(0, 2), 16);
            min = Integer.valueOf(dataStr.substring(3), 16);
            bpDatas[0] = Integer.toString(max);
            bpDatas[1] = Integer.toString(min);


        }
        // String datastring = Integer.parseInt(dataStr.substring(0,2), 16)+
        // Integer.parseInt(dataStr.substring(2), 16)+"";
        // Log.i(" datastring", datastring);
        // long dataf = Integer.parseInt(datastring);
        return bpDatas;
    }

    private String parseEcgData(String data) {
        DebugLogger.v(TAG, "Ecg data = " + data);
        String dataStr;
        dataStr = data.substring(27, 29);
        DebugLogger.v(TAG, "Ecg dataStr = " + dataStr);
        return dataStr;
    }

    //Abhijeet cre
    private String parseNewOxygenData(String data) {
        DebugLogger.v(TAG, "Oxy data = " + data);
        String dataStr;
        dataStr = data.substring(27, 29);
        DebugLogger.v(TAG, "Ecg dataStr = " + dataStr);
        return dataStr;
    }

    private int pareseBatteryData(String data) {
        String substring = data.substring(15, 17);
        int parseInt = Integer.parseInt(substring, 16);
        return parseInt;
    }

    private String loadSleepData(String data) {
        String datatype = data.substring(15, 17);// 1-3
        return datatype;

    }

    protected float parseEcgAlldata(String string) {

        //String hex = (string.substring(9, 11) + string.substring(6, 8) + string.substring(3, 5) + string.substring(0, 2));
        String hex = (string.substring(9) + string.substring(6, 8) + string.substring(3, 5) + string.substring(0, 2));
        BigInteger bi = new BigInteger(hex, 16);
        float data = bi.intValue();
        return data;
    }

    private float parseEcgdata(String string) {
        DebugLogger.i(TAG+"ecgvdo", string);
        float data = 0;
        String hex = string.substring(9, 11) + string.substring(6, 8) + string.substring(3, 5) + string.substring(0, 2);
        // Log.d("sqs", "hex:  "+hex);
        byte[] hexStringToBytes = HexUtil.hexStringToBytes(hex);
        String bytesToHexString = HexUtil.bytesToHexString(hexStringToBytes);
        // Log.d("sqs", "ecg+bytesToString:  "+bytesToHexString);
        int bytesToInt = HexUtil.getInt(hexStringToBytes, false, 4);
        // Log.d("sqs", "ecg+bytesToInt"+bytesToInt);
        // data = Short.valueOf(hex, 16);
        return bytesToInt;

        // String hex = (string.substring(9, 11) + string.substring(6, 8) +
        // string.substring(3, 5) + string.substring(0, 2));
        // BigInteger bi = new BigInteger(hex, 16);
        // float data = bi.intValue();
        // // float data = Integer.parseInt((string.substring(9, 11) +
        // string.substring(6, 8) + string.substring(3, 5) + string.substring(0,
        // 2)), 16);
        // return data;

        // 2016.11.29
        // float data = Integer.parseInt((string.substring(9, 11) +
        // string.substring(6, 8) + string.substring(3, 5) + string.substring(0,
        // 2)), 16);
        // return data;
    }

    //Added By Abhijeet

    private String parseNewUVSingeData(String data) {
        try {
            String dataStr;
//        12 34 0B 35  05  32 F6 37 5A 00 FE 01 00 00  43 21
            String parsedString = "";
            // 12340b3608e8bf135a60000000bd0200004321
            //String data1="12340b350552c4135a00c80100004321";
            parsedString = data.substring(27, 29);
            DebugLogger.i(TAG, parsedString);
            int dataf = Integer.parseInt(parsedString, 16);
            return dataf + "";
        } catch (Exception e) {
            DebugLogger.e(TAG, "parseNewUVSingeData: " + e);
            return "";
        }
    }

    private String parseUVData(String data) {
        String dataStr;
        // 12 34 0B 32 08 AE 1E 4C 57 00 00 00 00 B4 01 00 00 43 21
        // 12 34 0b 35 05 29 af cf 59 05 4a 02 00 00 00 00 00 43 21 //UVSingeData
        dataStr = data.substring(27, 38);
        String datastring = dataStr.substring(9, 11) + dataStr.substring(6, 8)
                + dataStr.substring(3, 5) + dataStr.substring(0, 2);
        long dataf = Integer.parseInt(datastring, 16);
        return Long.toString(dataf);
    }

    private String parseOxygenSingeData(String data) {
        String dataStr;
        // 12 34 0B 32 08 61 FF 46 57 4F 00 00 00 91 02 00 00 43 21
        // 12 34 0B 32 08 AE 1E 4C 57 00 00 00 00 B4 01 00 00 43 21
        // 12 34 0b 36 08 f2 af cf 59 5c 00 00 00 6e 03 00 00 43 21//oxygen data
        dataStr = data.substring(27, 38);
        //dataStr = data.substring(30, 41);
        String datastring = dataStr.substring(9, 11) + dataStr.substring(6, 8)
                + dataStr.substring(3, 5) + dataStr.substring(0, 2);
        long dataf = Integer.parseInt(datastring, 16);
        return Long.toString(dataf);
    }

    private String getLastOxygenValue(Vector<String> oxygenList){
        if(oxygenList!=null) {
            removeZeros(oxygenList);
            int Oxygensize = oxygenList.size();
            if (Oxygensize != 0) {
                lastOxygenItem = oxygenList.get(Oxygensize - 1);
            }
        }else{
            lastOxygenItem= "0";
        }
        return lastOxygenItem;
    }
    private Vector<String> removeZeros(Vector<String> oxygenList){
        String removeElem = "0";
        Iterator<String> itr = oxygenList.iterator();
        while(itr.hasNext()){
            if(removeElem.equals(itr.next())){
                itr.remove();
            }
        }
        return oxygenList;
    }


    /*private String getDatadate(String data) {
        // 12 34 0B 3E 09 01 12 36 E4 56 00 00 00 00 D5 01 00 00 43 21
        String dataStr = data.substring(18, 29);// 12 36 E4 56
        DebugLogger.i(TAG, "-----" + dataStr);
        long datacount = Long.parseLong(dataStr.substring(9) + dataStr.substring(6, 8) + dataStr.substring(3, 5) + dataStr.substring(0, 2), 16);
        // Log.i(TAG, "loadDeviceData dataDate ä»Žè®¾å¤‡ç«¯æŽ¥æ”¶é“å¾·åŽŸå§‹æ—¶é—´æˆ³æ•°æ® = " + datacount);
//        DebugLogger.d("sqs", "è€ç¡çœ æ•°æ®æœ€åˆçš„æœªåšåç§»çš„...." + datacount);
        DebugLogger.v(TAG, "loadDeviceData dataDate Integer.parseInt(TimeUtils.offSetTimeZone()) / 1000) = " + Integer.parseInt(TimeUtils.offSetTimeZone()) / 1000);
        // return getFormatedDateTime("yyyy-MM-dd HH:mm:ss",datacount-28800);
        return getFormatedDateTime("yyyy-MM-dd HH:mm:ss", datacount - (Integer.parseInt(TimeUtils.offSetTimeZone()) / 1000));// æ—¶åŒºå…¼å®¹
    }*/

    private String getFormatedDateTime(String pattern, long dateTime) {
        DebugLogger.i("loadDeviceData dataDate", dateTime + "");
        SimpleDateFormat sDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        return sDateFormat.format(new java.sql.Date(dateTime * 1000));
    }

    private final ArrayList<NewSleep> outLineNewSleepEntities = new ArrayList<>();

    private void loadDeviceData(String dataType, String dataDate, String dataSource, Context context) {
        try {
//            LoggingManager.getMeasurementsInstance().log("loadDeviceData--->"+" dataType  "+dataType+" dataDate  "+dataDate+" dataSource   "+dataSource);

            DebugLogger.i(TAG, "datatype is " + dataType + " ,datasource is " + dataSource + " ,datadate is " + dataDate);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            java.util.Date parse = dateFormat.parse(dataDate); // ç¡çœ èµ·å§‹æ—¶é—´
            String dataDatestr = parse.getTime() + "";
            long timecurrentTimeMillis = System.currentTimeMillis();
            if (dataType.equals("SLEEP LIGHT") || dataType.equals("TURN OVER") || dataType.equals("SLEEP DEEP")) {
                String sleepType = "";
                int value = 0;
                if (dataType.equals("SLEEP LIGHT")) {
                    sleepType = "0";
                    value = Integer.parseInt(dataSource);
                } else if (dataType.equals("TURN OVER")) {
                    sleepType = "2";
                    dataSource = "0";
                } else if (dataType.equals("SLEEP DEEP")) {
                    sleepType = "1";
                    value = Integer.parseInt(dataSource);
                }
                long baginSleepTime = Long.parseLong(dataDatestr) / 1000;
                int sleepDurationStamp = value * 60;
//                int endSleepTimeStamp=sleepDurationStamp+baginSleepTime;
                String userIdStr = "";
                try {
                    userIdStr = SafePreferences.getInstance().getStringFromSecurePref(ConstantsImp.PREF_USERIDHELO, "");
                }catch (Exception e){
                    e.printStackTrace();
                }

                int USER_ID;
                if (!userIdStr.equals("")) {
                    USER_ID = Integer.parseInt(userIdStr);
                } else {
                    DebugLogger.i(TAG, "userIdStr =ä¸ºç©º ç¦»çº¿æ•°æ®æ— æ•ˆ ");
                    return;
                }

                String mac = "";
                try {
                    mac = SafePreferences.getInstance().getStringFromSecurePref(ConstantsImp.PREF_MAC, "");
                }catch (Exception e){
                    e.printStackTrace();
                }
                NewSleep newSleep = new NewSleep(USER_ID + "", "", baginSleepTime + "", sleepDurationStamp + baginSleepTime + "", sleepType, mac);// userId,  groupIndex,  beginSleepTimestamp,  endSleepTimestamp,  sleepType,  mac
                DebugLogger.i("sleep newsleep", new Gson().toJson(newSleep));
                outLineNewSleepEntities.add(newSleep);//ç¡çœ æ•°æ®æ·»åŠ åˆ° List
                updateDeviceNewSleepDataJson(outLineNewSleepEntities);

            }
        } catch (Exception e) {
            DebugLogger.d(TAG, "outline_json_error");
        }
    }


    private synchronized void updateDeviceNewSleepDataJson(ArrayList<NewSleep> outLineEntities) {
        long hourTwo = 7200;
        List<List<NewSleep>> dataSleep = new ArrayList<>();
        List<NewSleep> sectionalizationSleep = new ArrayList<NewSleep>(); // æ‰€æœ‰ç¡çœ æ•°æ®
        List<NewSleep> indeterminacySleep = new ArrayList<NewSleep>(); // ä¸ç¡®å®šåˆ†ç»„ç¡çœ æ•°æ®
        if (outLineEntities.size() <= 0) {
            return;
        }
        if (outLineEntities.size() > 0) {
            indeterminacySleep.add(outLineEntities.get(0));//åˆ¤æ–­æ˜¯å¦æœ‰æ®‹ç•™çš„ç¡çœ ï¼Œæ²¡æœ‰ç›´æŽ¥æ·»åŠ 
        }
   /*     if (indeterminacySleep.size() > 0) {//åˆ¤æ–­è¿™æ¡ç¡çœ æ˜¯å¦ä¸Žä¸Šæ¬¡ç¡çœ æœ‰å”¤é†’æˆ–å¤§äºŽä¸¤ä¸ªå°æ—¶
            NewSleep indeterminacyLastSleep = indeterminacySleep.get(indeterminacySleep.size() - 1);//ä¸Žä¸ç¡®å®šåˆ†ç»„çš„æœ€åŽä¸€æ¡ç¡çœ æ¯”è¾ƒ
            NewSleep newSleep = outLineEntities.get(0);
            String sleepType = outLineEntities.get(0).getSleepType();
            long beginTime = Long.parseLong(newSleep.getBeginSleepTimestamp());
            long endTime = Long.parseLong(indeterminacyLastSleep.getEndSleepTimestamp());
            long subTime = beginTime - endTime;
            if (!sleepType.equals("2") && subTime <= hourTwo) {
                indeterminacySleep.add(outLineEntities.get(0));
            } else if (sleepType.equals("2") || subTime > hourTwo) {//å¦‚æžœå¤§äºŽä¸¤ä¸ªå°æ—¶
                if (subTime > hourTwo) {
                    sectionalizationSleep.addAll(indeterminacySleep);
                    indeterminacySleep.clear();//æ¸…ç©ºä¸ç¡®å®šçš„ç¡çœ  boolean
                    indeterminacySleep.add(outLineEntities.get(0));//ç¬¬ä¸€ä¸ªç¡çœ 
                } else if (sleepType.equals("2")) {
                    indeterminacySleep.add(outLineEntities.get(0));//ç¬¬ä¸€ä¸ªç¡çœ 
                    sectionalizationSleep.addAll(indeterminacySleep);
                    indeterminacySleep.clear();//æ¸…ç©ºä¸ç¡®å®šçš„ç¡çœ  boolean
                }
            }
        }*/

        for (int i = 1; i < outLineEntities.size(); i++) {
            List<NewSleep> confirmSleep = new ArrayList<NewSleep>();
            NewSleep measureCell = outLineEntities.get(i);
            NewSleep measureCellLast = outLineEntities.get(i - 1);

            long startSleepTime = Long.parseLong(measureCell.getBeginSleepTimestamp());
            long endSleepTime = Long.parseLong(measureCellLast.getEndSleepTimestamp());

            long subTime = startSleepTime - endSleepTime;   //éœ€è¦åˆ¤æ–­ç‰¹æ®Šç‚¹ï¼Œç¬¬ä¸€ä¸ªè¿˜æ˜¯æœ€åŽä¸€ä¸ª
            String sleepType = measureCell.getSleepType();

            if (!sleepType.equals("2") && subTime <= hourTwo) {
                indeterminacySleep.add(outLineEntities.get(i));
            } else if (sleepType.equals("2") || subTime > hourTwo) {//å¦‚æžœå¤§äºŽä¸¤ä¸ªå°æ—¶
                if (subTime > hourTwo) {
                    sectionalizationSleep.addAll(indeterminacySleep);
                    indeterminacySleep.clear();//æ¸…ç©ºä¸ç¡®å®šçš„ç¡çœ  boolean
                    indeterminacySleep.add(outLineEntities.get(i));//ç¬¬ä¸€ä¸ªç¡çœ 
                } else if (sleepType.equals("2")) {
                    indeterminacySleep.add(outLineEntities.get(i));//ç¬¬ä¸€ä¸ªç¡çœ 
                    sectionalizationSleep.addAll(indeterminacySleep);
                    indeterminacySleep.clear();//æ¸…ç©ºä¸ç¡®å®šçš„ç¡çœ  boolean
                }
            }
            if (sectionalizationSleep.size() > 0) {
                confirmSleep.addAll(sectionalizationSleep);
                dataSleep.add(confirmSleep);
                sectionalizationSleep.clear();
            }
        }
        for (int i = 0; i < dataSleep.size(); i++) { //add graoupIndex
            ArrayList<NewSleep> spp = (ArrayList<NewSleep>) dataSleep.get(i);
            long time = Long.parseLong(spp.get(spp.size() - 1).getEndSleepTimestamp());// groupIndex
            DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time * 1000);
            NewSleep sleep;
            for (int j = 0; j < spp.size(); j++) {
                sleep = spp.get(j);
                sleep.setGroupIndex(formatter.format(calendar.getTime()));
            }
        }
        Gson gson = new Gson();
        JsonArray jsonArray = new JsonArray();
        for (int i = 0; i < dataSleep.size(); i++) {
            JsonElement element = gson.toJsonTree(dataSleep.get(i), new TypeToken<List<NewSleep>>() {
            }.getType());
            jsonArray.addAll(element.getAsJsonArray());


        }
//        for (int i = 0; i < dataSleep.size(); i++) {
//            List<NewSleep> sl = dataSleep.get(i);
//            for (int j = 0; j < sl.size(); j++) {
//                LoggingManager.getMeasurementsInstance().log("dataSleep----bagin-->" + sl.get(i).getBeginSleepTimestamp());
//            }
//        }
        SafePreferences.getInstance().saveStringToSecurePref("SECTIONALIZATION_SLEEP_JSON", jsonArray.toString());
        DebugLogger.i("SECTIONALIZATION_SLEEP_JSON", jsonArray.toString());
    }

    protected int parsePwdata(String string) {

        float data = 0;
        String hex = string.substring(9, 11) + string.substring(6, 8) + string.substring(3, 5) + string.substring(0, 2);
        byte[] hexStringToBytes = HexUtil.hexStringToBytes(hex);
        String bytesToHexString = HexUtil.bytesToHexString(hexStringToBytes);
        DebugLogger.d("sqs", "PPG+bytesToString:  "+bytesToHexString);
        int bytesToInt = HexUtil.getInt(hexStringToBytes,false, 4);
        DebugLogger.d("sqs", "PPG+bytesToInt"+bytesToInt);
//		data = Short.valueOf(hex, 16);
        return bytesToInt;
    }


    public void parseOxyIRNIR(final Context context, final String string) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                DebugLogger.i("irnir", string);

                String red1 = getFormattedString(string.substring(0, 11));
                String red2 = getFormattedString(string.substring(24, 35));
                String nir1 = getFormattedString(string.substring(12, 23));
                String nir2 = getFormattedString(string.substring(36, 47));
                /*int irVal1  = getParsedInt(red1);
                int irVal2  = getParsedInt(red2);
                int nirVal1 = getParsedInt(nir1);
                int nirVal2 = getParsedInt(nir2);*/

                long irVal1  = getParsedLong(red1);
                long irVal2  = getParsedLong(red2);
                long nirVal1 = getParsedLong(nir1);
                long nirVal2 = getParsedLong(nir2);


                ArrayList<String> ir = new ArrayList<>();
                ArrayList<String> nir = new ArrayList<>();
                /*ir.add(Integer.toString(irVal1));
                ir.add(Integer.toString(irVal2));
                nir.add(Integer.toString(nirVal1));
                nir.add(Integer.toString(nirVal2));
                */
                ir.add(Long.toString(irVal1));
                ir.add(Long.toString(irVal2));
                nir.add(Long.toString(nirVal1));
                nir.add(Long.toString(nirVal2));

                Intent intent = new Intent(ConstantsImp.BROADCAST_ACTION_OXY_IRNIR);

                intent.putExtra(ConstantsImp.INTENT_OXY_IR, ir);
                intent.putExtra(ConstantsImp.INTENT_OXY_NIR, nir);
                context.sendBroadcast(intent);


                long currTime = System.currentTimeMillis();
                if( lastredPPgtime == 0 ||  ((currTime - lastredPPgtime) > 1000)) {
//                            if(TextUtils.isEmpty(greenPpgFileName)) {

                        String format = getPPGFileName();
                        redPpgFileName = "InfraRed"+format+".txt";
                        irpgFileName = "NearInfraRed"+format+".txt";

//                            }
                } else {
                    if(TextUtils.isEmpty(redPpgFileName)) {
                        String format = getPPGFileName();
                        redPpgFileName = "InfraRed" + format + ".txt";
                        irpgFileName = "NearInfraRed" + format + ".txt";
                    }

                }
                lastredPPgtime = currTime;


                if(!TextUtils.isEmpty(redPpgFileName) || !TextUtils.isEmpty(irpgFileName)) {
                    File directory = new File(Environment.getExternalStorageDirectory()+"/"+"connector"+"/");
                    if(!directory.exists()) {
                        directory.mkdirs();
                    }
                    File redPpg = new File(directory, redPpgFileName);
                    File irppg = new File(directory, irpgFileName);
                    if(!redPpg.exists()) {
                        try {
                            redPpg.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(!irppg.exists()) {
                        try {
                            irppg.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
//                        if(Integer.parseInt(ir.get(0)) >= 0 && Integer.parseInt(ir.get(1)) >= 0 ) {
                            FileOutputStream redOS = new FileOutputStream(redPpg, true);
                            OutputStreamWriter redoswriter = new OutputStreamWriter(redOS);
                            redoswriter.append(ir.get(0) + "," + ir.get(1) + "\n");
                            redoswriter.close();
                            redOS.close();
//                        }

//                        if(Integer.parseInt(nir.get(0)) >= 0 && Integer.parseInt(nir.get(1)) >= 0 ) {
                            FileOutputStream iredOS = new FileOutputStream(irppg, true);
                            OutputStreamWriter iredoswriter = new OutputStreamWriter(iredOS);
                            iredoswriter.append(nir.get(0) + "," + nir.get(1) + "\n");
                            iredoswriter.close();
                            iredOS.close();
//                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }


            }
        });


    }


    private int getParsedInt(String hex) {
        return HexUtil.getInteger(hex);
    }

    private long getParsedLong(String hex) {
        return HexUtil.getLong(hex);
    }


       /* byte[] hexStringToBytes = HexUtil.hexStringToBytes(hex);
//        int bytesToInt = HexUtil.parseIrNir(hexStringToBytes);
        int bytesToInt = HexUtil.bytesToInt(hexStringToBytes, 0);

//		data = Short.valueOf(hex, 16);
        return bytesToInt;*/
//    }

    private String getFormattedString(String data) {
        return data.substring(9, 11) + data.substring(6, 8) + data.substring(3, 5) + data.substring(0, 2);
    }
    public void sendRRValue(final Context context, final String data) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String ppgtype = data.substring(15, 17);
                if(ppgtype.equals("00")) {
//                        String rrVal = data.substring(18, 20) + data.substring(21, 23) + data.substring(24, 26) + data.substring(27, 29); //[39:8]：4bytes wearing value
                    String rrValStr = data.substring(27, 29) + data.substring(24, 26) + data.substring(21, 23) + data.substring(18, 20); //[39:8]：4bytes wearing value

                    int rrVal = Integer.parseInt(rrValStr, 16);
                    Intent intent = new Intent();
                    intent.setAction(ConstantsImp.BROADCAST_ACTION_RRVALUE);
                    intent.putExtra(ConstantsImp.INTENT_RRVALUE, rrVal);
                    context.sendBroadcast(intent);
                    intent = null;
                }
            }
        });
    }


    public void sendAcceleratorValue(final Context context, final String data) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                /*x value is (B3 << 0) | (02 << 8)
                y value is (00 << 8) | (97 << 0)
                z value is (02 << 8) | (E0 << 0)
                String x = data.substring(15, 17)+data.substring(18, 20);
		        String y = data.substring(21, 23)+data.substring(24, 26);
		        String z = data.substring(27, 29)+data.substring(30, 32);
		        */

                int x = (Integer.parseInt(data.substring(15, 17), 16) << 0) | (Integer.parseInt(data.substring(18, 20), 16) << 8);
                int y = (Integer.parseInt(data.substring(21, 23), 16) << 0) | (Integer.parseInt(data.substring(24, 26), 16) << 8);
                int z = (Integer.parseInt(data.substring(27, 29), 16) << 0) | (Integer.parseInt(data.substring(30, 32), 16) << 8);

                int gX = (Integer.parseInt(data.substring(33, 35), 16) << 0) | (Integer.parseInt(data.substring(36, 38), 16) << 8);
                int gY = (Integer.parseInt(data.substring(39, 41), 16) << 0) | (Integer.parseInt(data.substring(42, 44), 16) << 8);
                int gZ = (Integer.parseInt(data.substring(45, 47), 16) << 0) | (Integer.parseInt(data.substring(48, 50), 16) << 8);



                Intent intent = new Intent();
                intent.setAction(ConstantsImp.BROADCAST_ACTION_ACCEL);
                intent.putExtra(ConstantsImp.INTENT_XVAL, x);
                intent.putExtra(ConstantsImp.INTENT_YVAL, y);
                intent.putExtra(ConstantsImp.INTENT_ZVAL, z);
                intent.putExtra(ConstantsImp.INTENT_GYRO_XVAL, gX);
                intent.putExtra(ConstantsImp.INTENT_GYRO_YVAL, gY);
                intent.putExtra(ConstantsImp.INTENT_GYRO_ZVAL, gZ);

                context.sendBroadcast(intent);
                intent = null;

            }
        });
    }


    private String getDatadate(String data) {
        // 12 34 0B 3E 09 01 12 36 E4 56 00 00 00 00 D5 01 00 00 43 21
        String dataStr = data.substring(18, 29);// 12 36 E4 56
        //Log.i(TAG, "-----" + dataStr);
        long datacount = Long
                .parseLong(dataStr.substring(9) + dataStr.substring(6, 8)
                        + dataStr.substring(3, 5) + dataStr.substring(0, 2), 16);
        long dateTime = datacount
                - (Integer.parseInt(TimeUtils.offSetTimeZone()) / 1000);

        if(isOlderDate(dateTime* 1000)){
            if(lastDateTime == 0){
                lastDateTime = System.currentTimeMillis();
            }
            dateTime = lastDateTime/1000;
        }

        lastDateTime = dateTime*1000;

        return getFormatedDateTime("yyyy-MM-dd HH:mm:ss", dateTime);// 时区兼容


    }

    public static boolean isOlderDate(long timestamp){
        long delta = System.currentTimeMillis()-timestamp;
        boolean result = false;
        if(delta > 7* AlarmManager.INTERVAL_DAY){
            result = true;
        }
        return result;
    }

    private String getPPGFileName() {
        String format = "";
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
        format = sdf.format(new Date());

        return format;
    }


}