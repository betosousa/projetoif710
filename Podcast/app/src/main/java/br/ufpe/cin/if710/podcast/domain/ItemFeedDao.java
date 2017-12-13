package br.ufpe.cin.if710.podcast.domain;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by Beto on 13/12/2017.
 */
@Dao
public interface ItemFeedDao {
    @Insert
    void insertAll(List<ItemFeed> itens);

    @Update
    void updateItem(ItemFeed itemFeed);

    @Query("SELECT * FROM ItemFeed")
    List<ItemFeed> getAll();

    @Query("SELECT * FROM ItemFeed WHERE id LIKE :id")
    ItemFeed findById(int id);

    @Query("SELECT * FROM ItemFeed WHERE download_id LIKE :downloadID")
    ItemFeed findByDownloadId(long downloadID);
}
