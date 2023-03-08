package com.worldgn.connector;

/**
 * Created by mudassarhussain on 9/13/17.
 */

public interface IPinCallback {
    public void onSuccess(long heloUserId);
    public void onFailure(String description);
}
