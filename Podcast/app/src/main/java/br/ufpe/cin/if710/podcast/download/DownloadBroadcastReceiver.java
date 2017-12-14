package br.ufpe.cin.if710.podcast.download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import br.ufpe.cin.if710.podcast.db.PodcastProviderHelper;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;

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

    long downloadID;
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("DOWNLOAD_RECEIVER", "onreceive");
        Toast.makeText(context, "Download Completo", Toast.LENGTH_SHORT).show();
        this.context = context;
        // recupera o id do download solicitado
        downloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
        // recupera o item correspondente
        new GetTask().execute();
    }

    void update(ItemFeed itemFeed){
        // recupera a uri do arquivo baixado
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        String fileURI = downloadManager.getUriForDownloadedFile(downloadID).toString();

        if (itemFeed != null) {
            // atuaiza o item com a uri do arquivo
            itemFeed.setFileURI(fileURI);
            new UpdateTask().execute(itemFeed);
        }

        Log.d("DOWNLOAD_URI", "onReceive: " + fileURI.toString());
    }

    class GetTask extends AsyncTask<Void, Void, ItemFeed>{
        @Override
        protected ItemFeed doInBackground(Void... contexts) {
            return PodcastProviderHelper.getItem(context, downloadID);
        }

        @Override
        protected void onPostExecute(ItemFeed itemFeed) {
            super.onPostExecute(itemFeed);
            update(itemFeed);
        }
    }

    class UpdateTask extends AsyncTask<ItemFeed, Void, Void>{
        @Override
        protected Void doInBackground(ItemFeed... itens) {
            ItemFeed itemFeed = itens[0];
            PodcastProviderHelper.updateFileURI(context, itemFeed.getId(), itemFeed.getFileURI());
            return null;
        }
    }
}
