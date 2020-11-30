package com.example.musicplayerapplication.Activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.example.musicplayerapplication.Adapter.AlbumDetailsAdapter;
import com.example.musicplayerapplication.Adapter.SingerDetailsAdapter;
import com.example.musicplayerapplication.Model.Music;
import com.example.musicplayerapplication.R;
import com.example.musicplayerapplication.Service.MusicService;
import com.example.musicplayerapplication.receiver.NotificationReceiver;

import java.util.ArrayList;
import java.util.Random;

import static com.example.musicplayerapplication.Activity.MainActivity.mMusicArrayList;
import static com.example.musicplayerapplication.Activity.MainActivity.repeatBoolean;
import static com.example.musicplayerapplication.Activity.MainActivity.shuffleBoolean;
import static com.example.musicplayerapplication.Activity.AlbumDetailsActivity.mAlbumSongs;
import static com.example.musicplayerapplication.Activity.SingerDetailActivity.mSingerSongs;
import static com.example.musicplayerapplication.ApplicationClass.ACTION_NEXT;
import static com.example.musicplayerapplication.ApplicationClass.ACTION_PLAY;
import static com.example.musicplayerapplication.ApplicationClass.ACTION_PREVIOUS;
import static com.example.musicplayerapplication.ApplicationClass.CHANNEL_ID_2;

