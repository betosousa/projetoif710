package br.ufpe.cin.if710.podcast.ui;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.db.PodcastProviderHelper;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;
import br.ufpe.cin.if710.podcast.download.BackgroundReceiver;
import br.ufpe.cin.if710.podcast.download.DownloadBroadcastReceiver;
import br.ufpe.cin.if710.podcast.download.ItensBackgroundReceiver;
import br.ufpe.cin.if710.podcast.download.ItensDownloadIntentService;
import br.ufpe.cin.if710.podcast.ui.adapter.XmlFeedAdapter;

public class MainActivity extends Activity {

    //ao fazer envio da resolucao, use este link no seu codigo!
    private final String RSS_FEED = "http://leopoldomt.com/if710/fronteirasdaciencia.xml";
    //TODO teste com outros links de podcast

    private ListView items;

    private ForegroundReceiver foregroundReceiver;
    private ItensForegroundReceiver itensReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        items = (ListView) findViewById(R.id.items);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // inicia service que atualiza a lista de podcasts
        Intent intent = new Intent(getApplicationContext(), ItensDownloadIntentService.class);
        intent.putExtra(ItensDownloadIntentService.DOWNLOAD_URL, RSS_FEED);
        startService(intent);


        // atualiza lista com itens ja salvos no BD
        new ProviderTask().execute();
    }

    @Override
    protected void onStop() {
        super.onStop();
        XmlFeedAdapter adapter = (XmlFeedAdapter) items.getAdapter();
        adapter.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // cria e registra receiver dinamico para atualizar a tela automaticamente se app em primeiro plano
        foregroundReceiver = new ForegroundReceiver();
        registerReceiver(foregroundReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        // cria e registra receiver dinamico para o broadcast de itens atualizados
        // para atualizar a tela automaticamente se app em primeiro plano
        itensReceiver = new ItensForegroundReceiver();
        registerReceiver(itensReceiver, new IntentFilter(ItensDownloadIntentService.ACTION_ITENS_UPDATED));

        // desativa receivers estaticos que emitem as notificacoes
        setEnabledStateReceiver(PackageManager.COMPONENT_ENABLED_STATE_DISABLED, BackgroundReceiver.class);
        setEnabledStateReceiver(PackageManager.COMPONENT_ENABLED_STATE_DISABLED, ItensBackgroundReceiver.class);
    }

    @Override
    protected void onPause() {
        // desregistra os receivers
        unregisterReceiver(foregroundReceiver);
        unregisterReceiver(itensReceiver);

        // reativa receivers estaticos que emitem as notificacoes
        setEnabledStateReceiver(PackageManager.COMPONENT_ENABLED_STATE_ENABLED, BackgroundReceiver.class);
        setEnabledStateReceiver(PackageManager.COMPONENT_ENABLED_STATE_ENABLED, ItensBackgroundReceiver.class);

        super.onPause();
    }

    // ativa ou desativa receiver estatico de Notificacoes
    private void setEnabledStateReceiver(int enabledState, Class receiverClass){
        PackageManager pm = getPackageManager();
        ComponentName componentName = new ComponentName(getApplicationContext(), receiverClass);
        pm.setComponentEnabledSetting(componentName,
                enabledState,
                PackageManager.DONT_KILL_APP);
    }

    // AsyncTask para acessar o Provider e recuperar a lista de episodios do BD
    private class ProviderTask extends AsyncTask<Void, Void, List<ItemFeed>> {
        @Override
        protected List<ItemFeed> doInBackground(Void... params) {
            return PodcastProviderHelper.getItens(getApplicationContext());
        }
        @Override
        protected void onPostExecute(List<ItemFeed> itemFeeds) {
            atualizaLista(itemFeeds);
            Toast.makeText(getApplicationContext(), "Lista atualizada", Toast.LENGTH_SHORT).show();
        }
    }

    public void atualizaLista(List<ItemFeed> feed) {
        //Adapter Personalizado
        XmlFeedAdapter adapter = new XmlFeedAdapter(getApplicationContext(), R.layout.itemlista, feed);

        //atualizar o list view
        items.setAdapter(adapter);
        items.setTextFilterEnabled(true);
    }

    // Receiver dinamico que atualiza a lista de podcasts ao terminar um download de podcast
    public class ForegroundReceiver extends DownloadBroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
            new ProviderTask().execute();
        }
    }

    // Receiver dinamico que atualiza a lista de podcasts ao terminar a atualizacao da lista de podcasts
    public class ItensForegroundReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            new ProviderTask().execute();
        }
    }
}
