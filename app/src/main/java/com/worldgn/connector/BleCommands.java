package com.worldgn.connector;

import android.text.format.Time;



import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Krishna Rao on 18/09/2017.
 */

public class BleCommands {

    private final static String TAG = BleCommands.class.getSimpleName();

    public static byte[] getHeartRate() {
        byte[] byte_hr = new byte[15];
        byte_hr[0] = (byte) 0x43;
        byte_hr[1] = (byte) 0x21;
        return byte_hr;
    }

    public static byte[] getSecondMach(int cmd) {
        DebugLogger.i(TAG, "secondMach");
        byte[] bytes = new byte[12];
        bytes[0] = (byte) 0x12;
        bytes[1] = (byte) 0x34;
        bytes[2] = (byte) 0x0B;
        bytes[3] = (byte) 0x1b;//cmd
        bytes[4] = (byte) 0x01;//length
        bytes[5] = (byte) cmd;//data
        int chk = 0x0b + 0x1b + 0x01 + cmd;
        DebugLogger.i(TAG, "CHKSUM==" + (chk));
        byte[] chksum = new byte[4];
        chksum = intToBytes(chk);
        bytes[6] = chksum[0];
        bytes[7] = chksum[1];
        bytes[8] = chksum[2];
        bytes[9] = chksum[3];
        bytes[10] = (byte) 0x43;
        bytes[11] = (byte) 0x21;
        return bytes;
    }

    public static byte[] getAckForBindRequest(int status) {
        byte[] bytes = new byte[12];
        bytes[0] = (byte) 0x12;
        bytes[1] = (byte) 0x34;
        bytes[2] = (byte) 0x0b;// type
        bytes[3] = (byte) 0x13;// cmd
        bytes[4] = (byte) 0x01;// length
        bytes[5] = (byte) status;
        int a = Integer.parseInt("0b", 16) + Integer.parseInt("13", 16) + Integer.parseInt("01", 16);
        DebugLogger.d(TAG, "CHKSUM==" + (a + status));
        byte[] chksum = new byte[4];
        chksum = intToBytes(a + status);
        bytes[6] = chksum[0];
        bytes[7] = chksum[1];
        bytes[8] = chksum[2];
        bytes[9] = chksum[3];
        bytes[10] = (byte) 0x43;
        bytes[11] = (byte) 0x21;
        DebugLogger.i(TAG, "绑定设备 响应信息 = " + bytesToInt(chksum, 0));
        return bytes;
    }

    public static byte[] getMatchInfo(int userid) {
        DebugLogger.i(TAG, "matchInfo userid");
        byte[] userid2byte = new byte[4];
        userid2byte = intToBytes(userid);
        byte[] bytes = new byte[15];
        bytes[0] = (byte) 0x12;
        bytes[1] = (byte) 0x34;
        bytes[2] = (byte) 0x0b;// type
        bytes[3] = (byte) 0x11;// cmd
        bytes[4] = (byte) 0x04;// length
        bytes[5] = userid2byte[0];
        bytes[6] = userid2byte[1];
        bytes[7] = userid2byte[2];
        bytes[8] = userid2byte[3];
        int a = Integer.parseInt("0b", 16) + Integer.parseInt("11", 16) + Integer.parseInt("04", 16);
        DebugLogger.v(TAG, "matchInfo CHKSUM = " + (a + userid));
        byte[] chksum = new byte[4];
        chksum = intToBytes(a + userid);
        bytes[9] = chksum[0];
        bytes[10] = chksum[1];
        bytes[11] = chksum[2];
        bytes[12] = chksum[3];
        bytes[13] = (byte) 0x43;
        bytes[14] = (byte) 0x21;
        DebugLogger.v(TAG, "匹配信息: " + bytesToInt(chksum, 0));
        return bytes;
    }

