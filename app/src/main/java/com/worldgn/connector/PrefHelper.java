package com.worldgn.connector;

import android.content.Context;

/**
 * Created by mudassarhussain on 5/24/17.
 */

class PrefHelper extends PreferenceBase {
    Context mContext;
    public PrefHelper(Context context){
        mContext = context;
    }

    @Override
    public String prefName() {
        return "connector_sdk";
    }

    @Override
    public Context getContext() {
        return mContext;
    }
}
