package com.IAppDevelopment.virtual_marathon.Dialog_and_Notification;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

/**
 * This divider is designed to enable the server to run in the background and is a necessary means.
 */
public class Notification extends Application

    {
        public static final String CHANNEL_ID = "ServiceChannel";
        public static final String CHANNEL_2_ID = "erviceChanne2";
        public static final String CHANNEL_3_ID = "ServiceChanne3";
        public static final String CHANNEL_4_ID = "ServiceChanne4";

        public void onCreate() {
        super.onCreate();

        createNotificatioChannel();
    }
        private void createNotificatioChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT

            );
            NotificationChannel serviceChannel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Service Channel2",
                    NotificationManager.IMPORTANCE_DEFAULT

            );
            NotificationChannel serviceChannel3 = new NotificationChannel(
                    CHANNEL_3_ID,
                    "Service Channel3",
                    NotificationManager.IMPORTANCE_DEFAULT

            );
            NotificationChannel serviceChannel4 = new NotificationChannel(
                    CHANNEL_4_ID,
                    "Service Channel4",
                    NotificationManager.IMPORTANCE_DEFAULT

            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
            manager.createNotificationChannel(serviceChannel2);
            manager.createNotificationChannel(serviceChannel3);
            manager.createNotificationChannel(serviceChannel4);
        }
    }
}
