package com.kathryn.kathryn.packages.PaystackControllers;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Path;

public interface PaystackEndPoint {
    @GET("{ReferenceId}")
    Call<ResponseBody> verifyPurchase(@Path("ReferenceId") String ReferenceId, @HeaderMap Map<String, String> headers);
}
