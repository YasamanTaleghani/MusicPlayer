package com.example.musicplayerapplication.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.musicplayerapplication.Activity.ActionPlaying;
import com.example.musicplayerapplication.Activity.PlayerActivity;
import com.example.musicplayerapplication.Model.Music;

import java.util.ArrayList;

import static com.example.musicplayerapplication.Activity.PlayerActivity.mMusicLists;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {

    IBinder mBinder = new MyBinder();
    MediaPlayer mMediaPlayer;
    ArrayList<Music> mMusicsFiles = new ArrayList<>();
    Uri uri;
    int position = -1;
    ActionPlaying actionPlaying;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, MusicService.class);
        return intent;
    }

    public MusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("MusicSerice", "onBind");
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int myPosition = intent.getIntExtra(PlayerActivity.EXTRA_SERVICE_POSITION, -1);
        if (myPosition != -1) {
            playMedia(myPosition);
        }

        return START_STICKY;
    }

    private void playMedia(int startPosition) {
        mMusicsFiles = mMusicLists;
        position = startPosition;
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();

            if (mMusicsFiles != null) {
                createMediaPlayer(position);
                mMediaPlayer.start();
            }
        } else {
            createMediaPlayer(position);
            mMediaPlayer.start();
        }
    }

    public class MyBinder extends Binder {
        public MusicService getServices() {
            return MusicService.this;
        }
    }

    public void start() {
        mMediaPlayer.start();
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void stop() {
        mMediaPlayer.stop();
    }

    public void release() {
        mMediaPlayer.release();
    }

    public void pause() {
        mMediaPlayer.pause();
    }

    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    public void seekToPosition(int position) {
        mMediaPlayer.seekTo(position);
    }

    public void createMediaPlayer(int position) {
        uri = Uri.parse(mMusicsFiles.get(position).getPath());
        mMediaPlayer = MediaPlayer.create(getBaseContext(), uri);
    }

    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    public void onCompleted() {
        mMediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

        if (actionPlaying != null){
            actionPlaying.nextBtnClicked();
        }
        createMediaPlayer(position);
        mMediaPlayer.start();
        onCompleted();
    }
}