package View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sebaroundtheworld.mediaplayer.Model.Song;
import com.sebaroundtheworld.mediaplayer.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Service.MusicService;
import Service.PermissionService;
import Utils.Constants;

public class MainActivity extends AppCompatActivity {

    private MusicService musicService;

    private MediaPlayer mediaPlayer;

    private Button playButton;
    private TextView singerTV;
    private TextView titleTV;
    private SeekBar durationSB;

    private List<Song> songList;
    private int currentIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musicService = new MusicService(this);
        mediaPlayer = new MediaPlayer();
        addEndListener(mediaPlayer);

        initWidgets();
        initDurationSeekBar(mediaPlayer, durationSB);

        currentIndex = 0;

        if (PermissionService.hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE, this)) {

            songList = musicService.getMusics();
            prepareMusic();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    songList = musicService.getMusics();
                    prepareMusic();
                } else {
                    Toast.makeText(this, "No autorised to get the musics", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public void playMusic (View v) {

        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playButton.setText("Play");
        } else {
            mediaPlayer.start();
            playButton.setText("Pause");
        }
    }

    public void backward (View v) {

        Log.i("Info", mediaPlayer.getCurrentPosition() +"");

        if (mediaPlayer.getCurrentPosition() > 1000) {
            mediaPlayer.seekTo(0);
        } else {
            currentIndex = currentIndex == 0 ? 0 : currentIndex - 1;
            prepareMusic();
            playMusic(playButton);
        }
    }

    public void forward (View v) {
        currentIndex++;
        prepareMusic();
        playMusic(playButton);
    }

    public void repeat (View v) {
        mediaPlayer.setLooping(!mediaPlayer.isLooping());
        v.setBackgroundResource(mediaPlayer.isLooping()? R.color.green: R.color.grey);
    }

    public void shuffle (View v) {
        Log.i("Number of Song", songList.size() +"");
        Song currentSong = songList.get(currentIndex);

        songList.remove(currentIndex);
        Collections.shuffle(songList);

        Song firstSong = songList.get(0);
        songList.add(firstSong);

        songList.set(0, currentSong);
        Log.i("Number of Song", songList.size() +"");
    }

    private void prepareMusic() {
        if(songList != null && songList.size() > currentIndex) {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(songList.get(currentIndex).getPathName());
                mediaPlayer.prepare();

                titleTV.setText(songList.get(currentIndex).getTitle());
                singerTV.setText(songList.get(currentIndex).getArtist());

                durationSB.setProgress(0);
                durationSB.setMax(mediaPlayer.getDuration());

            } catch(IOException excetion) {
                Log.e("ERREUR", excetion.getMessage());
            }
        }
    }

    private void addEndListener (MediaPlayer mediaPlayer) {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                if(songList.size() < currentIndex) {
                    playButton.setText("Play");
                    currentIndex = 0;
                } else {
                    forward(null);
                }
            }
        });
    }

    private void initDurationSeekBar(final MediaPlayer mediaPlayer, final SeekBar seekBar){

        final Handler handler = new Handler();
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                handler.postDelayed(this, 1000);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void initWidgets() {
        playButton = (Button) findViewById(R.id.playButton);
        singerTV = (TextView) findViewById(R.id.singerTV);
        titleTV = (TextView) findViewById(R.id.titleTV);
        durationSB = (SeekBar) findViewById(R.id.duration);
    }
}
