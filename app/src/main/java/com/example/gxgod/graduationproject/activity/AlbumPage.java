package com.example.gxgod.graduationproject.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.gxgod.graduationproject.R;
import com.example.gxgod.graduationproject.entry.AlbumEntry;
import com.example.gxgod.graduationproject.fragment.MyPhotoFragment;
import com.example.gxgod.graduationproject.service.PhotoService;
import com.makeramen.roundedimageview.RoundedImageView;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/3/15 0015.
 */
public class AlbumPage extends FragmentActivity {

    private ImageButton reportButton;
    private String baseUrl;
    private ImageView topImageView;
    private TextView topTextView;
    private ImageView discussButton;
    private ImageView collectButton;
    private Retrofit retrofit;
    private AlbumEntry albumEntry;
    private LinearLayout topButton;
    private RoundedImageView userphotoView;
    private TextView usernameView;
    private PopupWindow popupWindow;
    private int initState;
    private int albumId;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        initDataAndView();
    }

    private void initDataAndView() {
        userId = getSharedPreferences("info",MODE_PRIVATE).getString("userId","0");
        reportButton = (ImageButton) findViewById(R.id.reportButton);
        if (getSharedPreferences("info",MODE_PRIVATE).getInt("userType",0)==1) reportButton.setVisibility(View.GONE);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow();
            }
        });
        topButton = (LinearLayout) findViewById(R.id.top_user);
        usernameView = (TextView) findViewById(R.id.top_username);
        userphotoView = (RoundedImageView) findViewById(R.id.top_userphoto);
        retrofit = new Retrofit.Builder().baseUrl("http://192.168.191.1:8080/project/").addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create()).build();
        discussButton = (ImageView) findViewById(R.id.button_discuss);
        collectButton = (ImageView) findViewById(R.id.button_collect);
        final Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        topImageView = (ImageView) findViewById(R.id.top_iv);
        topTextView = (TextView) findViewById(R.id.top_tv);
        if (url != null && !url.equals("fail"))
            Glide.with(this).load(url).dontAnimate().override(400, 300).into(topImageView);
        topImageView.setAlpha(0.3f);
        albumId = intent.getIntExtra("albumId", 0);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        baseUrl = "http://192.168.191.1:8080/project/albumService/loadAlbumPicData/" + albumId + "/";
        ft.add(R.id.album_fragment, MyPhotoFragment.newInstance(baseUrl, 1));
        ft.commit();
        albumEntry = null;

        discussButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(AlbumPage.this, DiscussPage.class);
                newIntent.putExtra("albumId", albumId);
                startActivity(newIntent);
            }
        });

        retrofit.create(PhotoService.class).loadDetailAlbumData(albumId, getSharedPreferences("info", MODE_PRIVATE).getString("userId", "0"))
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<AlbumEntry>() {
                    @Override
                    public void call(AlbumEntry albumEntry) {
                        Log.i("albumEntry", albumEntry.getUserId() + "");
                        AlbumPage.this.albumEntry = albumEntry;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        collectButton.setImageDrawable(ContextCompat.getDrawable(AlbumPage.this, R.drawable.album_collect_gray));
                        collectButton.setClickable(false);
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        Log.i("info", albumEntry.getName());
                        topTextView.setText(albumEntry.getName());
                        final int userId = Integer.parseInt(getSharedPreferences("info", MODE_PRIVATE).getString("userId", "0"));
                        if (albumEntry != null) {
                            usernameView.setText(albumEntry.getUserName());
                            if (albumEntry.getUserUrl()==null || albumEntry.getUserUrl().equals(""))
                                Glide.with(AlbumPage.this).load(R.drawable.person).into(userphotoView);
                            else
                                Glide.with(AlbumPage.this).load(albumEntry.getUserUrl()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(userphotoView);
                            topButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent personIntent = new Intent(AlbumPage.this, PersonPage.class);
                                    personIntent.putExtra("userId", albumEntry.getUserId());
                                    startActivity(personIntent);
                                }
                            });
                        }
                        if (albumEntry == null || albumEntry.getUserId() == userId) {
                            collectButton.setImageDrawable(ContextCompat.getDrawable(AlbumPage.this, R.drawable.album_collect_gray));
                            collectButton.setClickable(false);
                        } else {
                            initState = albumEntry.getIsCollected();
                            if (albumEntry.getIsCollected() == 1)
                                collectButton.setImageDrawable(ContextCompat.getDrawable(AlbumPage.this, R.drawable.album_collect_yellow));
                            collectButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    albumEntry.setIsCollected(albumEntry.getIsCollected() ^ 1);
                                    if (albumEntry.getIsCollected() == 0) {
                                        Glide.with(AlbumPage.this).load(R.drawable.album_collect).into(collectButton);
                                    } else {
                                        Glide.with(AlbumPage.this).load(R.drawable.album_collect_yellow).into(collectButton);
                                    }
                                }
                            });
                        }
                    }
                });
    }

    private void popWindow() {
        Button yesButton;
        Button noButton;
        View view=LayoutInflater.from(this).inflate(R.layout.report_window,null);
        yesButton = (Button) view.findViewById(R.id.report_yes);
        noButton = (Button) view.findViewById(R.id.report_no);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        backgroundAlpha(0.5f);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportAlbum();
                popupWindow.dismiss();
            }
        });
        popupWindow.showAtLocation(LayoutInflater.from(this).inflate(R.layout.activity_album, null), Gravity.CENTER, 0, 0);
    }
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        this.getWindow().setAttributes(lp);
    }

    private void reportAlbum(){
        retrofit.create(PhotoService.class)
                .reportAlbum(userId,albumId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(AlbumPage.this,"举报成功",Toast.LENGTH_SHORT).show();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        Toast.makeText(AlbumPage.this,"举报成功",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (albumEntry != null)
            Log.i("info", albumEntry.getIsCollected() + "");
        if (collectButton.isClickable()) {
            retrofit.create(PhotoService.class).changeCollectState(albumEntry.getAlbumId(),
                    Integer.parseInt(getSharedPreferences("info", MODE_PRIVATE).getString("userId", "0")), albumEntry.getIsCollected())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Log.i("error", "状态转换异常");
                        }
                    });
        }
        super.onDestroy();
    }
}
