package com.example.studysimplifier01.ui.settings;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;


import androidx.core.app.NotificationCompat;

import com.example.studysimplifier01.R;
import com.example.studysimplifier01.main.Values;
import com.example.studysimplifier01.main.MainActivity;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

            NotificationManager manager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,Values.RECEIVER_CHANNEL_ID)
                    .setContentTitle(intent.getStringExtra(Values.RECEIVER_LESSON_NAME))
                    .setContentText(context.getString(R.string.lesson_begins_soon)+intent.getStringExtra(Values.RECEIVER_INTERVAL))
                    .setOngoing(false)
                    .setSound(notificationSound)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(false);
            Intent i = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent =
                    PendingIntent.getActivity(
                            context,
                            intent.getIntExtra(Values.RECEIVER_ID,-1),
                            i,
                            PendingIntent.FLAG_ONE_SHOT
                    );

            builder.setContentIntent(pendingIntent);
            manager.notify(intent.getIntExtra(Values.RECEIVER_ID,-1), builder.build());
        }

}