package br.ufpe.cin.if710.podcast.jobscheduler;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.PersistableBundle;
import android.util.Log;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.download.ItensDownloadIntentService;
import br.ufpe.cin.if710.podcast.ui.SettingsActivity;

/**
 * Created by Beto on 14/10/2017.
 */

public class UpdateJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("KD scheduler", "onStartJob: started");
        // recupera o link de download passado nos parametros
        PersistableBundle pb = params.getExtras();
        String feedLink = pb.getString(SettingsActivity.FEED_LINK, getString(R.string.feed_link));

        // Inicia  service de atualizacao da lista de podcast
        Intent intent = new Intent(getApplicationContext(), ItensDownloadIntentService.class);
        intent.putExtra(ItensDownloadIntentService.DOWNLOAD_URL, feedLink);
        startService(intent);

        Log.d("KD scheduler", "onStartJob: ");

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        stopService(new Intent(getApplicationContext(), ItensDownloadIntentService.class));
        return false;
    }
}
