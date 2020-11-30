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
import com.example.musicplayerapplication.Activity.PlayerActivity;
import com.example.musicplayerapplication.Model.Music;
import com.example.musicplayerapplication.R;

import java.util.ArrayList;

public class SingerDetailsAdapter
        extends RecyclerView.Adapter<SingerDetailsAdapter.SingerDetailHolder> {

    public static final String EXTRA_SENDER = "com.example.musicplayerapplication.sender";
    public static final String DETAIL_SINGER = "detail_singer";

    private Context mContext;
    private ArrayList<Music> mMusicsAlbums;

    public SingerDetailsAdapter(Context context, ArrayList<Music> musicsAlbums) {
        mContext = context;
        mMusicsAlbums = musicsAlbums;
    }

    @NonNull
    @Override
    public SingerDetailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).
                inflate(R.layout.music_item, parent, false);
        return new SingerDetailHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SingerDetailHolder holder, int position) {
        holder.mTextViewAlbum.setText(mMusicsAlbums.get(position).getTitle());
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
                Intent intent = PlayerActivity.newIntent(mContext, position);
                intent.putExtra(EXTRA_SENDER, DETAIL_SINGER);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return  mMusicsAlbums.size();
    }

    public class SingerDetailHolder extends RecyclerView.ViewHolder {

        private ImageView mImageViewAlbum;
        private TextView mTextViewAlbum;

        public SingerDetailHolder(@NonNull View itemView) {
            super(itemView);
            mImageViewAlbum = itemView.findViewById(R.id.music_item_image_view);
            mTextViewAlbum = itemView.findViewById(R.id.music_item_text);
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