    //prevent return -1 in this getMatch if mac id is null
    public static byte[] getMatchInfo(String mac) {
        DebugLogger.i(TAG, "matchInfo mac " + mac);
        String[] macIds = mac.split(":");
        DebugLogger.i("sqs", "发送MAC匹配信息");
        @SuppressWarnings("unused")
        int s1, s2;
        int s3, s4, s5, s6;
        s1 = Integer.parseInt(macIds[0], 16);
        s2 = Integer.parseInt(macIds[1], 16);
        s3 = Integer.parseInt(macIds[2], 16);
        s4 = Integer.parseInt(macIds[3], 16);
        s5 = Integer.parseInt(macIds[4], 16);
        s6 = Integer.parseInt(macIds[5], 16);
        byte[] byte_info = new byte[15];
        byte_info[0] = (byte) 0x12;
        byte_info[1] = (byte) 0x34;
        byte_info[2] = (byte) 0x0b;// type
        byte_info[3] = (byte) 0x11;// cmd
        byte_info[4] = (byte) 0x04;// length
        byte_info[5] = (byte) s3;
        byte_info[6] = (byte) s4;
        byte_info[7] = (byte) s5;
        byte_info[8] = (byte) s6;
        int a = Integer.parseInt("0b", 16) + Integer.parseInt("11", 16) + Integer.parseInt("04", 16);
        DebugLogger.v(TAG, "CHKSUM = " + (a + s5 + s6 + s3 + s4));
        byte[] chksum = new byte[4];
        chksum = intToBytes(a + s5 + s6 + s3 + s4);
        byte_info[9] = chksum[0];
        byte_info[10] = chksum[1];
        byte_info[11] = chksum[2];
        byte_info[12] = chksum[3];
        byte_info[13] = (byte) 0x43;
        byte_info[14] = (byte) 0x21;
        DebugLogger.i(TAG, "发送MAC匹配信息  = " + bytesToInt(chksum, 0) + "\nbyte_info = " + bytesToHexString(byte_info));
        return byte_info;
    }

    public static byte[] getStartSendDecryToken(int dataLength) {
        DebugLogger.i(TAG, "startSendDecryToken ");
        byte[] vers = new byte[4];
        vers = intToBytes(dataLength);
        String vers16 = bytesToHexString(vers);
        byte[] bb = new byte[15];
        bb[0] = (byte) 0x12;
        bb[1] = (byte) 0x34;
        bb[2] = (byte) 0x0B;
        bb[3] = (byte) 0x1d;
        bb[4] = (byte) 0x04;
        bb[5] = (byte) vers[0];
        bb[6] = (byte) vers[1];
        bb[7] = (byte) vers[2];
        bb[8] = (byte) vers[3];
        int chk = 0x0B + 0x1d + 0x04 + Integer.parseInt(vers16.substring(0, 2), 16) +
                Integer.parseInt(vers16.substring(2, 4), 16) +
                Integer.parseInt(vers16.substring(4, 6), 16) + Integer.parseInt(vers16.substring(6), 16);
        byte[] chksum = new byte[4];
        chksum = intToBytes(chk);
        bb[9] = chksum[0];
        bb[10] = chksum[1];
        bb[11] = chksum[2];
        bb[12] = chksum[3];
        bb[13] = (byte) 0x43;
        bb[14] = (byte) 0x21;
        return bb;
    }

    public static byte[] getSendDecryTokenContent(byte[] eightStr) {
        DebugLogger.i(TAG, "sendDecryTokenContent");
        byte[] hexStringToBytes = new byte[8];
        hexStringToBytes = eightStr;
        String bytesToHexString = bytesToHexString(hexStringToBytes);
        byte[] bb = new byte[19];
        bb[0] = (byte) 0x12;
        bb[1] = (byte) 0x34;
        bb[2] = (byte) 0x0B;
        bb[3] = (byte) 0x1E;
        bb[4] = (byte) 0x08;
        for (int i = 0; i < 8; i++) {
            bb[i + 5] = (byte) hexStringToBytes[i];
        }
        int chk = 0x0B + 0x1E + 0x08
                + Integer.parseInt(bytesToHexString.substring(0, 2), 16)
                + Integer.parseInt(bytesToHexString.substring(2, 4), 16)
                + Integer.parseInt(bytesToHexString.substring(4, 6), 16)
                + Integer.parseInt(bytesToHexString.substring(6, 8), 16)
                + Integer.parseInt(bytesToHexString.substring(8, 10), 16)
                + Integer.parseInt(bytesToHexString.substring(10, 12), 16)
                + Integer.parseInt(bytesToHexString.substring(12, 14), 16)
                + Integer.parseInt(bytesToHexString.substring(14, 16), 16);
        byte[] chksum = new byte[4];
        chksum = intToBytes(chk);
        bb[13] = chksum[0];
        bb[14] = chksum[1];
        bb[15] = chksum[2];
        bb[16] = chksum[3];
        bb[17] = (byte) 0x43;
        bb[18] = (byte) 0x21;

        return bb;
    }

