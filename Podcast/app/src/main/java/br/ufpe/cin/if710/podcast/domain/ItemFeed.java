package br.ufpe.cin.if710.podcast.domain;

import java.io.Serializable;

public class ItemFeed implements Serializable {
    private final int id;
    private final String title;
    private final String link;
    private final String pubDate;
    private final String description;
    private final String downloadLink;
    private long downloadID;
    private String fileURI;
    private int playedMsec;


    public ItemFeed(int id, String title, String link, String pubDate, String description, String downloadLink) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.pubDate = pubDate;
        this.description = description;
        this.downloadLink = downloadLink;
    }

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