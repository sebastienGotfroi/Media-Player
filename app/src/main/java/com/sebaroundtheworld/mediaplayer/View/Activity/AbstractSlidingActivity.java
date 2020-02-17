package com.sebaroundtheworld.mediaplayer.View.Activity;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.sebaroundtheworld.mediaplayer.Model.Song;
import com.sebaroundtheworld.mediaplayer.Service.MusicService;
import com.sebaroundtheworld.mediaplayer.View.SongListFragment;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;

public abstract class AbstractSlidingActivity extends FragmentActivity implements SongListFragment.SongListListener {

    private MusicService musicService;

    private SlidingUpPanelLayout slidingUpPanelLayout;
    private boolean panelIsCollapsed = true;
    private boolean isPaused = false;

    @Override
    public void onResume() {
        super.onResume();
        if(isPaused) {
            if(musicService != null && musicService.musicIsLoaded() && panelIsCollapsed) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                panelIsCollapsed = true;
            }
            isPaused = false;
        }
    }

    @Override
    public void onPause() {
        isPaused = true;
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(!panelIsCollapsed) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    getPlayerFragment().onCollapsing();
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onItemSelected(int position) {
        getPlayerFragment().setSongList(getListSong());
        getPlayerFragment().setSongPos(position);
        getPlayerFragment().start();

        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    public void setSlidingPanel(SlidingUpPanelLayout slidingPanel) {

        slidingUpPanelLayout = slidingPanel;

        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i("INFO", "SLIDE OFFSET " + slideOffset);

                if(slideOffset > 0.75 && panelIsCollapsed) {
                    getPlayerFragment().showController();
                    panelIsCollapsed = false;
                    getPlayerFragment().onExpanding();
                } else if (slideOffset < 0.25 && !panelIsCollapsed) {
                    getPlayerFragment().removeController();
                    panelIsCollapsed = true;
                    getPlayerFragment().onCollapsing();
                }
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
            }
        });
    }

    public void setMusicService(MusicService musicService) {
        this.musicService = musicService;

        if(getPlayerFragment() != null) {
            if(musicService.musicIsLoaded()) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        }
    }

    public abstract List<Song> getListSong();
    public abstract PlayerFragment getPlayerFragment();
}
