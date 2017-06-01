package com.example.gxgod.graduationproject.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;

import com.example.gxgod.graduationproject.R;
import com.example.gxgod.graduationproject.adapter.AdminListAdapter;

/**
 * Created by Administrator on 2017/5/28 0028.
 */
public class AdminPage extends Activity implements View.OnClickListener{

    private ImageButton imageButton;
    private RecyclerView reportInfoList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminpage);
        initView();
    }

    private void initView() {
        imageButton = (ImageButton) findViewById(R.id.admin_close);
        imageButton.setOnClickListener(this);

        reportInfoList = (RecyclerView) findViewById(R.id.admin_list);
        reportInfoList.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        reportInfoList.setAdapter(new AdminListAdapter(this));
    }


    @Override
    public void onClick(View v) {
        if (v.equals(imageButton)){
            Intent intent = new Intent(this,LoginPage.class);
            startActivity(intent);
            this.finish();
        }
    }
}
