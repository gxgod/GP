package com.example.gxgod.graduationproject.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;

import com.example.gxgod.graduationproject.R;
import com.example.gxgod.graduationproject.adapter.SearchListAdapter;
import com.wyt.searchbox.SearchFragment;
import com.wyt.searchbox.custom.IOnSearchClickListener;

/**
 * Created by Administrator on 2017/5/15 0015.
 */
public class SearchPage extends FragmentActivity implements View.OnClickListener {
    private ImageButton backButton;
    private ImageButton searchButton;
    private RecyclerView searchList;
    private SearchListAdapter searchListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
    }

    private void initView() {
        backButton = (ImageButton) findViewById(R.id.search_backButton);
        searchButton = (ImageButton) findViewById(R.id.search_searchButton);
        backButton.setOnClickListener(this);
        searchButton.setOnClickListener(this);

        searchList = (RecyclerView) findViewById(R.id.search_list);
        searchList.setLayoutManager(new LinearLayoutManager(SearchPage.this,LinearLayoutManager.VERTICAL,false));
        String keyword = getIntent().getStringExtra("keyword");
        searchListAdapter = new SearchListAdapter(this,keyword);
        searchList.setAdapter(searchListAdapter);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(backButton)) {
            this.finish();
        } else if (v.equals(searchButton)) {
            popupSearchWindow();
        }
    }

    private void popupSearchWindow() {
        SearchFragment searchFragment = SearchFragment.newInstance();
        searchFragment.setOnSearchClickListener(new IOnSearchClickListener() {
            @Override
            public void OnSearchClick(String keyword) {
                if (searchListAdapter!=null)
                    searchListAdapter.loadData(keyword);
            }
        });
        searchFragment.show(getSupportFragmentManager(),SearchFragment.TAG);
    }
}
