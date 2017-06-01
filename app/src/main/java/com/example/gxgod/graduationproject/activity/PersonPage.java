package com.example.gxgod.graduationproject.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.gxgod.graduationproject.R;
import com.example.gxgod.graduationproject.adapter.PersonAdapter;
import com.example.gxgod.graduationproject.entry.UserEntry;
import com.example.gxgod.graduationproject.fragment.PersonFragment;
import com.example.gxgod.graduationproject.service.LoginService;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/5/8 0008.
 */
public class PersonPage extends FragmentActivity {

    private String userId;
    private TextView usernameTextView;
    private RoundedImageView userPhotoImageView;
    private TextView attentionCountTextView;
    private Retrofit retrofit;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<Fragment> dataList;
    private Button attentionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        initView();
    }

    private void initView() {
        retrofit = new Retrofit.Builder().baseUrl("http://192.168.191.1:8080/project/").addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create()).addConverterFactory(ScalarsConverterFactory.create())
                .build();
        attentionButton = (Button) findViewById(R.id.attention_button);
        Intent intent = getIntent();
        userId = String.valueOf(intent.getIntExtra("userId", 0));
        int type = getSharedPreferences("info", MODE_PRIVATE).getInt("userType",0);
        if (type==1 || userId.equals(getSharedPreferences("info", MODE_PRIVATE).getString("userId", "0")))
            attentionButton.setVisibility(View.GONE);
        else {
            loadAttentionState(attentionButton);
            attentionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeButtonState(attentionButton);
                }
            });
        }
        userPhotoImageView = (RoundedImageView) findViewById(R.id.person_userPhoto);
        Glide.with(this).load(R.drawable.person).into(userPhotoImageView);
        attentionCountTextView = (TextView) findViewById(R.id.person_attentionCount);
        usernameTextView = (TextView) findViewById(R.id.person_username);
        attentionCountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonPage.this, AttentionPage.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });
        loadAttentionCount();

        tabLayout = (TabLayout) findViewById(R.id.person_tabLayout);
        viewPager = (ViewPager) findViewById(R.id.person_viewPager);
        dataList = new ArrayList<>();
        for (int i = 0; i <= 1; i++) dataList.add(PersonFragment.newInstance(i, userId,getSharedPreferences("info", MODE_PRIVATE).getString("userId", "0")));
        viewPager.setAdapter(new PersonAdapter(getSupportFragmentManager(), dataList));
        tabLayout.setupWithViewPager(viewPager);
    }

    private void loadAttentionState(final Button attentionButton) {
        retrofit.create(LoginService.class).isAttention(getSharedPreferences("info",MODE_PRIVATE).getString("userId","0"),userId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        if (s.equals("0"))
                            attentionButton.setText("关注");
                        else
                            attentionButton.setText("已关注");
                    }
                });
    }

    private void changeButtonState(Button attentionButton) {
        if (attentionButton.getText().toString().equals("已关注"))
            attentionButton.setText("关注");
        else
            attentionButton.setText("已关注");
    }

    private void loadAttentionCount() {
        retrofit.create(LoginService.class)
                .loadUserAttentionCount(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<UserEntry>() {
                    @Override
                    public void call(UserEntry userEntry) {
                        String info = "关注" + userEntry.getAttentionCount() + "  |  粉丝" + userEntry.getFansCount();
                        attentionCountTextView.setText(info);
                        usernameTextView.setText(userEntry.getUserName());
                        String userUrl = userEntry.getUserUrl();
                        if (!userUrl.equals(""))
                            Glide.with(PersonPage.this).load(userUrl).into(userPhotoImageView);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (attentionButton.getVisibility() != View.GONE) {
            int type;
            if (attentionButton.getText().toString().equals("已关注")) type = 1;
            else type = 0;
            retrofit.create(LoginService.class).attentionUser(getSharedPreferences("info",MODE_PRIVATE).getString("userId","0"),userId,type)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {

                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {

                        }
                    });
        }
        super.onDestroy();
    }
}
