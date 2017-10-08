package br.ufpe.cin.if710.podcast.download;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.db.PodcastProviderHelper;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;
import br.ufpe.cin.if710.podcast.ui.MainActivity;

/**
 * Created by Beto on 07/10/2017.
 * Receiver para armazenar a uri do arquivo
 * Registrado no manifest, recebe broadcasts do DownloadManager executado pelo service
 * Desativado se app estiver em primeiro plano.
 */

public class DownloadBroadcastReceiver extends BroadcastReceiver {

    private static final String NOTIFICATION_TITLE = "Download Completo";
    private static final String NOTIFICATION_TEXT = "Podcast baixado com sucesso!";

    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("DOWNLOAD_RECEIVER", "onreceive");
        Toast.makeText(context, "Download Completo", Toast.LENGTH_SHORT).show();
        // recupera o id do download solicitado
        long downloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
        // recupera o item correspondente
        ItemFeed itemFeed = PodcastProviderHelper.getItem(context, downloadID);
        // recupera a uri do arquivo baixado
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        String fileURI = downloadManager.getUriForDownloadedFile(downloadID).toString();

        if (itemFeed != null) {
            // atuaiza o item com a uri do arquivo
            PodcastProviderHelper.updateFileURI(context, itemFeed.getId(), fileURI);
        }
        Log.d("DOWNLOAD_URI", "onReceive: " + fileURI.toString());
    }
}
