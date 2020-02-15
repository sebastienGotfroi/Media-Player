package View;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.sebaroundtheworld.mediaplayer.Model.Song;
import com.sebaroundtheworld.mediaplayer.R;

import java.util.ArrayList;
import java.util.List;

import com.sebaroundtheworld.mediaplayer.Repository.MusicRepository;
import com.sebaroundtheworld.mediaplayer.Service.MusicService;
import com.sebaroundtheworld.mediaplayer.Service.PermissionService;
import com.sebaroundtheworld.mediaplayer.Utils.Constants;

public class SearchActivity extends AppCompatActivity {

    private Toolbar searchMenu;
    private ListView musicListView;
    private ListView genreListView;

    private MusicRepository musicRepository;
    private MusicService musicService;

    private ArrayList<Song> listSong;
    private List<String> listName;
    private List<Pair<Integer,String>> listNamePair;
    private ArrayAdapter<Song> songArrayAdapter;
    private ArrayAdapter<String> nameArrayAdapter;

    private Intent playIntent;

    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicService = binder.getService();
            musicService.setList(listSong);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        listSong = new ArrayList<>();
        listName = new ArrayList<>();

        musicRepository = new MusicRepository(this);

        if (PermissionService.hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE, this)) {
            listSong.clear();
            listSong.addAll((ArrayList) musicRepository.getMusics());
        }

        initWidget();
        initSearchMenu();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(playIntent==null) {
            playIntent = new Intent(this, MusicService.class);

            if (!bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE)){
                startService(playIntent);
            }
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onDestroy() {
        unbindService(musicConnection);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    listSong.clear();
                    listSong.addAll((ArrayList<Song>) musicRepository.getMusics());
                    songArrayAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "No autorised to get the musics", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void initSearchMenu() {
        searchMenu = (Toolbar) findViewById(R.id.searchMenu);
        setSupportActionBar(searchMenu);

        searchMenu.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch(item.getItemId()) {
                    case R.id.search_menu_genres :
                        listName.clear();
                        listNamePair = musicRepository.getAllGenres();
                        listName.addAll(returnListNameFromAPair(listNamePair));
                        nameArrayAdapter.notifyDataSetChanged();
                        musicListView.setVisibility(View.GONE);
                        genreListView.setVisibility(View.VISIBLE);

                }
                return true;
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


    private void initWidget() {
        musicListView = (ListView) findViewById(R.id.searchActivitySongListView);

        songArrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, listSong);
        musicListView.setAdapter(songArrayAdapter);

        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent musicIntent = new Intent(SearchActivity.this, PlayerActivity.class);
                musicIntent.putParcelableArrayListExtra(Constants.INTENT_KEY_LIST_SONG, listSong);
                musicIntent.putExtra(Constants.INTENT_KEY_INDEX_SONG, position);

                startActivity(musicIntent);
            }
        });
        genreListView = (ListView) findViewById(R.id.searchActivityGenreListView);

        nameArrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, listName);
        genreListView.setAdapter(nameArrayAdapter);


        genreListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listSong.clear();
                listSong.addAll((ArrayList<Song>) musicRepository.getSongByGenre(listNamePair.get(position).first))
                ;
                genreListView.setVisibility(View.GONE);
                musicListView.setVisibility(View.VISIBLE);
                songArrayAdapter.notifyDataSetChanged();
            }
        });
    }
}
