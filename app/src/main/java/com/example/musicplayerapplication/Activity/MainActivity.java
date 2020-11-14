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
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.Toast;

import com.example.musicplayerapplication.Fragment.AlbumFragment;
import com.example.musicplayerapplication.Fragment.SingerFragment;
import com.example.musicplayerapplication.Fragment.SongsFragment;
import com.example.musicplayerapplication.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 0;

    private TabLayout mTabLayout;
    private ViewPager2 mViewPager;
    private PageAdapter mPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permission();
        findViews();
        initView();
    }

    private void permission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE);
        } else{
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void findViews() {
        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.viewPager);
    }

    private void initView() {
        mPageAdapter = new PageAdapter(this);
        mViewPager.setAdapter(mPageAdapter);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator
                (mTabLayout, mViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch (position) {
                            case 0: {
                                tab.setText("Songs");
                                break;
                            }
                            case 1: {
                                tab.setText("Singers");
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //Do what ever you want permission related.
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE);
            }
        }
    }
}