public class PlayerActivity extends AppCompatActivity
        implements ActionPlaying, ServiceConnection {

    public static final String EXTRA_POSITION = "com.example.musicplayerapplication.position";
    public static final String EXTRA_SERVICE_POSITION =
            "com.example.musicplayerapplication.servicePosition";

    private TextView mTextViewSongName, mTextViewArtistName, mTextViewDurationPlayed,
            mTextViewDurationTotal;
    private ImageView mImageViewCoverArt, mImageViewNextBtn, mImageViewPrevBtn, mImageViewBackBtn,
            mImageViewShuffleBtn, mImageViewRepeatBtn, mImageViewPlayPauseBtn, mImageViewMenuBtn;
    private SeekBar mSeekBar;
    private int position = -1;
    public static ArrayList<Music> mMusicLists= new ArrayList<>();
    private static Uri uri;
    //static MediaPlayer mMediaPlayer;
    private Handler mHandler = new Handler();
    private Thread mThreadPlay, mThreadPrev, mThreadNext;
    private MusicService mMusicService;
    private MediaSessionCompat mMediaSessionCompat;

    public static Intent newIntent(Context context, int position){
        Intent intent = new Intent(context , PlayerActivity.class);
        intent.putExtra(EXTRA_POSITION,position);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        findView();
        initView();
        setListeners();
        setNewThread();

    }

    @Override
    protected void onResume() {
        Intent serviceIntent = MusicService.newIntent(this);
        bindService(serviceIntent, this, BIND_AUTO_CREATE);

        playThreadBtn();
        nextThreadBtn();
        prevThredBtn();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(this);
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

    public void playPauseBtnClicked() {
        if (mMusicService.isPlaying()){
            mImageViewPlayPauseBtn.setImageResource(R.drawable.play_icon);
            showNotification(R.drawable.play_icon);
            mMusicService.pause();
            mSeekBar.setMax(mMusicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mMusicService != null){
                        int mCurrentPosition = mMusicService.getCurrentPosition() / 1000;
                        mSeekBar.setProgress(mCurrentPosition);
                    }
                    mHandler.postDelayed(this, 100);
                }
            });
        } else {
            mImageViewPlayPauseBtn.setImageResource(R.drawable.ic_pause_icon);
            showNotification(R.drawable.ic_pause_icon);
            mMusicService.start();
            mSeekBar.setMax(mMusicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mMusicService != null){
                        int mCurrentPosition = mMusicService.getCurrentPosition() / 1000;
                        mSeekBar.setProgress(mCurrentPosition);
                    }
                    mHandler.postDelayed(this, 100);
                }
            });
        }
    }

    public void prevBtnClicked() {
        if (mMusicService.isPlaying()){
            mMusicService.stop();
            mMusicService.release();

            if (shuffleBoolean && !repeatBoolean){
                position = getRandom(mMusicLists.size() -1);
            } else if (!shuffleBoolean && !repeatBoolean){
                position = ((position - 1) < 0 ? (mMusicLists.size() -1) : (position - 1));
            }
            // else: position will not change.

            uri = Uri.parse(mMusicLists.get(position).getPath());
            mMusicService.createMediaPlayer(position);
            metaData(uri);
            mTextViewSongName.setText(mMusicLists.get(position).getTitle());
            mTextViewArtistName.setText(mMusicLists.get(position).getSinger());

            mSeekBar.setMax(mMusicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mMusicService != null){
                        int mCurrentPosition = mMusicService.getCurrentPosition() / 1000;
                        mSeekBar.setProgress(mCurrentPosition);
                    }
                    mHandler.postDelayed(this, 100);
                }
            });
            mMusicService.onCompleted();
            showNotification(R.drawable.ic_pause_icon);
            mImageViewPlayPauseBtn.setBackgroundResource(R.drawable.ic_pause_icon);
            mMusicService.start();
        } else {
            mMusicService.stop();
            mMusicService.release();

            if (shuffleBoolean && !repeatBoolean){
                position = getRandom(mMusicLists.size() -1);
            } else if (!shuffleBoolean && !repeatBoolean){
                position = ((position - 1) < 0 ? (mMusicLists.size() -1) : (position - 1));
            }
            // else: position will not change.

            uri = Uri.parse(mMusicLists.get(position).getPath());
            mMusicService.createMediaPlayer(position);
            metaData(uri);
            mTextViewSongName.setText(mMusicLists.get(position).getTitle());
            mTextViewArtistName.setText(mMusicLists.get(position).getSinger());

            mSeekBar.setMax(mMusicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mMusicService != null){
                        int mCurrentPosition = mMusicService.getCurrentPosition() / 1000;
                        mSeekBar.setProgress(mCurrentPosition);
                    }
                    mHandler.postDelayed(this, 100);
                }
            });
            mMusicService.onCompleted();
            showNotification(R.drawable.play_icon);
            mImageViewPlayPauseBtn.setBackgroundResource(R.drawable.play_icon);
        }
    }

    public void nextBtnClicked(){
        if (mMusicService.isPlaying()){
            mMusicService.stop();
            mMusicService.release();

            if (shuffleBoolean && !repeatBoolean){
                position = getRandom(mMusicLists.size() -1);
            } else if (!shuffleBoolean && !repeatBoolean){
                position = ((position + 1) % mMusicLists.size());
            }
            // else: position will not change.

            uri = Uri.parse(mMusicLists.get(position).getPath());
            mMusicService.createMediaPlayer(position);
            metaData(uri);
            mTextViewSongName.setText(mMusicLists.get(position).getTitle());
            mTextViewArtistName.setText(mMusicLists.get(position).getSinger());

            mSeekBar.setMax(mMusicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mMusicService != null){
                        int mCurrentPosition = mMusicService.getCurrentPosition() / 1000;
                        mSeekBar.setProgress(mCurrentPosition);
                    }
                    mHandler.postDelayed(this, 100);
                }
            });
            mMusicService.onCompleted();
            showNotification(R.drawable.ic_pause_icon);
            mImageViewPlayPauseBtn.setBackgroundResource(R.drawable.ic_pause_icon);
            mMusicService.start();
        } else {
            mMusicService.stop();
            mMusicService.release();

            if (shuffleBoolean && !repeatBoolean){
                position = getRandom(mMusicLists.size() -1);
            } else if (!shuffleBoolean && !repeatBoolean){
                position = ((position + 1) % mMusicLists.size());
            }
            // else: position will not change.

            uri = Uri.parse(mMusicLists.get(position).getPath());
            mMusicService.createMediaPlayer(position);
            metaData(uri);
            mTextViewSongName.setText(mMusicLists.get(position).getTitle());
            mTextViewArtistName.setText(mMusicLists.get(position).getSinger());

            mSeekBar.setMax(mMusicService.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mMusicService != null){
                        int mCurrentPosition = mMusicService.getCurrentPosition() / 1000;
                        mSeekBar.setProgress(mCurrentPosition);
                    }
                    mHandler.postDelayed(this, 100);
                }
            });
            mMusicService.onCompleted();
            showNotification(R.drawable.play_icon);
            mImageViewPlayPauseBtn.setBackgroundResource(R.drawable.play_icon);
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
        mMediaSessionCompat =
                new MediaSessionCompat(getBaseContext(), "mediaSessionCompat");
        position = getIntent().getIntExtra(EXTRA_POSITION, -1);
        String sender = getIntent().getStringExtra(AlbumDetailsAdapter.EXTRA_SENDER);

        if (sender != null && sender.equals(AlbumDetailsAdapter.DETAIL_ALBUM)) {
            mMusicLists = mAlbumSongs;
        }
        else if (sender!= null && sender.equals(SingerDetailsAdapter.DETAIL_SINGER)){
            mMusicLists = mSingerSongs;
        } else {
            mMusicLists = mMusicArrayList;
        }

        if (mMusicLists != null) {
            mImageViewPlayPauseBtn.setImageResource(R.drawable.ic_pause_icon);
            uri = Uri.parse(mMusicLists.get(position).getPath());
        }
        showNotification(R.drawable.ic_pause_icon);
        Intent intent = MusicService.newIntent(this);
        intent.putExtra(EXTRA_SERVICE_POSITION, position);
        startService(intent);

    }

    private void setListeners() {
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mMusicService != null && b) {
                    mMusicService.seekToPosition(i * 1000);
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
                if (mMusicService != null){
                    int mCurrentPosition = mMusicService.getCurrentPosition() / 1000;
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


    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i+1);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        MusicService.MyBinder myBinder = (MusicService.MyBinder) iBinder;
        mMusicService = myBinder.getServices();
        //Toast.makeText(this, "Service is Connected", Toast.LENGTH_SHORT).show();
        mSeekBar.setMax(mMusicService.getDuration() / 1000);
        metaData(uri);
        mTextViewSongName.setText(mMusicLists.get(position).getTitle());
        mTextViewArtistName.setText(mMusicLists.get(position).getSinger());
        mMusicService.onCompleted();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mMusicService = null;
    }

    void showNotification(int playPauseBtn){
        Intent intent = PlayerActivity.newIntent(getApplicationContext(), -2);
        PendingIntent contentntent =
                PendingIntent.getActivity(
                this,
                0,
                intent,
                0);

        Intent prevIntent = NotificationReceiver.newIntent(getApplicationContext())
                .setAction(ACTION_PREVIOUS);
        PendingIntent prevPending =
                PendingIntent.getBroadcast(
                        this,
                        1,
                        prevIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseIntent = NotificationReceiver.newIntent(getApplicationContext())
                .setAction(ACTION_PLAY);
        PendingIntent pausePending =
                PendingIntent.getBroadcast(
                        this,
                        2,
                        pauseIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = NotificationReceiver.newIntent(getApplicationContext())
                .setAction(ACTION_NEXT);
        PendingIntent nextPending =
                PendingIntent.getBroadcast(
                        this,
                        3,
                        nextIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        byte[] picture = null;
        Bitmap bitmap = null;
        picture = getAlbumArt(mMusicLists.get(position).getPath());
        if (picture != null){
            bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.song_item);
        }

        Notification notification =
                new NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setSmallIcon(playPauseBtn)
                .setLargeIcon(bitmap)
                .setContentTitle(mMusicLists.get(position).getTitle())
                .setContentText(mMusicLists.get(position).getSinger())
                .addAction(R.drawable.ic_prev_icon, "prevoius", prevPending)
                .addAction(playPauseBtn, "pause", pausePending)
                .addAction(R.drawable.ic_next_icon, "next", nextPending)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mMediaSessionCompat.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
}