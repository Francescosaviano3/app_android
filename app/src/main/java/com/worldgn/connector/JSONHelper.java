package com.worldgn.connector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mudassarhussain on 5/15/17.
 */

class JSONHelper {

    public static boolean isEmpty(JSONObject object,String key){
        return json(object,key).length() == 0;
    }

    public static JSONArray json(JSONObject object, String key){
        try {
            return object.getJSONArray(key);
        } catch (JSONException e) {
            //e.printStackTrace();
        }
        return new JSONArray();
    }

    public static int count(JSONObject object, String key){
        try {
            return object.getJSONArray(key).length();
        } catch (JSONException e) {
            //e.printStackTrace();
        }
        return 0;
    }

    public static JSONObject json1(JSONObject object, String key){
        try {
            return object.getJSONObject(key);
        } catch (JSONException e) {
            //e.printStackTrace();
        }
        return new JSONObject();
    }

    public static JSONObject json(String data){
        try {
            return new JSONObject(data);
        } catch (JSONException e) {
            //e.printStackTrace();
        }
        return new JSONObject();
    }

    public static JSONObject json(JSONArray array,int index){
        try {
            return array.getJSONObject(index);
        } catch (JSONException e) {
            //e.printStackTrace();
        }
        return new JSONObject();
    }

    public static int getInt(JSONObject object, String key){
        return getInt(object,key,0);
    }

    public static int getIntPstv(JSONObject object, String key,int defaultvalue){
        int value = getInt(object,key,defaultvalue);
        return value < 0 ? defaultvalue : value;
    }

    public static int getInt(JSONObject object, String key,int defaultvalue){
        try {
            return object.getInt(key);
        } catch (JSONException e) {
            //e.printStackTrace();
        }
        return defaultvalue;
    }

    public static long getLong(JSONObject object, String key){
        try {
            return object.getLong(key);
        } catch (JSONException e) {
            //e.printStackTrace();
        }
        return 0;
    }

    public static String getString(JSONObject object, String key){
        try {
            return object.getString(key);
        } catch (JSONException e) {
            //e.printStackTrace();
        }
        return "";
    }

    public static boolean getBoolean(JSONObject object, String key,boolean defaultval){
        try {
            return object.getBoolean(key);
        } catch (JSONException e) {
            //e.printStackTrace();
        }
        return defaultval;
    }

    public static boolean has(JSONObject object,String key){
        return object.has(key);
    }

    public static void put(JSONArray array,JSONObject object){
        array.put(object);
    }
}
