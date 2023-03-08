package com.worldgn.connector;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.worldgn.connector.util.PrefHelper;


/**
 * Created by WGN on 13-09-2017.
 */
class ScanBLE implements  HeloGattCallBackListenter, BleCompatScannerCallback { //BluetoothAdapter.LeScanCallback, {


    private BluetoothAdapter bluetoothAdapter;
    //    private Handler scanHandler;
    Context context;
    private ArrayList<DeviceItem> mDevices;
    //    private ConnectionCallBacks connectionCallBacks;
    private BluetoothGatt bluetoothGatt;
    //    BleCallback bleCallback;
//    List<String> macIds;
    private ScanCallBack scanCallBack;
//    private ConnectCallback connectCallback;
    private JobScheduler jobScheduler;
    private final int compatJobId = 123;
    private final int md5JobId = 124;

    private final int leScanTime = 10 * 1000;
    private static final String TAG = ScanBLE.class.getSimpleName();
    private static Handler mMatchHandler = null;
    private static String LOCK = "lock";
    private static ScanBLE scanBLE;

    public static Queue<WriteProperties> writeQueue;
    private StringBuilder ecgVideo = new StringBuilder();
    private String ecgdata = "";
    private String bpData = "";
    private String oxyData = "";
    private ArrayList<String> oxyList = new ArrayList<>();
    private Vector<String> oxygenList = new Vector<>();
    private String hrData = "";
    private String moodData = "";
    private String fatigueData = "";
    private String brData = "";
    private List<String> heloDeviceNames = new ArrayList<>();



//    private BluetoothGattCallback bluetoothGattCallback;

    private boolean isEcgRunning = false;
    private boolean isSleepRunning = false;
    private boolean isBpRunning = false;
    private boolean isOxyRunning = false;
    private boolean isHrRunning = false;
    private boolean isMoodRunning = false;
    private boolean isFatigueRunning = false;
    private boolean isBrRunning = false;


    private boolean hrMeasure = false;
    private boolean brMeasure = false;
    private boolean ecgMeasure = false;
    private boolean moodMeasure = false;
    private boolean fatigueMeasure = false;
    private boolean bpMeasure = false;
    private boolean stepsMeasure = false;




    private Handler ecgHandler = new Handler();
    private Handler bpHandler = new Handler();
    private Handler oxyHandler = new Handler();
    private Handler hrHandler = new Handler();
    private Handler sleepHandler = new Handler();
    private Handler moodHandler = new Handler();
    private Handler fatigueHandler = new Handler();
    private Handler brHandler = new Handler();
//    private List<String> allowedHeloDevices = new ArrayList<>();
    private HeloGattCallback heloGattCallback;
    private BleCompatScanner bleCompatScanner;
    Boolean isSkin = null;



//    private BluetoothAdapter.LeScanCallback lescanCallback = null;

    private ScanBLE(Context context) {
        this.context = context;
        if (writeQueue == null) {
            writeQueue = new LinkedList<>();
        }
        bleCompatScanner = new BleCompatScanner(this);
        heloGattCallback = new HeloGattCallback(this);
        getDeviceNames();
        getAllowedHeloDevices();
    }

