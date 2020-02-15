package View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;

import com.sebaroundtheworld.mediaplayer.Model.Song;
import com.sebaroundtheworld.mediaplayer.R;

import java.util.List;

import com.sebaroundtheworld.mediaplayer.Repository.MusicRepository;
import com.sebaroundtheworld.mediaplayer.Service.MusicService;
import com.sebaroundtheworld.mediaplayer.Service.MusicServiceCallback;
import com.sebaroundtheworld.mediaplayer.Service.ShuffleService;
import com.sebaroundtheworld.mediaplayer.Utils.Constants;

public class PlayerActivity extends AppCompatActivity implements MediaController.MediaPlayerControl, MusicServiceCallback {

    private MusicService musicService;
    private ShuffleService shuffleService;
    private MusicRepository musicRepository;

    private TextView singerTV;
    private TextView titleTV;
    private MusicController musicController;

    private List<Song> songList;
    private int currentIndex;

    private Intent playIntent;
    private boolean musicBound = false;
    private boolean pause = false;

    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;

            musicService = binder.getService();
            musicService.setMusicServiceCallback(PlayerActivity.this);
            musicService.setSong(currentIndex);
            start();
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
    protected void onResume() {
        super.onResume();

        if(pause) {
            musicController.show(0);
            pause = false;
        }

    }

    @Override
    protected void onPause() {
        pause = true;
        super.onPause();
    }

    @Override
    protected void onStop() {
        musicController.hide();
        super.onStop();
    }

     @Override
     protected void onDestroy(){
        unbindService(musicConnection);
        super.onDestroy();
     }

    private void setMusicController() {
        musicController = new MusicController(this);

        musicController.setMediaPlayer(this);
        musicController.setEnabled(true);
        musicController.setAnchorView(findViewById(R.id.playerActivityControllerView));

        musicController.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIndex = musicService.next();
                fillMetadata();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIndex = musicService.prev();
                fillMetadata();
            }
        });
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

    @Override
    public void start() {
        musicService.playSong();
        fillMetadata();
    }

    @Override
    public void pause() {
        musicService.pause();
    }

    @Override
    public int getDuration() {
        if (musicService != null && musicService.isPlaying()) {
            return musicService.getDuration();
        }
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (musicService != null && musicService.isPlaying()) {
            return musicService.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void seekTo(int pos) {
        musicService.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return musicService.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    private void fillMetadata() {
        singerTV.setText(songList.get(currentIndex).getArtist());
        titleTV.setText(songList.get(currentIndex).getTitle());
    }

    private void initWidgets() {
        singerTV = (TextView) findViewById(R.id.singerTV);
        titleTV = (TextView) findViewById(R.id.titleTV);

        if(musicController == null) {
            setMusicController();
        }
    }

    @Override
    public void showController() {
        musicController.show(0);
    }
}
