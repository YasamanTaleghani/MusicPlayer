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
import com.example.musicplayerapplication.Adapter.AlbumDetailsAdapter;
import com.example.musicplayerapplication.Model.Music;
import com.example.musicplayerapplication.R;

import java.util.ArrayList;

import static com.example.musicplayerapplication.Activity.MainActivity.mMusicArrayList;

public class AlbumDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_ALBUM_NAME = "com.example.musicplayerapplication.albumName";
    private RecyclerView mRecyclerView;
    private ImageView mImageView;
    private String albumName;
    private ArrayList<Music> mAlbumSongs = new ArrayList<>();
    private AlbumDetailsAdapter mAlbumDetailsAdapter;

    public static Intent newIntent(Context context, String albumName){
        Intent intent = new Intent(context , AlbumDetailsActivity.class);
        intent.putExtra(EXTRA_ALBUM_NAME, albumName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);

        findViews();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (! (mAlbumSongs.size()<1)){
            mAlbumDetailsAdapter = new AlbumDetailsAdapter(this, mAlbumSongs);
            mRecyclerView.setAdapter(mAlbumDetailsAdapter);
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
        albumName = getIntent().getStringExtra(EXTRA_ALBUM_NAME);
        for (int i = 0; i < mMusicArrayList.size() ; i++) {
            if (albumName.equals(mMusicArrayList.get(i).getAlbum())){
                mAlbumSongs.add(j, mMusicArrayList.get(i));
                j++;
            }
        }

        byte[] image = getAlbumArt(mAlbumSongs.get(0).getPath());
        if (image != null){
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