    public static void getSendDecryTokenContent1(byte[] eightByte) {
        DebugLogger.i(TAG, "sendDecryTokenContent1");
        for (int i = 0; i < eightByte.length / 8; i++) {
            byte[] aa = new byte[8];
            for (int j = 0; j < 8; j++) {
                aa[j] = eightByte[i * 8 + j];
            }
            getSendDecryTokenContent(aa);
        }
    }

    public static byte[] getUpdateNewTime() {
        DebugLogger.i(TAG, "UpdateNewTime offSetTimeZone = " + Integer.parseInt(offSetTimeZone()));
        String times = getNowTime();
        DebugLogger.v(TAG, "开始同步时间 getNowTime = " + times);
        DebugLogger.i("sqs", "开始同步时间 getNowTime = " + times);
        byte[] bytes = new byte[15];
        bytes[0] = (byte) 0x12;
        bytes[1] = (byte) 0x34;
        bytes[2] = (byte) 0x0b;
        bytes[3] = (byte) 0x12;
        bytes[4] = (byte) 0x04;
        bytes[5] = (byte) Integer.parseInt(times.substring(6, 8), 16);
        bytes[6] = (byte) Integer.parseInt(times.substring(4, 6), 16);
        bytes[7] = (byte) Integer.parseInt(times.substring(2, 4), 16);
        bytes[8] = (byte) Integer.parseInt(times.substring(0, 2), 16);
        bytes[9] = (byte) 0xa4;
        bytes[10] = (byte) 0x01;
        int b = Integer.parseInt(times.substring(6, 8), 16)
                + Integer.parseInt(times.substring(4, 6), 16)
                + Integer.parseInt(times.substring(2, 4), 16)
                + Integer.parseInt(times.substring(0, 2), 16)
                + Integer.parseInt("0b", 16)
                + Integer.parseInt("12", 16)
                + Integer.parseInt("04", 16);
        if (Integer.toHexString(b).length() == 0) {
            bytes[9] = (byte) 0x00;
            bytes[10] = (byte) 0x00;
        } else if (Integer.toHexString(b).length() == 1) {
            bytes[9] = (byte) Integer.parseInt(Integer.toHexString(b), 16);
            bytes[10] = (byte) 0x00;
        } else if (Integer.toHexString(b).length() == 2) {
            bytes[9] = (byte) Integer.parseInt(Integer.toHexString(b), 16);
            bytes[10] = (byte) 0x00;
        } else if (Integer.toHexString(b).length() == 3) {
            String temp = "0" + Integer.toHexString(b).toString();
            bytes[10] = (byte) Integer.parseInt(temp.substring(0, 2), 16);
            bytes[9] = (byte) Integer.parseInt(temp.substring(2, 4), 16);
            DebugLogger.v(TAG, "开始同步时间 bytes[10]= " + bytes[10] + " bytes[9]= " + bytes[9]);
        } else if (Integer.toHexString(b).length() == 4) {
            String temp = Integer.toHexString(b).toString();
            bytes[9] = (byte) Integer.parseInt(temp.substring(0, 2), 16);
            bytes[10] = (byte) Integer.parseInt(temp.substring(2, 4), 16);
        }
        bytes[11] = (byte) 0x00;
        bytes[12] = (byte) 0x00;
        bytes[13] = (byte) 0x43;
        bytes[14] = (byte) 0x21;

        return bytes;
    }

