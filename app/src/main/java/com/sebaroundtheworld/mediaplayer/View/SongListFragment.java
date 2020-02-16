package com.sebaroundtheworld.mediaplayer.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sebaroundtheworld.mediaplayer.Model.Song;
import com.sebaroundtheworld.mediaplayer.R;
import com.sebaroundtheworld.mediaplayer.Repository.MusicRepository;
import com.sebaroundtheworld.mediaplayer.Utils.Constants;

import java.util.ArrayList;

public class SongListFragment extends Fragment {

    private ArrayList<Song> listSong;
    private MusicRepository musicRepository;

    private ListView songListView;
    private ArrayAdapter<Song> songArrayAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        musicRepository = new MusicRepository(getContext());

        if(savedInstanceState != null && savedInstanceState.containsKey(Constants.INTENT_KEY_LIST_SONG)) {
            listSong = savedInstanceState.getParcelableArrayList(Constants.INTENT_KEY_LIST_SONG);
        } else {
            listSong = (ArrayList) musicRepository.getMusics();
        }
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_list_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initWidget(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        songArrayAdapter.notifyDataSetChanged();
        Log.i("INFO","Je suis de retour");
    }

    private void initWidget(View view) {
        songListView = (ListView) view.findViewById(R.id.fragmentListSeachListView);
        songArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, listSong);
        songListView.setAdapter(songArrayAdapter);

        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent musicIntent = new Intent(getActivity(), PlayerActivity.class);
                musicIntent.putParcelableArrayListExtra(Constants.INTENT_KEY_LIST_SONG, listSong);
                musicIntent.putExtra(Constants.INTENT_KEY_INDEX_SONG, position);

                startActivity(musicIntent);
            }
        });

    }
}
