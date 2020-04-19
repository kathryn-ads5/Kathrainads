package com.kathryn.kathryn.utills.NoInternet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.kathryn.kathryn.utills.SettingsMain;

/**
 * Created by GlixenTech on 3/21/2018.
 */

public class AppLifeCycleManager implements Application.ActivityLifecycleCallbacks {

    public static final String TAG = AppLifeCycleManager.class.getName();
    private static final long CHECK_DELAY = 500;
    @SuppressLint("StaticFieldLeak")
    static Context context;
    private static AppLifeCycleManager instance;
    private boolean foreground = false, paused = true;
    private Handler handler = new Handler();
    private List<Listener> listeners = new CopyOnWriteArrayList<Listener>();
    private Runnable check;

    public static AppLifeCycleManager init(Application application) {
        if (instance == null) {
            instance = new AppLifeCycleManager();
            application.registerActivityLifecycleCallbacks(instance);
        }
        return instance;
    }

    public static AppLifeCycleManager get(Application application) {
        if (instance == null) {
            init(application);
        }
        return instance;
    }

    public static AppLifeCycleManager get(Context ctx) {
        if (instance == null) {
            Context appCtx = ctx.getApplicationContext();
            if (appCtx != null) {
                context = appCtx;
            }
            if (appCtx instanceof Application) {
                init((Application) appCtx);
            }
            throw new IllegalStateException(
                    "Foreground is not initialised and " +
                            "cannot obtain the Application object");
        }
        return instance;
    }

    public static AppLifeCycleManager get() {
        if (instance == null) {
            throw new IllegalStateException(
                    "Foreground is not initialised - invoke " +
                            "at least once with parameterised init/get");
        }
        return instance;
    }

    public boolean isForeground() {
        return foreground;
    }

    public boolean isBackground() {
        return !foreground;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        paused = false;
        boolean wasBackground = !foreground;
        foreground = true;

        if (check != null)
            handler.removeCallbacks(check);

        if (wasBackground) {
            Log.d(TAG, "info went foreground");
//            if (!SettingsMain.getUserId().equals("0")) {
//                FirebaseDatabase database = FirebaseDatabase.getInstance();
//                DatabaseReference myRef = database.getReference("UserLogin");
//                ChatUserModel userModel = new ChatUserModel(true, SettingsMain.getUserId());
//                myRef.child(SettingsMain.getUserId()).setValue(userModel);
//            }
//            adforest__checkInternet(activity);
            for (Listener l : listeners) {
                try {
                    l.onBecameForeground();
                } catch (Exception exc) {
                    Log.e(TAG, "Listener threw exception!", exc);
                }
            }
        } else {
            Log.d(TAG, "info still foreground");
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        paused = true;

        if (check != null)
            handler.removeCallbacks(check);

        handler.postDelayed(check = () -> {
            if (foreground && paused) {
                foreground = false;
                Log.d(TAG, "info went background");
//                    if (!SettingsMain.getUserId().equals("0")) {
//                        FirebaseDatabase database = FirebaseDatabase.getInstance();
//                        DatabaseReference myRef = database.getReference("UserLogin");
//                        ChatUserModel userModel = new ChatUserModel(false, SettingsMain.getUserId());
//                        myRef.child(SettingsMain.getUserId()).setValue(userModel);
//                    }
                for (Listener l : listeners) {
                    try {
                        l.onBecameBackground();
                    } catch (Exception exc) {
                        Log.d(TAG, "Listener threw exception!", exc);
                    }
                }
            } else {
                Log.d(TAG, "info still foreground");
            }
        }, CHECK_DELAY);
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    private void adforest__checkInternet(Activity activity) {

        if (SettingsMain.getConnectivityStatus(activity) == 0) {

            if (SettingsMain.isInternetReceiverEnabled(activity))
                SettingsMain.disableInternetReceiver(activity);
        } else {

            if (!SettingsMain.isInternetReceiverEnabled(activity))
                SettingsMain.enableInternetReceiver(activity);
        }
    }

    public interface Listener {

        void onBecameForeground();

        void onBecameBackground();

    }
}
