# CPU & Performance

## Ação Download

### Analise

Pelo que se pode perceber, o app registrou um pico de consumo de 12% da CPU no instante em que recebe o comando de download de um episódio. Por mais que vários downloads sejam socilitados simultaneamente, o consumo da CPU permanece nessa faixa de 12%.

[//]:<> (add img profile cpu)

### Justificativa

Quando o usuário solicita um download, o app chama um IntentService para concluir a ação. Este por sua vez, chama o download manager do próprio sistema, dessa forma, o consumo de CPU necessário ao download se dá pelo sistema e não pelo app. 

```java

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

```
