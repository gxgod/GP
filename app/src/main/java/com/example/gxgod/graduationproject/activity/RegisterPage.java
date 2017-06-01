package com.example.gxgod.graduationproject.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gxgod.graduationproject.R;
import com.example.gxgod.graduationproject.service.LoginService;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/3/12 0012.
 */
public class RegisterPage extends Activity{

    private EditText username;
    private EditText password;
    private Button button;
    private Boolean isEmpty1;
    private Boolean isEmpty2;
    private final static  String baseUrl="http://192.168.191.1:8080/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);
        init();
    }
    private void changeloginButtonStatus(){
        if(isEmpty1==false && isEmpty2==false)
            button.setEnabled(true);
        else
            button.setEnabled(false);
    }
    private void init(){
        username = (EditText) findViewById(R.id.register_username);
        password = (EditText) findViewById(R.id.register_password);
        button= (Button) findViewById(R.id.check_button);
        button.setEnabled(false);
        isEmpty1 = true;
        isEmpty2=true;
        username.addTextChangedListener(new TextWatcher() {
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
        password.addTextChangedListener(new TextWatcher() {
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
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser(username.getText().toString(),password.getText().toString());
            }
        });
    }

    private void registerUser(String name, String password) {

        Retrofit retrofit = new Retrofit.Builder().baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        LoginService loginService=retrofit.create(LoginService.class);
        loginService.registerResult(name,password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        
                        Log.i("info",s);
                        Toast.makeText(RegisterPage.this, s, Toast.LENGTH_SHORT).show();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(RegisterPage.this,"出现异常",Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
