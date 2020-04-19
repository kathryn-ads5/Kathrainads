package com.kathryn.kathryn.packages.SquareUpServices;

import com.google.gson.JsonObject;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface SquareUpEndPoint {
    @POST("payments")
    Call<ResponseBody> purchase(@HeaderMap Map<String, String> headers, @Body JsonObject parameters);
}
