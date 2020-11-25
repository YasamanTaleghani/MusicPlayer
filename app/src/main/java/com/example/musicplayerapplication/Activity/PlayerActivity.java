package com.example.musicplayerapplication.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.musicplayerapplication.Model.Music;
import com.example.musicplayerapplication.R;

import java.util.ArrayList;

import static com.example.musicplayerapplication.Activity.MainActivity.mMusicArrayList;

public class PlayerActivity extends AppCompatActivity {

    public static final String EXTRA_POSITION = "com.example.musicplayerapplication.position";

    private TextView mTextViewSongName, mTextViewArtistName, mTextViewDurationPlayed,
            mTextViewDurationTotal;
    private ImageView mImageViewCoverArt, mImageViewNextBtn, mImageViewPrevBtn, mImageViewBackBtn,
            mImageViewShuffleBtn, mImageViewRepeatBtn, mImageViewPlayPauseBtn, mImageViewMenuBtn;
    private SeekBar mSeekBar;
    private int position = -1;
    private static ArrayList<Music> mMusicLists= new ArrayList<>();
    private static Uri uri;
    private static MediaPlayer mMediaPlayer;
    private Handler mHandler = new Handler();

    public static Intent newIntent(Context context){
        Intent intent = new Intent(context , PlayerActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        findView();
        getIntents();
        setListeners();
        setNewThread();
    }

    private void findView() {
        mTextViewArtistName = findViewById(R.id.artist_name);
        mTextViewSongName = findViewById(R.id.song_name);
        mTextViewDurationPlayed = findViewById(R.id.durationPlayed);
        mTextViewDurationTotal = findViewById(R.id.durationTotal);

        mImageViewBackBtn = findViewById(R.id.back_btn);
        mImageViewMenuBtn = findViewById(R.id.menu_btn);
        mImageViewCoverArt = findViewById(R.id.cover_art);
        mImageViewShuffleBtn = findViewById(R.id.shuffle_on_btn);
        mImageViewPrevBtn = findViewById(R.id.prev_btn);
        mImageViewPlayPauseBtn = findViewById(R.id.play_pause_btn);
        mImageViewNextBtn = findViewById(R.id.next_btn);
        mImageViewRepeatBtn = findViewById(R.id.repeat_on_btn);

        mSeekBar = findViewById(R.id.seekbar_playing);
    }

    private void getIntents() {
        position = getIntent().getIntExtra(EXTRA_POSITION, -1);
        mMusicLists = mMusicArrayList;
        if (mMusicLists != null) {
            mImageViewPlayPauseBtn.setImageResource(R.drawable.ic_pause_icon);
            uri = Uri.parse(mMusicLists.get(position).getPath());
        }
        if (mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mMediaPlayer.start();
        } else {
            mMediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mMediaPlayer.start();
        }

        mSeekBar.setMax(mMediaPlayer.getDuration()/1000);
    }

    private void setListeners() {
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mMediaPlayer != null && b) {
                    mMediaPlayer.seekTo(i * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        
    }

    private void setNewThread() {
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mMediaPlayer != null){
                    int mCurrentPosition = mMediaPlayer.getCurrentPosition() / 1000;
                    mSeekBar.setProgress(mCurrentPosition);
                    mTextViewDurationPlayed.setText(formattedTime(mCurrentPosition));
                }
                mHandler.postDelayed(this, 1000);
            }
        });
    }

    private String formattedTime(int mCurrentPosition) {
        String totalOut = "";
        String totalNew = "";
        String seconds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf(mCurrentPosition / 60);
        totalOut = minutes + ":" + seconds;
        totalNew = minutes + ":0" + seconds;
        if (seconds.length() == 1){
            return totalNew;
        } else{
            return totalOut;
        }
    }
}