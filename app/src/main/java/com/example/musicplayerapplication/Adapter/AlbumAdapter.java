package com.example.musicplayerapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayerapplication.Activity.AlbumDetailsActivity;
import com.example.musicplayerapplication.Model.Music;
import com.example.musicplayerapplication.R;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyAlbumHolder> {

    private Context mContext;
    private ArrayList<Music> mMusicsAlbums;

    public AlbumAdapter(Context context, ArrayList<Music> musicsAlbums) {
        mContext = context;
        mMusicsAlbums = musicsAlbums;
    }

    @NonNull
    @Override
    public MyAlbumHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).
                inflate(R.layout.album_item, parent, false);
        return new MyAlbumHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAlbumHolder holder, int position) {
        holder.mTextViewAlbum.setText(mMusicsAlbums.get(position).getAlbum());
        byte[] image = getAlbumArt(mMusicsAlbums.get(position).getPath());
        if (image != null){
            Glide.with(mContext)
                    .asBitmap()
                    .load(image)
                    .into(holder.mImageViewAlbum);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String albumName = mMusicsAlbums.get(position).getAlbum();
                Intent intent = AlbumDetailsActivity.newIntent(mContext, albumName);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMusicsAlbums.size();
    }

    public class MyAlbumHolder extends RecyclerView.ViewHolder {

        private ImageView mImageViewAlbum;
        private TextView mTextViewAlbum;

        public MyAlbumHolder(@NonNull View itemView) {
            super(itemView);
            mImageViewAlbum = itemView.findViewById(R.id.album_image);
            mTextViewAlbum = itemView.findViewById(R.id.album_name);
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