    public static byte[] getAppVersion(String version) {
        DebugLogger.i(TAG, "APPVersion " + version);
        byte[] vers = new byte[4];
        vers = intToBytes(Integer.parseInt(version));
        String vers16 = bytesToHexString(vers);
        byte[] bytes = new byte[15];
        bytes[0] = (byte) 0x12;
        bytes[1] = (byte) 0x34;
        bytes[2] = (byte) 0x0B;
        bytes[3] = (byte) 0x1c;//cmd
        bytes[4] = (byte) 0x04;//lenght

        bytes[5] = (byte) vers[0];//data
        bytes[6] = (byte) vers[1];
        bytes[7] = (byte) vers[2];
        bytes[8] = (byte) 0x00;
        int chk = 0x0b + 0x1c + 0x04 + Integer.parseInt(vers16.substring(0, 2), 16) +
                Integer.parseInt(vers16.substring(2, 4), 16) +
                Integer.parseInt(vers16.substring(4, 6), 16) + Integer.parseInt(vers16.substring(6), 16);
        DebugLogger.i(TAG, "CHKSUM==" + (chk));
        byte[] chksum = new byte[4];
        chksum = intToBytes(chk);
        bytes[9] = chksum[0];
        bytes[10] = chksum[1];
        bytes[11] = chksum[2];
        bytes[12] = chksum[3];

        bytes[13] = (byte) 0x43;
        bytes[14] = (byte) 0x21;
        int count = 0;
        boolean writeStatus = false;
        DebugLogger.i(TAG, "bytes==" + bytesToHexString(bytes));
        DebugLogger.i(TAG, "bytes==" + bytesToHexString(bytes));

        return bytes;
    }

    public static byte[] getInitDeviceLoadCode() {
        DebugLogger.i(TAG, "initDeviceLoadCode");
        byte[] bytes = new byte[15];
        bytes[0] = (byte) 0x12;
        bytes[1] = (byte) 0x34;
        bytes[2] = (byte) 0x0B;
        bytes[3] = (byte) 0x1a;//cmd
        bytes[4] = (byte) 0x04;//lenght

        bytes[5] = (byte) 0x01;//data
        bytes[6] = (byte) 0x00;
        bytes[7] = (byte) 0x00;
        bytes[8] = (byte) 0x00;
        int chk = 0x0b + 0x1a + 0x04 + 0x01;
        DebugLogger.i(TAG, "CHKSUM==" + (chk));
        byte[] chksum = new byte[4];
        chksum = intToBytes(chk);
        bytes[9] = chksum[0];
        bytes[10] = chksum[1];
        bytes[11] = chksum[2];
        bytes[12] = chksum[3];

        bytes[13] = (byte) 0x43;
        bytes[14] = (byte) 0x21;

        return bytes;
    }

    public static byte[] getSendStandardBP(int sys, int dis) {
        DebugLogger.i(TAG, "发送血压标定值");
        DebugLogger.i(TAG + "VIJ", "sendStandardBP");
        // <12340b12 048afde6 56e40200 004321>56E43613
        byte[] sys2byte = new byte[4];
        byte[] dis2byte = new byte[4];
        sys2byte = intToBytes(sys);
        dis2byte = intToBytes(dis);
        byte[] bytes = new byte[19];
        bytes[0] = (byte) 0x12;
        bytes[1] = (byte) 0x34;
        bytes[2] = (byte) 0x0b;// type
        bytes[3] = (byte) 0x19;// cmd
        bytes[4] = (byte) 0x08;// length
        bytes[5] = sys2byte[0];
        bytes[6] = sys2byte[1];
        bytes[7] = sys2byte[2];
        bytes[8] = sys2byte[3];

        bytes[9] = dis2byte[0];
        bytes[10] = dis2byte[1];
        bytes[11] = dis2byte[2];
        bytes[12] = dis2byte[3];
        int a = Integer.parseInt("0b", 16) + Integer.parseInt("19", 16) + Integer.parseInt("08", 16);

        DebugLogger.v(TAG, "CHKSUM = " + (a + sys + dis));
        byte[] chksum = new byte[4];
        chksum = intToBytes(a + sys + dis);
        bytes[13] = chksum[0];
        bytes[14] = chksum[1];
        bytes[15] = chksum[2];
        bytes[16] = chksum[3];
        bytes[17] = (byte) 0x43;
        bytes[18] = (byte) 0x21;
        DebugLogger.d(TAG, "发送血压标定值 匹配信息 : " + bytesToInt(chksum, 0));

        return bytes;

    }

