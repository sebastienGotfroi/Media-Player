package com.sebaroundtheworld.mediaplayer.Repository;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Pair;

import com.sebaroundtheworld.mediaplayer.Model.Song;

import java.util.ArrayList;
import java.util.List;

public class MusicRepository {

    private ContentResolver contentResolver;
    private Uri externalSongUri;
    private Uri externalGenderUri;

    public MusicRepository (Context context) {
        contentResolver = context.getContentResolver();
        externalSongUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        externalGenderUri = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI;
    }

    public List<Song> getMusics() {

        Cursor musicCursor = contentResolver.query(externalSongUri, getSongColumns(), null, null, null);

        return createSongListWithCursor(musicCursor);
    }

    public List<Song> getRecentsSongs(int songLimit) {

        String sort = MediaStore.Audio.Media.DATE_ADDED + " DESC LIMIT " + songLimit;

        Cursor musicCursor = contentResolver.query(externalSongUri, getSongColumns(), null, null, sort);

        return createSongListWithCursor(musicCursor);

    }

    public List<Song> getSongByGenre(int genreId) {
        Cursor musicCursor = contentResolver.query(MediaStore.Audio.Genres.Members.getContentUri(
                "external", genreId), getSongColumns(), null, null, null);

        return createSongListWithCursor(musicCursor);
    }


    public List<Pair<Integer, String>> getAllGenres() {

        List<Pair<Integer, String>> genreList = new ArrayList<>();
        int genreId;
        String genreName;
        String sortClause = "_ID LIMIT 1";

        Cursor genreCursor = contentResolver.query(externalGenderUri, getGenreColumns(), null, null, null);

        if (genreCursor.getCount() > 0) {
            genreCursor.moveToFirst();

            do {
                genreId = genreCursor.getInt(0);
                genreName = genreCursor.getString(1);

                //If there is a music attach to is genre, we add it to the list
                Cursor audioOfHisGenre = contentResolver.query(MediaStore.Audio.Genres.Members.getContentUri(
                        "external", genreId), null, null, null, sortClause);
                if(audioOfHisGenre != null && audioOfHisGenre.getCount() > 0) {
                    genreList.add(new Pair<Integer, String>(genreId, genreName));
                }

            } while (genreCursor.moveToNext());
        }

        return genreList;

    }

    private  List<Song> createSongListWithCursor(Cursor musicCursor) {
        List<Song> songList = new ArrayList<>();

        if (musicCursor != null && musicCursor.getCount() > 0) {
            musicCursor.moveToFirst();

            do {
                Song song = new Song();

                song.setPathName(musicCursor.getString(0));
                song.setTitle(musicCursor.getString(1));
                song.setArtist(musicCursor.getString(2));

                songList.add(song);
            } while (musicCursor.moveToNext());
        }

        return songList;
    }

    private String[] getSongColumns() {
        String[] columns = {MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST};
        return columns;
    }

    private String[] getGenreColumns() {
        String[] columns = {MediaStore.Audio.Genres._ID, MediaStore.Audio.Genres.NAME};
        return columns;
    }
}
