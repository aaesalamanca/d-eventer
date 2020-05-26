package es.achraf.deventer.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import es.achraf.deventer.R;
import es.achraf.deventer.view.ChatActivity;
import es.achraf.deventer.view.IView;
import es.achraf.deventer.viewmodel.ViewModelNotification;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    // Fields
    private static final int NOTIFICATION_ID = 123456;

    // Methods

    /**
     * Handler que ejecuta la acción requerida cuando la app está en primer plano y recibe una
     * notificación —notification + data se trata igual que notificación—.
     *
     * @param remoteMessage es el mensaje remoto.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        ViewModelNotification vmn = new ViewModelNotification();
        vmn.setGetEventListener(event -> {
            Bundle bundle = new Bundle();
            bundle.putString(IView.K_EVENT_ID, remoteMessage.getData().get(IView.K_EVENT_ID));
            bundle.putParcelable(IView.K_EVENT, event);

            Intent intentChat = new Intent(this, ChatActivity.class);
            intentChat.putExtras(bundle);
            intentChat.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    intentChat, PendingIntent.FLAG_UPDATE_CURRENT);

            RemoteMessage.Notification notification = remoteMessage.getNotification();
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this, getString(R.string.channel_id))
                            .setSmallIcon(R.mipmap.logo)
                            .setContentTitle(notification.getTitle())
                            .setContentText(notification.getBody())
                            .setAutoCancel(true)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(pendingIntent);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel =
                        new NotificationChannel(getString(R.string.channel_id),
                                getString(R.string.channel_description),
                                NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        });
        vmn.getEvent(remoteMessage.getData().get(IView.K_EVENT_ID));
    }
}
