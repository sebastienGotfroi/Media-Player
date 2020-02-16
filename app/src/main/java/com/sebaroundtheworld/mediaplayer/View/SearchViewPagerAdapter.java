package com.sebaroundtheworld.mediaplayer.View;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class SearchViewPagerAdapter extends FragmentStateAdapter {

    public SearchViewPagerAdapter(FragmentActivity fragmentActivity){
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch(position) {
            case 0:
                return new SongListFragment();
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
