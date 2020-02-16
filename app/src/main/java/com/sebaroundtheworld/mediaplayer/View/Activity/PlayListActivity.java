package com.sebaroundtheworld.mediaplayer.View.Activity;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;

import com.sebaroundtheworld.mediaplayer.Model.Song;
import com.sebaroundtheworld.mediaplayer.R;
import com.sebaroundtheworld.mediaplayer.Utils.Constants;
import com.sebaroundtheworld.mediaplayer.View.SongListFragment;

import java.util.ArrayList;
import java.util.List;

public class PlayListActivity extends FragmentActivity {

    List<Song> songList;
    SongListFragment songListFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        songList = getIntent().getExtras().getParcelableArrayList(Constants.INTENT_KEY_LIST_SONG);
        initWidget();
    }

    private void initWidget() {

        songListFragment = new SongListFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constants.INTENT_KEY_LIST_SONG, (ArrayList)songList);
        songListFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.activity_playlist_container, songListFragment).commit();
    }
}
