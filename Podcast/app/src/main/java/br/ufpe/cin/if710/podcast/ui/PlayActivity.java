package br.ufpe.cin.if710.podcast.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.player.PodcastPlayer;

/**
 * Created by Beto on 12/10/2017.
 */

public class PlayActivity extends Activity {

    public static final String FILE_URI = PodcastPlayer.FILE_URI;
    public static final String TITLE = "title";

    private String title;
    private String fileURI;

    private PodcastPlayer podcastPlayer;
    private boolean isBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        fileURI = getIntent().getStringExtra(FILE_URI);
        title = getIntent().getStringExtra(TITLE);

        // cria intent para chamar o service
        Intent serviceIntent = new Intent(getApplicationContext(), PodcastPlayer.class);

        // inicia o service para reproduzir o podcast
        startService(serviceIntent);

        // coloca o titulo do podcast na tela
        ((TextView) findViewById(R.id.playtitle)).setText(title);

        // define os botoes da tela para reproduzir e pausar o podcast com chamadas ao service
        ((Button) findViewById(R.id.play)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isBound){
                    podcastPlayer.play();
                }
            }
        });
        ((Button) findViewById(R.id.pause)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isBound){
                    podcastPlayer.pause();
                }
            }
        });
    }

    // trata a conexao com o service que reproduz o podcast
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isBound = true;
            // ao conectar recupera instancia do service
            podcastPlayer = ((PodcastPlayer.PodcastBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            podcastPlayer = null;
        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        if(!isBound){
            // cria intent para realizar bind com service
            Intent bindIntent = new Intent(getApplicationContext(), PodcastPlayer.class);
            // passa titulo e a uri do arquivo baixado
            bindIntent.putExtra(FILE_URI, fileURI);
            bindIntent.putExtra(TITLE, title);
            // realiza bind com service
            isBound = bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceConnection);
        isBound = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // para a reproducao do podcast caso activity seja destruida
        // permitindo q ao trocar de podcast um novo service seja criado
        stopService(new Intent(getApplicationContext(), PodcastPlayer.class));
    }
}
