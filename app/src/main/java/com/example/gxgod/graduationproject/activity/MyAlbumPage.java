package com.example.gxgod.graduationproject.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.gxgod.graduationproject.R;
import com.example.gxgod.graduationproject.adapter.MyAlbumAdapter;

/**
 * Created by Administrator on 2017/4/18 0018.
 */
public class MyAlbumPage extends Activity {

    private String picUrl;
    private RecyclerView myAlbumList;
    private MyAlbumAdapter myAlbumAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myalbum);
        initView();
    }

    private void initView() {
        picUrl = getIntent().getStringExtra("url");
        Log.i("gxd",picUrl);

        myAlbumList = (RecyclerView) findViewById(R.id.myAlbumList);
        myAlbumList.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        myAlbumAdapter = new MyAlbumAdapter(this,picUrl);
        myAlbumList.setAdapter(myAlbumAdapter);

    }
}
