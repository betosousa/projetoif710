package br.ufpe.cin.if710.podcast.domain;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class ItemFeed implements Serializable {
    @PrimaryKey
    private final int id;
    @ColumnInfo
    private final String title;
    @ColumnInfo
    private final String link;
    @ColumnInfo
    private final String pubDate;
    @ColumnInfo
    private final String description;
    @ColumnInfo
    private final String downloadLink;
    @ColumnInfo(name = "download_id")
    private long downloadID;
    @ColumnInfo
    private String fileURI;
    @ColumnInfo
    private int playedMsec;

    public ItemFeed(int id, String title, String link, String pubDate, String description, String downloadLink) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.pubDate = pubDate;
        this.description = description;
        this.downloadLink = downloadLink;
    }

    @Ignore
    public ItemFeed(String title, String link, String pubDate, String description, String downloadLink) {
        this.id = -1;
        this.title = title;
        this.link = link;
        this.pubDate = pubDate;
        this.description = description;
        this.downloadLink = downloadLink;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getPubDate() {
        return pubDate;
    }

    public String getDescription() {
        return description;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return title;
    }

    public long getDownloadID(){
        return downloadID;
    }

    public String getFileURI() {
        return fileURI;
    }

    public void setDownloadID(long downloadID){
        this.downloadID = downloadID;
    }

    public void setFileURI(String fileURI) {
        this.fileURI = fileURI;
    }

    public int getPlayedMsec() {
        return playedMsec;
    }

    public void setPlayedMsec(int playedMsec) {
        this.playedMsec = playedMsec;
    }
}