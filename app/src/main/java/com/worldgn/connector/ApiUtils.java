package com.worldgn.connector;

/**
 * Created by WGN on 27-09-2017.
 */

class ApiUtils {

    private ApiUtils() {}



    public static APIService getAPIService() {

        return ApiClient.getClient(Config.BASE_URL).create(APIService.class);
    }

    public static APIService getDevServcie() {
        return ApiClient.getClient(Config.compatibility_url).create(APIService.class);
    }

    public static APIService getMD5Service() {
        return ApiClient.getClient(Config.md5_url).create(APIService.class);
    }
}
