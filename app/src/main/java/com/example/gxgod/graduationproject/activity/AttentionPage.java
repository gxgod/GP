package com.example.gxgod.graduationproject.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.gxgod.graduationproject.R;
import com.example.gxgod.graduationproject.adapter.AttentionViewPagerAdapter;
import com.example.gxgod.graduationproject.fragment.AttentionFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/5 0005.
 */
public class AttentionPage extends FragmentActivity implements View.OnClickListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<Fragment> dataList;
    private ImageButton backButton;
    private String userId;
    private TextView textView;
    private int type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attention);
        initData();
        initView();
    }

    private void initData() {
        userId = getIntent().getStringExtra("userId");
        dataList = new ArrayList<>();
        for (int i = 0; i <= 1; i++)
            dataList.add(AttentionFragment.newInstance(i, userId));
    }

    private void initView() {
        textView = (TextView) findViewById(R.id.attention_title);
        if (!userId.equals(getSharedPreferences("info", MODE_PRIVATE).getString("userId", "0"))) {
            type = 1;
            textView.setText("TA的好友");
        }
        backButton = (ImageButton) findViewById(R.id.attention_back);
        backButton.setOnClickListener(this);
        tabLayout = (TabLayout) findViewById(R.id.attention_tabLayout);
        viewPager = (ViewPager) findViewById(R.id.attention_viewPager);
        viewPager.setAdapter(new AttentionViewPagerAdapter(getSupportFragmentManager(), dataList,type));
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(backButton)) {
            this.finish();
        }
    }
}
