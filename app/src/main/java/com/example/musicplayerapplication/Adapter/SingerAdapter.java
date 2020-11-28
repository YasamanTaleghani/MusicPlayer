package com.example.musicplayerapplication.Adapter;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayerapplication.Model.Music;
import com.example.musicplayerapplication.R;

import java.util.ArrayList;

public class SingerAdapter extends RecyclerView.Adapter<SingerAdapter.MySingerHolder> {

    private Context mContext;
    private ArrayList<Music> mMusicsAlbums;

    public SingerAdapter(Context context, ArrayList<Music> musicsAlbums) {
        mContext = context;
        mMusicsAlbums = musicsAlbums;
    }

    @NonNull
    @Override
    public MySingerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).
                inflate(R.layout.singer_item, parent, false);
        return new MySingerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MySingerHolder holder, int position) {
        holder.mTextViewAlbum.setText(mMusicsAlbums.get(position).getSinger());
        byte[] image = getAlbumArt(mMusicsAlbums.get(position).getPath());
        if (image != null){
            Glide.with(mContext)
                    .asBitmap()
                    .load(image)
                    .into(holder.mImageViewAlbum);
        }
    }

    @Override
    public int getItemCount() {
        return mMusicsAlbums.size();
    }

    public class MySingerHolder extends RecyclerView.ViewHolder {

        private ImageView mImageViewAlbum;
        private TextView mTextViewAlbum;

        public MySingerHolder(@NonNull View itemView) {
            super(itemView);
            mImageViewAlbum = itemView.findViewById(R.id.singer_image);
            mTextViewAlbum = itemView.findViewById(R.id.singer_name);
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