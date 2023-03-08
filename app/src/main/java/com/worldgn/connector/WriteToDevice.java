package com.worldgn.connector;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.text.format.Time;
import android.util.Log;


import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;


/**
 * Created by WGN on 09-09-2017.
 */
//0x24 - sos request
class WriteToDevice {

    private final static String TAG = WriteToDevice.class.getSimpleName();
    private final static String RESULTTAG = "RESULTTAG";

    public static boolean heartRate(BluetoothGatt mBluetoothGatt) {

        //DebugLogger.i(TAG, "heartRate");
        //DebugLogger.i(TAG, "heartRate");
        byte[] byte_hr = new byte[15];
        int count = 0;
        byte_hr[0] = (byte) 0x43;
        byte_hr[1] = (byte) 0x21;
        boolean result = false;
        while (!result) {
            result = writeRXCharacteristic(mBluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0001-facebeadaaaa", byte_hr);
            if (GlobalData.status_Connected == false)
                return false;
            if (count > 20000) {
                break;
            } else {
                count++;
            }
        }

        //DebugLogger.i(TAG, "heartRate result " + result);
        //DebugLogger.i(TAG, "heartRate result " + result);
        return result ;
    }


    public static int secondMach(int cmd, BluetoothGatt mBluetoothGatt) {
        //DebugLogger.i(TAG, "secondMach  "+cmd);
        //Timber.tag(TAG);
        byte[] bytes = new byte[12];
        bytes[0] = (byte) 0x12;
        bytes[1] = (byte) 0x34;
        bytes[2] = (byte) 0x0B;
        bytes[3] = (byte) 0x1b;//cmd
        bytes[4] = (byte) 0x01;//lenght

        bytes[5] = (byte) cmd;//data
        int chk = 0x0b + 0x1b + 0x01 + cmd;
        //Timber.i("CHKSUM==" + (chk));
        byte[] chksum = new byte[4];
        chksum = intToBytes(chk);
        bytes[6] = chksum[0];
        bytes[7] = chksum[1];
        bytes[8] = chksum[2];
        bytes[9] = chksum[3];

        bytes[10] = (byte) 0x43;
        bytes[11] = (byte) 0x21;
        int count = 0;
        boolean writeStatus = false;
        //Timber.i( "bytes==" + bytesToHexString(bytes));
        writeStatus = writeRXCharacteristic(mBluetoothGatt, "2aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0100-facebeadaaaa", bytes);

        //DebugLogger.i(TAG, "secondMach = " + writeStatus);
        return writeStatus ? 1 : -1;
    }


    public static int ackForBindRequest(int status, BluetoothGatt bluetoothGatt) {
        //DebugLogger.i(TAG, "ackForBindRequest  " + status);

        boolean notifStatus = setCharacteristicNotification(bluetoothGatt, "1aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0010-facebeadaaaa", true);
//        DebugLogger.i("NOTIFSTATs", "notifStatus  "+notifStatus);

        byte[] bytes = new byte[12];
        bytes[0] = (byte) 0x12;
        bytes[1] = (byte) 0x34;
        bytes[2] = (byte) 0x0b;// type
        bytes[3] = (byte) 0x13;// cmd
        bytes[4] = (byte) 0x01;// length
        bytes[5] = (byte) status;
        int a = Integer.parseInt("0b", 16) + Integer.parseInt("13", 16) + Integer.parseInt("01", 16);

        //DebugLogger.d(TAG, "CHKSUM==" + (a+status));
        byte[] chksum = new byte[4];
        chksum = intToBytes(a + status);
        bytes[6] = chksum[0];
        bytes[7] = chksum[1];
        bytes[8] = chksum[2];
        bytes[9] = chksum[3];
        bytes[10] = (byte) 0x43;
        bytes[11] = (byte) 0x21;

        //DebugLogger.i(TAG, "绑定设备 响应信息 = " + bytesToInt(chksum, 0));
        int count = 0;
        boolean writeStatus = false;
        while (!writeStatus) {

            writeStatus = writeRXCharacteristic(bluetoothGatt, "1aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0010-facebeadaaaa", bytes);
            if (count > 5000) {
                break;
            } else count++;
        }

//        if (null != MainActivity.iOnBondListener) {
//            MainActivity.iOnBondListener.onBond(writeStatus);
//        }
        //DebugLogger.i("WriteStatus", "ackForBindRequest result " + writeStatus);
        //DebugLogger.i(TAG, "ackForBindRequest result " + writeStatus);
        return writeStatus ? 1 : -1;
    }


    public static int stopMeasuring(BluetoothGatt bluetoothGatt) {
        //DebugLogger.i(TAG, "stopMeasuring");
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

        int count = 0;
        boolean writeStatus = false;
        while (!writeStatus) {
            writeStatus = writeRXCharacteristic(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0001-facebeadaaaa", bytes);
//            if (GlobalData.status_Connected == false)
//                return -1;
            if (count > 5000) {
                break;
            } else count++;
        }
        //DebugLogger.i(TAG, "stopMeasuring result " + writeStatus);
        return writeStatus ? 1 : -1;
    }


    public static int matchInfo(int userid, BluetoothGatt bluetoothGatt) {
        //DebugLogger.i(TAG, "matchInfo userid");
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

        //DebugLogger.v(TAG, "matchInfo CHKSUM = " + (a + userid));
        byte[] chksum = new byte[4];
        chksum = intToBytes(a + userid);
        bytes[9] = chksum[0];
        bytes[10] = chksum[1];
        bytes[11] = chksum[2];
        bytes[12] = chksum[3];
        bytes[13] = (byte) 0x43;
        bytes[14] = (byte) 0x21;

        //DebugLogger.v(TAG, "匹配信息: " + bytesToInt(chksum, 0));
        int count = 0;
        boolean writeStatus = false;
        while (!writeStatus) {

            writeStatus = writeRXCharacteristic(bluetoothGatt, "1aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0010-facebeadaaaa", bytes);
//            if (GlobalData.status_Connected == false)
//                return -1;
            if (count > 5000) {
                break;
            } else count++;
        }
        //DebugLogger.i(TAG, "matchInfo userid writeStatus = " + writeStatus);
        return writeStatus ? 1 : -1;
    }

    //turn off helo device
    public static int letMeashineDown(BluetoothGatt bluetoothGatt) {
        //DebugLogger.i(TAG, "letMeashineDown");
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

        int count = 0;
        boolean writeStatus = false;
        while (!writeStatus) {
            writeStatus = writeRXCharacteristic(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0001-facebeadaaaa", bytes);
//            if (GlobalData.status_Connected == false)
//                return -1;
            if (count > 5000) {
                break;
            } else count++;
        }
        //DebugLogger.i(TAG, "letMeashineDown result " + writeStatus);
        return writeStatus ? 1 : -1;
    }


    public static int matchInfo(String mac, BluetoothGatt bluetoothGatt) {
        //DebugLogger.i(TAG, "matchInfo mac " + mac);
        //DebugLogger.i("Loggerr", "matchInfo");
        if (mac == null) {
            return -1;
        }
        String[] macIds = mac.split(":");

        @SuppressWarnings("unused")
        int s1, s2;
        int s3, s4, s5, s6;
        s1 = Integer.parseInt(mac.substring(0, 2), 16);
        s2 = Integer.parseInt(mac.substring(2, 4), 16);
        s3 = Integer.parseInt(mac.substring(4, 6), 16);
        s4 = Integer.parseInt(mac.substring(6, 8), 16);
        s5 = Integer.parseInt(mac.substring(8, 10), 16);
        s6 = Integer.parseInt(mac.substring(10), 16);
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
        //DebugLogger.v(TAG, "CHKSUM = " + (a + s5 + s6 + s3 + s4));
        byte[] chksum = new byte[4];
        chksum = intToBytes(a + s5 + s6 + s3 + s4);
        byte_info[9] = chksum[0];
        byte_info[10] = chksum[1];
        byte_info[11] = chksum[2];
        byte_info[12] = chksum[3];
        byte_info[13] = (byte) 0x43;
        byte_info[14] = (byte) 0x21;

        //DebugLogger.i(TAG, "MAC  = " + bytesToInt(chksum, 0) + "\nbyte_info = " + bytesToHexString(byte_info));
        int count = 0;
        boolean writeStatus = false;
        writeStatus = writeRXCharacteristic(bluetoothGatt, "1aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0010-facebeadaaaa", byte_info);
//        while (!writeStatus) {
//
//            writeStatus = writeRXCharacteristic(bluetoothGatt, "1aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0010-facebeadaaaa", byte_info);
////            if (GlobalData.status_Connected == false)
////                return -1;
//            if (count > 50000) {
//                break;
//            } else count++;
//        }
        //DebugLogger.i(TAG, "matchInfo mac ：writeStatus = " + writeStatus);
        return writeStatus ? 1 : -1;
    }


    public static int startSendDecryToken(int dataLength, BluetoothGatt bluetoothGatt) {
        //DebugLogger.i(TAG, "startSendDecryToken ");
        //DebugLogger.i(TAG, "startSendDecryToken length");
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
        int count = 0;
        boolean writeStatus = false;
//		//DebugLogger.i("sqs", "开始bytes==" + bytesToHexString(bb));
        while (!writeStatus) {
            writeStatus = writeRXCharacteristic(bluetoothGatt, "1aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0010-facebeadaaaa", bb);
//            if (GlobalData.status_Connected == false)
//                return -1;
            if (count > 50000) {
                break;
            } else count++;
//		DebugLogger.i("sqs", "APP版本号发送程序加载写入结果：writeStatus = " + writeStatus);
            //DebugLogger.i(TAG, "startSendDecryToken write status " + writeStatus);
            //DebugLogger.i(TAG, "startSendDecryToken length write status " + writeStatus);
            return writeStatus ? 1 : -1;
        }
        //DebugLogger.i(TAG, "startSendDecryToken write status " + writeStatus);
        //DebugLogger.i(TAG, "startSendDecryToken length write status " + writeStatus);
        return chk;
    }


    public static void sendDecryTokenContent1(byte[] eightByte, BluetoothGatt bluetoothGatt) {
        //DebugLogger.i(TAG, "sendDecryTokenContent1");
        //DebugLogger.i(TAG, "sendDecryTokenContent1");
        for (int i = 0; i < eightByte.length / 8; i++) {
            byte[] aa = new byte[8];
            for (int j = 0; j < 8; j++) {
                aa[j] = eightByte[i * 8 + j];
            }
            sendDecryTokenContent(aa, bluetoothGatt);
        }
    }

    public static void getDecryTokenContent(byte[] eightByte) {
        //DebugLogger.i(TAG, "sendDecryTokenContent1");
        //DebugLogger.i(TAG, "sendDecryTokenContent1");
        for (int i = 0; i < eightByte.length / 8; i++) {
            byte[] aa = new byte[8];
            for (int j = 0; j < 8; j++) {
                aa[j] = eightByte[i * 8 + j];
            }
            WriteProperties wp = new WriteProperties();
            wp.setServiceUuid("1aabcdef-1111-2222-0000-facebeadaaaa");
            wp.setServiceUuid("facebead-ffff-eeee-0010-facebeadaaaa");
            wp.setData(aa);
            ScanBLE.writeQueue.add(wp);
            wp = null;
        }
    }


    public static int sendDecryTokenContent(byte[] eightStr, BluetoothGatt bluetoothGatt) {
        //DebugLogger.i(TAG, "sendDecryTokenContent byte[]");
        //DebugLogger.i(TAG, "sendDecryTokenContent byte[] started");
        byte[] hexStringToBytes = new byte[8];
//		String hexString = HexUtil.toHexString(eightStr);
//		DebugLogger.d("sqs", "字符串转16进制字符串:hexStr = "+hexString);
//		hexStringToBytes = HexUtil.hexStringToBytes(eightStr);
        hexStringToBytes = eightStr;
        String bytesToHexString = bytesToHexString(hexStringToBytes);
//		DebugLogger.d("sqs", "bytesToHexString!!!"+bytesToHexString);
        int chkByte = 0;
        int chkByte1 = 0;
        byte[] bb = new byte[19];
        bb[0] = (byte) 0x12;
        bb[1] = (byte) 0x34;
        bb[2] = (byte) 0x0B;
        bb[3] = (byte) 0x1E;
        bb[4] = (byte) 0x08;
        for (int i = 0; i < 8; i++) {
            bb[i + 5] = (byte) hexStringToBytes[i];
//			chkByte =  hexStringToBytes[i]+ chkByte;
//			chkByte1 += hexStringToBytes[i];
        }
//		DebugLogger.d("sqs", "内容chkByte"+chkByte);
//		DebugLogger.d("sqs", "内容chkByte1"+chkByte1);
        int chk = 0x0B + 0x1E + 0x08
                + Integer.parseInt(bytesToHexString.substring(0, 2), 16)
                + Integer.parseInt(bytesToHexString.substring(2, 4), 16)
                + Integer.parseInt(bytesToHexString.substring(4, 6), 16)
                + Integer.parseInt(bytesToHexString.substring(6, 8), 16)
                + Integer.parseInt(bytesToHexString.substring(8, 10), 16)
                + Integer.parseInt(bytesToHexString.substring(10, 12), 16)
                + Integer.parseInt(bytesToHexString.substring(12, 14), 16)
                + Integer.parseInt(bytesToHexString.substring(14, 16), 16);
//		DebugLogger.i("sqs", "内容CHKSUM==" + (chk));
        byte[] chksum = new byte[4];
        chksum = intToBytes(chk);
        bb[13] = chksum[0];
        bb[14] = chksum[1];
        bb[15] = chksum[2];
        bb[16] = chksum[3];
        bb[17] = (byte) 0x43;
        bb[18] = (byte) 0x21;
        int count = 0;
        boolean writeStatus = false;
//		DebugLogger.i("sqs", "内容bytes==" + bytesToHexString(bb));
        while (!writeStatus) {
            writeStatus = writeRXCharacteristic(bluetoothGatt, "1aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0010-facebeadaaaa", bb);
//            if (GlobalData.status_Connected == false)
//                return -1;
            if (count > 50000) {
                //DebugLogger.i(TAG, "sendDecryTokenContent：writeStatus count exceeds 50000 " + writeStatus);
                break;
            } else count++;
        }
        //DebugLogger.i(TAG, "sendDecryTokenContent：writeStatus = " + writeStatus);
        //DebugLogger.i(TAG, "sendDecryTokenContent writeStatus " + writeStatus);
        return writeStatus ? 1 : -1;
    }

    //datatime syncgronization
    public static int UpdateNewTime(BluetoothGatt bluetoothGatt) {
        //DebugLogger.i(TAG, "UpdateNewTime offSetTimeZone = " + Integer.parseInt(offSetTimeZone()));
        String times = getNowTime();
        //DebugLogger.i(TAG, "UpdateNewTime");
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
            //DebugLogger.v(TAG, "开始同步时间 bytes[10]= " + bytes[10] + " bytes[9]= " + bytes[9]);
        } else if (Integer.toHexString(b).length() == 4) {
            String temp = Integer.toHexString(b).toString();
            bytes[9] = (byte) Integer.parseInt(temp.substring(0, 2), 16);
            bytes[10] = (byte) Integer.parseInt(temp.substring(2, 4), 16);
        }
        bytes[11] = (byte) 0x00;
        bytes[12] = (byte) 0x00;
        bytes[13] = (byte) 0x43;
        bytes[14] = (byte) 0x21;
        boolean result = false;
        int count = 0;
        while (!result && (count < 100000)) {
            result = writeRXCharacteristic(bluetoothGatt, "1aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0020-facebeadaaaa", bytes);
//            if (GlobalData.status_Connected == false) {
//                return -1;
//            }
            count++;
        }
        //DebugLogger.i(TAG, "UpdateNewTime result = " + result);
        //DebugLogger.i(TAG, "UpdateNewTime result = " + result);
        return result ? 1 : -1;
    }


    public static int APPVersion(String version, BluetoothGatt bluetoothGatt) {

        //DebugLogger.i(TAG, "APPVersion " + version);
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
        //DebugLogger.i(TAG, "CHKSUM==" + (chk));
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
        //DebugLogger.i(TAG, "bytes==" + bytesToHexString(bytes));
        //DebugLogger.i(TAG, "bytes==" + bytesToHexString(bytes));
        while (!writeStatus) {
            writeStatus = writeRXCharacteristic(bluetoothGatt, "2aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0100-facebeadaaaa", bytes);
//            if (GlobalData.status_Connecting == false)
//                return -1;
            if (count > 50000) {
                break;
            } else count++;
        }

        //DebugLogger.i(TAG, "APPVersion result " + writeStatus);
        return writeStatus ? 1 : -1;
    }


    public static int initDeviceLoadCode(BluetoothGatt bluetoothGatt) {

        //DebugLogger.i(TAG, "initDeviceLoadCode");
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
        //DebugLogger.i(TAG, "CHKSUM==" + (chk));
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
//        WriteProperties wp = new WriteProperties();
//        wp.setServiceUuid("2aabcdef-1111-2222-0000-facebeadaaaa");
//        wp.setCharUuid("facebead-ffff-eeee-0100-facebeadaaaa");
//        wp.setData(bytes);
//        wp.setFunction(Constants.initDeviceLoadCode);
//        ScanBLE.writeQueue.add(wp);
//        wp = null;
        while (!writeStatus) {
            writeStatus = writeRXCharacteristic(bluetoothGatt, "2aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0100-facebeadaaaa", bytes);
//            if (GlobalData.status_Connected == false)
//                return -1;
            if (count > 10000) {
                break;
            } else count++;
        }

        //DebugLogger.i(TAG, "initDeviceLoadCode result " + writeStatus);
        return writeStatus ? 1 : -1;
    }

    public static boolean measureHr(BluetoothGatt bluetoothGattt) {

        //DebugLogger.i(TAG, "measureHr");
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

        int count = 0;
        boolean writeStatus = false;

        while (!writeStatus) {
            count++;
            writeStatus = writeRXCharacteristic(bluetoothGattt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", bytes);
            if (GlobalData.status_Connected == false)
                return false;
            if (count > 5000) {
                break;
            } else count++;
        }
        if (writeStatus) {
            setCharacteristicNotification(bluetoothGattt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", true);
        }

        //DebugLogger.i(TAG, "measureHr result " + writeStatus);

        return writeStatus;
    }

    /**
     * 血压测量指令
     *
     * @param context
     * @return
     */
    //get blood
    public static boolean measureBp(BluetoothGatt bluetoothGatt) {

        //DebugLogger.i(TAG, "measureBp");
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
        int count = 0;
        boolean writeStatus = false;
        while (!writeStatus) {
            writeStatus = writeRXCharacteristic(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", bytes);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (!GlobalData.status_Connected)
                return false;
            if (count > 49) {
                break;
            } else count++;

        }
        if (writeStatus) {
            setCharacteristicNotification(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", true);
            setCharacteristicNotification(bluetoothGatt, "2aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0100-facebeadaaaa", true);
        }

        //DebugLogger.i(TAG, "measureBp result " + writeStatus);
        return writeStatus;

    }

    public static int identifyClientStyle(BluetoothGatt bluetoothGatt) {
        //DebugLogger.i(TAG, "identifyClientStyle");
        int count = 0;
        boolean result = false;
        byte[] data = new byte[1];
        data[0] = (byte) 0xaa;
        while (!result) {
            result = writeRXCharacteristic(bluetoothGatt, "2aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0100-facebeadaaaa", data);
//            if (GlobalData.status_Connected == false )
//                return -1;
            if (count > 100) {
                break;
            } else
                count++;
        }
        //DebugLogger.i(TAG, "identifyClientStyle result " + result);
        return result ? 1 : -1;
    }


    /**
     * 血压复位
     *
     * @param context
     * @return
     */
    public static int bpReset(BluetoothGatt bluetoothGatt) {
        //DebugLogger.i(TAG, "bpReset");
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
        int count = 0;
        boolean writeStatus = false;

        while (!writeStatus) {
            writeStatus = writeRXCharacteristic(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", bytes);
//            if (GlobalData.status_Connected == false)
//                return -1;
            if (count > 10000) {
                break;
            } else count++;
        }
        if (writeStatus) {
            setCharacteristicNotification(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", true);
            setCharacteristicNotification(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0100-facebeadaaaa", true);
            setCharacteristicNotification(bluetoothGatt, "2aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0100-facebeadaaaa", true);
        }
        //DebugLogger.i(TAG, "bpReset result " + writeStatus);
        return writeStatus ? 1 : -1;
    }

    //
//    /**
//     * ECG测量指令
//     * @param context
//     * @return
//     */
    public static boolean measureECG(BluetoothGatt bluetoothGatt) {
        ;
        //DebugLogger.i(TAG, "measureECG");
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
        int count = 0;
        boolean writeStatus = false;

        while (!writeStatus) {
            writeStatus = writeRXCharacteristic(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", bytes);
            if (!GlobalData.status_Connected)
                return false;
            if (count > 5000) {
                break;
            } else count++;
        }
        if (writeStatus) {
            setCharacteristicNotification(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", true);
            setCharacteristicNotification(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0004-facebeadaaaa", true);
        }

        //DebugLogger.i(TAG, "measureECG writeStatus " + writeStatus);
        return writeStatus;
    }

    /**
     * 呼吸频率测量指令
     *
     * @param context
     * @return
     */
    public static boolean measureBr(BluetoothGatt bluetoothGatt) {
        //DebugLogger.i(TAG, "measureBr");
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
        int count = 0;
        boolean writeStatus = false;

        while (!writeStatus) {
            writeStatus = writeRXCharacteristic(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", bytes);
            if (!GlobalData.status_Connected)
                return false;
            if (count > 5000) {
                break;
            } else count++;
        }
        if (writeStatus) {
            setCharacteristicNotification(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", true);
        }
        //DebugLogger.i(TAG, "measureBr result " + writeStatus);
        return writeStatus;
    }

    /**
     * 心情疲劳值测量指令
     *
     * @param context
     * @return
     */
    public static boolean measureMF(BluetoothGatt bluetoothGatt) {
        //DebugLogger.i(TAG, "measureMF");
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
        int count = 0;
        boolean writeStatus = false;

        while (!writeStatus) {
            writeStatus = writeRXCharacteristic(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0003-facebeadaaaa", bytes);
            if (!GlobalData.status_Connected)
                return false;
            if (count > 5000) {
                break;
            } else count++;
        }
        if (writeStatus) {
            setCharacteristicNotification(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0003-facebeadaaaa", true);
        }
        //DebugLogger.i(TAG, "measureMF result " + writeStatus);
        return writeStatus;
    }

    public static boolean getSteps(BluetoothGatt bluetoothGatt) {
        //DebugLogger.i(TAG, "getSteps");
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

        int count = 0;
        boolean writeStatus = false;

        while (!writeStatus) {
            count++;
            writeStatus = writeRXCharacteristic(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0001-facebeadaaaa", bytes);
            if (!GlobalData.status_Connected)
                return false;
            if (count > 5000) {
                break;
            } else count++;
        }
        if (writeStatus) {
            setCharacteristicNotification(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0001-facebeadaaaa", true);
        }
        //DebugLogger.i(TAG, "getSteps result " + writeStatus);
        return writeStatus;
    }


    public static int setLedLight(BluetoothGatt bluetoothGatt, int data1, int data2) {
        //DebugLogger.i(TAG, "setLedLight");
//		data1 =(byte)0xaa;
//		data2 =(byte)0x7a;
        int chk = 0x0b + 0x61 + 0x02 + data1 + data2;
//		Log.i("sqs", "led CHKSUM==" + (chk));
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

        int count = 0;
        boolean writeStatus = false;

        while (!writeStatus) {
            count++;
            writeStatus = writeRXCharacteristic(bluetoothGatt, "2aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0100-facebeadaaaa", bytes);
//            if (GlobalData.status_Connected == false)
//                return -1;
            if (count > 5000) {
                break;
            } else count++;
        }
        if (writeStatus) {

        }
        //DebugLogger.i(TAG, "setLedLight result " + writeStatus);
//		DebugLogger.i("sqs", "发送设置led亮度指令写入结果：writeStatus = " + writeStatus);
        return writeStatus ? 1 : -1;
    }


    /**
     * 脉搏波测量指令
     *
     * @param context
     * @return 1 -1
     */
    public static boolean measurePW(BluetoothGatt bluetoothGatt) {
        //DebugLogger.i(TAG, "measurePW");
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
        int count = 0;
        boolean writeStatus = false;
//		Log.e(TAG, "nnnnnnn"+bytesToHexString(bb));
//		Log.e(TAG, "nnnnnnn"+bytesToHexString(bb));
//		Log.e(TAG, "ccccccc"+bytesToHexString(cc));
//		Log.e(TAG, "ccccccc"+bytesToHexString(cc));
        while (!writeStatus) {
            writeStatus = writeRXCharacteristic(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", bb);
//            if (GlobalData.status_Connected == false)
//                return -1;
            if (count > 50000) {
                break;
            } else count++;
        }
        if (writeStatus) {

            count = 0;
            writeStatus = false;
            while (!writeStatus) {
                writeStatus = writeRXCharacteristic(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", cc);
//                if (GlobalData.status_Connected == false)
//                    return -1;
                if (count > 100000) {
                    break;
                } else count++;
            }

            if (writeStatus) {
                setCharacteristicNotification(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0005-facebeadaaaa", true);
                setCharacteristicNotification(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0004-facebeadaaaa", true);
                setCharacteristicNotification(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", true);
            }
            //DebugLogger.i(TAG, "measurePW result " + writeStatus);
            return writeStatus;
        } else {
            //DebugLogger.i(TAG, "measurePW result -1");
            return false;
        }

    }


    /*public static int measurePW(BluetoothGatt bluetoothGatt) {

        Log.i(TAG, "脉搏波测量指令");
        byte[] bb, cc;
        bb = new byte[10];
        cc = new byte[10];
        cc[0] = bb[0] =
                (byte) 0x12;
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
        int count = 0;
        boolean writeStatus = false;
//		Log.e(TAG, "nnnnnnn"+bytesToHexString(bb));
//		Log.e(TAG, "nnnnnnn"+bytesToHexString(bb));
//		Log.e(TAG, "ccccccc"+bytesToHexString(cc));
//		Log.e(TAG, "ccccccc"+bytesToHexString(cc));
        while (!writeStatus) {
            writeStatus = writeRXCharacteristic(bluetoothGatt,"0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", bb);
            if (GlobalData.status_Connected == false)
                return -1;
            if (count > 50000) {
                break;
            } else count++;
        }
        if (writeStatus) {

            count = 0;
            writeStatus = false;
            while (!writeStatus) {
                writeStatus = writeRXCharacteristic(bluetoothGatt,"0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", cc);
                if (GlobalData.status_Connected == false)
                    return -1;
                if (count > 100000) {
                    break;
                } else count++;
            }

            if (writeStatus) {
                setCharacteristicNotification(bluetoothGatt,"0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0005-facebeadaaaa", true);
                setCharacteristicNotification(bluetoothGatt,"0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0004-facebeadaaaa", true);
                setCharacteristicNotification(bluetoothGatt,"0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", true);
            }
            Log.i(TAG, "发送脉搏波测量指令写入结果：writeStatus = " + writeStatus);
            return writeStatus ? 1 : -1;
        } else {
            Log.i(TAG, "发送脉搏波测量指令写入结果：writeStatus = " + writeStatus);
            return -1;
        }

    }*/

    //calibration of blood pressure
    public static int sendStandardBP(int sys, int dis, BluetoothGatt bluetoothGatt) {
        //DebugLogger.i(TAG, "sendStandardBP");
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

        //DebugLogger.v(TAG, "CHKSUM = " + (a + sys + dis));
        byte[] chksum = new byte[4];
        chksum = intToBytes(a + sys + dis);
        bytes[13] = chksum[0];
        bytes[14] = chksum[1];
        bytes[15] = chksum[2];
        bytes[16] = chksum[3];
        bytes[17] = (byte) 0x43;
        bytes[18] = (byte) 0x21;
        //DebugLogger.d(TAG, "发送血压标定值 匹配信息 : " + bytesToInt(chksum, 0));
        //Log.d(TAG, "bb" + bytesToHexString(bb));
        int count = 0;
        boolean writeStatus = false;
        while (!writeStatus) {
            writeStatus = writeRXCharacteristic(bluetoothGatt, "1aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0010-facebeadaaaa", bytes);
//            if (GlobalData.status_Connected == false)
//                return -1;
            if (count > 5000) {
                break;
            } else count++;
        }
        //DebugLogger.i(TAG, "sendStandardBP result " + writeStatus);
        return writeStatus ? 1 : -1;
    }

    // health plan
    public static int sendReviseAutoTime(BluetoothGatt bluetoothGatt, int min) {
        //DebugLogger.i(TAG, "sendReviseAutoTime byte[] ");
        byte[] bb = new byte[12];
        bb[0] = (byte) 0x12;
        bb[1] = (byte) 0x34;
        bb[2] = (byte) 0x0B;
        bb[3] = (byte) 0x17;
        bb[4] = (byte) 0x01;
        bb[5] = (byte) min;
        bb[6] = (byte) (bb[2] + bb[3] + bb[4] + bb[5]);
        //DebugLogger.i(TAG, String.valueOf(bb[6]));
        bb[7] = (byte) 0x00;
        bb[8] = (byte) 0x00;
        bb[9] = (byte) 0x00;
        bb[10] = (byte) 0x43;
        bb[11] = (byte) 0x21;
//        bb[7] = (byte) 0x01;
//        bb[8] = (byte) 0x00;
//        bb[9] = (byte) 0x00;
//        bb[10] = (byte) 0x43;
//        bb[11] = (byte) 0x21;
        //DebugLogger.i("min :", bb[5] + "");

        int count = 0;
        boolean writeStatus = false;

        while (!writeStatus) {
            count++;
            writeStatus = writeRXCharacteristic(bluetoothGatt, "1aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0020-facebeadaaaa", bb);
//            if (GlobalData.status_Connected == false)
//                return -1;
            if (count > 20000) {
                break;
            } else count++;
        }
        //DebugLogger.i(TAG, "sendReviseAutoTime byte[] result " + writeStatus);
        return writeStatus ? 1 : -1;

    }

    public static int sleepTime(BluetoothGatt bluetoothGatt) {
        //DebugLogger.i(TAG, "sleepTime byte[] ");
        byte[] bb = new byte[12];
        bb[0] = (byte) 0x12;
        bb[1] = (byte) 0x34;
        bb[2] = (byte) 0x0B;
        bb[3] = (byte) 0x44;
        bb[4] = (byte) 0x01; //need to be changed 0x01
        bb[5] = (byte) 0x00;
        bb[6] = (byte) 0x00;
        bb[7] = (byte) 0x00;
        bb[8] = (byte) 0x00;
        bb[9] = (byte) 0x00;
        bb[10] = (byte) 0x43;
        bb[11] = (byte) 0x21;
        //DebugLogger.i("min :", bb[5] + "");

        int count = 0;
        boolean writeStatus = false;

        while (!writeStatus) {
            count++;
            writeStatus = writeRXCharacteristic(bluetoothGatt, "2aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0020-facebeadaaaa", bb);
//            if (GlobalData.status_Connected == false)
//                return -1;
            if (count > 20000) {
                break;
            } else count++;
        }
        //DebugLogger.i(TAG, "sleepTime" +
//                "" +
//                " byte[] result " + writeStatus);
        return writeStatus ? 1 : -1;

    }


    public static int unbindDevice(BluetoothGatt bluetoothGatt) {
        //DebugLogger.i(TAG, "解除对设备的绑定");
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

        //DebugLogger.v(TAG, "解除绑定 响应信息" + bytesToInt(chksum, 0));
        int count = 0;
        boolean writeStatus = false;
        while (!writeStatus) {

            writeStatus = writeRXCharacteristic(bluetoothGatt, "1aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0010-facebeadaaaa", bytes);
            if (count > 5000) {
                break;
            } else count++;
        }
        //DebugLogger.i(TAG, "解除对设备的绑定写入结果：writeStatus = " + writeStatus);
        return writeStatus ? 1 : -1;
    }

    public static boolean dynamicStepsHeartRateMeasure(int enableDisbale, BluetoothGatt bluetoothGatt) {
//		1-enable
        //DebugLogger.i(TAG, "dynamicMeasureInstruction");
        byte[] bytes = new byte[12];
        bytes[0] = (byte) 0x12;
        bytes[1] = (byte) 0x34;
        bytes[2] = (byte) 0x0B;// type
        bytes[3] = (byte) 0x66;// cmd
        bytes[4] = (byte) 0x01; //length
        bytes[5] = (byte) enableDisbale;
        int a = Integer.parseInt("0b", 16) + Integer.parseInt("66", 16) + Integer.parseInt("01", 16);
        byte[] chksum = new byte[4];
        //DebugLogger.d(TAG, "dynamicMeasureInstruction-->CHKSUM==" + (a + enableDisbale));
        chksum = intToBytes(a + enableDisbale);
        bytes[6] = chksum[0];
        bytes[7] = chksum[1];
        bytes[8] = chksum[2];
        bytes[9] = chksum[3];
        bytes[10] = (byte) 0x43;
        bytes[11] = (byte) 0x21;
        //DebugLogger.v(TAG, "dynamicMeasureInstruction bytes" + bytes);
        int count = 0;
        boolean writeStatus = false;
        while (!writeStatus) {
            writeStatus = writeRXCharacteristic(bluetoothGatt,"0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", bytes);
            if (!GlobalData.status_Connected)
                return false;
            if (count > 5000) {
                break;
            } else count++;
        }
        if (writeStatus) {
            if (enableDisbale == 1) {
                setCharacteristicNotification(bluetoothGatt,"0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", true);
            } else {
                setCharacteristicNotification(bluetoothGatt,"0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", false);
            }
        }
        //DebugLogger.i(TAG, "dynamicMeasureInstruction ：writeStatus = " + writeStatus);
        return writeStatus;
    }

    //need to pass 0(close measurement) or 1(open measurement)
    //step dynamic heart rate and the meter value,
    /*public static boolean dynamicStepsHeartRateMeasure(int enableDisbale, BluetoothGatt bluetoothGatt) {
        //DebugLogger.i(TAG, "dynamicMeasureInstruction");
        byte[] bytes = new byte[12];
        bytes[0] = (byte) 0x12;
        bytes[1] = (byte) 0x34;
        bytes[2] = (byte) 0x0B;// type
        bytes[3] = (byte) 0x66;// cmd
        bytes[4] = (byte) 0x01; //length
        bytes[5] = (byte) enableDisbale;
        int a = Integer.parseInt("0b", 16) + Integer.parseInt("66", 16) + Integer.parseInt("01", 16);
        byte[] chksum = new byte[4];
        //DebugLogger.d(TAG, "dynamicMeasureInstruction-->CHKSUM==" + (a + enableDisbale));
        chksum = intToBytes(a + enableDisbale);
        bytes[6] = chksum[0];
        bytes[7] = chksum[1];
        bytes[8] = chksum[2];
        bytes[9] = chksum[3];
        bytes[10] = (byte) 0x43;
        bytes[11] = (byte) 0x21;
        //DebugLogger.v(TAG, "dynamicMeasureInstruction bytes" + bytes);
        int count = 0;
        boolean writeStatus = false;
        while (!writeStatus) {
            writeStatus = writeRXCharacteristic(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", bytes);
            if (!GlobalData.status_Connected)
                return false;
            if (count > 5000) {
                break;
            } else count++;
        }
        if (writeStatus) {
            if (enableDisbale == 1) {
                setCharacteristicNotification(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", true);
            } else {
                setCharacteristicNotification(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", false);
            }
        }
        //DebugLogger.i(TAG, "dynamicMeasureInstruction ：writeStatus = " + writeStatus);
        return writeStatus;
    }*/

    public static boolean getUVMeasurement(BluetoothGatt bluetoothGatt) {
        //DebugLogger.i(TAG, "getUvMeasure");
        byte[] bytes = new byte[10];
        bytes[0] = (byte) 0x12;
        bytes[1] = (byte) 0x34;
        bytes[2] = (byte) 0x0A;
        bytes[3] = (byte) 0x0D;//0x0D
        int a =  Integer.parseInt("0a", 16) + Integer.parseInt("0D", 16);
        //DebugLogger.i(TAG, "CHKSUM==" + a);
        byte[] chksum = new byte[4];
        chksum = intToBytes(a);
        bytes[4] = chksum[0];
        bytes[5] = chksum[1];
        bytes[6] = chksum[2];
        bytes[7] = chksum[3];
        bytes[8] = (byte) 0x43;
        bytes[9] = (byte) 0x21;

        int count=0;
        boolean writeStatus = false;

        while(!writeStatus) {
            count++;
            writeStatus = writeRXCharacteristic(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", bytes);
            if (!GlobalData.status_Connected)
                return false;
            if (count > 5000) {
                break;
            } else count++;
        }
        if(writeStatus){
            setCharacteristicNotification(bluetoothGatt,"0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", true);
            GlobalData.isUVMEasurement=true;
        }else{
            GlobalData.isUVMEasurement=false;
        }
        //DebugLogger.i(TAG, "getUvMeasure：writeStatus = " + writeStatus);
        return writeStatus;
    }

    public static boolean getOxygenMeasurement(BluetoothGatt bluetoothGatt) {
        //DebugLogger.i(TAG, "getOxygenMeasure");
        byte[] bytes = new byte[10];
        bytes[0] = (byte) 0x12;
        bytes[1] = (byte) 0x34;
        bytes[2] = (byte) 0x0A;
        bytes[3] = (byte) 0x05;//0x05
        int a =  Integer.parseInt("0a", 16) + Integer.parseInt("05", 16);
        //DebugLogger.i(TAG, "getOxygenMeasure--CHKSUM==" + a);
        byte[] chksum = new byte[4];
        chksum = intToBytes(a);
        bytes[4] = chksum[0];
        bytes[5] = chksum[1];
        bytes[6] = chksum[2];
        bytes[7] = chksum[3];
        bytes[8] = (byte) 0x43;
        bytes[9] = (byte) 0x21;

        int count=0;
        boolean writeStatus = false;

        while(!writeStatus) {
            count++;
            writeStatus = writeRXCharacteristic(bluetoothGatt,"0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", bytes);
            if (!GlobalData.status_Connected)
                return false;
            if (count > 5000) {
                break;
            } else count++;
        }
        if(writeStatus){
            setCharacteristicNotification(bluetoothGatt,"0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", true);
            setCharacteristicNotification(bluetoothGatt,"0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0006-facebeadaaaa", true);

            /*setCharacteristicNotification(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0005-facebeadaaaa", true);
            setCharacteristicNotification(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0004-facebeadaaaa", true);
            setCharacteristicNotification(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", true);*/
        }
        //DebugLogger.i(TAG, "getOxygenMeasure：writeStatus = " + writeStatus);
        return writeStatus;
    }

    //Abhijeet Chnages

    private static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a) {
            sb.append(String.format("%02x", b));
            Log.i("Hexa-String ", sb.toString());
        }
        return sb.toString();
    }

    //-------------------------------------------//

    public static String offSetTimeZone() {
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
        //DebugLogger.v(TAG, "开始同步时间 getNowTime = " + time);
        String times = Long.toHexString(time);
        return times;
    }

    public static String bytesToHexString(byte[] src) {
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


    private static boolean writeRXCharacteristic(BluetoothGatt mBluetoothGatt, String serviceUUID, String charactersticUUID, byte[] value) {
        //DebugLogger.i("writeRXCharacteristic", "Writechar  "+serviceUUID+" "+charactersticUUID);
        BluetoothGattService RxService = null;
        if (mBluetoothGatt != null) {
            try {
                RxService = mBluetoothGatt.getService(UUID.fromString(serviceUUID));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return false;
        }


        if (RxService == null) {
//            Timber.i(TAG, "653 写入失败 GATT服务未找到 Rx service not found!");
            // broadcastUpdate(GlobalData.DEVICE_DOES_NOT_SUPPORT_UART);
            return false;
        }
        BluetoothGattCharacteristic RxChar = RxService.getCharacteristic(UUID.fromString(charactersticUUID));
        if (RxChar == null) {
            //Timber.i(TAG, "659 写入失败 GATT属性未找到 Rx charateristic not found!");
            // broadcastUpdate(GlobalData.DEVICE_DOES_NOT_SUPPORT_UART);
            return false;
        }

        RxChar.setValue(value);

        boolean status = false;
        if (mBluetoothGatt != null) {
            status = mBluetoothGatt.writeCharacteristic(RxChar);
        }

        // Timber.i(TAG, "666 写入成功 返回响应值 :write TXchar - status=" + status);
        return status;
    }


    public static boolean setCharacteristicNotification(BluetoothGatt bluetoothGatt, String serviceUUID, String characteristicUUID, boolean enabled) {

        boolean status = false;
        if (bluetoothGatt == null) {
            //DebugLogger.w(TAG, "Characteristicnotification  BluetoothAdapter not initialized");
            return false;
        }
        BluetoothGattService RxService = bluetoothGatt.getService(UUID.fromString(serviceUUID));
        if (RxService == null) {
            //DebugLogger.i(TAG, "Characteristicnotification GATT  null Service");
            return false;
        }
        BluetoothGattCharacteristic TxChar = RxService.getCharacteristic(UUID.fromString(characteristicUUID));
        if (TxChar == null) {
            //DebugLogger.i(TAG, "Characteristicnotification :GATT  null Characteristic");
            return false;
        } else {
            status = bluetoothGatt.setCharacteristicNotification(TxChar, enabled);
            //DebugLogger.i(TAG, "Characteristicnotification  :Characteristic on status = " + status);
            return status;
        }
    }

    //Created By Abhijeet for Green ppg data - 6-3-18

    public static boolean getGreenPPG (BluetoothGatt bluetoothGatt) {
        //DebugLogger.i(TAG, "getSteps");
        byte[] bytes = new byte[10];
        bytes[0] = (byte) 0x12;
        bytes[1] = (byte) 0x34;
        bytes[2] = (byte) 0x0A;
        bytes[3] = (byte) 0x71;

        bytes[4] = (byte) 0x7B;
        bytes[5] = (byte) 0x00;
        bytes[6] = (byte) 0x00;
        bytes[7] = (byte) 0x00;

        bytes[8] = (byte) 0x43;
        bytes[9] = (byte) 0x21;

        int count = 0;
        boolean writeStatus = false;

        while (!writeStatus) {
            count++;
            writeStatus = writeRXCharacteristic(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0001-facebeadaaaa", bytes);
            if (!GlobalData.status_Connected)
                return false;
            if (count > 5000) {
                break;
            } else count++;
        }
        if (writeStatus) {
            setCharacteristicNotification(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0001-facebeadaaaa", true);
            setCharacteristicNotification(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", true);
        }
        //DebugLogger.i(TAG, "getSteps result " + writeStatus);
        return writeStatus;
    }

    //Created By Abhijeet for Red and Infrared data - 6-3-18
    // For LX+ Only

    public static boolean getRNIRData (BluetoothGatt bluetoothGatt) {
        //DebugLogger.i(TAG, "getSteps");
        byte[] bytes = new byte[10];
        bytes[0] = (byte) 0x12;
        bytes[1] = (byte) 0x34;
        bytes[2] = (byte) 0x0A;
        bytes[3] = (byte) 0x73;

        /*bytes[4] = (byte) 0x7B;
        bytes[5] = (byte) 0x00;
        bytes[6] = (byte) 0x00;
        bytes[7] = (byte) 0x00;*/
        int scf = bytes[2] + bytes[3];
        bytes[4] =  (byte)(scf & 0xff);
        bytes[5] = (byte) ((scf >> 8) & 0xff);
        bytes[6] = (byte) ((scf >> 16) & 0xff);
        bytes[7] = (byte) ((scf >> 24) & 0xff);
        /*bytes[4] = (byte) 0x0B;
        bytes[5] = (byte) 0x00;
        bytes[6] = (byte) 0x00;
        bytes[7] = (byte) 0x00;*/

        bytes[8] = (byte) 0x43;
        bytes[9] = (byte) 0x21;

        int count = 0;
        boolean writeStatus = false;

        while (!writeStatus) {
            count++;
            writeStatus = writeRXCharacteristic(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0001-facebeadaaaa", bytes);
            if (!GlobalData.status_Connected)
                return false;
            if (count > 5000) {
                break;
            } else count++;
        }
        if (writeStatus) {
            setCharacteristicNotification(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0001-facebeadaaaa", true);
            setCharacteristicNotification(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", true);
        }
        //DebugLogger.i(TAG, "getSteps result " + writeStatus);
        return writeStatus;
    }


    public static boolean openHumanDetect(int index, String x1, String y1, BluetoothGatt bluetoothGatt) {
        byte[] bytes = new byte[20];
        bytes[0] = (byte) 0x12;
        bytes[1] = (byte) 0x34;
        bytes[2] = (byte) 0x0b;
        bytes[3] = (byte) 0x69;

        bytes[4] = (byte) 0x09;
        bytes[5] = (byte) index;//data
        bytes[6] = (byte) Integer.parseInt(x1.substring(0, 2), 16);
        bytes[7] = (byte) Integer.parseInt(x1.substring(2, 4), 16);
        bytes[8] = (byte) Integer.parseInt(x1.substring(4, 6), 16);
        bytes[9] = (byte) Integer.parseInt(x1.substring(6, 8), 16);
        bytes[10] = (byte) Integer.parseInt(y1.substring(0, 2), 16);
        bytes[11] = (byte) Integer.parseInt(y1.substring(2, 4), 16);
        bytes[12] = (byte) Integer.parseInt(y1.substring(4, 6), 16);
        bytes[13] = (byte) Integer.parseInt(y1.substring(6, 8), 16);
        byte[] chksum = new byte[4];//校验
        int a = Integer.parseInt("0b", 16) + Integer.parseInt("69", 16) + Integer.parseInt("09", 16) + Integer.parseInt(index + "", 16) + Integer.parseInt(x1.substring(0, 2), 16) + +Integer.parseInt(x1.substring(2, 4) + "", 16) + Integer.parseInt(x1.substring(4, 6) + "", 16) + Integer.parseInt(x1.substring(6, 8) + "", 16) + Integer.parseInt(y1.substring(0, 2) + "", 16) + Integer.parseInt(y1.substring(2, 4) + "", 16) + Integer.parseInt(y1.substring(4, 6) + "", 16) + Integer.parseInt(x1.substring(6, 8) + "", 16);
        chksum = intToBytes(a);
        bytes[14] = chksum[0];
        bytes[15] = chksum[1];
        bytes[16] = chksum[2];
        bytes[17] = chksum[3];
        bytes[18] = (byte) 0x43;
        bytes[19] = (byte) 0x21;
        boolean writeStatus = false;
        int count = 0;
        while (!writeStatus) {
            writeStatus = writeRXCharacteristic(bluetoothGatt,"1aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0010-facebeadaaaa", bytes);
            if (GlobalData.status_Connected == false)
                return false;
            if (count > 50000) {
                break;
            } else count++;
        }
        if (writeStatus) {
            setCharacteristicNotification(bluetoothGatt, "0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0001-facebeadaaaa", true);
        }
        return writeStatus;
    }


    public static boolean accelerometer(BluetoothGatt bluetoothGatt, int data) {
        byte[] bytes = new byte[12];
        bytes[0] = 0x12;
        bytes[1] = 0x34;
        bytes[2] = 0x0B;
        bytes[3] = 0x65;
        bytes[4] = 0x01;
        bytes[5] = (byte) data;
        int k =0;
        for (int i = 2; i < 6; i ++) {
            k += bytes[i];
        }

        for (int i = 0; i < 4; i ++) {
            bytes[6 + i] = (byte) ((k >> 8 * i) & 0xff);
        }
        bytes[10] = 0x43;
        bytes[11] = 0x21;
        String serviceUuid = "0aabcdef-1111-2222-0000-facebeadaaaa";
        String charUuid = "facebead-ffff-eeee-0002-facebeadaaaa";
        boolean writeStatus = writeRXCharacteristic(bluetoothGatt, serviceUuid, charUuid, bytes);
        if (writeStatus) {
            setCharacteristicNotification(bluetoothGatt, serviceUuid, charUuid, true);
        }
        return writeStatus;
    }



    public static boolean scheduledMeasure(BluetoothGatt bluetoothGatt, int mins) {
        byte[] bb = new byte[12];
        bb[0] = (byte) 0x12;
        bb[1] = (byte) 0x34;
        bb[2] = (byte) 0x0B;
        bb[3] = (byte) 0x17;
        bb[4] = (byte) 0x01;
        bb[5] = (byte) mins;
        bb[6] = (byte) (bb[2] + bb[3] + bb[4] + bb[5]);
        bb[7] = (byte) 0x00;
        bb[8] = (byte) 0x00;
        bb[9] = (byte) 0x00;
        bb[10] = (byte) 0x43;
        bb[11] = (byte) 0x21;
        String serviceUuid = "1aabcdef-1111-2222-0000-facebeadaaaa";
        String charUuid = "facebead-ffff-eeee-0020-facebeadaaaa";
        boolean writeStatus = writeRXCharacteristic(bluetoothGatt, serviceUuid, charUuid, bb);
        return writeStatus;
    }






}
