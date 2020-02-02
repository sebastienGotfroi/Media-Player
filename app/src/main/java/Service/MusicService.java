package Service;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sebaroundtheworld.mediaplayer.Model.Song;

import java.util.ArrayList;
import java.util.List;

import Utils.Constants;
import View.MainActivity;

public  class MusicService {

    private AudioManager audioManager;
    private Activity activity;

    public MusicService(Activity activity) {
        audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        this.activity = activity;
    }

    public List<Song> getMusics() {

        List<Song> songList = new ArrayList<>();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = activity.getContentResolver();
        String[] columns = {MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DATA};

        Cursor musicCursor = contentResolver.query(uri, columns, null, null, null);

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