    private void getAllowedHeloDevices() {
        try {
            String permission = SafePreferences.getInstance().getStringFromSecurePref(ConstantsImp.PREF_PERMISSION, "");
            if(permission.equals("0")) {
                GlobalData.isDev = ConstantsImp.DEV_YES;
                String restrictedMacIdsArrlist = SafePreferences.getInstance().getStringFromSecurePref(Constants.JSON_KEY_MACDEVICE, "[]");
                Type listType = new TypeToken<ArrayList<String>>() {
                }.getType();
                GlobalData.macDevices = new Gson().fromJson(restrictedMacIdsArrlist, listType);
                DebugLogger.i("allowedHeloDevices", GlobalData.macDevices.size() + "");
            } else {
                GlobalData.isDev = ConstantsImp.DEV_NO;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private List<String> getDeviceNames() {
        heloDeviceNames = new ArrayList<>();
        heloDeviceNames.add(Constants.DEVICE_NAME);
        heloDeviceNames.add(Constants.DEVICE_SUBNAME1);
        heloDeviceNames.add(Constants.DEVICE_SUBNAME2);
        heloDeviceNames.add(Constants.DEVICE_SUBNAME3);
        heloDeviceNames.add(Constants.DEVICE_SUBNAME4);
        heloDeviceNames.add(Constants.DEVICE_SUBNAME5);
        heloDeviceNames.add(Constants.DEVICE_SUBNAME6);
        return heloDeviceNames;
    }

    private static String WRTAG = "Wrtag";

    public static ScanBLE getInstance(Context context) {

        if (scanBLE == null) {
            synchronized (LOCK) {
                if (scanBLE == null) {
                    scanBLE = new ScanBLE(context);
                }
            }
        }
        return scanBLE;
    }


    public void scan( ScanCallBack scanCallBack) {
        if (GlobalData.canSearchNewDevice && !GlobalData.status_Connected) {
            this.scanCallBack = scanCallBack;
            mDevices = new ArrayList<>();
//            scanHandler = new Handler();

            getDefaultBTadapter();
            startScan();
        }
    }

    public void setScanCallback(ScanCallBack scanCallback) {
        this.scanCallBack = scanCallback;
        getDefaultBTadapter();
    }

    public boolean connect(DeviceItem device) {  //ConnectCallback connectCallback, GetBleGatt getBleGatt

        if (GlobalData.status_Connected) {
            disconnect();
        }
        return connectToDevice(device);
    }





    public void disconnectDevice() {
//        0aabcdef-1111-2222-0000-facebeadaaaa
        int writeStatus = WriteToDevice.ackForBindRequest(0, bluetoothGatt);
        if (writeStatus == 1) {
            sendBroadcast(ConstantsImp.BROADCAST_ACTION_HELO_UNBONDED);
            disconnect();

        }

    }


    private void startScan() {
        //stopScan();
        if(bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            bleCompatScanner.startScan(bluetoothAdapter);
        } else {
            scanCallBack.onFailure("Bluetooth adapter Not enabled");
        }
    }

    public boolean stopScan() {
        if(bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            bleCompatScanner.stopScan(bluetoothAdapter);
            bleCompatScanner.removeCallback();
            GlobalData.canSearchNewDevice = true;
            return true;
        }
        else {
            if(scanCallBack != null)
            scanCallBack.onFailure("Bluetooth adapter Not enabled");
            return false;
        }
    }


    private void getDefaultBTadapter() {

        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

    }




    private void connectNearestDevice() {

        String savedHeloMac = "";
        try {
            savedHeloMac = SafePreferences.getInstance().getStringFromSecurePref(ConstantsImp.PREF_MAC, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (savedHeloMac) {
            case "":
                scanCallBack.onScanFinished();
                //notify app new device found
                int tempRiss = -100;
                String tempMac = "";
                String tempName = "";
                DeviceItem closerDevice = null;
                for (DeviceItem device : mDevices) {
                    if (device.getRssi() > tempRiss && checkMAC(device.getDeviceMac())) {
                        tempRiss = device.getRssi();
                        tempMac = device.getDeviceMac();
                        tempName = device.getDeviceName();
                        closerDevice = device;
                    }
                }
                if (tempMac.equals("")) {
                    startScan();
                } else {
                    scanCallBack.onLedeviceFound(closerDevice);
                    GlobalData.canSearchNewDevice = true;
                }
                mDevices.clear();
                break;

            default:
                DeviceItem deviceItem = new DeviceItem();
                deviceItem.setDeviceMac(savedHeloMac);
                if (mDevices.contains(deviceItem)) {
                    BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(savedHeloMac);
                    bluetoothGatt = bluetoothDevice.connectGatt(context, false, heloGattCallback);
                    mDevices.clear();
                    stopScan();
                } else {
                    scanCallBack.onPairedDeviceNotFound();
                    mDevices.clear();
                    startScan();
                }
                break;
        }

    }

    private boolean connectToDevice(DeviceItem deviceItem) {
        if(bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceItem.getDeviceMac());
            bluetoothGatt = bluetoothDevice.connectGatt(context, false, heloGattCallback);

            if (bluetoothGatt != null) {
                SafePreferences.getInstance().saveStringToSecurePref(ConstantsImp.PREF_MAC, deviceItem.getDeviceMac());
                return true;
            }
            return false;
        } else {
            if(scanCallBack!=null)
            scanCallBack.onFailure("Bluetooth adapter not enabled");
            return false;
        }

//        connectCallback.onGattConnecting();
    }


    private boolean checkMAC(final String mac) { // æ­£åˆ™æ£€éªŒmac
        String regEx = "[0-9a-fA-F][0-9a-fA-F]:" + "[0-9a-fA-F][0-9a-fA-F]:"
                + "[0-9a-fA-F][0-9a-fA-F]:" + "[0-9a-fA-F][0-9a-fA-F]:"
                + "[0-9a-fA-F][0-9a-fA-F]:" + "[0-9a-fA-F][0-9a-fA-F]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(mac);

        return m.matches();
    }


    private void disconnect() {
        if(bleCompatScanner!=null)
        bleCompatScanner.stopScan(bluetoothAdapter);
        bluetoothGatt.disconnect();
    }



    public boolean isHeart = false;
    public boolean isMatchInfo = false;
    public String connectionState = "";
    byte[] decode;


    private void callNextWrite(byte[] data) {
        byte writeCmd = data[3];
        switch (writeCmd) {
            case ConstantsImp.bindReqCmd:
                DebugLogger.i("callNextWrite", "bindReqCmd");
                WriteToDevice.initDeviceLoadCode(bluetoothGatt);
                break;

            case ConstantsImp.initDeviceLoadCodeCmd:
                DebugLogger.i("callNextWrite", "initDeviceLoadCodeCmd");
                final byte[] mac = BleHelper.getSelfBlueMac();
                WriteToDevice.matchInfo(WriteToDevice.bytesToHexString(mac), bluetoothGatt);
                break;

            case ConstantsImp.matchInfoCmd:

                break;

            case ConstantsImp.secondMachCmd:
                switch (data[5]) {
                    case 1:
                        DebugLogger.i("callNextWrite", "secondMachCmd case 1");
                        WriteToDevice.UpdateNewTime(bluetoothGatt);
                        break;
                    case 0:
                        break;
                }

                break;

            case ConstantsImp.updateNewTimeCmd:
                /** COmmented on 15-03-2018
                 * to remove licence functionality**/
                /*DebugLogger.i("callNextWrite", "updateNewTimeCmd");
                String token = "";
                try {
                    token = SafePreferences.getInstance().getStringFromSecurePref(ConstantsImp.PREF_DECRY_TOKEN, "");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                byte[] decode = Base64.decode(token.getBytes(), Base64.NO_PADDING);
                WriteToDevice.getDecryTokenContent(decode);
                WriteToDevice.startSendDecryToken(decode.length, bluetoothGatt);*/
                break;

            case ConstantsImp.startSendDecryTokenCmd:
                DebugLogger.i("callNextWrite", "startSendDecryTokenCmd");
                if (writeQueue.size() > 0) {
                    WriteProperties wp = writeQueue.poll();
                    WriteToDevice.sendDecryTokenContent(wp.getData(), bluetoothGatt);
                }
                break;

            case ConstantsImp.sendDecryTokenContentCmd:
                DebugLogger.i("callNextWrite", "sendDecryTokenContentCmd");
                if (writeQueue.size() > 0) {
                    WriteProperties wp = writeQueue.poll();
                    WriteToDevice.sendDecryTokenContent(wp.getData(), bluetoothGatt);
                }
                break;
        }

    }


    public void sendDynamicStepseasurement(String data) {

//            String dataStr = data.substring(30, 41);
        data = data.substring(27, 38);
        Intent dynStepIntent = new Intent();
//            long stepsCount = Long.parseLong(data.substring(9) + data.substring(6, 8) + data.substring(3, 5) + data.substring(0, 2), 16);
        long stepsCount = Long.parseLong(data.substring(9,11) + data.substring(6, 8) + data.substring(3, 5) + data.substring(0, 2), 16);

        dynStepIntent.setAction(ConstantsImp.BROADCAST_ACTION_DYNAMIC_STEPS_MEASUREMENT);
        dynStepIntent.putExtra(ConstantsImp.INTENT_KEY_DYNAMIC_STEPS_MEASUREMENT, Long.toString(stepsCount));
        context.sendBroadcast(dynStepIntent);
        dynStepIntent = null;


    }

    public void sendDynamicHeartRateMeasurement(String data) {
        String result;
        Intent dynHRIntent = new Intent();
        result = parseSingeData(data);
        dynHRIntent.setAction(ConstantsImp.BROADCAST_ACTION_DYNAMIC_HR_MEASUREMENT);
        dynHRIntent.putExtra(ConstantsImp.INTENT_KEY_HR_MEASUREMENT, result);
        context.sendBroadcast(dynHRIntent);
        dynHRIntent = null;

    }


    private void sendBindBroadcast(String data) {
        DebugLogger.i(TAG, "sendBindBroadcast");
        String cmdType = data.substring(9, 11);
        if (cmdType.equals("37")) { // åŒ¹é…å“åº”
            DebugLogger.i(TAG, "74 åŒ¹é…å“åº”");
            // æ”¶åˆ°åŒ¹é…å“åº” ç½®true
            GlobalData.isMatchInfo = true;

            String valuse = data.substring(15, 17);
            long datavalue = Long.valueOf(valuse, 16);
            DebugLogger.i(TAG, "sendBindBroadcast datavalue " + datavalue);
            if (datavalue == 1) {
                callBindSuccessCallbacks();
            } else {
//                DebugLogger.i(TAG, "sendBindBroadcast datavalue " + datavalue);
                Intent intent = new Intent();
                intent.setAction(ConstantsImp.BROADCAST_ACTION_HELO_DEVICE_RESET);
                context.sendBroadcast(intent);
                WriteToDevice.secondMach(0, bluetoothGatt);

            }
        } else if (cmdType.equals("38")) { // è§£ç»‘å“åº”
            String valuse = data.substring(15, 17);
            DebugLogger.i(TAG, "80 è§£ç»‘å“åº”");
            long datavalue = Long.valueOf(valuse, 16);
            //broadcastUpdate(GlobalData.ACTION_GATT_DEVICE_UNBIND_ACK, datavalue);
        } else if (cmdType.equals("23")) { // è®¾å¤‡è¯·æ±‚ç»‘å®š
            DebugLogger.i(TAG, "Need to show bind dialog");
            DebugLogger.d("sqs", "æ”¶åˆ°è¯·æ±‚ç»‘å®š IS_MATCH_INFO_FROM_DEVICE");
            //broadcastUpdate(GlobalData.ACTION_GATT_DEVICE_BIND_REQUEST);
            int writeStatus = WriteToDevice.ackForBindRequest(1, bluetoothGatt);
            if (writeStatus == 1) {
                //GlobalData.status_Connected = true;
                sendBroadcast(ConstantsImp.BROADCAST_ACTION_HELO_BONDED);
            } else {
                //GlobalData.status_Connected = false;
                sendBroadcast(ConstantsImp.BROADCAST_ACTION_HELO_UNBONDED);
            }
        }
    }


    private void sendUpdataFirmInfo(Context context, String data) {
        /*
         * [12 34 0B 3C 05 01 02 00 00 00 4F 00 00 00 43 21 ] [A6 36 E4 56 CF 00
         * 00 00 A6 36 E4 56 CF 00 00 00 70 ] [12 34 0B 3E 04 CA 05 00 00 1C 01
         * 00 00 43 21 ]
         */

        DebugLogger.v(TAG, "139 ç¦»çº¿é€šé“ :" + data);
        String cmdType = data.substring(9, 11); // å‘½ä»¤
        String dataType = data.substring(15, 17); // data
        if (cmdType.equals("3A")) { //
            DebugLogger.i(TAG, "dataType.equals('3A')");
            if (dataType.equals("01")) {
                DebugLogger.i(TAG, "145  ACTION_MAIN_DATA_FIR_SUCCESS");
//                broadcastUpdate(GlobalData.ACTION_MAIN_DATA_FIRM_SUCCESS);
            } else {
                DebugLogger.i(TAG, "148 ACTION_MAIN_DATA_FIR_FAULT");
//                broadcastUpdate(GlobalData.ACTION_MAIN_DATA_FIRM_FAULT);
            }
        } else if (cmdType.equals("39")) {
            String[] str = {data.substring(15, 17), data.substring(18, 20),
                    data.substring(21, 23), data.substring(24, 26)};
            String tem = str[3] + str[2] + str[1] + str[0];
            DebugLogger.d(TAG, "str:versioncore = " + tem);
            DebugLogger.d(TAG, "ç‰ˆæœ¬å· int:versioncore = " + Long.valueOf(tem, 16));
            int version = -1;
            if (tem.equals("FFFFFFFF")) {
                DebugLogger.d(TAG, "ç‰ˆæœ¬å· ä¿å­˜ä¸ºé›¶" + tem);
                version = 1;
            } else {
                version = Integer.valueOf(tem, 16);
            }
            GlobalData.VERSION_FIRM = version; // æ”¾åˆ°å…¨å±€å˜é‡

            SafePreferences.getInstance().saveStringToSecurePref(ConstantsImp.PREF_FIRMWARE, version + "");
            Intent intent = new Intent();
            intent.setAction(ConstantsImp.BROADCAST_ACTION_HELO_FIRMWARE);
            intent.putExtra(ConstantsImp.INTENT_KEY_FIRMWARE, version);
            context.sendBroadcast(intent);
            DebugLogger.d("firmware", ConstantsImp.PREF_FIRMWARE + " " + version);
            DebugLogger.i(TAG, "167 ACTION_DEVICE_FIRMVERSION = " + version);

//            broadcastUpdate(GlobalData.ACTION_DEVICE_FIRMVERSION, version + "");  // update firmware in UI
        } else if (cmdType.equals("3E")) { // ç¦»çº¿æ•°æ®
//            DebugLogger.v(TAG, "load  data 3E----------------" + data);
//            broadcastUpdate(GlobalData.ACTION_GATT_LOAD_DATA, data);
            switch (dataType) {
                case "04":
                        if (!isSleepRunning) {
                            sleepHandler.postDelayed(sleepRunnable, 60 * 10000);
                            isSleepRunning = true;
                        }
                    DebugLogger.i("sleepparser", "sleep auto data");
                    DataParser.getInstance().parseSleepData(data, context);
                    break;
                case "01":
                    if(GlobalData.isDynamic){
                        sendDynamicStepseasurement( data);
                    } else {
                        DataParser.getInstance().loadScheduleData(data, context);
                    }
                    break;

                default:
                    DebugLogger.i("ScheduleMeasure==", data);
                    DataParser.getInstance().loadScheduleData(data, context);
                    break;

            }

//            DataParser.getInstance().loadScheduleData(data, context, prefHelper);

        } else if (cmdType.equals("44")) { // ç¡çœ è´¨é‡
            DebugLogger.i(TAG, "sleepdata");
            if (!isSleepRunning) {
                sleepHandler.postDelayed(sleepRunnable, 60 * 1000);
                isSleepRunning = true;
            }
            DebugLogger.i("sleepparser", "cmd 44");
            DataParser.getInstance().parseSleepData( data, context);

        } else if (cmdType.equals("4F")) {
            DebugLogger.d("sqs", "æŽ¥æ”¶åˆ°è®¾å¤‡ç«¯ä¼ æ¥å™ªéŸ³4Fæ•°æ®..." + data);
            // 12 34 0B 4F 05 00 00 00 00 00 5F 00 00 00 43 21
//            broadcastUpdate(GlobalData.ACTION_GATT_BLOOD_PRESSURE_NOISE, data);
        }

    }


    private void callBindSuccessCallbacks() {
        DebugLogger.i(TAG, "callBindSuccessCallbacks");

        WriteToDevice.secondMach(1, bluetoothGatt);
        // Log.d("sqs", "decode é•¿åº¦:"+decode.length);
        //WriteToDevice.startSendDecryToken(decode.length, bluetoothGatt);
        // String bytesToHexString =
        // HexUtil.bytesToHexString(decode);

//        WriteToDevice.sendDecryTokenContent1(decode, bluetoothGatt);


    }


    private boolean isNewSleep(String data) {
        DebugLogger.d("sqs", "æ–°ç¡çœ  data1 = " + data);
        if ("44".equals(data.substring(3, 5))) {
            DebugLogger.d("sqs", "æ–°ç¡çœ  data2 = " + data);
            //broadcastUpdate(GlobalData.ACTION_GATT_SLEEP_NEW, data);
        }
        return false;
    }

    public boolean listCharacteristics() {
        if (bluetoothAdapter == null || bluetoothGatt == null) {
            DebugLogger.w(TAG, "Characteristicnotification  BluetoothAdapter not initialized");
            return false;
        }
        StringBuffer charsBuffer = new StringBuffer();

        List<BluetoothGattService> bluetoothGattServices = bluetoothGatt.getServices();
        charsBuffer.append(bluetoothGattServices.size()).append(" services!\n");
        for (BluetoothGattService rxService: bluetoothGattServices) {
            charsBuffer.append("- ").append(rxService.getUuid().toString()).append(" [").append(rxService.getType()).append("]:\n");
            List<BluetoothGattCharacteristic> serviceCharacteristics = rxService.getCharacteristics();
            charsBuffer.append("  ").append(serviceCharacteristics.size()).append(" characteristics!\n");
            for (BluetoothGattCharacteristic txChar: serviceCharacteristics) {
                charsBuffer.append("  + ").append(txChar.getUuid().toString()).append(":\n");
            }
        }
        DebugLogger.w(TAG, charsBuffer.toString());
        return true;
    }

    public boolean setCharacteristicNotification(String serviceUUID, String characteristicUUID, boolean enabled) {

        boolean status = false;
        if (bluetoothAdapter == null || bluetoothGatt == null) {
            DebugLogger.w(TAG, "Characteristicnotification  BluetoothAdapter not initialized");
            return false;
        }
        BluetoothGattService RxService = bluetoothGatt.getService(UUID.fromString(serviceUUID));
        if (RxService == null) {
            DebugLogger.i(TAG, "Characteristicnotification GATT  null Service");
            return false;
        }
        BluetoothGattCharacteristic TxChar = RxService.getCharacteristic(UUID.fromString(characteristicUUID));
        if (TxChar == null) {
            DebugLogger.i(TAG, "Characteristicnotification :GATT  null Characteristic");
            return false;
        } else {
            status = bluetoothGatt.setCharacteristicNotification(TxChar, enabled);
            DebugLogger.i(TAG, "Characteristicnotification  :Characteristic on status = " + status);
            return status;
        }
    }


    private void sendDataBroadcast(String data) {
        DebugLogger.i(TAG, "sendDataBroadcast " + data);
        if (data.equals("CF")) {
            // broadcastUpdate(ACTION_MAIN_DATA_ECG, 111);
        } else {
            String dataType = data.substring(9, 11);
            switch (dataType) {
                case "31":
                    if (GlobalData.isDynamic) {
                        sendDynamicStepseasurement(data);
                    } else {
                        DataParser.getInstance().dataMeasurementParser(data, context);
                    }
                    break;
                case "32":
                    hrData = data;
                    if (GlobalData.isDynamic) {
                        sendDynamicHeartRateMeasurement(hrData);
                    } else if (!isHrRunning && hrMeasure) {
                        isHrRunning = true;
                        hrHandler.postDelayed(hrRunnable, 30 * 1000);
                    }

                    break;
                case "3B":
                    if (moodMeasure) {
                        if (!isMoodRunning) {
                            isMoodRunning = true;
                            moodHandler.postDelayed(moodRunnable, 30 * 1000);
                        }
                    }
                    moodData = data;

                    break;
                case "3C":
                    if (fatigueMeasure) {
                        if (!isFatigueRunning) {
                            isFatigueRunning = true;
                            fatigueHandler.postDelayed(fatigueRunnable, 30 * 1000);
                        }
                        fatigueData = data;
                    }
                    break;
                case "3D":
                    if(brMeasure) {
                        if (!isBrRunning) {
                            isBrRunning = true;
                            brHandler.postDelayed(brRunnable, 30 * 1000);
                        }
                        brData = data;
                    }
                    break;


                case "42":
                    if(data!=null && !data.equalsIgnoreCase("")) {
                        ecgdata = data;
                        if (!isEcgRunning) {
                            isEcgRunning = true;
                            ecgHandler.postDelayed(ecgRunnable,  30 * 1000); //10 secs
                        }
                        if (isEcgRunning) {
                            ecgVideo.append("," + DataParser.getInstance().parseEcgAlldata(ecgdata.substring(0, 11)));
                            ecgVideo.append("," + DataParser.getInstance().parseEcgAlldata(ecgdata.substring(12, 23)));
                            ecgVideo.append("," + DataParser.getInstance().parseEcgAlldata(ecgdata.substring(24, 35)));
                            ecgVideo.append("," + DataParser.getInstance().parseEcgAlldata(ecgdata.substring(36, 47)));
                            if (ecgdata.length()>48) {
                                ecgVideo.append("," + DataParser.getInstance().parseEcgAlldata(ecgdata.substring(48)));
                            }
                        }
                    }
                    break;
                case "41":
                    if(data!=null && !data.equalsIgnoreCase("")) {
                        bpData = data;
                        if (!isBpRunning) {
                            isBpRunning = true;
                            if (bpMeasure) {
                                bpHandler.postDelayed(bpRunnable, 30* 1000);
                            }
                        }
                    }
                        /*if (!isBpRunning) {
                            isBpRunning = true;
                            if (bpMeasure) {
                            bpHandler.postDelayed(bpRunnable, 30 * 1000);
                        }
                        bpData = data;
                        }*/

                    break;

                case "36":
//                    if(data!=null && !data.equalsIgnoreCase("")) {
                    oxyData = data;
                    oxyList.add(data);
                    oxygenList.add(data);
                       /* if (!isOxyRunning) {
                            isOxyRunning = true;
                            oxyHandler.postDelayed(oxyRunnable, 30 * 1000);
                        }
                    }*/
                    break;

                case "48":
                    DataParser.getInstance().sendAcceleratorValue(context, data);
                    break;

                case "4E":
                    DataParser.getInstance().sendRRValue(context, data);
                    break;

                case "43":
                    DataParser.getInstance().dataMeasurementParser(data, context);
                    break;

                default:
                    DataParser.getInstance().dataMeasurementParser(data, context);
                    break;
            }

        }
    }


    private Runnable sleepRunnable = new Runnable() {
        @Override
        public void run() {
            DataParser.getInstance().sendSleepData(context);
            isSleepRunning = false;

        }
    };

    private Runnable bpRunnable = new Runnable() {
        @Override
        public void run() {
            isBpRunning = false;
            bpMeasure = false;
            DataParser.getInstance().dataMeasurementParser(bpData, context);

        }
    };

    private Runnable hrRunnable = new Runnable() {
        @Override
        public void run() {
            DataParser.getInstance().dataMeasurementParser(hrData, context);
            isHrRunning = false;
            hrMeasure = false;
        }
    };

    private Runnable moodRunnable = new Runnable() {
        @Override
        public void run() {
            DataParser.getInstance().dataMeasurementParser(moodData, context);
            isMoodRunning = false;
            moodMeasure = false;
        }
    };

    private Runnable fatigueRunnable = new Runnable() {
        @Override
        public void run() {
            DataParser.getInstance().dataMeasurementParser(fatigueData, context);
            isFatigueRunning = false;
            fatigueMeasure = false;
        }
    };

    private Runnable brRunnable = new Runnable() {
        @Override
        public void run() {
            DataParser.getInstance().dataMeasurementParser(brData, context);
            isBrRunning = false;
            brMeasure = false;
        }
    };

    private Runnable oxyRunnable = new Runnable() {
        @Override
        public void run() {
            isOxyRunning = false;
            if(oxyList!=null && oxyList.size()>0) {
                Log.e("OxyList",oxyList.toString());
                DataParser.getInstance().oxyMeasurementParser(oxyList, context);
                oxyList.clear();
            }

        }
    };

    private Runnable ecgRunnable = new Runnable() {
        @Override
        public void run() {
            isEcgRunning = false;
            ecgMeasure = false;
            DataParser.getInstance().sendEcg(context, ecgdata, ecgVideo.replace(0, 1, "").toString());
            //ecgdata = "";
            ecgVideo = new StringBuilder();
        }
    };

    private int pareseBatteryData(String data) {
        String substring = data.substring(15, 17);
        int parseInt = Integer.parseInt(substring, 16);
        return parseInt;
    }


    private String parseSingeData(String data) {
        String dataStr;
        // 12 34 0B 32 08 61 FF 46 57 4F 00 00 00 91 02 00 00 43 21
        // 12 34 0B 32 08 AE 1E 4C 57 00 00 00 00 B4 01 00 00 43 21
        dataStr = data.substring(27, 38);
        String datastring = dataStr.substring(9, 11) + dataStr.substring(6, 8) + dataStr.substring(3, 5) + dataStr.substring(0, 2);
        long dataf = Integer.parseInt(datastring, 16);
        return dataf + "";

    }

    public boolean stopMeasuring() {
        if (GlobalData.status_Connected) {
            int stopWriteStatus = WriteToDevice.stopMeasuring(bluetoothGatt);
            if (stopWriteStatus == 1) {
                return true;
            }
        }
        return false;
    }

    public boolean unbindDevice() {
        if (GlobalData.status_Connected) {
            int stopWriteStatus = WriteToDevice.stopMeasuring(bluetoothGatt);
            if(stopWriteStatus == 1) {
                int writeStatus = WriteToDevice.unbindDevice(bluetoothGatt);
                //if (writeStatus == 1) {
                SafePreferences.getInstance().saveStringToSecurePref(ConstantsImp.PREF_MAC, "");
                bluetoothGatt.disconnect();
                GlobalData.status_Connected = false;
//        bluetoothGatt.disconnect();
                Intent intent = new Intent();
                intent.setAction(ConstantsImp.BROADCAST_ACTION_HELO_UNBONDED);
                context.sendBroadcast(intent);
                bleCompatScanner.stopScan(bluetoothAdapter);
                stopScan();
                if(jobScheduler!= null)
                jobScheduler.cancelAll();
                return true;
                //}
            }
            return false;
        } else {
            SafePreferences.getInstance().saveStringToSecurePref(ConstantsImp.PREF_MAC, "");
            Intent intent = new Intent();
            intent.setAction(ConstantsImp.BROADCAST_ACTION_HELO_UNBONDED);
            context.sendBroadcast(intent);
            stopScan();
            if(jobScheduler!= null) {
                jobScheduler.cancel(compatJobId);
            }
            return true;

        }



    }

    /*private BluetoothGattCallback getBluetoothGattCallback() {
       return new BluetoothGattCallback() {
           @Override
           public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
               super.onConnectionStateChange(gatt, status, newState);
           }

           @Override
           public void onServicesDiscovered(BluetoothGatt gatt, int status) {
               super.onServicesDiscovered(gatt, status);
           }

           @Override
           public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
               super.onCharacteristicWrite(gatt, characteristic, status);
           }

           @Override
           public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
               super.onCharacteristicChanged(gatt, characteristic);
           }
       };
    }*/


    private String getAppVersion() {
        PackageManager manager;
        PackageInfo info = null;
        manager = context.getPackageManager();
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String str = info.versionCode + "";
        DebugLogger.i(TAG, "OldVersionCore1 = " + str);
        String result = str.substring(2, 8);
        DebugLogger.i(TAG, "OldVersionCore2 = " + result);
        return result;
    }

    private void sendBroadcast(String intentAction) {
        Intent intent = new Intent();
        intent.setAction(intentAction);
        context.sendBroadcast(intent);
    }

    private boolean checkDeviceName(List<String> deviceNames, String deviceName) {
        for (String device : deviceNames) {
            if (device.equals(deviceName)) {
                return true;
            }
        }
        return false;
    }

    public boolean getHeartRate() {
        if(WriteToDevice.heartRate(bluetoothGatt)) {
            hrMeasure = true;
            return true;
        } else {
            sendMeasureFailureBroadcast(Constants.HR_REQ_FAIL);
            return false;
        }

    }

    public void sendMeasureFailureBroadcast(String data) {
        Intent intent = new Intent();
        intent.setAction(ConstantsImp.BROADCAST_ACTION_MEASUREMENT_WRITE_FAILURE);
        intent.putExtra(ConstantsImp.INTENT_MEASUREMENT_WRITE_FAILURE, data);
        context.sendBroadcast(intent);
    }

    public boolean updateNewTime() {
        int writestatus = WriteToDevice.UpdateNewTime(bluetoothGatt);
        return writestatus == 1;
    }

    public boolean  measureHR() {

        hrMeasure = WriteToDevice.measureBp(bluetoothGatt);
        /*boolean pwMeasure = false;
        if(hrMeasure) {
            pwMeasure =  WriteToDevice.measurePW(bluetoothGatt);
        }
        boolean measureResult = hrMeasure && pwMeasure;*/
        if(!(hrMeasure)) {
            sendMeasureFailureBroadcast(Constants.HR_REQ_FAIL);

        }
        return hrMeasure;


        /*hrMeasure = WriteToDevice.measureHr(bluetoothGatt);
        if(hrMeasure) {
            return WriteToDevice.measurePW(bluetoothGatt);
        } else {
            hrMeasure = false;
            sendMeasureFailureBroadcast(Constants.HR_REQ_FAIL);
            return false;
        }*/

    }

    public boolean measureBP() {

        bpMeasure = WriteToDevice.measureBp(bluetoothGatt);
       /* boolean pwMeasure = false;
        if(bpMeasure) {
            pwMeasure =  WriteToDevice.measurePW(bluetoothGatt);
        }
        boolean measureResult = bpMeasure && pwMeasure;*/
//        if(!(measureResult)) {
        if(!(bpMeasure)) {
            sendMeasureFailureBroadcast(Constants.BP_REQ_FAIL);
            bpMeasure = false;
        }
        /*if(bpMeasure){
            GlobalData.isBpPPGMeasurement=true;
        }*/
        return bpMeasure;


    }

    public boolean bpReset() {
        int writestatus = WriteToDevice.bpReset(bluetoothGatt);
        return writestatus == 1;
    }

    public boolean measureECG() {
        if(WriteToDevice.measureECG(bluetoothGatt)) {
            ecgMeasure = true;
            return true;
        } else {
            sendMeasureFailureBroadcast(Constants.ECG_REQ_FAIL);
            return false;
        }
    }

    public boolean measureBr() {

        brMeasure = WriteToDevice.measureBr(bluetoothGatt);
       /* boolean pwMeasure = false;
        if(brMeasure) {
            pwMeasure =  WriteToDevice.measurePW(bluetoothGatt);
        }
        boolean measureResult = brMeasure && pwMeasure;*/
        if(!(brMeasure)) {
            brMeasure = false;
            sendMeasureFailureBroadcast(Constants.BR_REQ_FAIL);
        }
        return brMeasure;
    }

    public boolean measureMF() {

        boolean measureSuccess = WriteToDevice.measureMF(bluetoothGatt);
       /* boolean pwMeasure = false;
        if(measureSuccess) {
            pwMeasure =  WriteToDevice.measurePW(bluetoothGatt);
        }
        boolean measureResult = measureSuccess && pwMeasure;*/
        if(!(measureSuccess)) {
            sendMeasureFailureBroadcast(Constants.BR_REQ_FAIL);
        } else {
            moodMeasure     =   true;
            fatigueMeasure  =   true;
        }
        return measureSuccess;
    }

    public boolean getStepsData() {
        if(WriteToDevice.getSteps(bluetoothGatt)) {
            GlobalData.stepsMeasure = true;
            return true;
        } else {
            sendMeasureFailureBroadcast(Constants.STEPS_REQ_FAIL);
            return false;
        }

    }

    public boolean sleepTime() {
        int writestatus = WriteToDevice.sleepTime(bluetoothGatt);
        return writestatus == 1;
    }

    public boolean sendReviseAutoTimeData() {
        int writestatus = WriteToDevice.sendReviseAutoTime(bluetoothGatt, 2);
        return writestatus == 1;
    }

    public boolean getDynamicStepsHeartRateMeasure(int value) {
        if(WriteToDevice.dynamicStepsHeartRateMeasure(value, bluetoothGatt)) {
            return true;
        } else {
            switch (value) {
                case 0:
                    sendMeasureFailureBroadcast(Constants.STOP_DYNAMIC_REQ_FAIL);
                    break;
                case 1:
                    sendMeasureFailureBroadcast(Constants.START_DYNAMIC_REQ_FAIL);
                    break;
            }

            return false;
        }
    }

    public boolean getUV() {
        if(WriteToDevice.getUVMeasurement(bluetoothGatt)) {
            return true;
        } else {
            GlobalData.isUVMEasurement=false;
            sendMeasureFailureBroadcast(Constants.UV_REQ_FAIL);
            return false;
        }
    }

    //Abhijeet 6-3-18

    public boolean startGreenMeasurement() {
        if(WriteToDevice.getGreenPPG(bluetoothGatt)) {

            GlobalData.greenPpgFile = "GreenPPG"+getPPGFileName()+".txt";
            return true;
        } else {
            sendMeasureFailureBroadcast(Constants.GREEN_REQ_FAIL);
            return false;
        }
    }

    public void stopGreenMeasurement() {
        WriteToDevice.stopMeasuring(bluetoothGatt);
        GlobalData.greenPpgFile = "";
    }

    public boolean getRnIRData() {
        if(WriteToDevice.getRNIRData(bluetoothGatt)) {
            GlobalData.isOxyMeasurement = true;
            String sdf = getPPGFileName();
            GlobalData.redPpgFile = "RedPPG"+sdf+".txt";
            GlobalData.irPpgFile = "InfraRedPPG"+sdf+".txt";
            return true;
        } else {
            sendMeasureFailureBroadcast(Constants.RnIR_REQ_FAIL);
            return false;
        }
    }

    public void stopRnIRData() {
        GlobalData.isOxyMeasurement = false;
        WriteToDevice.stopMeasuring(bluetoothGatt);
        GlobalData.redPpgFile = "";
        GlobalData.irPpgFile  = "";
    }

    //------------------//

    public boolean getOxygen() {
//        CountDownTimer countDownTimer;

        boolean measureSuccess = WriteToDevice.getOxygenMeasurement(bluetoothGatt);
        boolean pwMeasure = false;
        if(measureSuccess) {
            GlobalData.isOxyMeasurement = true;

            oxyHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    GlobalData.isOxyMeasurement = false;
                    if(!TextUtils.isEmpty(oxyData)) {
                        DataParser.getInstance().dataMeasurementParser(oxyData, context);
                    }
                    oxyData = "";
                    WriteToDevice.stopMeasuring(bluetoothGatt);
                }
            }, 40 * 1000);

            /*new CountDownTimer(40 * 1000, 3 * 1000) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    GlobalData.isOxyMeasurement = false;
                    if(!TextUtils.isEmpty(oxyData)) {
                        DataParser.getInstance().dataMeasurementParser(oxyData, context);
                    }
                    oxyData = "";
                    WriteToDevice.stopMeasuring(bluetoothGatt);
                }
            }.start();*/
            return true;

        }else {
            sendMeasureFailureBroadcast(Constants.OXY_REQ_FAIL);
            return false;
        }

    }

    private boolean refreshDeviceCache() {
        Log.i(TAG, "234 æ¸…ç©ºç³»ç»Ÿè“ç‰™ç¼“å­˜ BluetoothLeService refreshDeviceCache");
        if (bluetoothGatt != null) {
            try {
                Log.i(TAG,
                        "237 mBluetoothGattæœåŠ¡å™¨ä¸ä¸ºç©º BleService.refreshDeviceCache : mBluetoothGatt != null");
                BluetoothGatt localBluetoothGatt = bluetoothGatt;
                Method localMethod = localBluetoothGatt.getClass().getMethod(
                        "refresh", new Class[0]);
                if (localMethod != null) {
                    boolean bool = ((Boolean) localMethod.invoke(localBluetoothGatt, new Object[0])).booleanValue();
                    return bool;
                }
            } catch (Exception localException) {
                Log.i(TAG,
                        "102 åœ¨æ›´æ–°è®¾å¤‡çš„è¿‡ç¨‹ä¸­å‘ç”Ÿä¸å¯æ•èŽ·çš„å¼‚å¸¸ An exception occured while refreshing device");
                localException.printStackTrace();
            }
        }
        return false;
    }

    /*@Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (rssi >= -100 && device.getName() != null) {
            if (checkDeviceName(heloDeviceNames, device.getName())) {
                DeviceItem item = new DeviceItem();
                item.setDeviceMac(device.getAddress());
                item.setRssi(rssi);
                item.setDeviceName(device.getName());
//                    onDeviceFound(item);
                if (!mDevices.contains(item)) { // && allowedHeloDevices.contains(item.getDeviceMac())
                    mDevices.add(item);
                }
            }
        }
    }*/

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        DebugLogger.i(TAG, "onConnectionStateChange");
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            GlobalData.GET_BLE_NAME = gatt.getDevice().getName();
//            connectCallback.onGattObj(gatt);
            if (!GlobalData.status_Connected) {
                GlobalData.canSearchNewDevice = true;
                GlobalData.status_Connected = true;
                DebugLogger.i(TAG, "onConnectionStateChange STATE_CONNECTED");
                gatt.discoverServices();
//                getBleGatt.getBleGatt(gatt);
//                GlobalData.status_Connected = true;
                String mac = gatt.getDevice().getAddress();
                if(gatt.getDevice().getName().equals(Constants.DEVICE_SUBNAME6)) {
                    GlobalData.isLXPLUS = true;
                } else {
                    GlobalData.isLXPLUS = false;
                }
                SafePreferences.getInstance().saveStringToSecurePref(ConstantsImp.MAC_PREF, mac);
                Intent intent = new Intent();
                intent.setAction(ConstantsImp.BROADCAST_ACTION_HELO_CONNECTED);
                intent.putExtra(ConstantsImp.INTENT_KEY_MAC, mac);
                context.sendBroadcast(intent);
                // 04-12-2021 [start]

                // checkCompatible();
                GlobalData.isCompatible = true;

                // LocationValues.getInstance(context).registerLocation();

                // 04-12-2021 [end]
                //bleCallback.onGattConnected();
            }


        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            GlobalData.isLXPLUS = false;
            GlobalData.canSearchNewDevice = true;
            GlobalData.status_Connected = false;
            GlobalData.VERSION_FIRM = -1;
            GlobalData.GET_BLE_NAME = "";
            DebugLogger.i(TAG, "onConnectionStateChange STATE_DISCONNECTED");
//            GlobalData.status_Connected = false;
            refreshDeviceCache();
//            LocationValues.getInstance(context).unRegisterLocation();
            if (bluetoothGatt != null)
                bluetoothGatt.close();
            bluetoothGatt = null;
            String savedBleMac = "";
            try {
                savedBleMac = SafePreferences.getInstance().getStringFromSecurePref(ConstantsImp.PREF_MAC, "");
            } catch (Exception e) {
                e.printStackTrace();
            }

            sendBroadcast(ConstantsImp.BROADCAST_ACTION_HELO_DISCONNECTED);
            if (!savedBleMac.equals("")) {
                startScan();
            }

        }/*else if(newState==BluetoothProfile.STATE_CONNECTING){
            GlobalData.Connection_status="Connecting";
            GlobalData.status_Connected = false;
        }else if(newState==BluetoothProfile.STATE_DISCONNECTING){
            GlobalData.Connection_status="Disconnecting";
            GlobalData.status_Connected = false;
        }*/
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        DebugLogger.i(TAG, "onServicesDiscovered");

