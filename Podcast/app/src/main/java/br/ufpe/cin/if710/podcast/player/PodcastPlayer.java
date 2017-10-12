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
import br.ufpe.cin.if710.podcast.ui.PlayActivity;

/**
 * Created by Beto on 08/10/2017.
 */

public class PodcastPlayer extends Service {

    public static final String FILE_URI = "fileUri";
    public static final String TITLE = PlayActivity.TITLE;

    private static final String NOTIFICATION_TITLE = "Reproduzindo Podcast";
    private static final String NOTIFICATION_TEXT = "Clique para acessar o player";

    private static final int NOTIFICATION_ID = 2;

    private MediaPlayer mediaPlayer;
    private String fileUri;
    private String title;
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
        fileUri = intent.getStringExtra(FILE_URI);
        title = intent.getStringExtra(TITLE);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(fileUri));

        criaNotificacao();

        return podcastBinder;
    }

    private void criaNotificacao(){
        // cria uma notificacao que permite voltar a activity
        Intent activityIntent = new Intent(getApplicationContext(), PlayActivity.class);
        // passa o titulo e a uri no intent
        activityIntent.putExtra(PlayActivity.TITLE, title);
        activityIntent.putExtra(PlayActivity.FILE_URI, fileUri);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, activityIntent, 0);

        Notification.Builder notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(NOTIFICATION_TEXT)
                .setSubText(title)
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
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    public void play() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public void pause(){
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public class PodcastBinder extends Binder{
        public PodcastPlayer getService(){
            return PodcastPlayer.this;
        }
    }
}
