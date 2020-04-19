package com.kathryn.kathryn;

import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.ads.MobileAds;
import com.google.android.libraries.places.api.Places;

import com.kathryn.kathryn.R;
import com.kathryn.kathryn.helper.LocaleHelper;
import com.kathryn.kathryn.utills.NoInternet.AppLifeCycleManager;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        MobileAds.initialize(this, String.valueOf(R.string.Admob_app_id));
        AppLifeCycleManager.init(this);
        String apiKey = getString(R.string.places_api_key);
        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
        MultiDex.install(this);
    }
}
