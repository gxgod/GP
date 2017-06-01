package com.example.gxgod.graduationproject.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.gxgod.graduationproject.R;
import com.example.gxgod.graduationproject.adapter.ShowPhotoAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Administrator on 2017/3/9 0009.
 */
public class ShowPage extends Activity {

    private List dataList;
    private int curPage;
    private ViewPager mViewPager;
    private ShowPhotoAdapter mAdapter;
    private TextView mTextView;
    private int pageSize;
    private Button saveButton;
    private Handler handler;
    private String filePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photo);
        init();
    }

    private void init() {
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x123)
                    Toast.makeText(ShowPage.this,"成功下载到"+filePath,Toast.LENGTH_SHORT).show();
            }
        };
        Intent intent = getIntent();
        curPage = intent.getIntExtra("id", 0);
        dataList = (List) intent.getSerializableExtra("data");
        pageSize = dataList.size();
        mViewPager = (ViewPager) findViewById(R.id.showActivity_viewPager);
        mTextView = (TextView) findViewById(R.id.showActivity_textView);
        mAdapter = new ShowPhotoAdapter(dataList, this);
        mViewPager.setAdapter(mAdapter);
        mTextView.setText((curPage + 1) + "/" + pageSize);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                curPage= position;
                mTextView.setText((position + 1) + "/" + pageSize);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setCurrentItem(curPage);
        saveButton = (Button) findViewById(R.id.saveButton);
        if (intent.getIntExtra("hideButton",0)==1) saveButton.setVisibility(View.GONE);else
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadPhoto();
            }
        });
    }

    private void downloadPhoto() {
        new Thread() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = Glide.with(ShowPage.this).load(dataList.get(curPage))
                            .asBitmap().into(Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL).get();
                    if (bitmap!=null)
                        saveBitmap(bitmap);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void saveBitmap(Bitmap bitmap) {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsoluteFile();
        String fileName = "我的图片APP";
        File myDir  = new File(dir,fileName);
        if (!myDir.exists()) myDir.mkdir();
        fileName = System.currentTimeMillis()+".jpg";
        File currentFile = new File(myDir,fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(currentFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ShowPage.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(new File(currentFile.getPath()))));
        filePath = currentFile.getAbsolutePath();
        handler.sendEmptyMessage(0x123);
    }
}
