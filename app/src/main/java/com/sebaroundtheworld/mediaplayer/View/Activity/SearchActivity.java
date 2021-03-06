package com.sebaroundtheworld.mediaplayer.View.Activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.sebaroundtheworld.mediaplayer.Model.Song;
import com.sebaroundtheworld.mediaplayer.R;

import java.util.ArrayList;
import java.util.List;

import com.sebaroundtheworld.mediaplayer.Repository.MusicRepository;
import com.sebaroundtheworld.mediaplayer.Service.MusicService;
import com.sebaroundtheworld.mediaplayer.Service.PermissionService;
import com.sebaroundtheworld.mediaplayer.Utils.Constants;
import com.sebaroundtheworld.mediaplayer.View.SearchViewPagerAdapter;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class SearchActivity extends AbstractSlidingActivity {

    private MusicService musicService;
    private MusicRepository musicRepository;

    private ArrayList<Song> listSong;

    private Intent playIntent;

    private PlayerFragment playerFragment;
    private SlidingUpPanelLayout slidingUpPanelLayout;

    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            musicService = binder.getService();
            listSong = (ArrayList) musicRepository.getMusics();

            setMusicService(musicService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        musicRepository = new MusicRepository(this);

        if (PermissionService.hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE, this)) {

        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    listSong.clear();

                } else {
                    Toast.makeText(this, "No autorised to get the musics", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void initWidget() {
        ViewPager2 viewPager = findViewById(R.id.search_activity_pager);
        viewPager.setAdapter(new SearchViewPagerAdapter(this));

        TabLayout tabLayout = findViewById(R.id.search_activity_tab_layout);
        new TabLayoutMediator(tabLayout, viewPager, false, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                String text = "";
                switch(position) {
                    case 0 : text = getResources().getString(R.string.songs);
                        break;
                    case 1 : text = getResources().getString(R.string.types);
                        break;
                    case 2 : text = getResources().getString(R.string.singers);
                        break;
                }
                tab.setText(text);
            }
        }).attach();

        slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.activity_search_sliding_layout);

        setSlidingPanel(slidingUpPanelLayout);

        initPlayerFragment();

    }

    private void initPlayerFragment() {

        playerFragment = new PlayerFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_search_player, playerFragment).commit();
    }

    @Override
    public List<Song> getListSong() {
        return listSong;
    }

    @Override
    public PlayerFragment getPlayerFragment() {
        return playerFragment;
    }
}
