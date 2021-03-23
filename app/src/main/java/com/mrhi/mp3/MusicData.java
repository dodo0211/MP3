package com.mrhi.mp3;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Objects;

public class MusicData {
    private String id;
    private String artist;
    private String title;
    private String albumArt;
    private int duration;
    private boolean liked;

    public MusicData(String id, String artist, String title, String albumArt, int duration) {
        this.id = id;
        this.artist = artist;
        this.title = title;
        this.albumArt = albumArt;
        this.duration = duration;
    }

    public MusicData(String id, String artist, String title, String albumArt,
                     int duration, boolean liked) {
        this.id = id;
        this.artist = artist;
        this.title = title;
        this.albumArt = albumArt;
        this.duration = duration;
        this.liked = liked;
    }

    //getter
    public String getId() {return id;}
    public String getArtist() {return artist;}
    public String getTitle() {return title;}
    public String getAlbumArt() {return albumArt;}
    public int getDuration() {return duration;}
    public boolean getLiked() {return liked;}

    //setter
    public void setId(String id) {this.id = id;}
    public void setArtist(String artist) {this.artist = artist;}
    public void setTitle(String title) {this.title = title;}
    public void setAlbumArt(String albumArt) {this.albumArt = albumArt;}
    public void setDuration(int duration) {this.duration = duration;}
    public void setLiked(boolean liked) {this.liked = liked;}


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MusicData musicData = (MusicData) o;
        return Objects.equals(id, musicData.id);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
