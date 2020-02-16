package com.sebaroundtheworld.mediaplayer.View.Activity;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

public class PlayerFragment extends Fragment implements MediaController.MediaPlayerControl, MusicServiceCallback {

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
    private boolean isPaused = false;

    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;

            musicService = binder.getService();
            musicService.setMusicServiceCallback(PlayerFragment.this);

            musicBound = true;

            if(musicService.musicIsLoaded()) {
                initPlayerIfMusicIsPlaying();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shuffleService = new ShuffleService();
        musicRepository = new MusicRepository(getContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(getContext(), MusicService.class);
            getContext().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savecInstanceState) {
        singerTV = (TextView) view.findViewById(R.id.singerTV);
        titleTV = (TextView) view.findViewById(R.id.titleTV);

        if(musicController == null) {
            setMusicController(view);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (isPaused) {
            initPlayerIfMusicIsPlaying();
            musicService.setMusicServiceCallback(this);
            isPaused = false;
        }
    }

    @Override
    public void onPause() {
        isPaused = true;
        super.onPause();
    }

     @Override
     public void onDestroy(){
        getContext().unbindService(musicConnection);
        super.onDestroy();
     }

     public void setSongList(List<Song> songList) {
        this.songList = currentList = songList;
        this.musicService.setList(songList);
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

    public void setSongPos(int pos) {
        if(pos < currentList.size()){
            currentIndex = pos;
            musicService.setSong(pos);
        }
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

    @Override
    public void showController() {
        if(!isPaused) {
            musicController.show(0);
        }
    }

    @Override
    public void onSongChange(int newPosition) {
        currentIndex = newPosition;
        fillMetadata();
    }

    public void removeController() {
        musicController.remove();
    }

    private void initPlayerIfMusicIsPlaying() {
        songList = currentList = musicService.getListSong();
        currentIndex = musicService.getCurrentSongPosition();
        fillMetadata();
    }

    private void setMusicController(View view) {
        musicController = new MusicController(getContext());

        musicController.setMediaPlayer(this);
        musicController.setEnabled(true);
        musicController.setAnchorView(view.findViewById(R.id.playerActivityControllerView));

        musicController.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicService.next();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicService.prev();
            }
        });
    }

    private void fillMetadata() {
        singerTV.setText(currentList.get(currentIndex).getArtist());
        titleTV.setText(currentList.get(currentIndex).getTitle());
    }
}
