package com.sebaroundtheworld.mediaplayer.View.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;

import com.sebaroundtheworld.mediaplayer.Model.Song;
import com.sebaroundtheworld.mediaplayer.R;

import java.util.ArrayList;
import java.util.List;

import com.sebaroundtheworld.mediaplayer.Repository.MusicRepository;
import com.sebaroundtheworld.mediaplayer.Service.MusicService;
import com.sebaroundtheworld.mediaplayer.Service.MusicServiceCallback;
import com.sebaroundtheworld.mediaplayer.Service.ShuffleService;
import com.sebaroundtheworld.mediaplayer.Utils.Constants;
import com.sebaroundtheworld.mediaplayer.View.MusicController;

public class PlayerActivity extends AppCompatActivity implements MediaController.MediaPlayerControl, MusicServiceCallback {

    private MusicService musicService;
    private ShuffleService shuffleService;
    private MusicRepository musicRepository;

    private TextView singerTV;
    private TextView titleTV;
    private MusicController musicController;

    private List<Song> songList;
    private List<Song> currentList;
    private int currentIndex;

    private Intent playIntent;
    private boolean musicBound = false;
    private boolean isShuffle = false;

    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;

            musicService = binder.getService();
            musicService.setMusicServiceCallback(PlayerActivity.this);
            musicService.setList(songList);
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

        currentList = new ArrayList<>();
        currentList.addAll(songList);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.player_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.player_activity_shuffle :
                if(isShuffle) {
                    SpannableString s = new SpannableString(item.getTitle());
                    s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0);
                    item.setTitle(s);

                    deShuffle();

                    isShuffle = false;
                } else {
                    SpannableString s = new SpannableString(item.getTitle());
                    s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0);
                    item.setTitle(s);

                    isShuffle = true;

                    shuffle();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                if (currentIndex == 0) {
                    pause();
                    seekTo(0);
                    musicService.setSong(0);
                    showController();
                }
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

    public void shuffle () {

        currentList = new ArrayList<>();
        currentList.addAll(songList);

        List<Song> prioritySong;

        Song currentSong = songList.get(currentIndex);

        currentList.remove(currentIndex);

        prioritySong = musicRepository.getRecentsSongs(20);
        currentList.removeAll(prioritySong);

        currentList = shuffleService.shuffleWithPriority(prioritySong, currentList, 80);

        currentList.add(0, currentSong);
        currentIndex = 0;
        musicService.setList(currentList);
        musicService.setSong(0);
    }

    public void deShuffle() {
        musicService.setList(songList);
        musicService.setSong(songList.indexOf(currentList.get(currentIndex)));

        currentList.clear();
        currentList.addAll(songList);
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
        if (musicService != null) {
            return musicService.getDuration();
        }
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (musicService != null) {
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
        singerTV.setText(currentList.get(currentIndex).getArtist());
        titleTV.setText(currentList.get(currentIndex).getTitle());
    }

    private void initWidgets() {
        singerTV = (TextView) findViewById(R.id.singerTV);
        titleTV = (TextView) findViewById(R.id.titleTV);

        if(musicController == null) {
            setMusicController();
        }

        Toolbar playerMenu = (Toolbar) findViewById(R.id.searchMenu);
        setSupportActionBar(playerMenu);
    }

    @Override
    public void showController() {
        musicController.show(0);
    }
}
