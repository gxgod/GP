package com.example.gxgod.graduationproject.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.gxgod.graduationproject.R;
import com.example.gxgod.graduationproject.service.UploadService;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/4/6 0006.
 */
public class SubmitPage extends Activity {

    private ImageView closeBtn;
    private ImageView submitBtn;
    private ImageView cameraBtn;
    private ImageView fileBtn;
    private ImageView showArea;
    private Boolean isSelectedImage = false;
    private String imagePath;
    private final static int CAMERA = 1;
    private final static int LOCALFILE = 2;
    private final static String baseUrl = "http://192.168.191.1:8080/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submitpage);
        initView();
    }

    private void initView() {
        closeBtn = (ImageView) findViewById(R.id.close_btn);
        submitBtn = (ImageView) findViewById(R.id.submit_btn);
        fileBtn = (ImageView) findViewById(R.id.localFile);
        cameraBtn = (ImageView) findViewById(R.id.camera_btn);
        showArea = (ImageView) findViewById(R.id.showArea);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubmitPage.this.finish();
            }
        });
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(getExternalCacheDir(), "temp.png");
                Uri imageUri = Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, CAMERA);
            }
        });
        fileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, LOCALFILE);
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelectedImage == false) {
                    Toast.makeText(SubmitPage.this,"未选择图片",Toast.LENGTH_SHORT).show();
                }else{
                    uploadFile();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA && resultCode == Activity.RESULT_OK) {
            isSelectedImage = true;
            Glide.with(this).load(getExternalCacheDir().getPath() + "/temp.png").diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(showArea);
            imagePath = getExternalCacheDir().getPath() + "/temp.png";
        }
        if (requestCode == LOCALFILE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                String[] proj = new String[]{MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    imagePath = cursor.getString(columnIndex);
                }
                cursor.close();
                isSelectedImage = true;
                Glide.with(this).load(new File(imagePath)).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(showArea);
            }
        }

        if ((imagePath!=null)&&(!imagePath.equals(""))){
            File file=new File(imagePath);
            long len = file.length();
            double length=(len*1.0/(1<<20));
            TextView tv = (TextView) findViewById(R.id.filesize);
            tv.setText("文件大小:"+length+"MB");
        }
    }

    private void uploadFile(){
        File file = new File(imagePath);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        UploadService uploadService = retrofit.create(UploadService.class);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"),file);
        MultipartBody.Part requestBody = MultipartBody.Part.createFormData("image",file.getName(),requestFile);
        Observer<String> observer=new Observer<String>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(SubmitPage.this,"上传失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(String s) {
                Toast.makeText(SubmitPage.this,"上传成功",Toast.LENGTH_SHORT).show();
            }
        };
        String userId = getSharedPreferences("info",MODE_PRIVATE).getString("userId","0");
        uploadService.uploadFile(requestBody,userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }
}
