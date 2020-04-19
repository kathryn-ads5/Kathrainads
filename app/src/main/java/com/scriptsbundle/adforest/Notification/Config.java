package com.kathryn.kathryn.Notification;

/**
 * Created by apple on 11/23/17.
 */

public class Config {
    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    public static final String SHARED_PREF = "ah_firebase";
    // id to handle the notification in the notification tray
    public static int NOTIFICATION_ID = 100;
}
