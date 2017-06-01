package com.example.gxgod.graduationproject.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gxgod.graduationproject.R;
import com.example.gxgod.graduationproject.adapter.DiscussListAdapter;
import com.example.gxgod.graduationproject.service.DiscussService;
import com.example.gxgod.graduationproject.view.DividerItemDecoration;


import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/4/28 0028.
 */
public class DiscussPage extends Activity implements View.OnClickListener,TextWatcher{

    private ImageView closeButton;
    private TextView discussCountView;
    private RecyclerView discussList;
    private int albumId;
    private Retrofit retrofit;
    private Button sendButton;
    private EditText discussEdit;
    private DiscussListAdapter discussListAdapter;
    private static final String baseUrl = "http://192.168.191.1:8080/project/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discuss);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        retrofit = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();
        albumId = intent.getIntExtra("albumId", 0);
        Log.i("albumId",albumId+"");
        closeButton = (ImageView) findViewById(R.id.button_discuss_close);
        sendButton = (Button) findViewById(R.id.button_send);
        sendButton.setEnabled(false);
        discussEdit = (EditText) findViewById(R.id.edit_discuss);
        discussEdit.addTextChangedListener(this);
        closeButton.setOnClickListener(this);
        sendButton.setOnClickListener(this);
        discussCountView = (TextView) findViewById(R.id.discussCount);
        discussList = (RecyclerView) findViewById(R.id.discuss_list);
        discussList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        discussListAdapter = new DiscussListAdapter(DiscussPage.this,retrofit,albumId);
        discussList.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));
        discussList.setAdapter(discussListAdapter);
        refreshDiscussCount();
    }

    private void refreshDiscussCount() {
        retrofit.create(DiscussService.class).getDiscussCount(String.valueOf(albumId)).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                discussCountView.setText("(" + s + ")");
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.equals(closeButton)) {
            this.finish();
        }else if (v.equals(sendButton)){
            sendDiscussToServer(discussEdit.getText().toString());
        }
    }

    private void sendDiscussToServer(String s) {
        retrofit.create(DiscussService.class).
                sendDiscussToServer(albumId,getSharedPreferences("info",MODE_PRIVATE).getString("userId","0"),s)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(DiscussPage.this, "发送失败", Toast.LENGTH_SHORT).show();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        Toast.makeText(DiscussPage.this,"评论已发送",Toast.LENGTH_SHORT).show();
                        refreshDiscussCount();
                        refreshList(discussEdit.getText().toString(),getSharedPreferences("info",MODE_PRIVATE).getString("userName","0"));
                        discussEdit.setText("");
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm.isActive())
                        imm.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                });
    }

    private void refreshList(String content,String username) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=new Date(System.currentTimeMillis());
        String time=sdf.format(date);
        String url = getSharedPreferences("info",MODE_PRIVATE).getString("userUrl","");
        discussListAdapter.addData(content,username,time,url);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().equals("")) sendButton.setEnabled(false);else sendButton.setEnabled(true);
    }
}
