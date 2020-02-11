package View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sebaroundtheworld.mediaplayer.Model.Song;
import com.sebaroundtheworld.mediaplayer.Service.MusicService.MusicBinder;
import com.sebaroundtheworld.mediaplayer.R;

import java.util.List;

import com.sebaroundtheworld.mediaplayer.Repository.MusicRepository;
import com.sebaroundtheworld.mediaplayer.Service.MusicService;
import com.sebaroundtheworld.mediaplayer.Service.ShuffleService;
import com.sebaroundtheworld.mediaplayer.Utils.Constants;

public class PlayerActivity extends AppCompatActivity {

    private MusicService musicService;
    private ShuffleService shuffleService;
    private MusicRepository musicRepository;

    private Button playButton;
    private TextView singerTV;
    private TextView titleTV;
    private SeekBar durationSB;

    private List<Song> songList;
    private int currentIndex;

    private Intent playIntent;
    private boolean musicBound = false;

    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicService = binder.getService();
            musicService.setSong(currentIndex);
            musicService.playSong();
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shuffleService = new ShuffleService();
        musicRepository = new MusicRepository(this);

        initWidgets();

        songList = getIntent().getExtras().getParcelableArrayList(Constants.INTENT_KEY_LIST_SONG);

        currentIndex = getIntent().getExtras().getInt(Constants.INTENT_KEY_INDEX_SONG);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }
    }

     @Override
     protected void onDestroy(){
        unbindService(musicConnection);
        super.onDestroy();
     }

    public void playMusic (View v) {
        musicService.setSong(currentIndex);
        musicService.playSong();
    }

    public void backward (View v) {

    }

    public void forward (View v) {

    }

    public void repeat (View v) {

    }

    public void shuffle (View v) {

        List<Song> prioritySong;

        Song currentSong = songList.get(currentIndex);

        songList.remove(currentIndex);

        prioritySong = musicRepository.getRecentsSongs(20);
        songList.removeAll(prioritySong);

        songList = shuffleService.shuffleWithPriority(prioritySong, songList, 80);

        songList.add(0, currentSong);
        currentIndex = 0;
    }


   /** private void initDurationSeekBar(final MediaPlayer mediaPlayer, final SeekBar seekBar){

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
    }*/

    private void initWidgets() {
        playButton = (Button) findViewById(R.id.playButton);
        singerTV = (TextView) findViewById(R.id.singerTV);
        titleTV = (TextView) findViewById(R.id.titleTV);
        durationSB = (SeekBar) findViewById(R.id.duration);
    }
}
