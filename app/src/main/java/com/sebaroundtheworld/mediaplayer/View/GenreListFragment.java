package com.sebaroundtheworld.mediaplayer.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sebaroundtheworld.mediaplayer.R;
import com.sebaroundtheworld.mediaplayer.Repository.MusicRepository;
import com.sebaroundtheworld.mediaplayer.Utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class GenreListFragment extends ListFragment {

    public GenreListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO it in a thread!
        listPair = (ArrayList) musicRepository.getAllGenres();
        listName = returnListNameFromAPair(listPair);
    }

    protected void onItemSelected(View view, int position) {
        Intent musicIntent = new Intent(getActivity(), PlayerActivity.class);
        musicIntent.putParcelableArrayListExtra(Constants.INTENT_KEY_LIST_SONG, (ArrayList) musicRepository.getSongByGenre(listPair.get(position).first));
        musicIntent.putExtra(Constants.INTENT_KEY_INDEX_SONG, 0);

        startActivity(musicIntent);
    }
}
