package com.sebaroundtheworld.mediaplayer.View.Fragment;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

public abstract class ListFragment extends Fragment {

    protected ArrayList<Pair<Integer, String>> listPair;
    protected List<String> listName;
    protected MusicRepository musicRepository;

    protected ArrayAdapter<String> arrayAdapter;
    protected ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        musicRepository = new MusicRepository(getContext());
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
        arrayAdapter.notifyDataSetChanged();
    }

    protected List<String> returnListNameFromAPair (List<Pair<Integer,String>> listPair) {
        List<String> nameList = new ArrayList<>();

        for(Pair<Integer, String> pair : listPair) {
            nameList.add(pair.second);
        }
        return nameList;
    }

    protected void initWidget(View view) {
        listView = (ListView) view.findViewById(R.id.fragmentListSeachListView);

        arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, listName);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemSelected(view, position);
            }
        });
    }

    protected abstract void onItemSelected(View view, int position);
}
