package com.worldgn.connector;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.text.TextUtils;


import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import it.rialtlas.healthmonitor.BuildConfig;
/**
 * Created by mudassarhussain on 9/12/17.
 */

 public class Connector{
    private static Connector instance;
    private static String LOCK = "lock";
    private boolean init;
    private String app_key, app_token;
    private Context mContext;





    private Connector() {

    }

    public static Connector getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new Connector();
                    try {
                        EncryDecryHelper encryDecryHelper = new EncryDecryHelper();
//                        Config.BASE_URL = encryDecryHelper.decrypt(BuildConfig.produrl);
                        Config.BASE_URL = encryDecryHelper.decrypt(BuildConfig.url);
//                        Config.BASE_URL = "https://myapi.worldgn.com/";
                        Config.LICENCE_URL = encryDecryHelper.decrypt(BuildConfig.licUrl);
                        Config.heloaAPI = encryDecryHelper.decrypt(BuildConfig.heloAPI);
                        Config.heloaAPIDati = encryDecryHelper.decrypt(BuildConfig.heloAPIdati);
//                        Config.BASE_DEV_URL = encryDecryHelper.decrypt(BuildConfig.devurl);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return instance;
    }


    public void initialize(Context context, String appKey, String appToken) {
        DebugLogger.i("Conndev ",context.getPackageName());
        mContext = context.getApplicationContext();
        init = true;
        app_key = appKey;
        GlobalData.app_key = appKey;
        app_token = appToken;
//        getDecryToken(); //get licence token from API //COmmented on 15-03-2018 // to remove licence functionality
        SafePreferences.getInstance().intilizeSecure(mContext);
        SafePreferences.getInstance().saveStringToSecurePref("APP_KEY", appKey);
        ScanBLE.getInstance(mContext);
    }

    public boolean isLoggedIn() {
        boolean isLoggeddin = false;
        try {
//            isLoggeddin = SafePreferences.getInstance().getBooleanFromSecurePref(ConstantsImp.PREF_IS_LOGGED_IN, false);
            isLoggeddin = PrefUtils.getBoolean(mContext, ConstantsImp.PREF_IS_LOGGED_IN, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isLoggeddin && isTokenSessionLength(); //        return isTokenSessionLength(); //

    }

    private boolean isTokenSessionLength(){
        String tokenSession = "";
        try {
            tokenSession = SafePreferences.getInstance().getStringFromSecurePref(ConstantsImp.PREF_TOKENSESSION, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tokenSession.length()>5;
    }

    private boolean isCompatible() {
        boolean isCompatible = false;
        try {
            isCompatible = SafePreferences.getInstance().getBooleanFromSecurePref(ConstantsImp.compatabilty_pref, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isCompatible;
    }
    /*public void clearPreferences() {
        try {
            SafePreferences.getInstance().saveBooleanToSecurePref(ConstantsImp.PREF_IS_LOGGED_IN, false);
            SafePreferences.getInstance().saveStringToSecurePref(ConstantsImp.PREF_TOKENSESSION, null);
        } catch (Exception e){
            e.printStackTrace();
        }

    }*/

    public void setScanCallback(Context mContext, ScanCallBack scanCallback) {
        ScanBLE.getInstance(mContext).setScanCallback(scanCallback);
    }
    public void login(String email, ILoginCallback callback) {
        login(true, null, email, callback);
    }

    public void login(String prefix, String phoneNo, ILoginCallback callback) {
        login(false, prefix, phoneNo, callback);
    }

    //check api docuemnt for reference New Helo Api - Authentication.pdf

    private void login(boolean byeamil, String prefix, String phoneNo, final ILoginCallback callback) {
        String flv = "pdn"; // FlavorTest.FLAV;

        //vijay changes
        boolean internetConn = checkInternetConnection();
        if(!internetConn){
            callback.onFailure(Constants.NO_INTERNET);
            return;
        }
        /*if(!isServerReachable()) {
            callback.onFailure(Constants.SERVER_NOT_REACHABLE);
            return;
        }*/

        //krishna changes
        initlizeAppKey_Token(callback);

        final HashMap<String, String> map;
        if (byeamil) {
            if (TextUtils.isEmpty(phoneNo)) {
                if (callback != null) {
                    callback.onFailure(Constants.CALLBACK_EMAIL_EMPTY);
                }
                return;
            } else if (!Util.isvalidEmail(phoneNo)) {
                if (callback != null) {
                    callback.onFailure(Constants.CALLBACK_VALID_EMAIL);
                }
                return;
            }
            map = Config.reg_email_params(mContext, app_key, app_token, phoneNo);
        } else {
            if (TextUtils.isEmpty(prefix)) {
                if (callback != null) {
                    callback.onFailure(Constants.CALLBACK_PREFIX_EMPTY);
                }
                return;
            }
            if (TextUtils.isEmpty(phoneNo)) {
                if (callback != null) {
                    callback.onFailure(Constants.CALLBACK_PHONENUMBER_EMPTY);
                }
                return;
            }
            map = Config.reg_phone_params(mContext, app_key, app_token, phoneNo, prefix);
        }
        //make a http request  to authenticate provided credentials in AsyncTask or background thread. See details in provided helo apis

        SimpleTask task = new SimpleTask() {

            @Override
            protected Void doInBackground(Void... voids) {

                HttpServerResponse response = HttpPostClient.postRequest(Config.registration(), map);
                data = response;
                return null;
            }


            @Override
            protected void onPostExecute(Void aVoid) {

                super.onPostExecute(aVoid);
                HttpServerResponse response = (HttpServerResponse) data;
                if (data != null) {
                    if (response.hasError()) {
                        if (callback != null) {
                            callback.onFailure(Constants.CALLBACK_INTILIZE_APPKEY_TOKEN);
                        }
                        return;
                    }
                    JSONObject object = JSONHelper.json(response.response());
                    int success = JSONHelper.getInt(object, Constants.JSON_KEY_SUCCESS, -1);
                    int update = JSONHelper.getInt(object, Constants.JSON_KEY_UPDATE, -1);

                    if (success == 1) {
                        int isAssociate = JSONHelper.getInt(object, Constants.JSON_KEY_IS_ASSOCIATE, -1);
                        switch (update) {
                            case 0://Malik Changes
                                if (callback != null) {
                                    callback.onPinverification();
                                }
                                break;
                            case 1:
                                if (callback != null) {
                                    callback.onPinverification();
                                }
                                break;
                            case 2://Malik Changes
                                if (callback != null) {
                                    callback.onPinverification();
                                }
                                break;
                        }
                        if(isAssociate==0)
                        {
                            if (callback!=null)
                                callback.accountVerification();


                        }
                    } else if (success == 2) {  //krishna Changes
                        int isAssociate = JSONHelper.getInt(object, Constants.JSON_KEY_IS_ASSOCIATE, -1);
                        if (isAssociate==0)
                        {
                            if (callback != null) {
                                callback.onPinverification();
                            }

                        }else {
                            parseJsonPref(object);
                            String helo_id = "0";
                            try {
                                helo_id = SafePreferences.getInstance().getStringFromSecurePref(ConstantsImp.PREF_USERIDHELO, "0");
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            long userHeloId = Long.parseLong(helo_id);
                            ScanBLE.getInstance(mContext).setScheduleMd5Check();
                            if (callback != null) {
                                callback.onSuccess(userHeloId);
//                            getDecryToken();


                            }
                        }

                        //Malik new changes
//                        checkIfPurchasedApp(mContext);
                    } else {//Malik Changes
                        if (callback != null) {
                            callback.onFailure(JSONHelper.getString(object, Constants.JSON_KEY_DESCRIPTION));
                        }
                    }


                }
            }
        };

        AsyncTaskHelper.execute(task);

    }

    //Vijaya changes
    public void accountVerifyByUsernamewithEmail(String email, String userName, String password, IPinCallback callback) {
        initilizePinAppKey_Token(callback);
        HashMap<String, String> map = Config.getRegConfirmAccountParams(mContext, app_key, app_token, userName, password, email);
        accountVerify(map, callback);

    }

    public void accountVerifyByUsernamewithPhone(String phone, String prefix, String userName, String password, IPinCallback callback) {
        initilizePinAppKey_Token(callback);
        HashMap<String, String> map = Config.getRegConfirmAccountParams(mContext, app_key, app_token, userName, password, phone, prefix);
        accountVerify(map, callback);
    }

    public void accountVerifyBySponsor(String userName, String email, IPinCallback callback) {
        initilizePinAppKey_Token(callback);
        HashMap<String, String> map = Config.getRegConfirmAccountBySponsor(mContext, app_key, app_token, userName, email);
        accountVerify(map, callback);
    }



    private void accountVerify(final HashMap<String, String> map, final IPinCallback callback) {
        boolean internetConn = checkInternetConnection();
        if (!internetConn) {
            callback.onFailure(Constants.NO_INTERNET);
            return;
        }

        final SimpleTask task = new SimpleTask() {
            @Override
            protected Void doInBackground(Void... voids) {
//                HashMap<String, String> map = Config.reg_confirm_account_params(mContext, app_key, app_token, username, password, sponsored,email,phone,prefix,sponsorEmail);
                HttpServerResponse response = HttpPostClient.postRequest(Config.registration(), map);
                data = response;
                return null;
            }


            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                HttpServerResponse response = (HttpServerResponse) data;
                if (data != null) {
                    if (response.hasError()) {
                        if (callback != null) {
                            callback.onFailure(Constants.CALLBACK_INTILIZE_APPKEY_TOKEN);
                        }
                        return;
                    }
                    JSONObject object = JSONHelper.json(response.response());
                    int success = JSONHelper.getInt(object, Constants.JSON_KEY_SUCCESS, -1);
                    if (success == 1) {
                        int itExist = JSONHelper.getInt(object, ConstantsImp.JSON_KEY_IT_EXIST, -1);
                        long heloId = JSONHelper.getInt(object, ConstantsImp.PREF_USERIDHELO, -1);
                        if (heloId==-1)
                        {
                            if (callback != null) {
                                callback.onFailure(ConstantsImp.CALLBACK_INVALID_HELOID);
                            }
                        }

                        else if ((itExist == 1)&&(heloId!=-1)) {
                            parseJsonPref(object);
                            String UserIDHelo = JSONHelper.getString(object, ConstantsImp.PREF_USERIDHELO);
                            SafePreferences.getInstance().saveStringToSecurePref(ConstantsImp.PREF_USERIDHELO, UserIDHelo);

                            String helo_user_id = "0";
                            try {
                                helo_user_id = SafePreferences.getInstance().getStringFromSecurePref(ConstantsImp.PREF_USERIDHELO, "0");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //long userHeloId =1;
                            long userHeloId = Long.parseLong(helo_user_id);
                            if (callback != null) {

                                SafePreferences.getInstance().saveStringToSecurePref(ConstantsImp.PREF_USERIDHELO, Long.toString(userHeloId));
                                /*String tokensession = JSONHelper.getString(object, ConstantsImp.PREF_TOKENSESSION);
                                SafePreferences.getInstance().saveStringToSecurePref(ConstantsImp.PREF_TOKENSESSION, tokensession);
                                String permission = JSONHelper.getString(object, ConstantsImp.PREF_PERMISSION);
                                SafePreferences.getInstance().saveStringToSecurePref(ConstantsImp.PREF_PERMISSION, permission);
                                SafePreferences.getInstance().saveBooleanToSecurePref(ConstantsImp.PREF_IS_LOGGED_IN, true);
                                JSONObject helodevice = JSONHelper.json1(object, ConstantsImp.PREF_HELODEVICEDEV);
                                String helodevicedev = helodevice.getString(ConstantsImp.PREF_DEVELOPER);
                                SafePreferences.getInstance().saveStringToSecurePref(ConstantsImp.PREF_HELODEVICEDEV, helodevicedev);*/
                                callback.onSuccess(userHeloId);
                            } else {
                                if (callback != null) {
                                    callback.onFailure(ConstantsImp.CALLBACK_INVALID_CREDENTIALS);
                                }
                            }
                        }
                        else {
                            if (callback != null) {
                                callback.onFailure(ConstantsImp.CALLBACK_INVALID_CREDENTIALS);
                            }
                        }

                    } /*else if (success == 0) {
                        if (callback != null) {
                            callback.onFailure(JSONHelper.getString(object, ConstantsImp.CALLBACK_INVALID_CREDENTIALS));

                        }
                    }*/ else {
                        if (callback != null) {
                            callback.onFailure(ConstantsImp.CALLBACK_INVALID_CREDENTIALS);

                        }
                    }
                    /*else if (success == 0) {
                        if (callback != null) {
                            callback.onFailure(JSONHelper.getString(object, ConstantsImp.CALLBACK_INVALID_CREDENTIALS));

                        }
                    }*/

                }
            }
        };

        AsyncTaskHelper.execute(task);

    }

    //Vijay changes - start
    public void accountVerify(final String email,final String phone,final String prefix,final String username,final String password,final String sponsorEmail,final String sponsored, final IPinCallback callback) {

        boolean internetConn = checkInternetConnection();
        if (!internetConn) {
            callback.onFailure(Constants.NO_INTERNET);
            return;
        }
        /*if(!isServerReachable()) {
            callback.onFailure(Constants.SERVER_NOT_REACHABLE);
            return;
        }*/

        //krishna changes
        initilizePinAppKey_Token(callback);


        final SimpleTask task = new SimpleTask() {


            @Override
            protected Void doInBackground(Void... voids) {
                HashMap<String, String> map = Config.reg_confirm_account_params(mContext, app_key, app_token, username, password, sponsored,email,phone,prefix,sponsorEmail);
                HttpServerResponse response = HttpPostClient.postRequest(Config.registration(), map);
                data = response;
                return null;
            }


            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                HttpServerResponse response = (HttpServerResponse) data;
                if (data != null) {
                    if (response.hasError()) {
                        if (callback != null) {
                            callback.onFailure(Constants.CALLBACK_INTILIZE_APPKEY_TOKEN);
                        }
                        return;
                    }
                    JSONObject object = JSONHelper.json(response.response());
                    int success = JSONHelper.getInt(object, Constants.JSON_KEY_SUCCESS, -1);
                    if (success == 1) {
                        int itExist = JSONHelper.getInt(object, ConstantsImp.JSON_KEY_IT_EXIST, -1);
                        long heloId = JSONHelper.getInt(object, ConstantsImp.PREF_USERIDHELO, -1);
                        if (heloId==-1)
                        {
                            if (callback != null) {
                                callback.onFailure(ConstantsImp.CALLBACK_INVALID_HELOID);
                            }
                        }

                        else if ((itExist == 1)&&(heloId!=-1)) {
                            parseJsonPref(object);
                            String UserIDHelo = JSONHelper.getString(object, ConstantsImp.PREF_USERIDHELO);
                            SafePreferences.getInstance().saveStringToSecurePref(ConstantsImp.PREF_USERIDHELO, UserIDHelo);

                            String helo_user_id = "0";
                            try {
                                helo_user_id = SafePreferences.getInstance().getStringFromSecurePref(ConstantsImp.PREF_USERIDHELO, "0");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //long userHeloId =1;
                            long userHeloId = Long.parseLong(helo_user_id);
                            if (callback != null) {
                                callback.onSuccess(userHeloId);
                            } else {
                                if (callback != null) {
                                    callback.onFailure(ConstantsImp.CALLBACK_INVALID_CREDENTIALS);
                                }
                            }
                        }
                        else {
                            if (callback != null) {
                                callback.onFailure(ConstantsImp.CALLBACK_INVALID_CREDENTIALS);
                            }
                        }

                    } else if (success == 0) {
                        if (callback != null) {
                            callback.onFailure(JSONHelper.getString(object, ConstantsImp.CALLBACK_INVALID_CREDENTIALS));

                        }
                    }

                }
            }
        };

        AsyncTaskHelper.execute(task);
    }

    //Vijay changes - end

    private void getDecryToken() {
        SimpleTask task = new SimpleTask() {

            @Override
            protected Void doInBackground(Void... voids) {
                HashMap hashMap = new HashMap();
                HttpServerResponse response = HttpPostClient.postRequest(Config.licence(), hashMap);
                data = response;
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);


                if (data != null) {
                    HttpServerResponse response = (HttpServerResponse) data;
                    String jsonStr = response.response();
                    try {
                        JSONObject t = new JSONObject(jsonStr);
                        if(t != null){
                            int statusCode = t.getInt("statusCode");
                            if (statusCode == 1) {
                                JSONArray info = t.getJSONArray("info");
                                JSONObject jsonObject = info.getJSONObject(0);
                                String token = jsonObject.getString("decryptionToken");
                                SafePreferences.getInstance().saveStringToSecurePref(ConstantsImp.PREF_DECRY_TOKEN, token);
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        };

        AsyncTaskHelper.execute(task);

    }

    //Malik Changes
    public void verifyPin(final String pin, final IPinCallback callback) {
        //Vijay changes
        boolean internetConn = checkInternetConnection();
        if(!internetConn){
            callback.onFailure(Constants.NO_INTERNET);
            return;
        }
        /*if(!isServerReachable()) {
            callback.onFailure(Constants.SERVER_NOT_REACHABLE);
            return;
        }*/

        //krishna changes
        initilizePinAppKey_Token(callback);

        if (TextUtils.isEmpty(pin)) {
            if (callback != null) {
                callback.onFailure(Constants.CALLBACK_VERIFICATIONCODE_EMPTY);
            }
            return;
        }

        final SimpleTask task = new SimpleTask() {

            @Override
            protected Void doInBackground(Void... voids) {
                HashMap<String, String> map = Config.reg_confirm_params(mContext, app_key, app_token, pin);
                HttpServerResponse response = HttpPostClient.postRequest(Config.registration(), map);
                data = response;
                return null;
            }


            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                HttpServerResponse response = (HttpServerResponse) data;
                if (data != null) {
                    if (response.hasError()) {
                        if (callback != null) {
                            callback.onFailure(Constants.CALLBACK_INTILIZE_APPKEY_TOKEN);
                        }
                        return;
                    }
                    JSONObject object = JSONHelper.json(response.response());
                    int success = JSONHelper.getInt(object, Constants.JSON_KEY_SUCCESS, -1);
                    int isAssociate = JSONHelper.getInt(object, Constants.JSON_KEY_IS_ASSOCIATE, -1);

                    if (success == 1) {  //krishna changes
                        parseJsonPref(object);
                        String helo_user_id = "0";
                        try {
                            helo_user_id = SafePreferences.getInstance().getStringFromSecurePref(ConstantsImp.PREF_USERIDHELO, "0");
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                        long userHeloId = Long.parseLong(helo_user_id);
                        ScanBLE.getInstance(mContext).setScheduleMd5Check();
                        if (callback != null) {
                            callback.onSuccess(userHeloId);
                        }
                        //Malik new changes
//                        checkIfPurchasedApp(mContext);
                    } else {
                        if (callback != null) {
                            callback.onFailure(Constants.CALLBACK_INVALIDPIN_CODE);
                        }
                    }
                    /*if ((isAssociate==1)) {
                        if ((success == 2) || (success == 1)) {
                            try {
                                parseJsonPref(object);
                                String UserIDHelo = JSONHelper.getString(object, ConstantsImp.PREF_USERIDHELO);
                                // SafePreferences.getInstance().saveStringToSecurePref(Constants.PREF_USERIDHELO, UserIDHelo);
                                long userHeloId = Long.parseLong(UserIDHelo);
                                if (callback != null) {
                                    callback.onSuccess(userHeloId);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    }
                    else if (isAssociate==0){
                        if ((success==2)||(success==1)){
                            long userHeloId = -999;
                            if (callback != null) {
                                callback.onSuccess(userHeloId);
                            }
                        }
                    }*/
                    /********************************************************************/

                    /*else {
                        if (callback != null) {
                            callback.onFailure(Constants.CALLBACK_INVALIDPIN_CODE);
                        }
                    }*/
                }
            }
        };

        AsyncTaskHelper.execute(task);
    }

    //krishna changes
    public void CallBack_Phone(boolean byeamil, String prefix, String phoneNo, final ICallbackPin callback) {
        //vijay changes
        boolean internetConn = checkInternetConnection();
        if(!internetConn){
            callback.onFailure(Constants.NO_INTERNET);
            return;
        }
        /*if(!isServerReachable()) {
            callback.onFailure(Constants.SERVER_NOT_REACHABLE);
            return;
        }*/

        //krishna changes
        initCallbackPinAppKey_Token(callback);

        final HashMap<String, String> map;
        if (byeamil) {
            if (TextUtils.isEmpty(phoneNo)) {
                if (callback != null) {
                    callback.onFailure(Constants.CALLBACK_EMAIL_EMPTY);
                }
                return;
            } else if (!Util.isvalidEmail(phoneNo)) {
                if (callback != null) {
                    callback.onFailure(Constants.CALLBACK_VALID_EMAIL);
                }
                return;
            }
            map = Config.reg_CallBack_Email_Pin(mContext, app_key, app_token, phoneNo);
        } else {
            if (TextUtils.isEmpty(prefix)) {
                if (callback != null) {
                    callback.onFailure(Constants.CALLBACK_PREFIX_EMPTY);
                }
                return;
            }
            if (TextUtils.isEmpty(phoneNo)) {
                if (callback != null) {
                    callback.onFailure(Constants.CALLBACK_PHONENUMBER_EMPTY);
                }
                return;
            }
            map = Config.reg_CallBack_Phone_Pin(mContext, app_key, app_token, phoneNo, prefix);
        }

        //need to work as per the response
        SimpleTask task = new SimpleTask() {

            @Override
            protected Void doInBackground(Void... voids) {
                HttpServerResponse response = HttpPostClient.postRequest(Config.registration(), map);
                data = response;
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                HttpServerResponse response = (HttpServerResponse) data;
                if (data != null) {
                    if (response.hasError()) {
                        if (callback != null) {
                            callback.onFailure(Constants.SIMPLETASK_CONNECTION_ERROR);
                        }
                        return;
                    }
                    JSONObject object = JSONHelper.json(response.response());
                    int success = JSONHelper.getInt(object, Constants.JSON_KEY_SUCCESS, -1);
                    if (success == 1) {
                        if (callback != null) {
                            callback.onPinverification();
                        }
                    } else if (success == 0) {  //krishna Changes
                        if (callback != null) {
                            callback.onFailure(JSONHelper.getString(object, Constants.JSON_KEY_DESCRIPTION));

                        }
                    } else {//krishna Changes
                        if (callback != null) {
                            callback.onFailure(JSONHelper.getString(object, Constants.JSON_KEY_DESCRIPTION));
                        }
                    }
                }
            }
        };

        AsyncTaskHelper.execute(task);
    }



    private void parseJsonPref(JSONObject object) {
        try {
            String UserIDHelo = JSONHelper.getString(object, ConstantsImp.PREF_USERIDHELO);
            SafePreferences.getInstance().saveStringToSecurePref(ConstantsImp.PREF_USERIDHELO, UserIDHelo);
            PrefUtils.setString(mContext, ConstantsImp.PREF_USERIDHELO, UserIDHelo);
            String tokensession = JSONHelper.getString(object, ConstantsImp.PREF_TOKENSESSION);
            SafePreferences.getInstance().saveStringToSecurePref(ConstantsImp.PREF_TOKENSESSION, tokensession);
            String permission = JSONHelper.getString(object, ConstantsImp.PREF_PERMISSION);
            SafePreferences.getInstance().saveStringToSecurePref(ConstantsImp.PREF_PERMISSION, permission);
            if(permission.equals("0")) {
                GlobalData.isDev = ConstantsImp.DEV_YES;
            } else {
                GlobalData.isDev = ConstantsImp.DEV_NO;
            }
            PrefUtils.setBoolean(mContext, ConstantsImp.PREF_IS_LOGGED_IN, true);
            JSONObject helodevice = JSONHelper.json1(object, ConstantsImp.PREF_HELODEVICEDEV);
            String helodevicedev = helodevice.getString(ConstantsImp.PREF_DEVELOPER);
            SafePreferences.getInstance().saveStringToSecurePref(ConstantsImp.PREF_HELODEVICEDEV, helodevicedev);

            /*String macids =  new Gson().toJson(helodevice.getJSONObject(Constants.JSON_KEY_MACDEVICE));
            SafePreferences.getInstance().saveStringToSecurePref(Constants.JSON_KEY_MACDEVICE, helodevicedev);*/

            if (helodevicedev.equals("1")) {
                parseMacAddress(helodevice.getJSONArray(Constants.JSON_KEY_MACDEVICE));
                /*String restrictedMacIdsArrlist  = new Gson().toJson(GlobalData.macDevices);
                SafePreferences.getInstance().saveStringToSecurePref(Constants.JSON_KEY_MACDEVICE, restrictedMacIdsArrlist);*/
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseMacAddress(JSONArray array) {

        try {
            GlobalData.macDevices.clear();
            for (int i = 0; i < array.length(); i++) {
                GlobalData.macDevices.add(array.getJSONObject(i).getString(Constants.JSON_KEY_DEVICEID));
                SafePreferences.getInstance().saveStringToSecurePref(Constants.JSON_KEY_MACDEVICE, new Gson().toJson(GlobalData.macDevices));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        return macDevices;
    }



    //ILoginCallback
    private void initlizeAppKey_Token(final ILoginCallback callback) {
        if (!init) {
            if (callback != null) {
                callback.onFailure(Constants.CALLBACK_INTILIZE_APPKEY_TOKEN);
            }
            return;
        }

        if (TextUtils.isEmpty(app_key)) {
            if (callback != null) {
                callback.onFailure(Constants.CALLBACK_APPKEY_EMPTY);
            }
            return;
        }

        if (TextUtils.isEmpty(app_token)) {
            if (callback != null) {
                callback.onFailure(Constants.CALLBACK_TOKEN_EMPTY);
            }

        }
    }



    //IPinCallback
    private void initilizePinAppKey_Token(final IPinCallback callback) {
        if (!init) {
            if (callback != null) {
                callback.onFailure(Constants.CALLBACK_INTILIZE_APPKEY_TOKEN);
            }
            return;
        }

        if (TextUtils.isEmpty(app_key)) {
            if (callback != null) {
                callback.onFailure(Constants.CALLBACK_APPKEY_EMPTY);
            }
            return;
        }

        if (TextUtils.isEmpty(app_token)) {
            if (callback != null) {
                callback.onFailure(Constants.CALLBACK_TOKEN_EMPTY);
            }
            return;
        }
    }

    //ICallbackPin
    private void initCallbackPinAppKey_Token(final ICallbackPin callback) {
        if (!init) {
            if (callback != null) {
                callback.onFailure(Constants.CALLBACK_INTILIZE_APPKEY_TOKEN);
            }
            return;
        }

        if (TextUtils.isEmpty(app_key)) {
            if (callback != null) {
                callback.onFailure(Constants.CALLBACK_APPKEY_EMPTY);
            }
            return;
        }

        if (TextUtils.isEmpty(app_token)) {
            if (callback != null) {
                callback.onFailure(Constants.CALLBACK_TOKEN_EMPTY);
            }
            return;
        }
    }

    public boolean scan(ScanCallBack scanCallback) {
        if(isLoggedinandTokensession()){  // && ValidHelodeviceCheckHelper.isScanAllowed()
            ScanBLE.getInstance(mContext).scan(scanCallback);
            return true;
        } else {
            return false;
        }
    }

    public boolean stopScan() {
        if(isLoggedIn() && isTokenSessionLength()){
//            GlobalData.isManualStop = true;
            return ScanBLE.getInstance(mContext).stopScan();

        } else {
            return false;
        }
    }

    public boolean connect(DeviceItem deviceItem) {
        if(isLoggedIn() && isTokenSessionLength()) {
            return ScanBLE.getInstance(mContext).connect(deviceItem);
        } else {
            return false;
        }
    }

    public boolean listCharacteristics() {
        if(isLoggedIn() && isTokenSessionLength()) {
            return ScanBLE.getInstance(mContext).listCharacteristics();
        } else {
            return false;
        }
    }






    public void disconnect() {
        ScanBLE.getInstance(mContext).disconnectDevice();
    }



    public boolean getHeartRate() {
        return ScanBLE.getInstance(mContext).getHeartRate();
    }

//    public boolean identifyClientStyle() {
//        if (isGattNull()) {
//            int writestatus = WriteToDevice.identifyClientStyle(bluetoothGatt);
//            return writestatus == 1;
//        } else {
//            return false;
//        }
//    }

    public boolean updateNewTime() {
        return ScanBLE.getInstance(mContext).updateNewTime();
    }

    public boolean stopMeasuring() {
        return ScanBLE.getInstance(mContext).stopMeasuring();
    }



    public boolean unbindDevice() {
        if(isLoggedIn() && isTokenSessionLength()) {
            return ScanBLE.getInstance(mContext).unbindDevice();
        } else {
            return false;
        }
    }



    public boolean measureHR() {
        if(isLoggedinandConnected()) {
            return ScanBLE.getInstance(mContext).measureHR();
        } else {
            return false;
        }

    }

    public boolean measureBP() {
        if(isLoggedinandConnected() && GlobalData.VERSION_FIRM >= 5017) {
            return ScanBLE.getInstance(mContext).measureBP();
        } else {
            return false;
        }

    }

    /*public String getBP_PPGdata(){
        if(GlobalData.isBpPPGMeasurement){

        }else{
            return "Empty PPG Data";
        }
    }*/

    private boolean bpReset() {
        return ScanBLE.getInstance(mContext).bpReset();
    }

    public boolean measureECG() {
        if(isLoggedinandConnected() && GlobalData.VERSION_FIRM >= 5017 ) {
            return ScanBLE.getInstance(mContext).measureECG();
        }
        return false;
    }

    public boolean measureBr() {
        if(isLoggedinandConnected()) {
            return ScanBLE.getInstance(mContext).measureBr();
        } else {
            return false;
        }
    }

    public boolean measureMF() {
        if(isLoggedinandConnected()) {
            return ScanBLE.getInstance(mContext).measureMF();
        } else {
            return false;
        }
    }



    public boolean getStepsData() {
        if(isLoggedinandConnected()) {

            return ScanBLE.getInstance(mContext).getStepsData();
        } else {
            return false;
        }

    }

    private boolean sleepTime() {
        return ScanBLE.getInstance(mContext).sleepTime();
    }


    private boolean sendReviseAutoTimeData(int minutes) {
        return ScanBLE.getInstance(mContext).sendReviseAutoTimeData();
    }

    private void sendBpData(Context context) {

    }

    public boolean startStepsHRDynamicMeasurement() {
        if(isLoggedinandConnected() && GlobalData.VERSION_FIRM >= 5017) {
            GlobalData.isDynamic = true;
            return ScanBLE.getInstance(mContext).getDynamicStepsHeartRateMeasure(1);
        } else {
            ScanBLE.getInstance(mContext).sendMeasureFailureBroadcast(Constants.FIRMWARE_NOT_SUPPORTED_FOR_DYN);
        }
        return false;
    }

    public boolean stopStepsHRDynamicMeasurement() {
        if (isLoggedinandConnected() /*&& GlobalData.VERSION_FIRM >= 5017*/) {
                GlobalData.isDynamic = false;
                return ScanBLE.getInstance(mContext).getDynamicStepsHeartRateMeasure(0);
        } else {
            ScanBLE.getInstance(mContext).sendMeasureFailureBroadcast(Constants.FIRMWARE_NOT_SUPPORTED_FOR_DYN);
        }
        return false;
    }

    //Added Here
    public boolean getUV() {
        if (isLoggedinandConnected() && GlobalData.VERSION_FIRM >= 5017 && ScanBLE.getInstance(mContext).isLXplus()) {
                return ScanBLE.getInstance(mContext).getUV();
        } else {
            ScanBLE.getInstance(mContext).sendMeasureFailureBroadcast(Constants.FIRMWARE_NOT_SUPPORTED_FOR_UV);
        }
        return false;
    }


    //Abhijeet changes
    public boolean measureOxygen() {
        if (isLoggedinandConnected() && ScanBLE.getInstance(mContext).isLXplus() && GlobalData.VERSION_FIRM >= 5017 ) {

                return ScanBLE.getInstance(mContext).getOxygen();
        } else {
            ScanBLE.getInstance(mContext).sendMeasureFailureBroadcast(Constants.FIRMWARE_NOT_SUPPORTED_FOR_OXY);
        }
        return false;
    }

    //6-3-18

    public boolean startGreenMeasurement() {
        if(isLoggedinandConnected() && GlobalData.VERSION_FIRM >= 5017) {
            return ScanBLE.getInstance(mContext).startGreenMeasurement();
        }
        return false;
    }

    public void stopGreenMeasurement() {
        if (isLoggedinandConnected() && GlobalData.VERSION_FIRM >= 5017) {
            ScanBLE.getInstance(mContext).stopGreenMeasurement();
        }
    }


    public boolean startRnIRMeasurement() {
        return (isLoggedinandConnected() && GlobalData.VERSION_FIRM >= 5017 &&  ScanBLE.getInstance(mContext).isLXplus() && ScanBLE.getInstance(mContext).getRnIRData());
    }

    public void stopRnIRMeasurement() {
        if (isLoggedinandConnected() && GlobalData.VERSION_FIRM >= 5017 && ScanBLE.getInstance(mContext).isLXplus()) {
            ScanBLE.getInstance(mContext).stopRnIRData();
        }
    }

    public boolean skinCalibration() {
        if (isLoggedinandConnected()) {
            if(GlobalData.VERSION_FIRM >= 5124) {
                return ScanBLE.getInstance(mContext).skinCalibration(true);
            } else {
                ScanBLE.getInstance(mContext).sendMeasureFailureBroadcast(Constants.FIRMWARE_NOT_SUPPORTED_FOR_CALIB);
            }
        }
        return false;

    }

    public boolean deviceCalibration() {
        if (isLoggedinandConnected()) {
            if(GlobalData.VERSION_FIRM >= 5124) {
                return ScanBLE.getInstance(mContext).skinCalibration(false);
            } else {
                ScanBLE.getInstance(mContext).sendMeasureFailureBroadcast(Constants.FIRMWARE_NOT_SUPPORTED_FOR_CALIB);
            }
        }
        return false;

    }

    public boolean accelerometer(boolean isStart) {
        if (isLoggedinandConnected()) {
            if(GlobalData.VERSION_FIRM >= 5124 && ScanBLE.getInstance(mContext).isLXplus()) {
                return ScanBLE.getInstance(mContext).accelerometer(isStart);
            } else {
                ScanBLE.getInstance(mContext).sendMeasureFailureBroadcast(Constants.FIRMWARE_NOT_SUPPORTED_FOR_ACCEL);

            }
        }
        return false;
    }


    public boolean scheduleMeasure(int mins) {
        if (isLoggedinandConnected()) {
            return ScanBLE.getInstance(mContext).scheduleMeasure(mins);
        }
        return false;
    }


    //----------//

    private boolean checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }

    private boolean isServerReachable() {
        try {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy().Builder()
            InetAddress inetAddress = InetAddress.getByName(Config.registration().replace("https://", ""));
            return !inetAddress.toString().equals("");
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private boolean isLoggedinandConnected() {
        return ( isLoggedIn() && isTokenSessionLength() && GlobalData.status_Connected); //  && isCompatible()
    }

    private boolean isLoggedinandTokensession() {
        return (isLoggedIn() && isTokenSessionLength());
    }


    //Malik new changes
    private void checkIfPurchasedApp(final Context context){
        if(installedFromPlayStore(context)){
            if(!alreadyChecked()){
                SimpleTask task = new SimpleTask() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        //send user  heloid to server along with pkg name

                        String helo_id = "0";
                        try {
                            helo_id = SafePreferences.getInstance().getStringFromSecurePref(ConstantsImp.PREF_USERIDHELO, "0");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        HashMap<String, String> map = Config.checkPaidAppParams(helo_id, mContext.getPackageName()); //mContext.getPackageName()
//                        HttpServerResponse response = HttpPostClient.postRequest(Config.checkPaidApp(), map);
//                        data = response;

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        setCheckPaidApp();
                        HttpServerResponse response = (HttpServerResponse) data;
                        if (data != null) {
                            if (response.hasError()) {
                                return;
                            }
                            JSONObject object = JSONHelper.json(response.response());
                            DebugLogger.i("Connector", object.toString());
                        }
                    }
                };

                AsyncTaskHelper.execute(task);
            }
        }
    }

    private boolean installedFromPlayStore(Context context){
        PackageManager packageManager = context.getPackageManager();
        String source = packageManager.getInstallerPackageName(context.getPackageName());
        return !TextUtils.isEmpty(source);//source empty means not installed from play or Amazon store
    }

    private boolean alreadyChecked(){
        try {
            return SafePreferences.getInstance().getBooleanFromSecurePref(CHECK_PAID_APP, false);
        } catch (Exception e) {

        }
        return false;
    }

    private void setCheckPaidApp(){
        SafePreferences.getInstance().saveBooleanToSecurePref(CHECK_PAID_APP,true);

    }

    /*private boolean isNotLXPlus() {
        return !GlobalData.GET_BLE_NAME.equals(Constants.DEVICE_SUBNAME6); //DEVICE_SUBNAME6
    }

    private void broadcastLxPlus(String function) {
        Intent intent = new Intent();
        intent.setAction(ConstantsImp.BROADCAST_ACTION_LX_PLUS_NOT_ALLOWED);
        intent.putExtra(ConstantsImp.INTENT_LX_PLUS, Constants.lxPlusNotALlowed.replaceFirst("XXX", function));
        mContext.sendBroadcast(intent);
    }*/

    static String CHECK_PAID_APP = "check_paid_app";

    //Malik new changes end




    private String getSelfChecksum(Context context){
        String path = getApkPath(context);
        String hash = "";
        if(!TextUtils.isEmpty(path)){
            try {
                hash = HashUtil.calculateMD5(new File(path));
                DebugLogger.i("APPKEY", hash+" "+app_key);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return hash;
    }

    private static String getApkPath(Context context) {
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






}

