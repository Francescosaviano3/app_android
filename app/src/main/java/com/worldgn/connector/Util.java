package com.worldgn.connector;

import android.content.Context;
import android.provider.Settings;
import android.telephony.PhoneNumberUtils;

import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mudassarhussain on 9/12/17.
 */

public class Util {
    /*public static String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        return imei;
    }*/

    //Added By Abhijeet

    public static int parseInt(String arg){
        try{
            return Integer.parseInt(arg);
        }catch (Exception ex){
            return 0;
        }
    }

    static String getIMEI(Context context) {
        String android_id = Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);
        return android_id;
    }

    public static boolean isvalidEmail(String email) {
        Pattern pattern = Pattern.compile("^([\\w\\.\\-]+)@([\\w\\-]+)((\\.(\\w){2,3})+)$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean validCellPhone(String number)
    {
        return android.util.Patterns.PHONE.matcher(number).matches();
    }

    public static boolean isvalidPhone(String phoneNumber) {
        return PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber);
    }

    static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }



}
