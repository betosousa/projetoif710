package br.ufpe.cin.if710.podcast.download;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import br.ufpe.cin.if710.podcast.db.PodcastProviderHelper;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;

/**
 * Created by Beto on 08/10/2017.
 */

public class DownloadIntentService extends IntentService {

    public static final String ITEM_FEED = "itemFeed";

    public DownloadIntentService(){
        super("DownloadIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("SERVICE", "intent");
        ItemFeed itemFeed = (ItemFeed) intent.getSerializableExtra(ITEM_FEED);
        // solicita o download manager do sistema para fazer o download do podcast
        DownloadManager downloadManager = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
        // coloca o download numa fila e recupera seu ID para recuperar o arquivo posteriormente
        // o proprio DownloadManager emite um broadcast quando termina o download
        long downloadID = downloadManager.enqueue(new DownloadManager.Request(Uri.parse(itemFeed.getDownloadLink())));
        // salva o ID no BD
        PodcastProviderHelper.updateDownloadID(getApplicationContext(), itemFeed.getId(), downloadID);
    }
}
