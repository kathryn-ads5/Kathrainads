package com.kathryn.kathryn.utills.Network;

import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by apple on 12/18/17.
 */

public class AuthenticationInterceptor implements Interceptor {

    Context context;
    private String authToken;

    public AuthenticationInterceptor(String token, Context context) {
        this.authToken = token;
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder builder = original.newBuilder()
                .header("Authorization", authToken);

        Request request = builder.build();
        return chain.proceed(request);
    }
}
