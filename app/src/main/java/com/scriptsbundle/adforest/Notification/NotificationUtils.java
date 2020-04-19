package com.kathryn.kathryn.Notification;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Patterns;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;

import com.kathryn.kathryn.R;
import com.kathryn.kathryn.home.HomeActivity;
import com.kathryn.kathryn.messages.ChatActivity;
import com.kathryn.kathryn.utills.SettingsMain;

import static com.facebook.FacebookSdk.getApplicationContext;

public class NotificationUtils {
    private static String TAG = NotificationUtils.class.getSimpleName();
    final String CHANNEL_ONE_ID = "scriptsbundlet";
    SettingsMain settingsMain;
    private Context mContext;
    private String CHANNEL_ID = "scriptsbundlet";
    private String CHANNEL_NAME = "Notifications";
    private NotificationManager mManager;

    NotificationUtils(Context mContext) {
        this.mContext = mContext;
        settingsMain = new SettingsMain(mContext);
    }

    /**
     * Method checks if the app is in background or not
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    // Clears notification tray messages
    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    void showNotificationMessage(final String title, final String message, final Date timeStamp, String ad_id,
                                 String recieverId, String senderId, String type, String imageURL, String topic) {
//        if (TextUtils.isEmpty(message))
//            return;

        // notification icon
        PendingIntent resultPendingIntent = null;
        final int icon = R.mipmap.ic_launcher;
        if (!settingsMain.getUserLogin().equals("0") && topic.equals("broadcast")) {
            if (isAppIsInBackground(mContext)) {
                Intent in = new Intent(mContext, HomeActivity.class);
                settingsMain.setNotificationTitle(title);
                settingsMain.setNotificationMessage(message);
                resultPendingIntent = PendingIntent.getActivity(mContext, 0, in, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    mContext);
            if (!TextUtils.isEmpty(imageURL)) {

                if (imageURL != null && imageURL.length() > 4 && Patterns.WEB_URL.matcher(imageURL).matches()) {

                    Bitmap bitmap = getBitmapFromURL(imageURL);

                    if (bitmap != null) {
                        showNotification(bitmap, mBuilder, icon, title, message, timeStamp, resultPendingIntent, senderId);
                    } else {
                        showNotification(null, mBuilder, icon, title, message, timeStamp, resultPendingIntent, ad_id);
                    }
                }
            } else {
                showNotification(null, mBuilder, icon, title, message, timeStamp, resultPendingIntent, ad_id);
            }
        }
        if (!settingsMain.getUserLogin().equals("0") && topic.equals("chat")) {
            Intent in = new Intent(mContext, ChatActivity.class);
            in.putExtra("adId", ad_id);
            in.putExtra("senderId", senderId);
            in.putExtra("recieverId", recieverId);
            in.putExtra("type", type);

            resultPendingIntent = PendingIntent.getActivity(mContext, 0, in, PendingIntent.FLAG_UPDATE_CURRENT);
            final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    mContext);
            showNotification(null, mBuilder, icon, title, message, timeStamp, resultPendingIntent, ad_id);
        }

    }

    private Notification.Builder getNotification(Bitmap bitmap, int icon, String title, String message, Date timeStamp,
                                                 PendingIntent resultPendingIntent, String ad_ID) {
        createChannels();
        Notification.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            if (bitmap == null) {
                builder = new Notification.Builder(getApplicationContext(), CHANNEL_ID).setSmallIcon(icon).setTicker(title).setWhen(0)
                        .setAutoCancel(true)
                        .setContentTitle(title)
                        .setContentIntent(resultPendingIntent)
                        .setStyle(new Notification.BigTextStyle().bigText(message))
                        .setWhen(timeStamp.getTime())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                        .setContentText(message);

            } else {
                builder = new Notification.Builder(getApplicationContext(), CHANNEL_ID).setSmallIcon(icon).setTicker(title).setWhen(0)
                        .setAutoCancel(true)
                        .setContentTitle(title)
                        .setContentIntent(resultPendingIntent)
                        .setStyle(new Notification.BigPictureStyle().bigPicture(bitmap).setBigContentTitle(title).setSummaryText(Html.fromHtml(message)))
                        .setWhen(timeStamp.getTime())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                        .setContentText(message);
            }
        } else {

        }
        return builder;
    }

    public void createChannels() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // create android channel
            NotificationChannel androidChannel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            // Sets whether notifications posted to this channel should display notification lights
            androidChannel.enableLights(true);
            // Sets whether notification posted to this channel should vibrate.
            androidChannel.enableVibration(true);
            // Sets the notification light color for notifications posted to this channel
            androidChannel.setLightColor(Color.GREEN);
            // Sets whether notifications posted to this channel appear on the lockscreen or not
            androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            getManager().createNotificationChannel(androidChannel);
        } else {

        }
    }

    private NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    private void showNotification(Bitmap bitmap, NotificationCompat.Builder mBuilder, int icon, String title,
                                  String message, Date timeStamp, PendingIntent resultPendingIntent, String ad_ID) {

        NotificationCompat.BigTextStyle inboxStyle = new NotificationCompat.BigTextStyle();

        inboxStyle.bigText(message);
//        inboxStyle.addLine(message);
        Notification notification = null;
        if (bitmap == null) {
            notification = showNotificationWithoutImage(null, mBuilder, icon, title, message, timeStamp, resultPendingIntent, ad_ID);
        } else {
            notification = showNotificationWithImage(bitmap, mBuilder, icon, title, message, timeStamp, resultPendingIntent, ad_ID);
        }

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Integer.parseInt(ad_ID), notification);

    }

    private Notification showNotificationWithoutImage(Bitmap bitmap, NotificationCompat.Builder mBuilder, int icon, String title,
                                                      String message, Date timeStamp, PendingIntent resultPendingIntent, String ad_ID) {
        Notification notification;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = getNotification(null, icon, title, message, timeStamp, resultPendingIntent, ad_ID).build();

        } else {
            notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setWhen(timeStamp.getTime())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                    .setContentText(message)
                    .build();
        }
        return notification;
    }

    private Notification showNotificationWithImage(Bitmap bitmap, NotificationCompat.Builder mBuilder, int icon, String title,
                                                   String message, Date timeStamp, PendingIntent resultPendingIntent, String ad_ID) {
        Notification notification;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = getNotification(bitmap, icon, title, message, timeStamp, resultPendingIntent, ad_ID).build();

        } else {
            notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setStyle(showBigPictureStyle(title, message, bitmap))
                    .setWhen(timeStamp.getTime())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                    .setContentText(message)
                    .build();
        }
        return notification;
    }

    private NotificationCompat.BigPictureStyle showBigPictureStyle(String title, String message, Bitmap bitmap) {
        return new NotificationCompat.BigPictureStyle().setBigContentTitle(title).setSummaryText(Html.fromHtml(message).toString()).bigPicture(bitmap);
    }

    private Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Playing notification sound
    void playNotificationSound() {
        Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        MediaPlayer mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(mContext, defaultRingtoneUri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
            mediaPlayer.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        try {
//            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
//                    + "://" + mContext.getPackageName() + "/raw/notification");
//            Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
//            r.play();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}
