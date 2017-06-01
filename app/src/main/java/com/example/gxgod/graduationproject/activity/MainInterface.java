package com.example.gxgod.graduationproject.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageButton;

import com.example.gxgod.graduationproject.R;
import com.example.gxgod.graduationproject.fragment.HomeFragment;
import com.example.gxgod.graduationproject.fragment.Recommend;


/**
 * Created by gxgod on 2017/2/9.
 */
public class MainInterface extends FragmentActivity {

    private Fragment recommendFragment;
    private Fragment homeFragment;
    private ImageButton btn1, btn2;
    private int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maininterface);
        initData();
        initView();
    }

    private void initData() {
        recommendFragment = new Recommend();
        homeFragment = new HomeFragment();
    }

    private void initView() {
        final FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.main_fragment, recommendFragment);
        ft.add(R.id.main_fragment, homeFragment);
        hideFragment(ft);
        ft.show(homeFragment);
        ft.commit();
        btn1 = (ImageButton) findViewById(R.id.main_btn1);
        btn2 = (ImageButton) findViewById(R.id.main_btn2);
        page = 0;
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (page != 0) {
                    page = 0;
                    FragmentTransaction ft = fm.beginTransaction();
                    hideFragment(ft);
                    ft.show(homeFragment);
                    ft.commit();
                }
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (page != 1) {
                    page = 1;
                    FragmentTransaction ft = fm.beginTransaction();
                    hideFragment(ft);
                    ft.show(recommendFragment);
                    ft.commit();
                }
            }
        });

    }

    private void hideFragment(FragmentTransaction ft) {
        ft.hide(recommendFragment);
        ft.hide(homeFragment);
    }


}
