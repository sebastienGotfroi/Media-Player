package Service;

import android.util.Log;

import com.sebaroundtheworld.mediaplayer.Model.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ShuffleService {

    public ShuffleService() {

    }

    public List<Song> shuffleWithPriority (List<Song> priorityList, List<Song> musicList, int percentageOfPriority) {

        List<Song> finalSongList = new ArrayList<>();
        Random random = new Random();
        int randomNumber;
        int numberOfMusics = priorityList.size() + musicList.size();

        if(percentageOfPriority < 0 || percentageOfPriority > 100) {
            percentageOfPriority = 80;
        }

        shuffle(priorityList);
        shuffle(musicList);

        for (int i =  0; i < numberOfMusics && priorityList.isEmpty() == false && musicList.isEmpty() == false; i++) {
            randomNumber = random.nextInt(100);
            if (randomNumber <= percentageOfPriority) {
                finalSongList.add (priorityList.remove(0));
            } else {
                finalSongList.add(musicList.remove(0));
            }
        }

        if (priorityList.isEmpty()) {
            finalSongList.addAll(musicList);
        } else {
            finalSongList.addAll(priorityList);
        }

        return finalSongList;
    }

    public List<Song> shuffle (List<Song>musicList) {
        Collections.shuffle(musicList);
        return musicList;
    }
}
