package com.worldgn.connector;

/**
 * Created by mudassarhussain on 9/12/17.
 */

public interface ILoginCallback {
    public void onSuccess(long heloUserId);
    public void onPinverification();
    public void onFailure(String description);
    public void accountVerification();
}
