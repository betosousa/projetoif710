package br.ufpe.cin.if710.podcast.ui.adapter;

/**
 * Created by leopoldomt on 9/19/17.
 */

import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.db.PodcastProviderHelper;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;
import br.ufpe.cin.if710.podcast.download.DownloadIntentService;
import br.ufpe.cin.if710.podcast.player.PodcastPlayer;
import br.ufpe.cin.if710.podcast.ui.EpisodeDetailActivity;
import br.ufpe.cin.if710.podcast.ui.PlayActivity;

public class XmlFeedAdapter extends ArrayAdapter<ItemFeed> {

    int linkResource;
    Context context;

    //PodcastPlayer podcastPlayer;
    //boolean isBound;

    public XmlFeedAdapter(Context context, int resource, List<ItemFeed> objects) {
        super(context, resource, objects);
        linkResource = resource;
        this.context = context;
        //context.startService(new Intent(context, PodcastPlayer.class));
    }

    /**
     * public abstract View getView (int position, View convertView, ViewGroup parent)
     * <p>
     * Added in API level 1
     * Get a View that displays the data at the specified position in the data set. You can either create a View manually or inflate it from an XML layout file. When the View is inflated, the parent View (GridView, ListView...) will apply default layout parameters unless you use inflate(int, android.view.ViewGroup, boolean) to specify a root view and to prevent attachment to the root.
     * <p>
     * Parameters
     * position	The position of the item within the adapter's data set of the item whose view we want.
     * convertView	The old view to reuse, if possible. Note: You should check that this view is non-null and of an appropriate type before using. If it is not possible to convert this view to display the correct data, this method can create a new view. Heterogeneous lists can specify their number of view types, so that this View is always of the right type (see getViewTypeCount() and getItemViewType(int)).
     * parent	The parent that this view will eventually be attached to
     * Returns
     * A View corresponding to the data at the specified position.
     */


	/*
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.itemlista, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.item_title);
		textView.setText(items.get(position).getTitle());
	    return rowView;
	}
	/**/

    //http://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder
    static class ViewHolder {
        TextView item_title;
        TextView item_date;
        Button action_button;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(getContext(), linkResource, null);
            holder = new ViewHolder();
            holder.item_title = (TextView) convertView.findViewById(R.id.item_title);
            holder.item_date = (TextView) convertView.findViewById(R.id.item_date);
            holder.action_button = (Button) convertView.findViewById(R.id.item_action);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ItemFeed itemFeed = getItem(position);

        holder.item_title.setText(itemFeed.getTitle());
        holder.item_date.setText(itemFeed.getPubDate());

        // ajusta o texto do botao de acordo com arquivo baixado ou nao
        if (itemFeed.isDownloadComplete()) {
            holder.action_button.setText(context.getString(R.string.reproduzir));
        } else {
            holder.action_button.setText(context.getString(R.string.baixar));
            // desativa o botao se ja estiver baixando
            holder.action_button.setEnabled(itemFeed.getDownloadID() == 0);
        }

        holder.action_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemFeed.isDownloadComplete()) {
                    playAction(itemFeed);
                } else {
                    // desativa o botao se ja estiver baixando
                    holder.action_button.setEnabled(false);
                    downloadAction(itemFeed);
                }
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EpisodeDetailActivity.class);
                intent.putExtra(EpisodeDetailActivity.ITEM_FEED, itemFeed);
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    void downloadAction(ItemFeed itemFeed){
        Toast.makeText(getContext(),"Baixando podcast", Toast.LENGTH_SHORT).show();

        // cria intent para o service de download
        Intent intent = new Intent(getContext(), DownloadIntentService.class);
        // passando o itemfeed como extra
        intent.putExtra(DownloadIntentService.ITEM_FEED, itemFeed);
        // chama o intent service para ealizar o download.
        getContext().startService(intent);
    }

    void playAction(ItemFeed itemFeed) {
        // cria intent para abrir activity que reproduzira o podcast
        Intent activityIntent = new Intent(getContext(), PlayActivity.class);
        // passa o podcast
        activityIntent.putExtra(PlayActivity.PODCAST, itemFeed);
        // inicia a activity
        getContext().startActivity(activityIntent);
    }
}