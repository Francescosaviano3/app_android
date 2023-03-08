package com.worldgn.connector;

/**
 * Created by Krishna Rao on 15/09/2017.
 */

public interface ICallbackPin {
    public void onPinverification();
    public void onFailure(String description);
}
