package com.example.studysimplifier01.ui.settings;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;


import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.studysimplifier01.R;
import com.example.studysimplifier01.main.Values;
import com.example.studysimplifier01.main.MainActivity;

public class MyReceiver extends BroadcastReceiver {

private NotificationManagerCompat notificationManager;
    @Override
    public void onReceive(Context context, Intent intent) {

            notificationManager = NotificationManagerCompat.from(context);
            createNotificationChannel();

            Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,Values.ALARM_CHANNEL)
                    .setSmallIcon(R.mipmap.my_launcher)
                    .setContentTitle(intent.getStringExtra(Values.RECEIVER_LESSON_NAME))
                    .setContentText(context.getString(R.string.lesson_begins_soon)+intent.getStringExtra(Values.RECEIVER_INTERVAL))
                    .setOngoing(false)
                    .setSound(notificationSound)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(false);


            notificationManager.notify(intent.getIntExtra(Values.RECEIVER_ID,-1), builder.build());
        }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(Values.ALARM_CHANNEL, Values.ALARM_CHANNEL, importance);
            channel.setDescription("For alarm");
            notificationManager.createNotificationChannel(channel);

        }
    }
}