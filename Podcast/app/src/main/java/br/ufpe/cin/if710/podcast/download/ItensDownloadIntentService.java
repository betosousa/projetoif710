package br.ufpe.cin.if710.podcast.download;

import android.app.IntentService;
import android.content.Intent;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import br.ufpe.cin.if710.podcast.db.PodcastProviderHelper;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;
import br.ufpe.cin.if710.podcast.domain.XmlFeedParser;

/**
 * Created by Beto on 12/10/2017.
 */

public class ItensDownloadIntentService extends IntentService {
    public static final String ACTION_ITENS_UPDATED = "br.ufpe.cin.if710.podcast.ItensUpdated";
    public static final String DOWNLOAD_URL = "downloadURL";

    public ItensDownloadIntentService(){
        super("ItensDownloadIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            List<ItemFeed> itemList = XmlFeedParser.parse(getRssFeed(intent.getStringExtra(DOWNLOAD_URL)));
            // salva itens no BD
            PodcastProviderHelper.saveItens(getApplicationContext(), itemList);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

        sendBroadcast(new Intent(ACTION_ITENS_UPDATED));
    }

    //TODO Opcional - pesquise outros meios de obter arquivos da internet
    private String getRssFeed(String feed) throws IOException {
        InputStream in = null;
        String rssFeed = "";
        try {
            URL url = new URL(feed);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            in = conn.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int count; (count = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, count);
            }
            byte[] response = out.toByteArray();
            rssFeed = new String(response, "UTF-8");
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return rssFeed;
    }
}
