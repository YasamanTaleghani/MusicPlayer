package com.example.musicplayerapplication.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicplayerapplication.Adapter.AlbumAdapter;
import com.example.musicplayerapplication.Adapter.MusicAdapter;
import com.example.musicplayerapplication.R;

import static com.example.musicplayerapplication.Activity.MainActivity.mMusicArrayList;


public class AlbumFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private AlbumAdapter mAlbumAdapter;

    public AlbumFragment() {
        // Required empty public constructor
    }

    public static AlbumFragment newInstance() {
        AlbumFragment fragment = new AlbumFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_album, container, false);

        findView(view);
        initView();

        return view;
    }

    private void findView(View view) {
        mRecyclerView = view.findViewById(R.id.songs_fragment_recyclerView);
    }

    private void initView() {
        mRecyclerView.setHasFixedSize(true);
        if (mMusicArrayList.size()>0){
            mAlbumAdapter = new AlbumAdapter(getContext(), mMusicArrayList);
            mRecyclerView.setAdapter(mAlbumAdapter);
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        }
    }
}