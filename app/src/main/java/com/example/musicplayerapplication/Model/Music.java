package com.example.musicplayerapplication.Model;

public class Music {

    private String mPath;
    private String mTitle;
    private String mSinger;
    private String mAlbum;
    private String mDuration;

    //Constructor
    public Music() {
    }

    public Music(String path, String title, String singer, String album, String duration) {
        mPath = path;
        mTitle = title;
        mSinger = singer;
        mAlbum = album;
        mDuration = duration;
    }

    //Getter & Setter
    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getSinger() {
        return mSinger;
    }

    public void setSinger(String singer) {
        mSinger = singer;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public void setAlbum(String album) {
        mAlbum = album;
    }

    public String getDuration() {
        return mDuration;
    }

    public void setDuration(String duration) {
        mDuration = duration;
    }
}
