package br.ufpe.cin.if710.podcast.player;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.db.PodcastProviderHelper;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;
import br.ufpe.cin.if710.podcast.ui.PlayActivity;

/**
 * Created by Beto on 08/10/2017.
 */

public class PodcastPlayer extends Service {
/*
    public static final String FILE_URI = "fileUri";
    public static final String TITLE = PlayActivity.TITLE;
    public static final String PODCAST_ID = "podcastID";
*/
    public static final String PODCAST = "podcast";

    private static final String NOTIFICATION_TITLE = "Reproduzindo Podcast";
    private static final String NOTIFICATION_TEXT = "Clique para acessar o player";

    private static final int NOTIFICATION_ID = 2;

    private MediaPlayer mediaPlayer;
    //private String fileUri;
    //private String title;
    //private int podcastID;
    private ItemFeed podcast;
    private IBinder podcastBinder = new PodcastBinder();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        /*
        fileUri = intent.getStringExtra(FILE_URI);
        title = intent.getStringExtra(TITLE);
        podcastID = Integer.parseInt(intent.getStringExtra(PODCAST_ID));
        */

        podcast = (ItemFeed) intent.getSerializableExtra(PODCAST);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(podcast.getFileURI()));

        criaNotificacao();

        mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mp) {
                mp.start();
            }
        });

        return podcastBinder;
    }

    private void criaNotificacao(){
        // cria uma notificacao que permite voltar a activity
        Intent activityIntent = new Intent(getApplicationContext(), PlayActivity.class);
        // passa o Podcast no intent
        activityIntent.putExtra(PlayActivity.PODCAST, podcast);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, activityIntent, 0);

        Notification.Builder notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(NOTIFICATION_TEXT)
                .setSubText(podcast.getTitle())
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true);

        // inicia em estado foreground, para ter prioridade na memoria
        // e evitando que seja facilmente eliminado pelo sistema
        startForeground(NOTIFICATION_ID, notification.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            int playedMsec = mediaPlayer.getCurrentPosition();
            mediaPlayer.stop();
            mediaPlayer.release();
            PodcastProviderHelper.updatePlayedMsec(getApplicationContext(), podcast.getId(), playedMsec);
        }
    }

    public void play() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(podcast.getPlayedMsec());
        }
    }

    public void pause(){
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            int playedMsec = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            PodcastProviderHelper.updatePlayedMsec(getApplicationContext(), podcast.getId(), playedMsec);
        }
    }

    public class PodcastBinder extends Binder{
        public PodcastPlayer getService(){
            return PodcastPlayer.this;
        }
    }
}
