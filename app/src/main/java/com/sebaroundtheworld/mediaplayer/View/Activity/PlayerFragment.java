package com.sebaroundtheworld.mediaplayer.View.Activity;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sebaroundtheworld.mediaplayer.Model.Song;
import com.sebaroundtheworld.mediaplayer.R;

import java.util.ArrayList;
import java.util.List;

import com.sebaroundtheworld.mediaplayer.Repository.MusicRepository;
import com.sebaroundtheworld.mediaplayer.Service.MusicService;
import com.sebaroundtheworld.mediaplayer.Service.MusicServiceCallback;
import com.sebaroundtheworld.mediaplayer.Service.ShuffleService;

public class PlayerFragment extends Fragment implements MusicServiceCallback {

    private MusicService musicService;
    private ShuffleService shuffleService;
    private MusicRepository musicRepository;

    private Toolbar toolbar;
    private ImageView playButtonToolBar;
    private ImageView pauseButtonToolBar;
    private TextView singerTV;
    private TextView titleTV;
    private SeekBar seekBar;
    private ImageView shuffleButton;
    private ImageView shuffleActiveButton;
    private ImageView prevButton;
    private ImageView playButton;
    private ImageView pauseButton;
    private ImageView nextButton;
    private ImageView repeatButton;

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
                onCollapsing();
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
        return inflater.inflate(R.layout.fragment_player, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savecInstanceState) {
        singerTV = (TextView) view.findViewById(R.id.singerTV);
        titleTV = (TextView) view.findViewById(R.id.titleTV);
        seekBar = (SeekBar) view.findViewById(R.id.fragment_player_duration);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    seekTo(seekBar.getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        toolbar = (Toolbar) view.findViewById(R.id.fragment_player_toolbar);
        playButtonToolBar = toolbar.findViewById(R.id.fragment_player_toolbar_play);
        pauseButtonToolBar = toolbar.findViewById(R.id.fragment_player_toolbar_pause);

        playButtonToolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
                swapPlayAndPauseButton(true, pauseButtonToolBar, playButtonToolBar);
            }
        });

        pauseButtonToolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause();
                swapPlayAndPauseButton(false, pauseButtonToolBar, playButtonToolBar);
            }
        });

        initMusicControllerButton(view);
    }

    @Override
    public void onResume(){
        super.onResume();
        if (isPaused && musicService.musicIsLoaded()) {
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

     public void onCollapsing() {
         toolbar.setTitle(currentList.get(currentIndex).getTitle());
         toolbar.setSubtitle(currentList.get(currentIndex).getArtist());

        if(musicService.isPlaying()) {
            pauseButtonToolBar.setVisibility(View.VISIBLE);
        } else {
            playButtonToolBar.setVisibility(View.VISIBLE);
        }
     }

     public void onExpanding() {
        toolbar.getMenu().clear();
        pauseButtonToolBar.setVisibility(View.GONE);
        playButtonToolBar.setVisibility(View.GONE);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
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

        currentList = shuffleService.shuffle(currentList);

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

    public void start() {
        musicService.playSong();
        swapPlayAndPauseButton(true, pauseButton, playButton);
        fillMetadata();
    }

    public void pause() {
        musicService.pause();
        swapPlayAndPauseButton(false, pauseButton, playButton);
    }

    public void seekTo(int pos) {
        musicService.seekTo(pos);
    }

    public void previous() {
        if(!musicService.isPlaying()) {
            swapPlayAndPauseButton(true, pauseButton, playButton);
        }
        musicService.prev();
    }

    public void next() {
        if(!musicService.isPlaying()) {
            swapPlayAndPauseButton(true, pauseButton, playButton);
        }
        musicService.next();
    }

    @Override
    public void onSongChange(int newPosition) {
        currentIndex = newPosition;
        initSeekBar();
        fillMetadata();
    }

    private void initPlayerIfMusicIsPlaying() {
        songList = currentList = musicService.getListSong();
        currentIndex = musicService.getCurrentSongPosition();
        fillMetadata();
    }

    private void initSeekBar() {
        seekBar.setMax(musicService.getDuration());

        final Handler handler = new Handler();

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if(musicService != null){
                    seekBar.setProgress(musicService.getCurrentPosition());
                }
                handler.postDelayed(this, 500);
            }
        });
    }

    private void initMusicControllerButton(View view) {
        shuffleButton = view.findViewById(R.id.fragment_player_shuffle);
        shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuffle();
                shuffleButton.setVisibility(View.GONE);
                shuffleActiveButton.setVisibility(View.VISIBLE);
            }
        });

        shuffleActiveButton = view.findViewById(R.id.fragment_player_green_shuffle);
        shuffleActiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deShuffle();
                shuffleButton.setVisibility(View.VISIBLE);
                shuffleActiveButton.setVisibility(View.GONE);
            }
        });

        prevButton = view.findViewById(R.id.fragment_player_prev);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previous();
            }
        });

        playButton = view.findViewById(R.id.fragment_player_play);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });

        pauseButton = view.findViewById(R.id.fragment_player_pause);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause();
            }
        });

        nextButton = view.findViewById(R.id.fragment_player_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });

        repeatButton = view.findViewById(R.id.fragment_player_repeat);
        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void swapPlayAndPauseButton(boolean isPlaying, ImageView pauseButton, ImageView playButton) {
        if(isPlaying) {
            pauseButton.setVisibility(View.VISIBLE);
            playButton.setVisibility(View.GONE);
        } else {
            pauseButton.setVisibility(View.GONE);
            playButton.setVisibility(View.VISIBLE);
        }
    }

    private void fillMetadata() {
        singerTV.setText(currentList.get(currentIndex).getArtist());
        titleTV.setText(currentList.get(currentIndex).getTitle());
    }
}
