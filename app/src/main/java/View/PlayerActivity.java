package View;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sebaroundtheworld.mediaplayer.Model.Song;
import com.sebaroundtheworld.mediaplayer.R;

import java.io.IOException;
import java.util.List;

import Service.MusicService;
import Service.PermissionService;
import Service.ShuffleService;
import Utils.Constants;

public class PlayerActivity extends AppCompatActivity {

    private MusicService musicService;
    private ShuffleService shuffleService;

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
        shuffleService = new ShuffleService();
        mediaPlayer = new MediaPlayer();
        addEndListener(mediaPlayer);

        initWidgets();
        initDurationSeekBar(mediaPlayer, durationSB);

        songList = getIntent().getExtras().getParcelableArrayList(Constants.INTENT_KEY_LIST_SONG);

        currentIndex = getIntent().getExtras().getInt(Constants.INTENT_KEY_INDEX_SONG);

        prepareMusic();
        playMusic(playButton);
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

        if(songList.size() <= currentIndex) {
            playButton.setText("Play");
            currentIndex = 0;
            prepareMusic();
        } else {
            prepareMusic();
            playMusic(playButton);
        }
    }

    public void repeat (View v) {
        mediaPlayer.setLooping(!mediaPlayer.isLooping());
        v.setBackgroundResource(mediaPlayer.isLooping()? R.color.green: R.color.grey);
    }

    public void shuffle (View v) {

        List<Song> prioritySong;

        Song currentSong = songList.get(currentIndex);

        songList.remove(currentIndex);

        prioritySong = musicService.getRecentsSongs(20);
        songList.removeAll(prioritySong);

        songList = shuffleService.shuffleWithPriority(prioritySong, songList, 80);

        songList.add(0, currentSong);
        currentIndex = 0;
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
                forward(null);
            }
        });
    }

    private void initDurationSeekBar(final MediaPlayer mediaPlayer, final SeekBar seekBar){

        final Handler handler = new Handler();
        PlayerActivity.this.runOnUiThread(new Runnable() {
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
