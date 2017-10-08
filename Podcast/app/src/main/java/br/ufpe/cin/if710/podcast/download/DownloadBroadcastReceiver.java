package br.ufpe.cin.if710.podcast.download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import br.ufpe.cin.if710.podcast.db.PodcastProviderHelper;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;

/**
 * Created by Beto on 07/10/2017.
 * Receiver para armazenar a uri do arquivo
 */

public class DownloadBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("DOWNLOAD_RECEIVER", "onreceive");
        // recupera o id do download solicitado
        long downloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
        // recupera o item correspondente
        ItemFeed itemFeed = PodcastProviderHelper.getItem(context, downloadID);
        // recupera a uri do arquivo baixado
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        String fileURI = downloadManager.getUriForDownloadedFile(downloadID).toString();

        if (itemFeed != null) {
            Log.d("DOWNLOAD_RECEIVER", "itemnotnull");
            // atuaiza o item com a uri do arquivo
            PodcastProviderHelper.updateFileURI(context, itemFeed.getId(), fileURI);
        }
        Log.d("DOWNLOAD_URI", "onReceive: " + fileURI.toString());
    }
}
