package com.example.gxgod.graduationproject.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.gxgod.graduationproject.R;
import com.example.gxgod.graduationproject.activity.AttentionPage;
import com.example.gxgod.graduationproject.activity.CachePage;
import com.example.gxgod.graduationproject.activity.LoginPage;
import com.example.gxgod.graduationproject.service.UploadService;
import com.makeramen.roundedimageview.RoundedDrawable;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/5/4 0004.
 */
public class DrawerFragment extends Fragment {
    private CardView exitButton;
    private CardView changeButton;
    private TextView usernameTv;
    private CardView cacheButton;
    private Retrofit retrofit;
    private CardView friendButton;
    private CardView clearButton;
    private Handler handler;
    private RoundedImageView userPhoto;
    private static final int PHOTO_REQUEST_GALLERY = 2;
    private static final int PHOTO_REQUEST_CUT = 3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawer, container, false);
        initView(view);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x132) {
                    Toast.makeText(getContext(),"清理完成",Toast.LENGTH_SHORT).show();
                }
            }
        };
        return view;
    }

    private void initView(View view) {
        retrofit = new Retrofit.Builder().baseUrl("http://192.168.191.1:8080/project/").addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create()).build();
        cacheButton = (CardView) view.findViewById(R.id.menu_save);
        cacheButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),CachePage.class);
                startActivity(intent);
            }
        });
        exitButton = (CardView) view.findViewById(R.id.button_exit);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        changeButton = (CardView) view.findViewById(R.id.button_changeAccountNumber);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginPage.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        usernameTv = (TextView) view.findViewById(R.id.menu_username);
        usernameTv.setText(getContext().getSharedPreferences("info", Context.MODE_PRIVATE).getString("userName", ""));

        userPhoto = (RoundedImageView) view.findViewById(R.id.menu_profilePhoto);
        String url = getContext().getSharedPreferences("info",Context.MODE_PRIVATE).getString("userUrl","");
        if (!url.equals("")) Glide.with(this).load(url).into(userPhoto);

        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
            }
        });

        friendButton = (CardView) view.findViewById(R.id.menu_friend);
        friendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AttentionPage.class);
                intent.putExtra("userId",getContext().getSharedPreferences("info",Context.MODE_PRIVATE).getString("userId","0"));
                startActivity(intent);
            }
        });

        clearButton = (CardView) view.findViewById(R.id.menu_message);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearDiskCache();
            }
        });
    }

    private void clearDiskCache() {
        new Thread(){
            @Override
            public void run() {
                Glide.get(DrawerFragment.this.getContext()).clearDiskCache();
                handler.sendEmptyMessage(0x132);
            }
        }.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            if (data != null) {
                Uri uri = data.getData();
                Crop(uri);
            }
        } else if (requestCode == PHOTO_REQUEST_CUT) {
            if (data != null) {
                Bitmap bitmap = data.getParcelableExtra("data");
                Drawable drawable = RoundedDrawable.fromBitmap(bitmap);
                String path = getContext().getExternalCacheDir().getPath() + "/temp.png";
                File file = new File(path);
                if (file.exists()) file.delete();
                try {
                    FileOutputStream fos=new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG,100,fos);
                    updateUserPhoto(file,drawable);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateUserPhoto(File file, final Drawable drawable) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"),file);
        MultipartBody.Part requestBody = MultipartBody.Part.createFormData("image",file.getName(),requestFile);
        String userId = getContext().getSharedPreferences("info",Context.MODE_PRIVATE).getString("userId","0");
        retrofit.create(UploadService.class).uploadUserPhoto(requestBody,userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        SharedPreferences sharedPreferences = getContext().getSharedPreferences("info", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userUrl", s);
                        editor.commit();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(getContext(),"修改失败",Toast.LENGTH_SHORT).show();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        Toast.makeText(getContext(),"修改成功",Toast.LENGTH_SHORT).show();
                        userPhoto.setImageDrawable(drawable);
                    }
                });
    }

    private void Crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);

        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }
}