        if (status == BluetoothGatt.GATT_SUCCESS) {
            BluetoothGattService service = gatt.getService(UUID.fromString("1aabcdef-1111-2222-0000-facebeadaaaa"));
            if (service == null) {
                disconnect();
                return;
            }
            BluetoothGattCharacteristic mOneCharacteristic = service.getCharacteristic(UUID.fromString("facebead-ffff-eeee-0010-facebeadaaaa"));
            if (mOneCharacteristic == null) { // try to disconnect gatt
                disconnect();
                return;
            }
            BluetoothGattCharacteristic mTwoCharacteristic = service.getCharacteristic(UUID.fromString("facebead-ffff-eeee-0020-facebeadaaaa"));
            if (mOneCharacteristic == null) { // try to disconnect gatt
                disconnect();
                return;
            }
            if (mTwoCharacteristic == null) { // try to disconnect gatt
                disconnect();
                return;
            }

            setCharacteristicNotification("0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0001-facebeadaaaa", true);
            setCharacteristicNotification("0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0003-facebeadaaaa", true);
//            setCharacteristicNotification("0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0003-facebeadaaaa", true);
            setCharacteristicNotification("0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0005-facebeadaaaa", true);
            setCharacteristicNotification("0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0006-facebeadaaaa", true);
            setCharacteristicNotification("1aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0010-facebeadaaaa", true); //pairing notification
            setCharacteristicNotification("2aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0100-facebeadaaaa", true);
            setCharacteristicNotification("2aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0200-facebeadaaaa", true);

            setCharacteristicNotification("0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", true);
            setCharacteristicNotification("0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0004-facebeadaaaa", true);
            setCharacteristicNotification("1aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0020-facebeadaaaa", true); //pairing notification


            WriteToDevice.initDeviceLoadCode(bluetoothGatt);


        } else {
            disconnect();
        }

    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        byte[] data = characteristic.getValue();
        final StringBuilder stringBuilder = new StringBuilder(data.length);
        for (byte byteChar : data) {
            stringBuilder.append(String.format("%02X ", byteChar));
        }

