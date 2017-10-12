package br.ufpe.cin.if710.podcast.download;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.ui.MainActivity;

/**
 * Created by Beto on 12/10/2017.
 */

public class ItensBackgroundReceiver extends BroadcastReceiver {
    private static final String NOTIFICATION_TITLE = "Podcasts Atualizados";
    private static final String NOTIFICATION_TEXT = "Lista de Podcasts Atualizada com sucesso!";

    private static final int NOTIFICATION_ID = 3;

    @Override
    public void onReceive(Context context, Intent intent) {
        // cria um pending intent para abrir o app
        Intent activityIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_ONE_SHOT);

        // define a notificacao
        Notification.Builder notification = new Notification.Builder(context)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(NOTIFICATION_TEXT)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // envia a notificacao para o notification manager
        notificationManager.notify(NOTIFICATION_ID, notification.build());
    }
}
