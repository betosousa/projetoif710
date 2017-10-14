package br.ufpe.cin.if710.podcast.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import br.ufpe.cin.if710.podcast.R;

public class SettingsActivity extends Activity {
    public static final String FEED_LINK = "feedlink";
    public static final String UPDATE_TIME = "updateTime";

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
            updatePref = (Preference) getPreferenceManager().findPreference(UPDATE_TIME);

            // cria listener para atualizar summary ao modificar link do feed
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
    }
}