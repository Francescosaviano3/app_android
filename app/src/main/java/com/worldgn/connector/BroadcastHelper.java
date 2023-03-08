package com.worldgn.connector;

import android.content.Context;
import android.content.Intent;

/**
 * Created by mudassarhussain on 2/8/18.
 */

public class BroadcastHelper {
    public static void sendBroadcast(Context context, Intent intent){
        sendBroadcast(context,intent,false);
    }

    public static void sendSysBroadcast(Context context, Intent intent){
        sendBroadcast(context,intent,true);
    }

    private static void sendBroadcast(Context context, Intent intent, boolean systemlevel){
        if(!systemlevel){
            intent.setPackage(context.getPackageName());
        }
        context.sendBroadcast(intent);
    }
}
