package com.example.gxgod.graduationproject.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageButton;

import com.example.gxgod.graduationproject.R;
import com.example.gxgod.graduationproject.fragment.MyPhotoFragment;

/**
 * Created by Administrator on 2017/4/1 0001.
 */
public class MyPhotoPage extends FragmentActivity {

    private ImageButton closeButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myphotopage);
        initView();

    }
    private void initView() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment myPhotoFragment = MyPhotoFragment.newInstance("http://192.168.191.1:8080/project/imageService/loadimage/"+getSharedPreferences("info",MODE_PRIVATE).getString("userId","0")+"/",0);
        fragmentTransaction.add(R.id.myphoto_fragment,myPhotoFragment);
        fragmentTransaction.commit();
        closeButton = (ImageButton) findViewById(R.id.myphoto_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyPhotoPage.this.finish();
            }
        });
    }
}
