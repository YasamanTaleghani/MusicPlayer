package com.example.musicplayerapplication.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.musicplayerapplication.Adapter.SingerDetailsAdapter;
import com.example.musicplayerapplication.Model.Music;
import com.example.musicplayerapplication.R;

import java.util.ArrayList;

import static com.example.musicplayerapplication.Activity.MainActivity.mMusicArrayList;

public class SingerDetailActivity extends AppCompatActivity {

    public static final String EXTRA_SINGER_NAME = "extra_singer_name";

    private RecyclerView mRecyclerView;
    private ImageView mImageView;
    private String albumName;
    public static ArrayList<Music> mSingerSongs = new ArrayList<>();
    private SingerDetailsAdapter mSingerDetailsAdapter;

    public static Intent newIntent(Context context, String singerName){
        Intent intent = new Intent(context , SingerDetailActivity.class);
        intent.putExtra(EXTRA_SINGER_NAME, singerName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singer_detail);

        findViews();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (! (mSingerSongs.size()<1)){
            mSingerDetailsAdapter = new SingerDetailsAdapter(this, mSingerSongs);
            mRecyclerView.setAdapter(mSingerDetailsAdapter);
            mRecyclerView.setLayoutManager(
                    new LinearLayoutManager(
                            this,
                            RecyclerView.VERTICAL,
                            false));
        }
    }

    private void findViews() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mImageView = findViewById(R.id.albumPhoto);
    }

    private void initView() {
        int j = 0;
        albumName = getIntent().getStringExtra(EXTRA_SINGER_NAME);
        if (albumName != null ){
            for (int i = 0; i < mMusicArrayList.size(); i++) {
                if (albumName.equals(mMusicArrayList.get(i).getSinger())) {
                    mSingerSongs.add(j, mMusicArrayList.get(i));
                    j++;
                }
            }
        }

        byte[] image = getAlbumArt(mSingerSongs.get(0).getPath());
        if (image != null) {
            Glide.with(this)
                    .load(image)
                    .into(mImageView);
        } else {
            Glide.with(this)
                    .load(R.mipmap.song_item)
                    .into(mImageView);
        }
    }

    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
}
