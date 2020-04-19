package com.kathryn.kathryn.utills.NoInternet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kathryn.kathryn.utills.SettingsMain;

/**
 * Created by GlixenTech on 3/21/2018.
 */

public class NetwordStateManager extends BroadcastReceiver {
    private static int counter = 0;
    private AppLifeCycleManager appLifeCycleManager;
//    DatabaseReference myRef;
//    FirebaseDatabase database;
//    ChatUserModel userModel;

    @Override
    public void onReceive(Context context, Intent intent) {


        int status = SettingsMain.getConnectivityStatusString(context);
//        database = FirebaseDatabase.getInstance();
//        myRef = database.getReference("UserLogin");
        Log.e("network reciever", "Sulod sa network reciever" + status + intent.getAction());
        if (!"android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            Log.d("info check net", "cccc");

            appLifeCycleManager = AppLifeCycleManager.get(context);
            if (appLifeCycleManager.isForeground()) {
                if (status == SettingsMain.NETWORK_STATUS_NOT_CONNECTED) {
                    Log.d("info check disconnect", "disconnet");
//                    if (!SettingsMain.getUserId().equals("0")) {
//
//                        ChatUserModel userModel = new ChatUserModel(false, SettingsMain.getUserId());
//                        myRef.child(SettingsMain.getUserId()).setValue(userModel);
//                    }
                }
            }
        } else {
            appLifeCycleManager = AppLifeCycleManager.get(context);
            if (appLifeCycleManager.isForeground()) {
                if (status == SettingsMain.TYPE_WIFI) {
                    Log.d("info check wifi", "connect");
//                    if (!SettingsMain.getUserId().equals("0")) {
//                        userModel = new ChatUserModel(true, SettingsMain.getUserId());
//                        myRef.child(SettingsMain.getUserId()).setValue(userModel);
//                    }
                }
                if (status == SettingsMain.TYPE_MOBILE) {
                    Log.d("info check mobile", "connect");
//                    if (!SettingsMain.getUserId().equals("0")) {
//
//                        userModel = new ChatUserModel(true, SettingsMain.getUserId());
//                        myRef.child(SettingsMain.getUserId()).setValue(userModel);
//                    }
                }
                if (status == SettingsMain.NETWORK_STATUS_NOT_CONNECTED) {

                    Log.d("info check net", "disconnet");

                }
            }
        }
        if (appLifeCycleManager.isBackground()) {
            Log.d("info check net", "app background");
        }
    }
}
