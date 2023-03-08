package com.worldgn.connector;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by WGN on 27-09-2017.
 */

interface APIService {

    @Multipart
    @POST("/heloapidati.php")
    Call<ApiResponse> saveMeasurement(@Part("token") RequestBody token, @Part("deviceid") RequestBody deviceid, @Part("devicedate") RequestBody devicedate);


    @POST("/myheloconnector.php")
    Call<CompatibilityResponse> compatibiltycheck(@Body CompatabiltyRequest compatabiltyRequest);

    @Headers({"identifier:lIjSwe6732YundErsd", "Content-Type:application/x-www-form-urlencoded"})
    @FormUrlEncoded
    @POST("/md5checksum.php")
    Call<Md5APIResponse> md5Checksum(@Field("md5checksum") String md5CheckSum, @Field("appkey") String appKey);

}
