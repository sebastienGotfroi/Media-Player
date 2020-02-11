package com.sebaroundtheworld.mediaplayer.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;

import com.sebaroundtheworld.mediaplayer.Model.Song;
import com.sebaroundtheworld.mediaplayer.R;

import java.io.IOException;
import java.util.List;

import View.SearchActivity;

public  class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private MediaPlayer mediaPlayer;
    private List<Song> songList;
    private int songPos;
    private final IBinder musicBind = new MusicBinder();

    private static final int NOTIFY_ID=1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        //mediaPlayer.stop();
        //mediaPlayer.release();
        return false;
    }

    public void onCreate(){
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        songPos = 0;

        initMediaPlayer();
    }

    public void onDestroy() {
        stopForeground(true);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        mp.start();
    }

    public void setList(List<Song> theSongs){
        songList = theSongs;
    }

    public void setSong(int songIndex){
        songPos=songIndex;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    public void playSong() {
        mediaPlayer.reset();

        try{
            mediaPlayer.setDataSource(songList.get(songPos).getPathName());
        }
        catch(IOException e) {
            Log.e("MUSIC SERVICE", "Error setting data source");
        }
        mediaPlayer.prepareAsync();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    public int prev() {
        songPos--;
        if(songPos<0) {
            songPos = 0;
        }
        playSong();
        return songPos;
    }

    public int next() {
        songPos++;
        if(songPos >= songList.size()) {
            songPos = 0;
        }
        playSong();
        return songPos;
    }

    public void initMediaPlayer() {
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }

    public class MusicBinder extends Binder {

        public MusicService getService() {
            return MusicService.this;
        }
    }
}