    public static byte[] getUnbindDevice() {
        DebugLogger.i(TAG, "解除对设备的绑定");
        DebugLogger.i(TAG, "unbindDevice");
        byte[] bytes = new byte[10];
        bytes[0] = (byte) 0x12;
        bytes[1] = (byte) 0x34;
        bytes[2] = (byte) 0x0a;// type
        bytes[3] = (byte) 0x09;// cmd
        int a = Integer.parseInt("0a", 16) + Integer.parseInt("09", 16);

        //Log.d(TAG, "CHKSUM==" + a);
        byte[] chksum = new byte[4];
        chksum = intToBytes(a);
        bytes[4] = chksum[0];
        bytes[5] = chksum[1];
        bytes[6] = chksum[2];
        bytes[7] = chksum[3];
        bytes[8] = (byte) 0x43;
        bytes[9] = (byte) 0x21;

        DebugLogger.v(TAG, "解除绑定 响应信息" + bytesToInt(chksum, 0));

        return bytes;
    }

    public static byte[] getStopMeasuring() {
        DebugLogger.i(TAG, "停止当前测量，让设备关灯");
        DebugLogger.i(TAG, "stopMeasuring");
        byte[] bytes = new byte[10];
        bytes[0] = (byte) 0x12;
        bytes[1] = (byte) 0x34;
        bytes[2] = (byte) 0x0A;
        bytes[3] = (byte) 0x0F;
        bytes[4] = (byte) 0x19;
        bytes[5] = (byte) 0x00;
        bytes[6] = (byte) 0x00;
        bytes[7] = (byte) 0x00;
        bytes[8] = (byte) 0x43;
        bytes[9] = (byte) 0x21;

        return bytes;
    }

    public static byte[] getLetMeashineDown() {
        DebugLogger.i(TAG, "让设备关机指令");
        DebugLogger.i(TAG, "letMeashineDown");
        byte[] bytes = new byte[10];
        bytes[0] = (byte) 0x12;
        bytes[1] = (byte) 0x34;
        bytes[2] = (byte) 0x0A;
        bytes[3] = (byte) 0x0E;
        bytes[4] = (byte) 0x18;
        bytes[5] = (byte) 0x00;
        bytes[6] = (byte) 0x00;
        bytes[7] = (byte) 0x00;
        bytes[8] = (byte) 0x43;
        bytes[9] = (byte) 0x21;
        return bytes;
    }

    public static byte[] getMeasureHR() {
        DebugLogger.i(TAG, "心率测量指令");
        DebugLogger.i(TAG, "measureHr");
        byte[] bytes = new byte[10];
        bytes[0] = (byte) 0x12;
        bytes[1] = (byte) 0x34;
        bytes[2] = (byte) 0x0A;
        bytes[3] = (byte) 0x02;
        bytes[4] = (byte) 0x0C;
        bytes[5] = (byte) 0x00;
        bytes[6] = (byte) 0x00;
        bytes[7] = (byte) 0x00;
        bytes[8] = (byte) 0x43;
        bytes[9] = (byte) 0x21;
        return bytes;
    }

    public static byte[] getMeasureBP() {
        DebugLogger.i(TAG, "血压测量指令");
        DebugLogger.i(TAG, "measureBp");
        byte[] bytes = new byte[10];
        bytes[0] = (byte) 0x12;
        bytes[1] = (byte) 0x34;
        bytes[2] = (byte) 0x0A;
        bytes[3] = (byte) 0x0C;
        bytes[4] = (byte) 0x16;
        bytes[5] = (byte) 0x00;
        bytes[6] = (byte) 0x00;
        bytes[7] = (byte) 0x00;
        bytes[8] = (byte) 0x43;
        bytes[9] = (byte) 0x21;
        return bytes;
    }