        DebugLogger.i("onCharacteristicWrite", characteristic.getUuid().toString() + "  " + characteristic.getService().getUuid().toString() + " " + stringBuilder.toString() + " " + status);
        callNextWrite(data);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) { //TODO
        DebugLogger.i(TAG, "onCharacteristicChanged");
        byte[] data = characteristic.getValue();

        final StringBuilder stringBuilder = new StringBuilder(data.length);

        for (byte byteChar : data) {
            stringBuilder.append(String.format("%02X ", byteChar));
        }
//        if(data[3] == (byte) 0x36) {
        if(String.format("%02X ", data[3]).equals("26")) {
            DebugLogger.i(TAG+"btn", "Button command");
            //DataParser.getInstance().dataMeasurementParser(stringBuilder.toString(), context);
        }
        String charactUUID = characteristic.getUuid().toString();
        String serviceUUID = characteristic.getService().getUuid().toString();
        DebugLogger.i("oncharchanged", characteristic.getUuid().toString() + "  " + characteristic.getService().getUuid().toString() + " " + stringBuilder.toString());
        DebugLogger.i("Loggerr ", "oncharchanged " + characteristic.getUuid().toString() + "  " + characteristic.getService().getUuid().toString() + " " + stringBuilder.toString());
        if (serviceUUID.equals("2aabcdef-1111-2222-0000-facebeadaaaa")) { // åˆ¤æ–­æ˜¯å¦æ˜¯å›ºä»¶æ›´æ–°,ç¦»çº¿æ•°æ®,æ–°ç¡çœ çš„å‘½ä»¤
//            String a;
//            DebugLogger.i(TAG, "sleepdate received-----> "+ stringBuilder.toString());
            // åˆ¤æ–­æ˜¯å¦æ˜¯æ–°ç¡çœ 
//             isNewSleep(stringBuilder.toString());

            sendUpdataFirmInfo(context,  stringBuilder.toString());
        } else if (serviceUUID.equals("0aabcdef-1111-2222-0000-facebeadaaaa") && charactUUID.equals("facebead-ffff-eeee-0004-facebeadaaaa")) {

            sendDataBroadcast(stringBuilder.toString());
        } else if (serviceUUID.equals("0aabcdef-1111-2222-0000-facebeadaaaa") && charactUUID.equals("facebead-ffff-eeee-0005-facebeadaaaa")) {
            DebugLogger.i(TAG+"ppg", stringBuilder.toString());
            DataParser.getInstance().sendPwDataAll(context, stringBuilder.toString());
        } else if (serviceUUID.equals("1aabcdef-1111-2222-0000-facebeadaaaa")) {
            sendBindBroadcast(stringBuilder.toString()); // ç»‘å®šé€šé“

        } else if(serviceUUID.equals("0aabcdef-1111-2222-0000-facebeadaaaa") && charactUUID.equals("facebead-ffff-eeee-0006-facebeadaaaa")) { // && GlobalData.isOxyMeasurement
            sendIRandNIR(stringBuilder.toString());

        }

        //Added By Abhijeet

        else if (serviceUUID.equals("0aabcdef-1111-2222-0000-facebeadaaaa")&& charactUUID.equals("facebead-ffff-eeee-0001-facebeadaaaa")) {
            sendUVDataBroadcastOrHumanDetect(stringBuilder.toString()); // UV
        }
        //Abhijeet Changes
       /* else if ( serviceUUID.equals("0aabcdef-1111-2222-0000-facebeadaaaa") && (charactUUID.equals("facebead-ffff-eeee-0002-facebeadaaaa") || charactUUID.equals("facebead-ffff-eeee-0001-facebeadaaaa"))) {
            //sendDynamicStepsHeartRate(stringBuilder.toString());
            sendDataBroadcast(stringBuilder.toString());
        }*/
        else {
            sendDataBroadcast(stringBuilder.toString());//TODO
        }
        //connectCallback.onGattBinded();

    }

    //Added BY Abhijeet

    /**
     * sendUVDataBroadcast
     */
    private void sendUVDataBroadcastOrHumanDetect(String data) {
        //12 34 0B 4C 09 01 95 90 7F 00 ED 90 7F 00 01 04 00 00 43 21
        String dataType = data.substring(9, 11);
        if (dataType.equals("35")) {//krishna changes
            String uvValue = parseNewUVSingeData(data);
            Log.e(TAG, "~~~~~~UVValue~~~~~~~~~~~~~ = " + data);
            Log.e(TAG, "~~~~~~UVParsedValue~~~~~~~~~~~~~ = " + uvValue);
            //broadcastUpdate(GlobalData.ACTION_MAIN_DATA_UV, uvValue);
            //broadcastUpdate(ConstantsImp.BROADCAST_ACTION_UV_MEASUREMENT, uvValue);
            sendDataBroadcast(data);

        } else if (dataType.equals("4C")) {//sinco changes
            Log.e("chang", "~~~~~~4c~~~~~~~~~~~~~ = " + data);
            String calibrationResult = data.substring(15, 17);
//            12340B4C09013D907F009E917F005B 03 00 00 43 21
            String leaveHand = data.substring(30, 32) + data.substring(33, 35) + data.substring(36, 38) + data.substring(39, 41);
            String hand = data.substring(18, 20) + data.substring(21, 23) + data.substring(24, 26) + data.substring(27, 29);
            if (calibrationResult.equals("01")) {//current calibration value
                if(isSkin != null && isSkin) {
                    WriteToDevice.openHumanDetect(0x03, hand, "00000000", bluetoothGatt);
                } else if(isSkin != null && !isSkin) {
                    WriteToDevice.openHumanDetect(0x04, "00000000", leaveHand, bluetoothGatt);
                }

                Log.e(TAG, data + " hand:" + hand + " leaveHand: " + leaveHand);
                isSkin = null;
            } else if (calibrationResult.equals("02")) {//calibration success
                broadcastUpdate(ConstantsImp.ACTION_HUMAN_DETECT_SUCCESS);
            } else if (calibrationResult.equals("03")) {//calibration fail
                broadcastUpdate(ConstantsImp.ACTION_HUMAN_DETECT_FAIL);
            } else if (calibrationResult.equals("00")) {// no calibration
//                long leaveHandTag = Integer.parseInt(leaveHand, 16);
//                long handTag = Integer.parseInt(hand, 16);
                //LoggingManager.ble().log("human detection unlabeled:" + data);
                if ("FFFFFFFF".equals(leaveHand) || "FFFFFFFF".equals(hand)) {//not tagged
                    broadcastUpdate(ConstantsImp.ACTION_HUMAN_DETECT_NOTAG);
                }
            }


        } else if(dataType.equals("31")) {

            if (GlobalData.isDynamic) {
                sendDynamicStepseasurement(data);
            } else {
                DataParser.getInstance().dataMeasurementParser(data, context);
            }

        }
        /*if (string.length() != 0) {
            broadcastUpdate(GlobalData.ACTION_MAIN_DATA_UV, string);
		}*/
    }

    private String parseNewUVSingeData(String data) {
        try {
            String dataStr;
//        12 34 0B 35  05  32 F6 37 5A 00 FE 01 00 00  43 21
            String parsedString = "";
            // 12340b3608e8bf135a60000000bd0200004321
            //String data1="12340b350552c4135a00c80100004321";
            parsedString = data.substring(27, 29);
            Log.i(TAG, parsedString);
            int dataf = Integer.parseInt(parsedString, 16);
            return dataf + "";
        } catch (Exception e) {
            Log.e(TAG, "parseNewUVSingeData: " + e);
            return "";
        }
    }

    private void broadcastUpdate(String action, long dataf) {
        final Intent intent = new Intent(action);
        intent.putExtra(action, dataf);
        //sendBroadcast(intent);
        BroadcastHelper.sendSysBroadcast(context,intent);
    }

    /**
     * int value
     */

    private void broadcastUpdate(String action, int dataf) {
        final Intent intent = new Intent(action);
        intent.putExtra(action, dataf);
        //sendBroadcast(intent);
        BroadcastHelper.sendSysBroadcast(context,intent);
    }

    /**
     * å‘å¤–å‘é€å¹¿æ’­
     */
    private void broadcastUpdate(String action, String dataf) {
        final Intent intent = new Intent(action);
        intent.putExtra(action, dataf);
        //sendBroadcast(intent);
        BroadcastHelper.sendSysBroadcast(context,intent);
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        Log.d(TAG, "142 å‘é€çŠ¶æ€å˜åŒ–ä¿¡æ¯å¹¿æ’­ broadcastUpdateï¼š\n 142 åŠ¨ä½œæ˜¯: action = "
                + action);
        //sendBroadcast(intent);
        BroadcastHelper.sendSysBroadcast(context,intent);
    }

    //--------------------------//


    private void sendIRandNIR(String irNirData) {
        DebugLogger.i("IrNir", irNirData);
        DataParser.getInstance().parseOxyIRNIR(context, irNirData);
    }

    @Override
    public void onScanStarted() {
        scanCallBack.onScanStarted();
    }

    @Override
    public void onScanFinished() {
        scanCallBack.onScanFinished();
        connectNearestDevice();
        /*if(!GlobalData.isManualStop) {
            connectNearestDevice();
        } else {
            GlobalData.isManualStop = false;
            GlobalData.canSearchNewDevice = true;
        }*/
    }

    @Override
    public void onLedeviceFound(BluetoothDevice device, int rssi) {
        if (rssi >= -100 && device.getName() != null) {
//            if (checkDeviceName(heloDeviceNames, device.getName())) {
            if (heloDeviceNames.contains(device.getName())) {
                DeviceItem item = new DeviceItem();
                item.setDeviceMac(device.getAddress());
                item.setRssi(rssi);
                item.setDeviceName(device.getName());
//                    onDeviceFound(item);
                if (!mDevices.contains(item)) { //&& ValidHelodeviceCheckHelper.checkValidHelo(item.getDeviceMac())
                    mDevices.add(item);
                }
            }
        }
    }



    public boolean skinCalibration(boolean isSkin) {
        if(WriteToDevice.openHumanDetect(0x01, "00000000", "00000000", bluetoothGatt)) {

            if(WriteToDevice.openHumanDetect(0x02, "00000000", "00000000", bluetoothGatt)) {
                this.isSkin = isSkin;
                return true;
            } else {
                return false;
            }
        } else {
            sendMeasureFailureBroadcast(Constants.SKIN_CALIB_REQ_FAIL);
            return false;
        }
    }

    public boolean accelerometer(boolean isStart) {

        if(WriteToDevice.accelerometer(bluetoothGatt, isStart ? 1 : 0)) {


            return true;

        } else {
            sendMeasureFailureBroadcast(Constants.SKIN_CALIB_REQ_FAIL);
            return false;
        }
    }

    public boolean scheduleMeasure(int mins) {
        if(mins < 30) {
            sendMeasureFailureBroadcast(Constants.SCHEDULE_MEASURE_LESS_30);
            return false;
        } else if(mins > 120) {
            sendMeasureFailureBroadcast(Constants.SCHEDULE_MEASURE_GRT_120);
            return false;
        }
        return  WriteToDevice.scheduledMeasure(bluetoothGatt, mins);
    }


    private void checkCompatible() {

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


                if(response.code() == 200) {
                    DebugLogger.i("COMPAPI", "true");
                    GlobalData.isCompatible = true;
                    SafePreferences.getInstance().saveBooleanToSecurePref(ConstantsImp.compatabilty_pref, true);
                    setScheduleCheck(compatJobId, 24 * 60 * 60 * 1000, ScheduleJobService.class.getName());
                } else {
                    DebugLogger.i("COMPAPI", "false");
                    GlobalData.isCompatible = false;
                    SafePreferences.getInstance().saveBooleanToSecurePref(ConstantsImp.compatabilty_pref, false);
                }
                /*if(response.isSuccessful() && compatibilityResponse != null) {
                    DebugLogger.i("APITEST", compatibilityResponse.getStatusMessage());
                } else {
                    ResponseBody responseBody = response.errorBody();
                    DebugLogger.i("APITEST", "Error "+responseBody.toString());
                }*/
            }

            @Override
            public void onFailure(Call<CompatibilityResponse> call, Throwable throwable) {
                DebugLogger.i("COMPAPI", "error "+throwable.getMessage());
            }
        });
    }

    private void setScheduleCheck(int jobId, long interval, String className) {

        jobScheduler = (JobScheduler) context.getSystemService(context.JOB_SCHEDULER_SERVICE);
        ComponentName jobservice = new ComponentName(context.getPackageName(), className);
        JobInfo jobInfo = new JobInfo.Builder(jobId, jobservice).setPeriodic(interval).setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).build(); //24 * 60 * 60 * 1000
        if(jobScheduler.getAllPendingJobs().size() == 0) {
            int jobidRes = jobScheduler.schedule(jobInfo);
            if(jobidRes > 0) {
                DebugLogger.i("JOBSER", "job set "+jobidRes+" "+jobId+" "+className+" "+interval);
            }
        }

    }


    public void setScheduleMd5Check() {
        setScheduleCheck(md5JobId, 1000, ScheduledMD5ApiCall.class.getName()) ;
    }

    //app key 151790367725180058
    //md5 d954bbfd719e2680504353b8ab2be375
    public void checkMD5() {
        String app_key = "";
        String md5 =getMD5Checksum();
        try {
            app_key = SafePreferences.getInstance().getStringFromSecurePref(ConstantsImp.AP_KEY_PREF, "");
        } catch (Exception e) {
            e.printStackTrace();
        }

       /* app_key = "150772884640619364";
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
//                        GlobalData.macDevices = apiResponse.getHelodevicedev().getMacdevicekey();
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
//        setScheduleCheck(md5JobId, 12 * 60 * 60 * 1000);

    }



    public String getMD5Checksum(){
        String path = getApkPath(context);
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



    public boolean isLXplus() {

        if(GlobalData.GET_BLE_NAME.equals(Constants.DEVICE_SUBNAME6)) {
            return true;
        } else {
            sendMeasureFailureBroadcast(Constants.LX_PLUS_MEASUREMENT);
            return false;
        }
    }

    private String getPPGFileName() {
        String format = "";
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
        format = sdf.format(new Date());

        return format;
    }


}