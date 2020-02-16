package com.sebaroundtheworld.mediaplayer.View;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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
import androidx.fragment.app.FragmentTransaction;

import com.sebaroundtheworld.mediaplayer.R;
import com.sebaroundtheworld.mediaplayer.Repository.MusicRepository;
import com.sebaroundtheworld.mediaplayer.Utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class GenreListFragment extends Fragment {

    private ArrayList<Pair<Integer, String>> listPairGenre;
    private List<String> listNameGenre;
    private MusicRepository musicRepository;

    private ListView genreListView;
    private ArrayAdapter<String> genreArrayAdapter;

    public GenreListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        musicRepository = new MusicRepository(getContext());

        //TODO it in a thread!
        Log.i("INFO", "Je vais chercher les data");
        listPairGenre = (ArrayList) musicRepository.getAllGenres();
        listNameGenre = returnListNameFromAPair(listPairGenre);
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
        genreArrayAdapter.notifyDataSetChanged();

    }

    private void initWidget(View view) {
        genreListView = (ListView) view.findViewById(R.id.fragmentListSeachListView);

        genreArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, listNameGenre);
        genreListView.setAdapter(genreArrayAdapter);

        genreListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent musicIntent = new Intent(getActivity(), PlayerActivity.class);
                musicIntent.putParcelableArrayListExtra(Constants.INTENT_KEY_LIST_SONG, (ArrayList) musicRepository.getSongByGenre(listPairGenre.get(position).first));
                musicIntent.putExtra(Constants.INTENT_KEY_INDEX_SONG, position);

                startActivity(musicIntent);
            }
        });
    }

    private List<String> returnListNameFromAPair (List<Pair<Integer,String>> listPair) {
        List<String> nameList = new ArrayList<>();

        for(Pair<Integer, String> pair : listPair) {
            nameList.add(pair.second);
        }
        return nameList;
    }
}
