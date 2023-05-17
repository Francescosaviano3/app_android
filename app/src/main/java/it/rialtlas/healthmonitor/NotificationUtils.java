package it.rialtlas.healthmonitor;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

public class NotificationUtils {

    public static final String DEFAULT_CHANNEL_ID = "default_channel_id";
    public static final CharSequence DEFAULT_CHANNEL_NAME = "Default Channel";
    public static final String DEFAULT_CHANNEL_DESCRIPTION = "This is the default channel for app notifications.";

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(DEFAULT_CHANNEL_ID, DEFAULT_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(DEFAULT_CHANNEL_DESCRIPTION);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void handleStompMessage(Context context, String message) {
        String title = "STOMP Notification";

        sendNotification(context, title, message);
    }

    public static void sendNotification(Context context, String title, String message) {
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, DEFAULT_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_notification))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        notificationManager.notify(1, builder.build());
    }
}
