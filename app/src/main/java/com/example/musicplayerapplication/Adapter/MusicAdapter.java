package com.example.musicplayerapplication.Adapter;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
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

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<Music> mMusics;

    //Constructor
    public MusicAdapter(Context context, ArrayList<Music> musics) {
        mContext = context;
        mMusics = musics;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(mContext).inflate(
                        R.layout.music_item,
                        parent,
                        false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mTextView.setText(mMusics.get(position).getTitle());
        byte[] image = getAlbumArt(mMusics.get(position).getPath());
        if (image != null){
            Glide.with(mContext)
                    .asBitmap()
                    .load(image)
                    .into(holder.mImageView);
        }
    }

    @Override
    public int getItemCount() {
        return mMusics.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageView mImageView;
        private TextView mTextView;

       public MyViewHolder(@NonNull View itemView) {
           super(itemView);
           mImageView = itemView.findViewById(R.id.music_item_image_view);
           mTextView = itemView.findViewById(R.id.music_item_text);
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
