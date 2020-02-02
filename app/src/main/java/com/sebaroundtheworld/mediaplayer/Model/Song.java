package com.sebaroundtheworld.mediaplayer.Model;

public class Song {

    private String pathName;
    private String title;
    private String artist;

    @Override
    public boolean equals(Object song) {
        if (song == this) {
            return true;
        } else if (song instanceof Song) {
            return false;
        }

        Song copySong = (Song) song;

        if (copySong.getPathName() == pathName) {
            return true;
        }
        return false;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
