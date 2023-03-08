package com.worldgn.connector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Krishna Rao on 18/09/2017.
 */

class BleServiceCharacteristics {

    //use this method name for stopMeasuring,letMeashineDown,getSteps
    public static List<String> getHRServiceCharacteristics() {
        List<String> characteristics = new ArrayList<>();
        characteristics.add("0aabcdef-1111-2222-0000-facebeadaaaa");
        characteristics.add("facebead-ffff-eeee-0001-facebeadaaaa");
        return characteristics;
    }
    //use this method name for identifyClientStyle
    public static List<String> getSecondMachServiceCharacteristics() {
        List<String> characteristics = new ArrayList<>();
        characteristics.add("2aabcdef-1111-2222-0000-facebeadaaaa");
        characteristics.add("facebead-ffff-eeee-0100-facebeadaaaa");
        return characteristics;
    }

    //call the same Service & Characteristics for getMatchInfo userid, mac params & startSendDecryToken & sendDecryTokenContent
    public static List<String> getAckForBindRequestServiceCharacteristics() {
        List<String> characteristics = new ArrayList<>();
        characteristics.add("1aabcdef-1111-2222-0000-facebeadaaaa");
        characteristics.add("facebead-ffff-eeee-0010-facebeadaaaa");
        return characteristics;
    }

    //call the same method for sendReviseAutoTime
    public static List<String> getUpdateNewTimeServiceCharacteristics() {
        List<String> characteristics = new ArrayList<>();
        characteristics.add("1aabcdef-1111-2222-0000-facebeadaaaa");
        characteristics.add("facebead-ffff-eeee-0020-facebeadaaaa");
        return characteristics;
    }

    //use the same method name for initDeviceLoadCode,setLedLight
    public static List<String> getAppVersionServiceCharacteristics() {
        List<String> characteristics = new ArrayList<>();
        characteristics.add("2aabcdef-1111-2222-0000-facebeadaaaa");
        characteristics.add("facebead-ffff-eeee-0100-facebeadaaaa");
        return characteristics;
    }

    //use the same method name for unbindDeviceServiceCharacteristics
    public static List<String> getSendStandardBPServiceCharacteristics() {
        List<String> characteristics = new ArrayList<>();
        characteristics.add("1aabcdef-1111-2222-0000-facebeadaaaa");
        characteristics.add("facebead-ffff-eeee-0010-facebeadaaaa");
        return characteristics;
    }

    //use the same methodname for measureBpServiceCharacteristics, bpResetServiceCharacteristics, measureBR, measurePW_BB, measurePW_CC
    public static List<String> getMeasureHrServiceCharacteristics() {
        List<String> characteristics = new ArrayList<>();
        characteristics.add("0aabcdef-1111-2222-0000-facebeadaaaa");
        characteristics.add("facebead-ffff-eeee-0002-facebeadaaaa");
        return characteristics;
    }

    public static List<String> getMeasureECGServiceCharacteristics() {
        List<String> characteristics = new ArrayList<>();
        characteristics.add("0aabcdef-1111-2222-0000-facebeadaaaa");
        characteristics.add("facebead-ffff-eeee-0004-facebeadaaaa");
        return characteristics;
    }

    public static List<String> getMeasureMFServiceCharacteristics() {
        List<String> characteristics = new ArrayList<>();
        characteristics.add("0aabcdef-1111-2222-0000-facebeadaaaa");
        characteristics.add("facebead-ffff-eeee-0003-facebeadaaaa");
        return characteristics;
    }

}
