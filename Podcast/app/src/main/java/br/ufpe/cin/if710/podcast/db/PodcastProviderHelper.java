package br.ufpe.cin.if710.podcast.db;

import android.arch.persistence.room.Room;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.if710.podcast.domain.ItemFeed;
import br.ufpe.cin.if710.podcast.domain.ItemFeedDao;

/**
 * Created by Beto on 08/10/2017.
 */

public class PodcastProviderHelper {

    private static ItemFeedDB db;

    private static ItemFeedDao getDao(Context context) {
        if (db == null) {
            db = Room.databaseBuilder(context, ItemFeedDB.class, "podcasts").build();
        }
        return db.itemFeedDao();
    }

    public static List<ItemFeed> getItens(Context context) {
        return getDao(context).getAll();
    }

    public static void updateDownloadID(Context context, int podcastID, long downloadID) {
        ItemFeed itemFeed = getDao(context).findById(podcastID);
        itemFeed.setDownloadID(downloadID);
        getDao(context).updateItem(itemFeed);
    }

    public static ItemFeed getItem(Context context, long downloadID) {
        return getDao(context).findByDownloadId(downloadID);
    }

    public static void updateFileURI(Context context, int podcastID, String fileURI) {
        ItemFeed itemFeed = getDao(context).findById(podcastID);
        itemFeed.setFileURI(fileURI);
        getDao(context).updateItem(itemFeed);
    }

    public static void saveItens(Context context, List<ItemFeed> itemList) {
        getDao(context).insertAll(itemList);
    }

    public static void updatePlayedMsec(Context context, int podcastID, int playedMsec) {
        ItemFeed itemFeed = getDao(context).findById(podcastID);
        itemFeed.setPlayedMsec(playedMsec);
        getDao(context).updateItem(itemFeed);
    }
}
