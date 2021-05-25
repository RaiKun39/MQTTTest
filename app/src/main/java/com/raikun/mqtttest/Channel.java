package com.raikun.mqtttest;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class Channel extends Application {

    public static final String CHANNEL_1_ID = "Channel 1";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    public void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_1_ID,
                    "Channel 1", NotificationManager.IMPORTANCE_DEFAULT);
            channel1.setDescription("This is channel 1.");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);

        }
    }
}
