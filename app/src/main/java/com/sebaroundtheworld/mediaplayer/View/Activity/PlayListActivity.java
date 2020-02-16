package com.sebaroundtheworld.mediaplayer.View.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.sebaroundtheworld.mediaplayer.Model.Song;
import com.sebaroundtheworld.mediaplayer.R;
import com.sebaroundtheworld.mediaplayer.Service.MusicService;
import com.sebaroundtheworld.mediaplayer.Utils.Constants;
import com.sebaroundtheworld.mediaplayer.View.SongListFragment;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

public class PlayListActivity extends AbstractSlidingActivity {

    private MusicService musicService;

    private List<Song> songList;
    private SongListFragment songListFragment;
    private PlayerFragment playerFragment;
    private SlidingUpPanelLayout slidingUpPanelLayout;

    private Intent playIntent;

    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            musicService = binder.getService();

            setMusicService(musicService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        songList = getIntent().getExtras().getParcelableArrayList(Constants.INTENT_KEY_LIST_SONG);
        initWidget();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(playIntent==null) {
            playIntent = new Intent(this, MusicService.class);

            if (!bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE)){
                startService(playIntent);
            }
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onDestroy() {
        unbindService(musicConnection);
        super.onDestroy();
    }

    private void initWidget() {

        slidingUpPanelLayout = findViewById(R.id.activity_playlist_sliding_layout);

        setSlidingPanel(slidingUpPanelLayout);

        songListFragment = new SongListFragment(this);

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constants.INTENT_KEY_LIST_SONG, (ArrayList)songList);
        songListFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.activity_playlist_container, songListFragment).commit();

        initPlayerFragment();
    }

    private void initPlayerFragment() {

        playerFragment = new PlayerFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_playlist_player, playerFragment).commit();

    }

    @Override
    public List<Song> getListSong() {
        return songList;
    }

    @Override
    public PlayerFragment getPlayerFragment() {
        return playerFragment;
    }
}
