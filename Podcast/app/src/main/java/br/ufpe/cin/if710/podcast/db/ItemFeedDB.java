package br.ufpe.cin.if710.podcast.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import br.ufpe.cin.if710.podcast.domain.ItemFeed;
import br.ufpe.cin.if710.podcast.domain.ItemFeedDao;

/**
 * Created by Beto on 13/12/2017.
 */
@Database(entities = {ItemFeed.class}, version = 1)
public abstract class ItemFeedDB extends RoomDatabase {
    public abstract ItemFeedDao itemFeedDao();
}
