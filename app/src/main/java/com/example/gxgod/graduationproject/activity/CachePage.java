package com.example.gxgod.graduationproject.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.gxgod.graduationproject.R;
import com.example.gxgod.graduationproject.adapter.CacheAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/17 0017.
 */
public class CachePage extends Activity {

    private ImageButton backButton;
    private RecyclerView cacheList;
    private List<String> dataList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache);
        initView();
    }

    private void initView() {
        backButton = (ImageButton) findViewById(R.id.cache_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CachePage.this.finish();
            }
        });
        cacheList = (RecyclerView) findViewById(R.id.cache_list);
        cacheList.setLayoutManager(new GridLayoutManager(this,2));
        dataList = getImagePathFromSD();
        cacheList.setAdapter(new CacheAdapter(this,dataList));
    }

    private List<String> getImagePathFromSD(){
        List<String> list=new ArrayList<>();
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsoluteFile();
        String fileName = "我的图片APP";
        File myDir  = new File(dir,fileName);
        if (!myDir.exists()) myDir.mkdir();
        File[] files = myDir.listFiles();
        for (int i=0;i<files.length;i++){
            File file = files[i];
            if (checkIsImageFile(file.getPath())) {
               Log.i("path",file.getPath());
                list.add(file.getPath());
            }
        }
        return list;
    }

    @SuppressLint("DefaultLocale")
    private boolean checkIsImageFile(String fName) {
        boolean isImageFile = false;
        // 获取扩展名
        String FileEnd = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if (FileEnd.equals("jpg") || FileEnd.equals("png") || FileEnd.equals("gif")
                || FileEnd.equals("jpeg")|| FileEnd.equals("bmp") ) {
            isImageFile = true;
        } else {
            isImageFile = false;
        }
        return isImageFile;
    }
}
