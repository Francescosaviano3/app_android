package com.worldgn.connector;

import android.bluetooth.BluetoothAdapter;
import android.os.Build;
import android.util.Log;



import net.vidageek.mirror.dsl.Mirror;

/**
 * Created by WGN on 19-09-2017.
 */

class BleHelper {

    public static byte[] getSelfBlueMac(){
//        prefHelper = new PrefHelper(context);

        String MacStr="";
        String bindMax = "";
        try {
            bindMax = SafePreferences.getInstance().getStringFromSecurePref("bindMax", "");
        } catch (Exception e){
            e.printStackTrace();
        }

        if(bindMax.equals("")){
            MacStr = BluetoothAdapter.getDefaultAdapter().getAddress();

            /*if(Build.VERSION.SDK_INT>=23){
//				MacStr=getMac();
//				if(MacStr==null){
//				}
                MacStr=getBtAddressViaReflection();
            }else{
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                MacStr= bluetoothAdapter.getAddress();
            }*/
            SafePreferences.getInstance().saveStringToSecurePref("bindMax", MacStr);
        }else{
            try {
                MacStr=SafePreferences.getInstance().getStringFromSecurePref("bindMax", "");
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        if(MacStr!=null && !MacStr.equals("")){

            String[] Mac=MacStr.split(":");
            byte[] data = new byte[6];
            data[0]= (byte) Integer.parseInt(Mac[0], 16);
            data[1]= (byte) Integer.parseInt(Mac[1], 16);
            data[2]= (byte) Integer.parseInt(Mac[2], 16);
            data[3]= (byte) Integer.parseInt(Mac[3], 16);
            data[4]= (byte) Integer.parseInt(Mac[4], 16);
            data[5]= (byte) Integer.parseInt(Mac[5], 16);
            return data;
        }else{
            return null;
        }
    }

    private static String getBtAddressViaReflection() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Object bluetoothManagerService = new Mirror().on(bluetoothAdapter).get().field("mService");
        if (bluetoothManagerService == null) {
            Log.w("tattatat", "couldn't find bluetoothManagerService");
            return null;
        }
        Object address = new Mirror().on(bluetoothManagerService).invoke().method("getAddress").withoutArgs();
        if (address != null && address instanceof String) {
            Log.w("tattatat", "using reflection to get the BT MAC address: " + address);
            return (String) address;
        } else {
            return null;
        }
    }

}
