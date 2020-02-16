package com.sebaroundtheworld.mediaplayer.View.Activity;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.sebaroundtheworld.mediaplayer.Model.Song;
import com.sebaroundtheworld.mediaplayer.R;
import com.sebaroundtheworld.mediaplayer.Utils.Constants;
import com.sebaroundtheworld.mediaplayer.View.SongListFragment;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

public class PlayListActivity extends FragmentActivity implements SongListFragment.SongListListener {

    List<Song> songList;
    SongListFragment songListFragment;
    PlayerFragment playerFragment;
    SlidingUpPanelLayout slidingUpPanelLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        songList = getIntent().getExtras().getParcelableArrayList(Constants.INTENT_KEY_LIST_SONG);
        initWidget();
    }

    private void initWidget() {

        slidingUpPanelLayout = findViewById(R.id.sliding_layout);

        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

                switch (newState) {
                    case COLLAPSED : playerFragment.removeController();
                        break;
                    case EXPANDED: playerFragment.showController();
                }
            }
        });

        songListFragment = new SongListFragment(this);

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constants.INTENT_KEY_LIST_SONG, (ArrayList)songList);
        songListFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.activity_playlist_container, songListFragment).commit();

        playerFragment = new PlayerFragment();
        playerFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.activity_playlist_player, playerFragment).commit();
    }

    @Override
    public void onItemSelected(int position) {
        playerFragment.setSongPos(position);
        playerFragment.start();
        playerFragment.showController();

        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }
}
