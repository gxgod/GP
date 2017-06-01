package com.example.gxgod.graduationproject.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.gxgod.graduationproject.R;

public class WelcomePage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkAutoLogin();
            }
        }, 2000);
    }


    private void checkAutoLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyApp", Context.MODE_PRIVATE);
        boolean isAutoLogin = sharedPreferences.getBoolean("isAutoLogin", false);
        Intent intent = null;
        if (isAutoLogin == true){
            intent = new Intent(WelcomePage.this,MainInterface.class);
            intent.putExtra("username",sharedPreferences.getString("username",""));
            startActivity(intent);
            this.finish();
        }else{
            intent = new Intent(WelcomePage.this,LoginPage.class);
            startActivity(intent);
            this.finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
