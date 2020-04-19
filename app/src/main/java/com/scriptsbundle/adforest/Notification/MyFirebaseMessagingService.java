package com.kathryn.kathryn.Notification;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executor;

import com.kathryn.kathryn.utills.SettingsMain;

/**
 * Created by apple on 11/23/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

      sendRegistrationToServer(s);
      storeRegIdInPref(s);
    }
    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Firebase Regid", token);
        Log.e("FireBase", token);
        editor.commit();
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                handleDataMessage(remoteMessage);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void handleDataMessage(RemoteMessage remoteMessage) {

        try {

            String title, message;

            if (remoteMessage.getData().get("topic").equals("broadcast")) {
                SettingsMain settingsMain = new SettingsMain(getApplicationContext());
                JSONObject broadcast = new JSONObject(remoteMessage.getData().get("data"));
                Log.d("info broadcat", broadcast.toString());
                title = broadcast.getString("title");
                message = broadcast.getString("message");
                String image = broadcast.getString("image");
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();

                Date currentTime = Calendar.getInstance().getTime();

                if (TextUtils.isEmpty(image)) {
                    settingsMain.setNotificationImage("");
                    showNotificationMessage(getApplicationContext(), title, message, currentTime, remoteMessage.getData().get("topic_id"), "", "1", "", image,
                            broadcast.getString("topic"));
                } else {
                    settingsMain.setNotificationImage(broadcast.getString("image_full"));
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, currentTime, remoteMessage.getData().get("topic_id"), "", "1", ""
                            , image, remoteMessage.getData().get("topic"));
                }
            }
            if (remoteMessage.getData().get("topic").equals("chat")) {
                title = remoteMessage.getData().get("title");
                message = remoteMessage.getData().get("message");
                String ad_id = remoteMessage.getData().get("adId");
                String recieverId = remoteMessage.getData().get("recieverId");
                String senderId = remoteMessage.getData().get("senderId");
                String type = remoteMessage.getData().get("type");


                // app is in foreground, broadcast the push message
                JSONObject json = new JSONObject(remoteMessage.getData().get("chat"));
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("date", json.getString("date"));
                pushNotification.putExtra("img", json.getString("img"));
                pushNotification.putExtra("text", json.getString("text"));
                pushNotification.putExtra("type", json.getString("type"));

                pushNotification.putExtra("adIdCheck", ad_id);
                pushNotification.putExtra("recieverIdCheck", recieverId);
                pushNotification.putExtra("senderIdCheck", senderId);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();

                Date currentTime = Calendar.getInstance().getTime();
                showNotificationMessage(getApplicationContext(), title, message, currentTime, ad_id, recieverId, senderId,
                        type, "", remoteMessage.getData().get("topic"));
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception:" + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void showNotificationMessage(Context context, String title, String message, Date timeStamp, String ad_id, String recieverId, String senderId,
                                         String type, String imageURL, String topic) {
        notificationUtils = new NotificationUtils(context);
        notificationUtils.showNotificationMessage(title, message, timeStamp, ad_id, recieverId, senderId, type, imageURL, topic);
    }

    /**
     * Showing notification with text and image
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void showNotificationMessageWithBigImage(Context context, String title, String message, Date timeStamp, String ad_id, String recieverId,
                                                     String senderId, String type, String imageURL, String topic) {
        notificationUtils = new NotificationUtils(context);
        notificationUtils.showNotificationMessage(title, message, timeStamp, ad_id, recieverId, senderId, type, imageURL, topic);
    }
}