package com.example.musicplayerapplication.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.musicplayerapplication.Model.Music;
import com.example.musicplayerapplication.R;

import java.util.ArrayList;
import java.util.Random;

import static com.example.musicplayerapplication.Activity.MainActivity.mMusicArrayList;
import static com.example.musicplayerapplication.Activity.MainActivity.repeatBoolean;
import static com.example.musicplayerapplication.Activity.MainActivity.shuffleBoolean;

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener{

    public static final String EXTRA_POSITION = "com.example.musicplayerapplication.position";

    private TextView mTextViewSongName, mTextViewArtistName, mTextViewDurationPlayed,
            mTextViewDurationTotal;
    private ImageView mImageViewCoverArt, mImageViewNextBtn, mImageViewPrevBtn, mImageViewBackBtn,
            mImageViewShuffleBtn, mImageViewRepeatBtn, mImageViewPlayPauseBtn, mImageViewMenuBtn;
    private SeekBar mSeekBar;
    private int position = -1;
    public static ArrayList<Music> mMusicLists= new ArrayList<>();
    private static Uri uri;
    private static MediaPlayer mMediaPlayer;
    private Handler mHandler = new Handler();
    private Thread mThreadPlay, mThreadPrev, mThreadNext;

    public static Intent newIntent(Context context){
        Intent intent = new Intent(context , PlayerActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        findView();
        initView();
        mMediaPlayer.setOnCompletionListener(this);
        setListeners();
        setNewThread();

    }

    @Override
    protected void onResume() {
        playThreadBtn();
        nextThreadBtn();
        prevThredBtn();
        super.onResume();
    }

    private void playThreadBtn() {
        mThreadPlay = new Thread(){
            @Override
            public void run() {
                super.run();
                mImageViewPlayPauseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playPauseBtnClicked();
                    }
                });
            }
        };
        mThreadPlay.start();
    }

    private void nextThreadBtn() {
        mThreadNext = new Thread(){
            @Override
            public void run() {
                super.run();
                mImageViewNextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nextBtnClicked();
                    }
                });
            }
        };
        mThreadNext.start();
    }

    private void prevThredBtn() {
        mThreadPrev = new Thread(){
            @Override
            public void run() {
                super.run();
                mImageViewPrevBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        prevBtnClicked();
                    }
                });
            }
        };
        mThreadPrev.start();
    }

    private void playPauseBtnClicked() {
        if (mMediaPlayer.isPlaying()){
            mImageViewPlayPauseBtn.setImageResource(R.drawable.play_icon);
            mMediaPlayer.pause();
            mSeekBar.setMax(mMediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mMediaPlayer != null){
                        int mCurrentPosition = mMediaPlayer.getCurrentPosition() / 1000;
                        mSeekBar.setProgress(mCurrentPosition);
                    }
                    mHandler.postDelayed(this, 100);
                }
            });
        } else {
            mImageViewPlayPauseBtn.setImageResource(R.drawable.ic_pause_icon);
            mMediaPlayer.start();
            mSeekBar.setMax(mMediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mMediaPlayer != null){
                        int mCurrentPosition = mMediaPlayer.getCurrentPosition() / 1000;
                        mSeekBar.setProgress(mCurrentPosition);
                    }
                    mHandler.postDelayed(this, 100);
                }
            });
        }
    }

    private void prevBtnClicked() {
        if (mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();
            mMediaPlayer.release();

            if (shuffleBoolean && !repeatBoolean){
                position = getRandom(mMusicLists.size() -1);
            } else if (!shuffleBoolean && !repeatBoolean){
                position = ((position - 1) < 0 ? (mMusicLists.size() -1) : (position - 1));
            }
            // else: position will not change.

            uri = Uri.parse(mMusicLists.get(position).getPath());
            mMediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            mTextViewSongName.setText(mMusicLists.get(position).getTitle());
            mTextViewArtistName.setText(mMusicLists.get(position).getSinger());

            mSeekBar.setMax(mMediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mMediaPlayer != null){
                        int mCurrentPosition = mMediaPlayer.getCurrentPosition() / 1000;
                        mSeekBar.setProgress(mCurrentPosition);
                    }
                    mHandler.postDelayed(this, 100);
                }
            });
            mMediaPlayer.setOnCompletionListener(this);
            mImageViewPlayPauseBtn.setBackgroundResource(R.drawable.ic_pause_icon);
            mMediaPlayer.start();
        } else {
            mMediaPlayer.stop();
            mMediaPlayer.release();

            if (shuffleBoolean && !repeatBoolean){
                position = getRandom(mMusicLists.size() -1);
            } else if (!shuffleBoolean && !repeatBoolean){
                position = ((position - 1) < 0 ? (mMusicLists.size() -1) : (position - 1));
            }
            // else: position will not change.

            uri = Uri.parse(mMusicLists.get(position).getPath());
            mMediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            mTextViewSongName.setText(mMusicLists.get(position).getTitle());
            mTextViewArtistName.setText(mMusicLists.get(position).getSinger());

            mSeekBar.setMax(mMediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mMediaPlayer != null){
                        int mCurrentPosition = mMediaPlayer.getCurrentPosition() / 1000;
                        mSeekBar.setProgress(mCurrentPosition);
                    }
                    mHandler.postDelayed(this, 100);
                }
            });
            mMediaPlayer.setOnCompletionListener(this);
            mImageViewPlayPauseBtn.setBackgroundResource(R.drawable.ic_pause_icon);
        }
    }

    private void nextBtnClicked(){
        if (mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();
            mMediaPlayer.release();

            if (shuffleBoolean && !repeatBoolean){
                position = getRandom(mMusicLists.size() -1);
            } else if (!shuffleBoolean && !repeatBoolean){
                position = ((position + 1) % mMusicLists.size());
            }
            // else: position will not change.

            uri = Uri.parse(mMusicLists.get(position).getPath());
            mMediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            mTextViewSongName.setText(mMusicLists.get(position).getTitle());
            mTextViewArtistName.setText(mMusicLists.get(position).getSinger());

            mSeekBar.setMax(mMediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mMediaPlayer != null){
                        int mCurrentPosition = mMediaPlayer.getCurrentPosition() / 1000;
                        mSeekBar.setProgress(mCurrentPosition);
                    }
                    mHandler.postDelayed(this, 100);
                }
            });
            mMediaPlayer.setOnCompletionListener(this);
            mImageViewPlayPauseBtn.setBackgroundResource(R.drawable.ic_pause_icon);
            mMediaPlayer.start();
        } else {
            mMediaPlayer.stop();
            mMediaPlayer.release();

            if (shuffleBoolean && !repeatBoolean){
                position = getRandom(mMusicLists.size() -1);
            } else if (!shuffleBoolean && !repeatBoolean){
                position = ((position + 1) % mMusicLists.size());
            }
            // else: position will not change.

            uri = Uri.parse(mMusicLists.get(position).getPath());
            mMediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            mTextViewSongName.setText(mMusicLists.get(position).getTitle());
            mTextViewArtistName.setText(mMusicLists.get(position).getSinger());

            mSeekBar.setMax(mMediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mMediaPlayer != null){
                        int mCurrentPosition = mMediaPlayer.getCurrentPosition() / 1000;
                        mSeekBar.setProgress(mCurrentPosition);
                    }
                    mHandler.postDelayed(this, 100);
                }
            });
            mMediaPlayer.setOnCompletionListener(this);
            mImageViewPlayPauseBtn.setBackgroundResource(R.drawable.ic_pause_icon);
        }
    }

    /////////////////////////////////////////

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

    private void initView() {
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

        mSeekBar.setMax(mMediaPlayer.getDuration() / 1000);
        metaData(uri);

        mTextViewSongName.setText(mMusicLists.get(position).getTitle());
        mTextViewArtistName.setText(mMusicLists.get(position).getSinger());
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

        mImageViewShuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shuffleBoolean){
                    shuffleBoolean = false;
                    mImageViewShuffleBtn.setImageResource(R.drawable.shuffle_on_icon);
                } else {
                    shuffleBoolean = true;
                    mImageViewShuffleBtn.setImageResource(R.drawable.ic_shuffle_off);
                }
            }
        });

        mImageViewRepeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (repeatBoolean){
                    repeatBoolean = false;
                    mImageViewRepeatBtn.setImageResource(R.drawable.ic_repeat_off);
                } else {
                    repeatBoolean = true;
                    mImageViewRepeatBtn.setImageResource(R.drawable.ic_repeat_icon);
                }
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

    private void metaData(Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal = Integer.parseInt(mMusicLists.get(position).getDuration()) / 1000;
        mTextViewDurationTotal.setText(formattedTime(durationTotal));
        byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap = null;
        if (art != null){

            bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
            ImageAnimation(this, mImageViewCoverArt, bitmap);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch = palette.getDominantSwatch();
                    if (swatch != null){
                        ImageView imageView = findViewById(R.id.cover_art);
                        RelativeLayout mContainer = findViewById(R.id.mPlayerContainer);
                        imageView.setImageResource(R.drawable.gredient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);

                        GradientDrawable gradientDrawable = new GradientDrawable(
                                GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(), 0x00000000});
                        imageView.setBackground(gradientDrawable);

                        GradientDrawable gradientDrawableBq = new GradientDrawable(
                                GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(), swatch.getRgb()});
                        mContainer.setBackground(gradientDrawableBq);
                        mTextViewSongName.setTextColor(swatch.getTitleTextColor());
                        mTextViewArtistName.setTextColor(swatch.getBodyTextColor());
                    } else {
                        ImageView imageView = findViewById(R.id.cover_art);
                        RelativeLayout mContainer = findViewById(R.id.mPlayerContainer);
                        imageView.setImageResource(R.drawable.gredient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);

                        GradientDrawable gradientDrawable = new GradientDrawable(
                                GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000, 0x00000000});
                        imageView.setBackground(gradientDrawable);

                        GradientDrawable gradientDrawableBq = new GradientDrawable(
                                GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000, 0xff000000});
                        mContainer.setBackground(gradientDrawableBq);
                        mTextViewSongName.setTextColor(Color.WHITE);
                        mTextViewArtistName.setTextColor(Color.DKGRAY);
                    }
                }
            });
        }
    }

    private void ImageAnimation(Context context, ImageView imageView, Bitmap bitmap){
        Animation animOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        Animation animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);

        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).into(imageView);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                imageView.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animOut);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        nextBtnClicked();
        if (mMediaPlayer != null){
            mMediaPlayer = MediaPlayer.create(getApplicationContext(), uri );
            mMediaPlayer.start();
            mMediaPlayer.setOnCompletionListener(this);
        }
    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i+1);
    }
}