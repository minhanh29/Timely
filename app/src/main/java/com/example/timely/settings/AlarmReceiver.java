package com.example.timely.settings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.timely.R;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String message="You've got a notification";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"notifyTimely")
                .setContentText(message)
                .setContentTitle("Timely Alarm")
                .setSmallIcon(R.drawable.ic_baseline_alarm_24)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(0, builder.build());
    }
}

//    NotificationCompat.Builder builder = new NotificationCompat.Builder(
//            Ringtone.this
//    )
//            .setContentText(message)
//            .setContentTitle("Timely Alarm")
//            .setSmallIcon(R.drawable.ic_baseline_alarm_24)
//            .setAutoCancel(true);
//
//    Intent newIntent = new Intent(Ringtone.this, NotificationActivity.class);
//        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                newIntent.putExtra("message", message);
//
//                PendingIntent pendingIntent = PendingIntent.getActivity(Ringtone.this,0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                builder.setContentIntent(pendingIntent);
//
//                NotificationManager notificationManager = (NotificationManager)getSystemService(
//                Context.NOTIFICATION_SERVICE
//                );
//                notificationManager.notify(0, builder.build());