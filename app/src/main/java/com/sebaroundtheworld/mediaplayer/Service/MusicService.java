package com.sebaroundtheworld.mediaplayer.Service;

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

import java.io.IOException;
import java.util.List;

public  class MusicService extends Service implements   MediaPlayer.OnPreparedListener,
                                                        MediaPlayer.OnErrorListener,
                                                        MediaPlayer.OnCompletionListener,
                                                        AudioManager.OnAudioFocusChangeListener {

    private MediaPlayer mediaPlayer;
    private MusicServiceCallback musicServiceCallback;
    private AudioManager audioManager;

    private List<Song> songList;
    private int songPos;
    private final IBinder musicBind = new MusicBinder();

    private boolean isPaused = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){

        return false;
    }

    public void onCreate(){
        super.onCreate();
        songPos = 0;

        initMediaPlayer();
        initAudioManager();
    }

    public void onDestroy() {
        stopForeground(true);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        musicServiceCallback.showController();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        next();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    public void setMusicServiceCallback(MusicServiceCallback msc) {
        musicServiceCallback = msc;
    }

    public void setList(List<Song> theSongs){
        songList = theSongs;
    }

    public void setSong(int songIndex){
        isPaused = false;
        songPos=songIndex;
    }

    public void playSong() {

        if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)) {

            if (isPaused) {
                mediaPlayer.start();
                isPaused = false;
            } else {
                mediaPlayer.reset();

                try {
                    mediaPlayer.setDataSource(songList.get(songPos).getPathName());
                } catch (IOException e) {
                    Log.e("MUSIC SERVICE", "Error setting data source");
                }
                mediaPlayer.prepareAsync();
            }
        }
    }

    public void pause() {
        mediaPlayer.pause();
        isPaused = true;
    }

    public int prev() {
        songPos--;

        if(songPos<0) {
            songPos = 0;
        }

        isPaused = false;
        playSong();
        return songPos;
    }

    public int next() {
        songPos++;

        if(songPos >= songList.size()) {
            songPos = 0;
        } else {
            isPaused = false;
            playSong();
        }

        return songPos;
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

    public int getCurrentSongPosition() {
        return songPos;
    }

    public void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }

    public void initAudioManager() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {

        if(!isPaused) {

            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    playSong();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    pause();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    pause();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    pause();
            }
        }
    }

    public class MusicBinder extends Binder {

        public MusicService getService() {
            return MusicService.this;
        }
    }
}
