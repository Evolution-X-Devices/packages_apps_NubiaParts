package org.lineageos.device.NubiaParts.power;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import org.lineageos.device.NubiaParts.Utils.ResourceUtils;
import org.lineageos.device.NubiaParts.power.R;

public class NotificationHandler {

    private static final String CHANNEL_ID = "persistent_channel";
    private static final int NOTIFICATION_ID = 1001;

    private static void createNotificationChannel(Context context) {
        ResourceUtils.init(context);
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Persistent notification",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel.setDescription("Shows persistent notification");
        NotificationManager nm = context.getApplicationContext().getSystemService(NotificationManager.class);
        nm.createNotificationChannel(channel);
    }

    public static void showPersistentNotification(Context context) {
        createNotificationChannel(context.getApplicationContext());

        Intent tapIntent = new Intent(context, ChargingController.class);
        tapIntent.setAction(Constants.Intent.NOTIFICATION_TAPPED);
        PendingIntent tapPendingIntent = PendingIntent.getService(
                context,
                0,
                tapIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Notification notification = new Notification.Builder(context, CHANNEL_ID)
                .setContentTitle(ResourceUtils.getString("notification_title"))
                .setContentText(ResourceUtils.getString("notification_text"))
                .setSmallIcon(R.drawable.ic_bypass_charging)
                .setOngoing(true)
                .setContentIntent(tapPendingIntent)
                .build();

        NotificationManager nm = context.getApplicationContext().getSystemService(NotificationManager.class);
        nm.notify(NOTIFICATION_ID, notification);
    }

    public static void clearPersistentNotification(Context context) {
        NotificationManager nm = context.getApplicationContext().getSystemService(NotificationManager.class);
        nm.cancel(NOTIFICATION_ID);
    }


}
