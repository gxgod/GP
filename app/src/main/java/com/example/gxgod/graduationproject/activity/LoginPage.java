package com.example.gxgod.graduationproject.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gxgod.graduationproject.R;
import com.example.gxgod.graduationproject.entry.UserEntry;
import com.example.gxgod.graduationproject.service.LoginService;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by gxgod on 2017/2/9.
 */
public class LoginPage extends Activity {

    private EditText usernameET;
    private EditText passwordET;
    private Button loginButton;
    private Button registerButton;
    private boolean isEmpty1;
    private boolean isEmpty2;
    private final static String baseUrl = "http://192.168.191.1:8080/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        initView();
    }

    private void initView(){
        usernameET = (EditText) findViewById(R.id.login_username);
        passwordET = (EditText) findViewById(R.id.login_password);
        loginButton = (Button) findViewById(R.id.login_button);
        registerButton = (Button) findViewById(R.id.register_button);
        loginButton.setEnabled(false);
        isEmpty1=true;
        isEmpty2=true;
        usernameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) isEmpty1=true;else isEmpty1=false;
                changeloginButtonStatus();
            }
        });
        passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) isEmpty2=true;else isEmpty2=false;
                changeloginButtonStatus();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUser();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginPage.this,RegisterPage.class);
                startActivity(intent);
            }
        });
    }

    private void changeloginButtonStatus(){
        if(isEmpty1==false && isEmpty2==false)
            loginButton.setEnabled(true);
        else
            loginButton.setEnabled(false);
    }
    private void checkUser(){
        String username = usernameET.getText().toString();
        String password = passwordET.getText().toString();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();
        LoginService loginService = retrofit.create(LoginService.class);
        loginService.getResult(username,password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<UserEntry>() {
                    @Override
                    public void call(UserEntry s) {
                        if (s==null)
                            unsuccessLogin();
                        else
                            successLogin(s);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        unsuccessLogin();
                    }
                });
    }
    private void successLogin(UserEntry s) {
        if (s.getUserType()==0) {
            Intent intent = new Intent(LoginPage.this, MainInterface.class);
            intent.putExtra("username", usernameET.getText().toString());
            SharedPreferences sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userName", usernameET.getText().toString());
            editor.putString("userId", s.getUserId() + "");
            editor.putString("userUrl", s.getUserUrl());
            editor.putInt("userType",0);
            editor.commit();
            startActivity(intent);
        }else
        if (s.getUserType()==1){
            Intent intent = new Intent(LoginPage.this,AdminPage.class);
            SharedPreferences sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userName", usernameET.getText().toString());
            editor.putString("userId", s.getUserId() + "");
            editor.putString("userUrl", s.getUserUrl());
            editor.putInt("userType",1);
            editor.commit();
            startActivity(intent);
        }
        this.finish();
    }
    private void unsuccessLogin(){
        Toast.makeText(this,"登录失败",Toast.LENGTH_SHORT).show();
    }
}
