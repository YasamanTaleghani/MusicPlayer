package com.example.musicplayerapplication.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.Toast;

import com.example.musicplayerapplication.Fragment.AlbumFragment;
import com.example.musicplayerapplication.Fragment.SingerFragment;
import com.example.musicplayerapplication.Fragment.SongsFragment;
import com.example.musicplayerapplication.Model.Music;
import com.example.musicplayerapplication.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE =1;

    private TabLayout mTabLayout;
    private ViewPager2 mViewPager;
    private PageAdapter mPageAdapter;
    public static ArrayList<Music> mMusicArrayList;
    static boolean shuffleBoolean = false, repeatBoolean = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        permission();
    }

    private void permission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE);
        } else{
            mMusicArrayList = getAllAudios(this);
            initView();
        }
    }

    private void findViews() {
        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.viewPager);
    }

    private void initView() {
        mPageAdapter = new PageAdapter(MainActivity.this);
        mViewPager.setAdapter(mPageAdapter);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator
                (mTabLayout, mViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch (position) {
                            case 0: {
                                tab.setText("Tracks");
                                break;
                            }
                            case 1: {
                                tab.setText("Artists");
                                break;
                            }
                            case 2: {
                                tab.setText("Albums");
                                break;
                            }
                        }
                    }
                });

        tabLayoutMediator.attach();

    }

    private class PageAdapter extends FragmentStateAdapter {

        public PageAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    SongsFragment songsFragment = SongsFragment.newInstance();
                    return songsFragment;
                case 1:
                    SingerFragment singerFragment = SingerFragment.newInstance();
                    return singerFragment;
                case 2:
                    AlbumFragment albumFragment = AlbumFragment.newInstance();
                    return albumFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                mMusicArrayList = getAllAudios(this);
                initView();
            } else {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE);
            }
        }
    }

    public static ArrayList<Music> getAllAudios(Context context){
        ArrayList<Music> tempAudiolist = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST
        };

        Cursor cursor = context.getContentResolver().query(
                uri
                ,projection
                ,null
                ,null
                ,null);

        cursor.moveToFirst();
        if (cursor != null){
            while (cursor.moveToNext()){
                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String path = cursor.getString(3);
                String artist = cursor.getString(4);

                Music music = new Music(path,title,artist,album,duration);
                Log.e("Path: " + path, "Album: "+ album);
                tempAudiolist.add(music);
            }
            cursor.close();
        }

        return tempAudiolist;
    }
}