package View;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView.ItemView;
import androidx.appcompat.widget.Toolbar;

import com.sebaroundtheworld.mediaplayer.Model.Song;
import com.sebaroundtheworld.mediaplayer.R;

import java.util.ArrayList;
import java.util.List;

import Service.MusicService;
import Service.PermissionService;
import Utils.Constants;

public class SearchActivity extends AppCompatActivity {

    private Toolbar searchMenu;
    private ListView musicListView;

    private MusicService musicService;

    private ArrayList<Song> listSong;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        musicService = new MusicService(this);

        if (PermissionService.hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE, this)) {

            listSong = (ArrayList) musicService.getMusics();
            initWidget();
        }

        initSearchMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    listSong = (ArrayList<Song>) musicService.getMusics();
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
    }

    private void initWidget() {
        musicListView = (ListView) findViewById(R.id.musicListView);

        ArrayAdapter<Song> musicAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, listSong);
        musicListView.setAdapter(musicAdapter);

        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent musicIntent = new Intent(SearchActivity.this, PlayerActivity.class);
                musicIntent.putParcelableArrayListExtra(Constants.INTENT_KEY_LIST_SONG, listSong);
                musicIntent.putExtra(Constants.INTENT_KEY_INDEX_SONG, position);

                startActivity(musicIntent);
            }
        });
    }
}