    public static byte[] getBPReset() {
        DebugLogger.i(TAG, "血压测量指令");
        DebugLogger.i(TAG, "bpReset");
        byte[] bytes = new byte[10];
        bytes[0] = (byte) 0x12;
        bytes[1] = (byte) 0x34;
        bytes[2] = (byte) 0x0A;
        bytes[3] = (byte) 0x07;
        bytes[4] = (byte) 0x11;
        bytes[5] = (byte) 0x00;
        bytes[6] = (byte) 0x00;
        bytes[7] = (byte) 0x00;
        bytes[8] = (byte) 0x43;
        bytes[9] = (byte) 0x21;
        return bytes;
    }

    public static byte[] getMeasureECG() {
        DebugLogger.i(TAG, "ECG测量指令");
        DebugLogger.i(TAG, "measureECG");
        byte[] bytes = new byte[10];
        bytes[0] = (byte) 0x12;
        bytes[1] = (byte) 0x34;
        bytes[2] = (byte) 0x0A;
        bytes[3] = (byte) 0x0A;
        bytes[4] = (byte) 0x14;
        bytes[5] = (byte) 0x00;
        bytes[6] = (byte) 0x00;
        bytes[7] = (byte) 0x00;
        bytes[8] = (byte) 0x43;
        bytes[9] = (byte) 0x21;
        return bytes;
    }

    public static byte[] getMeasureBR() {
        DebugLogger.i(TAG, "呼吸频率测量指令");
        DebugLogger.i(TAG, "measureBr");
        byte[] bytes = new byte[10];
        bytes[0] = (byte) 0x12;
        bytes[1] = (byte) 0x34;
        bytes[2] = (byte) 0x0A;
        bytes[3] = (byte) 0x0B;
        bytes[4] = (byte) 0x15;
        bytes[5] = (byte) 0x00;
        bytes[6] = (byte) 0x00;
        bytes[7] = (byte) 0x00;
        bytes[8] = (byte) 0x43;
        bytes[9] = (byte) 0x21;
        return bytes;

    }

    public static byte[] getMeasureMF() {
        DebugLogger.i(TAG, "心情疲劳值测量指令");
        DebugLogger.i(TAG, "measureMF");
        byte[] bytes = new byte[10];
        bytes[0] = (byte) 0x12;
        bytes[1] = (byte) 0x34;
        bytes[2] = (byte) 0x0A;
        bytes[3] = (byte) 0x06;
        bytes[4] = (byte) 0x10;
        bytes[5] = (byte) 0x00;
        bytes[6] = (byte) 0x00;
        bytes[7] = (byte) 0x00;
        bytes[8] = (byte) 0x43;
        bytes[9] = (byte) 0x21;
        return bytes;
    }

    public static byte[] getMeasurePW_BB() {
        DebugLogger.i(TAG, "脉搏波测量指令");
        DebugLogger.i(TAG, "measurePW");
        byte[] bb, cc;
        bb = new byte[10];
        cc = new byte[10];
        cc[0] = bb[0] = (byte) 0x12;
        cc[1] = bb[1] = (byte) 0x34;
        cc[2] = bb[2] = (byte) 0x0A;

        bb[3] = (byte) 0x61;
        bb[4] = (byte) 0x6b;

        cc[3] = (byte) 0x03;
        cc[4] = (byte) 0x0d;

        cc[5] = bb[5] = (byte) 0x00;
        cc[6] = bb[6] = (byte) 0x00;
        cc[7] = bb[7] = (byte) 0x00;

        cc[8] = bb[8] = (byte) 0x43;
        cc[9] = bb[9] = (byte) 0x21;
        DebugLogger.e(TAG, "bbbbbbb" + bytesToHexString(bb));
        return bb;
    }

    public static byte[] getMeasurePW_CC() {
        DebugLogger.i(TAG, "脉搏波测量指令");
        DebugLogger.i(TAG, "measurePW");
        byte[] bb, cc;
        bb = new byte[10];
        cc = new byte[10];
        cc[0] = bb[0] = (byte) 0x12;
        cc[1] = bb[1] = (byte) 0x34;

        cc[2] = bb[2] = (byte) 0x0A;

        bb[3] = (byte) 0x61;
        bb[4] = (byte) 0x6b;

        cc[3] = (byte) 0x03;
        cc[4] = (byte) 0x0d;

        cc[5] = bb[5] = (byte) 0x00;
        cc[6] = bb[6] = (byte) 0x00;
        cc[7] = bb[7] = (byte) 0x00;

        cc[8] = bb[8] = (byte) 0x43;
        cc[9] = bb[9] = (byte) 0x21;
        DebugLogger.e(TAG, "ccccccc" + bytesToHexString(cc));
        return cc;
    }

