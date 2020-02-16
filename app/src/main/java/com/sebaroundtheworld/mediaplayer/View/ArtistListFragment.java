package com.sebaroundtheworld.mediaplayer.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sebaroundtheworld.mediaplayer.Utils.Constants;

import java.util.ArrayList;

public class ArtistListFragment extends ListFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO it in a thread!
        listPair = (ArrayList) musicRepository.getAllArtist();
        listName = returnListNameFromAPair(listPair);
    }

    protected void onItemSelected(View view, int position) {
        Intent musicIntent = new Intent(getActivity(), PlayerActivity.class);
        musicIntent.putParcelableArrayListExtra(Constants.INTENT_KEY_LIST_SONG, (ArrayList) musicRepository.getSongByArtist(listPair.get(position).first));
        musicIntent.putExtra(Constants.INTENT_KEY_INDEX_SONG, 0);

        startActivity(musicIntent);
    }
}
