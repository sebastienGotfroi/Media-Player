package com.sebaroundtheworld.mediaplayer.View.Activity;

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
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

                switch (newState) {
                    case HIDDEN:
                    case COLLAPSED : getPlayerFragment().removeController();
                        panelIsCollapsed = true;
                        break;
                    case EXPANDED: getPlayerFragment().showController();
                        panelIsCollapsed = false;
                }
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
