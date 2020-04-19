package com.kathryn.kathryn.packages.Authorize2;

import com.google.gson.JsonObject;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface EndPointsAuthorize {
    @POST("request.api/")
    Call<ResponseBody> getToken(@Body JsonObject headers, @HeaderMap Map<String, String> map);
}
