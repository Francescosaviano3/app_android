package com.worldgn.connector;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import java.util.HashMap;

/**
 * Created by mudassarhussain on 9/12/17.
 */

class Config {
    public static String BASE_URL = ""; //production "https://connector.heloappstore.com/"
//    public static String BASE_DEV_URL = "";
    public static String LICENCE_URL = "";
    public static String heloaAPI = "";
    public static String heloaAPIDati = "";
    public static final String appkey_admin="jKihsg89ySfdgMio432";
    public static final String compatibility_url = "https://dev-myhlconnector.worldgn.com/";
    public static final String md5_url = "https://myapi.worldgn.com/";
    public static String registration(String dev){
        if(dev.equals("1")){
            return BASE_URL ;
        }else{
            return BASE_URL ;

        }
    }

    //to check the debug or signed build
    public static boolean isDebugBuild(Context context){
        return ( 0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE ));
    }

    public static String registration(){
        return BASE_URL + heloaAPI;
//        return BASE_URL + "loginconapi.php";
    }

    /*public static String checkPaidApp(){
        return BASE_DEV_URL + "connector_paid_api.php";
    }*/

    public static String licence(){
        return LICENCE_URL;
//        return "http://112.124.120.58:8081/helo/licenseproject/findlicenseprojectbycache.action";
    }

    public static HashMap<String,String> reg_Common_Params(Context context,String app_key, String app_token){
        String deviceId = Util.getIMEI(context);
        HashMap<String,String> map = new HashMap<>();
        map.put("key_check", Util.md5(app_token + deviceId));
        map.put("deviceid",deviceId);
        map.put("appkey",app_key);
        return map;
    }

    public static HashMap<String,String> reg_email_params(Context context,String app_key, String app_token, String email){
        HashMap<String,String> map = new HashMap<>();
        map.putAll(reg_Common_Params(context,app_key,app_token));
        map.put("action", "regist_helo");
//        map.put("action","externallogin");
        map.put("email",email);
        if(isHeloFit(context)){
            map.put("scope","helofit");
        }
        return map;
    }

    public static HashMap<String,String> reg_phone_params(Context context,String app_key, String app_token, String number, String prefix){
        HashMap<String,String> map = new HashMap<>();
        map.putAll(reg_Common_Params(context,app_key,app_token));
        map.put("action","regist_helo");
//        map.put("action","externallogin");
        map.put("number",number);
        map.put("prefix",prefix);
        return map;
    }



    public static HashMap<String,String> reg_confirm_params(Context context,String app_key, String app_token, String pin){
        HashMap<String,String> map = new HashMap<>();
        map.putAll(reg_Common_Params(context,app_key,app_token));
        map.put("action","confirm_reg_helo");
        map.put("pin",pin);
        if(isHeloFit(context)){
            map.put("scope","helofit");
        }
        return map;
    }

    public static HashMap<String,String> reg_CallBack_Phone_Pin(Context context,String app_key, String app_token, String number,String prefix){
        HashMap<String,String> map = new HashMap<>();
        map.putAll(reg_Common_Params(context,app_key,app_token));
        map.put("action","req_callback");
        map.put("number",number);
        map.put("prefix",prefix);
        return map;
    }

    public static HashMap<String,String> reg_CallBack_Email_Pin(Context context,String app_key, String app_token, String email){
        HashMap<String,String> map = new HashMap<>();
        map.putAll(reg_Common_Params(context,app_key,app_token));
        map.put("action","req_callback_email");
        map.put("email",email);
        return map;
    }

    public static HashMap<String,String> reg_Check_backlist(Context context,String app_key, String app_token, String macaddress){
        HashMap<String,String> map = new HashMap<>();
        map.putAll(reg_Common_Params(context,app_key,app_token));
        map.put("action","checkblacklist");
        map.put("macaddress",macaddress);
        return map;
    }

    public static HashMap<String,String> checkPaidAppParams(String helo_user_id, String app_identifier){
        HashMap<String,String> map = new HashMap<>();

        map.put("action","check_paid_app");
        map.put("helo_user_id", helo_user_id);
        map.put("app_identifier", app_identifier);
        map.put("platform", "android");
        return map;
    }


    public static HashMap<String,String> getDecryToken(Context context,String app_key, String app_token, String macaddress){
        HashMap<String,String> map = new HashMap<>();
        map.putAll(reg_Common_Params(context,app_key,app_token));
        map.put("action","checkblacklist");
        map.put("macaddress",macaddress);
        return map;
    }

    public static boolean isHeloFit(Context context){
        return context != null && context.getPackageName().equals("com.worldgn.helofit");
    }

    public static HashMap<String,String> reg_confirm_account_params(Context context,String app_key, String app_token, String username,String password,String sponsored,String email,String phone,String prefix,String sponsorEmail){
        HashMap<String,String> map = new HashMap<>();

        map.putAll(reg_Common_Params(context,app_key,app_token));
        map.put("action","create_wgn");
        map.put("username_wgn",username);
        map.put("is_sponsor",sponsored);
        map.put("appkey_admin",appkey_admin);
        if (Integer.parseInt(sponsored)==0)
        {
            map.put("email",email);
            map.put("number", phone);
            map.put("prefix", prefix);
            map.put("password_wgn", password);

        }
        else
        {
            map.put("email",sponsorEmail);
        }


        if(isHeloFit(context)){
            map.put("scope","helofit");
        }
        return map;
    }

    public static HashMap<String,String> getRegConfirmAccountParams(Context context, String app_key, String app_token, String username,String password, String email){
        HashMap<String,String> map = new HashMap<>();

        map.putAll(reg_Common_Params(context,app_key,app_token));
        map.put("action","create_wgn");
        map.put("username_wgn",username);
        map.put("is_sponsor","0");
        map.put("appkey_admin",appkey_admin);
        map.put("email",email);
        /*map.put("number", phone);
        map.put("prefix", prefix);*/
        map.put("password_wgn", password);
        if(isHeloFit(context)){
            map.put("scope","helofit");
        }
        return map;
    }

    public static HashMap<String,String> getRegConfirmAccountParams(Context context, String app_key, String app_token, String username,String password, String phone, String prefix){
        HashMap<String,String> map = new HashMap<>();

        map.putAll(reg_Common_Params(context,app_key,app_token));
        map.put("action","create_wgn");
        map.put("username_wgn",username);
        map.put("is_sponsor","0");
        map.put("appkey_admin",appkey_admin);
//        map.put("email",email);
        map.put("number", phone);
        map.put("prefix", prefix);
        map.put("password_wgn", password);
        if(isHeloFit(context)){
            map.put("scope","helofit");
        }
        return map;
    }

    public static HashMap<String,String> getRegConfirmAccountBySponsor(Context context, String app_key, String app_token, String username, String email){
        HashMap<String,String> map = new HashMap<>();

        map.putAll(reg_Common_Params(context,app_key,app_token));
        map.put("action", "create_wgn");
        map.put("username_wgn", username);
        map.put("is_sponsor", "1");
        map.put("appkey_admin", appkey_admin);
        map.put("email", email);

        if(isHeloFit(context)){
            map.put("scope","helofit");
        }
        return map;
    }

}