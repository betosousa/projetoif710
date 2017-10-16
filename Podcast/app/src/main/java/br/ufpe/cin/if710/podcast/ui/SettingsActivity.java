package br.ufpe.cin.if710.podcast.ui;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.jobscheduler.UpdateJobService;

public class SettingsActivity extends Activity {
    public static final String FEED_LINK = "feedlink";
    public static final String UPDATE_TIME = "updateTime";
    public static final int JOB_ID = 710;
    private static final long MINUTES_MILISECS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public static class FeedPreferenceFragment extends PreferenceFragment {

        protected static final String TAG = "FeedPreferenceFragment";
        private SharedPreferences.OnSharedPreferenceChangeListener mListener;
        private Preference feedLinkPref;
        private Preference updatePref;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // carrega preferences de um recurso XML em /res/xml
            addPreferencesFromResource(R.xml.preferences);

            // pega o valor atual de FeedLink
            feedLinkPref = (Preference) getPreferenceManager().findPreference(FEED_LINK);
            // pega o valor atual de intervalo de atualizacao
            updatePref = (Preference) getPreferenceManager().findPreference(UPDATE_TIME);

            // cria listener para atualizar summary ao modificar link do feed e intervalo de atualizacao
            mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    if(key.equals(FEED_LINK)) {
                        feedLinkPref.setSummary(sharedPreferences.getString(FEED_LINK, getActivity().getResources().getString(R.string.feed_link)));
                    } else if(key.equals(UPDATE_TIME)){
                        updatePref.setSummary(sharedPreferences.getString(UPDATE_TIME, getActivity().getResources().getString(R.string.periodic_pref_default)));
                    }
                }
            };

            // pega objeto SharedPreferences gerenciado pelo PreferenceManager deste fragmento
            SharedPreferences prefs = getPreferenceManager().getSharedPreferences();

            // registra o listener no objeto SharedPreferences
            prefs.registerOnSharedPreferenceChangeListener(mListener);

            // força chamada ao metodo de callback para exibir link atual
            mListener.onSharedPreferenceChanged(prefs, FEED_LINK);
            // força chamada ao metodo de callback para exibir intervalo atual
            mListener.onSharedPreferenceChanged(prefs, UPDATE_TIME);
        }

        @Override
        public void onStop() {
            // Ao sair do fragmento, define o jobscheduler de atualizacao dos podcasts com as preferencias salvas
            agendarJobAtualizacao();

            super.onStop();
        }

        void agendarJobAtualizacao(){
            // recupera o link para download dos podcasts e o intervalo de atualizacao
            SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
            String feedLink = sharedPreferences.getString(FEED_LINK,  getActivity().getResources().getString(R.string.feed_link));
            long updateInterval = Long.parseLong(sharedPreferences.getString(UPDATE_TIME, getString(R.string.periodic_pref_default)));

            // passa o link de download
            PersistableBundle pb = new PersistableBundle();
            pb.putString(FEED_LINK, feedLink);

            // cria o jobBuilder para o job service de atualizacao
            JobInfo.Builder job =
                    new JobInfo.Builder(JOB_ID, new ComponentName(getActivity(), UpdateJobService.class))
                    // passa o link como parametro
                    .setExtras(pb)
                    // define criterio de rede
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    // define intervalo de atualizacao, convertendo de minutos para milisegundos
                    .setPeriodic(updateInterval * MINUTES_MILISECS)
                    // define se precisa estar carregando
                    .setRequiresCharging(false)
                    // define se precisa estar idle
                    .setRequiresDeviceIdle(false)
                    // define se persiste o job
                    .setPersisted(false);

            // agenda o job de atualizacao
            JobScheduler jobScheduler = (JobScheduler) getActivity().getSystemService(JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(job.build());
            Log.d(TAG, "agendarJobAtualizacao: KD agendado "+ (updateInterval*MINUTES_MILISECS) + " ms" );
        }
    }
}