    public static byte[] getSteps() {
        DebugLogger.i(TAG, "获取步数指令");
        DebugLogger.i(TAG, "getSteps");
        byte[] bytes = new byte[10];
        bytes[0] = (byte) 0x12;
        bytes[1] = (byte) 0x34;
        bytes[2] = (byte) 0x0A;
        bytes[3] = (byte) 0x01;
        bytes[4] = (byte) 0x0B;
        bytes[5] = (byte) 0x00;
        bytes[6] = (byte) 0x00;
        bytes[7] = (byte) 0x00;
        bytes[8] = (byte) 0x43;
        bytes[9] = (byte) 0x21;
        return bytes;
    }

    public static byte[] setLedLight(int data1, int data2) {
        DebugLogger.i("sqs", "发送设置led亮度");
        DebugLogger.i(TAG, "setLedLight");
        int chk = 0x0b + 0x61 + 0x02 + data1 + data2;
        byte[] chksum = new byte[4];
        chksum = intToBytes(chk);
        byte[] bytes = new byte[13];
        bytes[0] = (byte) 0x12;
        bytes[1] = (byte) 0x34;
        bytes[2] = (byte) 0x0B;
        bytes[3] = (byte) 0x61;
        bytes[4] = (byte) 0x02;
        bytes[5] = (byte) data1;
        bytes[6] = (byte) data2;
        bytes[7] = (byte) chksum[0];
        bytes[8] = (byte) chksum[1];
        bytes[9] = (byte) chksum[2];
        bytes[10] = (byte) chksum[3];
        bytes[11] = (byte) 0x43;
        bytes[12] = (byte) 0x21;
        return bytes;
    }

    public static byte[] sendReviseAutoTime() {
        DebugLogger.i(TAG, "sendReviseAutoTime");
        DebugLogger.i(TAG, "sendReviseAutoTime byte[] ");
        byte[] bb = new byte[12];
        bb[0] = (byte) 0x12;
        bb[1] = (byte) 0x34;
        bb[2] = (byte) 0x0B;
        bb[3] = (byte) 0x17;
        bb[4] = (byte) 0x01;
        bb[5] = (byte) (0xff);
        bb[6] = (byte) ( (bb[2] + bb[3] + bb[4] + bb[5])%255);
        bb[7] = (byte) 0x01;
        bb[8] = (byte) 0x00;
        bb[9] = (byte) 0x00;
        bb[10] = (byte) 0x43;
        bb[11] = (byte) 0x21;
        DebugLogger.i("min :", bb[5] + "");
        return bb;
    }

    private static String offSetTimeZone() {
        Calendar cal = Calendar.getInstance();
        TimeZone timeZone = cal.getTimeZone();
        String displayName = timeZone.getDisplayName();
        int offset = timeZone.getOffset(System.currentTimeMillis());
        int offsetHour = offset / (1000 * 60 * 60);
        return String.valueOf(offset);
    }

    private static String getNowTime() {
        Time mTime = new Time();
        mTime.setToNow();
//		long time = System.currentTimeMillis() / 1000 + 28800;
        long time = (System.currentTimeMillis() + Integer.parseInt(offSetTimeZone())) / 1000;
        DebugLogger.v(TAG, "开始同步时间 getNowTime = " + time);
        String times = Long.toHexString(time);
        return times;
    }

    private static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[3] = (byte) ((value >> 24) & 0xFF);
        src[2] = (byte) ((value >> 16) & 0xFF);
        src[1] = (byte) ((value >> 8) & 0xFF);
        src[0] = (byte) (value & 0xFF);
        return src;
    }

    private static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (src[offset] & 0xFF) | ((src[offset + 1] & 0xFF) << 8) | ((src[offset + 2] & 0xFF) << 16) | ((src[offset + 3] & 0xFF) << 24);
        return value;
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}
