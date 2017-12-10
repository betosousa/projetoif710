package br.ufpe.cin.if710.podcast.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.ufpe.cin.if710.podcast.domain.ItemFeed;

/**
 * Created by Beto on 08/10/2017.
 */

public class PodcastProviderHelper {
    public static List<ItemFeed> getItens(Context context){
        List<ItemFeed> lista = new ArrayList<>();
        // recupera a lista de itens salvos no BD atrves do Provider
        Cursor c = context.getContentResolver().query(PodcastProviderContract.EPISODE_LIST_URI,null,null,null,null);
        if (c != null) {
            c.moveToFirst();
            if (c.getCount() > 0) {
                do {
                    // para cada linha do cursor cria um objeto ItemFeed ...
                    ItemFeed itemFeed = new ItemFeed(
                            c.getInt(c.getColumnIndex(PodcastProviderContract._ID)),
                            c.getString(c.getColumnIndex(PodcastProviderContract.TITLE)),
                            c.getString(c.getColumnIndex(PodcastProviderContract.EPISODE_LINK)),
                            c.getString(c.getColumnIndex(PodcastProviderContract.DATE)),
                            c.getString(c.getColumnIndex(PodcastProviderContract.DESCRIPTION)),
                            c.getString(c.getColumnIndex(PodcastProviderContract.DOWNLOAD_LINK))
                    );
                    Long downloadID = c.getLong(c.getColumnIndex(PodcastProviderContract.EPISODE_DOWNLOAD_ID));
                    if (downloadID != null) {
                        itemFeed.setDownloadID(downloadID);
                    }
                    String fileURI = c.getString(c.getColumnIndex(PodcastProviderContract.EPISODE_URI));
                    if (fileURI != null) {
                        itemFeed.setFileURI(fileURI);
                    }
                    Integer playedMsec = c.getInt(c.getColumnIndex(PodcastProviderContract.PLAYED_MSEC));
                    if (playedMsec != null) {
                        itemFeed.setPlayedMsec(playedMsec);
                    }
                    // ... e adiciona na lista a ser retornada
                    lista.add(itemFeed);
                } while (c.moveToNext());
            }
            c.close();
        }
        return lista;
    }

    public static void updateDownloadID(Context context, int podcastID, long downloadID){
        ContentValues contentValues = new ContentValues();
        // salva o downloadID do podcast para recupera o arquivo no fim do download
        contentValues.put(PodcastProviderContract.EPISODE_DOWNLOAD_ID, downloadID);
        // realiza uma query de update no podcast de id passado como argumento
        context.getContentResolver().update(
                PodcastProviderContract.EPISODE_LIST_URI,
                contentValues,
                PodcastProviderContract._ID+"=?",
                new String[]{String.valueOf(podcastID)}
        );
    }

    public static ItemFeed getItem(Context context, long downloadID){
        ItemFeed itemFeed = null;
        // realiza uma query pelo item de downloadID passado
        Cursor c = context.getContentResolver().query(
                PodcastProviderContract.EPISODE_LIST_URI,
                null,
                PodcastProviderContract.EPISODE_DOWNLOAD_ID + "=?",
                new String[]{String.valueOf(downloadID)},
                null);
        if (c != null) {
            if(c.getCount() > 0) {
                c.moveToFirst();
                // cria um objeto ItemFeed
                itemFeed = new ItemFeed(
                        c.getInt(c.getColumnIndex(PodcastProviderContract._ID)),
                        c.getString(c.getColumnIndex(PodcastProviderContract.TITLE)),
                        c.getString(c.getColumnIndex(PodcastProviderContract.EPISODE_LINK)),
                        c.getString(c.getColumnIndex(PodcastProviderContract.DATE)),
                        c.getString(c.getColumnIndex(PodcastProviderContract.DESCRIPTION)),
                        c.getString(c.getColumnIndex(PodcastProviderContract.DOWNLOAD_LINK))
                );
                // adiciona o downloadID e o fileURI
                itemFeed.setDownloadID(downloadID);
                String fileURI = c.getString(c.getColumnIndex(PodcastProviderContract.EPISODE_URI));
                if(fileURI != null){
                    itemFeed.setFileURI(fileURI);
                    Log.d("FILE_URI", fileURI);
                }
                Integer playedMsec = c.getInt(c.getColumnIndex(PodcastProviderContract.PLAYED_MSEC));
                if(playedMsec != null){
                    itemFeed.setPlayedMsec(playedMsec);
                }
            }
            c.close();
        }
        return itemFeed;
    }

    public static void updateFileURI(Context context, int podcastID, String fileURI){
        ContentValues contentValues = new ContentValues();
        // salva a URI do podcast
        contentValues.put(PodcastProviderContract.EPISODE_URI, fileURI);
        // realiza uma query de update no podcast de id passado como argumento
        int x = context.getContentResolver().update(
                PodcastProviderContract.EPISODE_LIST_URI,
                contentValues,
                PodcastProviderContract._ID+"=?",
                new String[]{String.valueOf(podcastID)}
        );

        Log.d("DOWNLOAD_RECEIVER", " updated "+x);
    }

    public static void saveItens(Context context, List<ItemFeed> itemList){
        ContentValues[] valuesArray = new ContentValues[itemList.size()];
        for (int i = 0; i < itemList.size(); i++) {
            ItemFeed itemFeed = itemList.get(i);
            ContentValues values = new ContentValues();
            // preenche um ContentValues com os dados recuperados no parser
            values.put(PodcastProviderContract.DATE, getValidString(itemFeed.getPubDate()));
            values.put(PodcastProviderContract.DESCRIPTION, getValidString(itemFeed.getDescription()));
            values.put(PodcastProviderContract.DOWNLOAD_LINK, getValidString(itemFeed.getDownloadLink()));
            values.put(PodcastProviderContract.EPISODE_LINK, getValidString(itemFeed.getLink()));
            values.put(PodcastProviderContract.TITLE, getValidString(itemFeed.getTitle()));
            // como o ep ainda nao foi baixado...
            values.put(PodcastProviderContract.EPISODE_URI, "");
            valuesArray[i] = values;
        }
        if(itemList.size() > 1){
            // salva os itens no BD atraves de chamada ao Content Provider
            context.getContentResolver().bulkInsert(PodcastProviderContract.EPISODE_LIST_URI, valuesArray);
        }else{
            // salva o item no BD atraves de chamada ao Content Provider
            Uri uri = context.getContentResolver().insert(PodcastProviderContract.EPISODE_LIST_URI, valuesArray[0]);
        }
    }

    // metodo para validar strings e evitar insercoes de campos nulos no BD
    private static String getValidString(String str) {
        return str != null ? str : "";
    }

    public static void updatePlayedMsec(Context context, int podcastID, int playedMsec){
        ContentValues contentValues = new ContentValues();
        // salva posicao atual do podcast
        contentValues.put(PodcastProviderContract.PLAYED_MSEC, playedMsec);
        // realiza uma query de update no podcast de id passado como argumento
        int x = context.getContentResolver().update(
                PodcastProviderContract.EPISODE_LIST_URI,
                contentValues,
                PodcastProviderContract._ID+"=?",
                new String[]{String.valueOf(podcastID)}
        );

        Log.d("PLAYED_MSEC", " updated "+x);
    }
}
