package com.worldgn.connector;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by WGN on 15-09-2017.
 */

class GlobalData {

    public static boolean isDynamic = false;
    public static boolean stepsMeasure = false;
    public static boolean isUVMEasurement = false;
    public static boolean isOxyMeasurement = false;
    public static boolean isBpPPGMeasurement = false;
    public static String app_key = "";
    /** 固件版本号 */
    public static int VERSION_FIRM = 0;
    /** 5 蓝牙连接状态 */
    public static boolean status_Connected = false;
    public static boolean isCompatible = false;
    /**
     * 是否接收到了匹配响应
     */
    public static boolean isMatchInfo = false;
    /**连接的设备名字*/
    public static String  GET_BLE_NAME="";
    public static boolean canSearchNewDevice = true;
//    public static boolean isManualStop = false;


    public static String isDev = ConstantsImp.DEV_NOT_CALLED;
    public static List<String> macDevices = new ArrayList<>();
    public static boolean isLXPLUS = false;

    public static String greenPpgFile = "";
    public static String redPpgFile = "";
    public static String irPpgFile = "";


}
