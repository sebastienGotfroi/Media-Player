package Service;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.MediaStore;

import com.sebaroundtheworld.mediaplayer.Model.Song;

import java.util.ArrayList;
import java.util.List;

public  class MusicService {

    private AudioManager audioManager;
    private ContentResolver contentResolver;
    private Uri externalUri;

    public MusicService(Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        contentResolver = context.getContentResolver();
        externalUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    }

    public List<Song> getMusics() {

        String[] columns = {MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DATA};
        Cursor musicCursor = contentResolver.query(externalUri, columns, null, null, null);

        return createSongListWithCursor(musicCursor);
    }

    public List<Song> getRecentsSongs(int songLimit) {

        String[] columns = {MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DATA};
        String whereClause = MediaStore.Audio.Media.IS_PODCAST + " IS NOT \"true\" ";
        String sort = MediaStore.Audio.Media.DATE_ADDED + " DESC LIMIT " + songLimit;

        Cursor musicCursor = contentResolver.query(externalUri, columns, null, null, sort);

        return createSongListWithCursor(musicCursor);

    }

    private  List<Song> createSongListWithCursor(Cursor musicCursor) {
        List<Song> songList = new ArrayList<>();

        if (musicCursor.getCount() > 0) {
            musicCursor.moveToFirst();

            do {
                Song song = new Song();

                song.setTitle(musicCursor.getString(0));
                song.setArtist(musicCursor.getString(1));
                song.setPathName(musicCursor.getString(2));

                songList.add(song);
            } while (musicCursor.moveToNext());
        }

        return songList;
    }

}
