package com.sebaroundtheworld.mediaplayer.View;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.sebaroundtheworld.mediaplayer.View.Fragment.ArtistListFragment;
import com.sebaroundtheworld.mediaplayer.View.Fragment.GenreListFragment;

public class SearchViewPagerAdapter extends FragmentStateAdapter {

    SongListFragment.SongListListener activity;

    public SearchViewPagerAdapter(FragmentActivity fragmentActivity){

        super(fragmentActivity);
        activity = (SongListFragment.SongListListener) fragmentActivity;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch(position) {
            case 0:
                return new SongListFragment(activity);
            case 1:
                return new GenreListFragment();
            case 2:
                return new ArtistListFragment();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
