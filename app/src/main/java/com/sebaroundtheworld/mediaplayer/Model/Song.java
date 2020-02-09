package com.sebaroundtheworld.mediaplayer.Model;


import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {

    private String pathName;
    private String title;
    private String artist;

    public Song() {

    }

    public Song(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>() {
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        public Song[] newArray(int size) {

            return new Song[size];
        }

    };

    @Override
    public String toString() {
        return title;
    }

    @Override
    public boolean equals(Object song) {
        if (song == this) {
            return true;
        } else if (!(song instanceof Song)) {
            return false;
        }

        Song copySong = (Song) song;

        if (copySong.getPathName().equals(pathName)) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 17;

        result = 31 * result + pathName.hashCode();

        return result;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pathName);
        dest.writeString(title);
        dest.writeString(artist);
    }

    private void readFromParcel(Parcel in) {
        pathName = in.readString();
        title = in.readString();
        artist = in.readString();
    }
}
