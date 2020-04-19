package com.kathryn.kathryn.packages.Authorize2;

import com.google.gson.annotations.SerializedName;

public class AuthorizeModel {
    @SerializedName("access_token")
    public String token;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }



}
