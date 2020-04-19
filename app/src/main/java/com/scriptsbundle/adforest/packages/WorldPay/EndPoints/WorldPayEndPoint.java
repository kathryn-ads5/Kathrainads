package com.kathryn.kathryn.packages.WorldPay.EndPoints;

import com.google.gson.JsonObject;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface WorldPayEndPoint {
    @POST("v1/orders")
    Call<ResponseBody> chargeOrder(@HeaderMap Map<String, String> headers, @Body JsonObject params);
}
