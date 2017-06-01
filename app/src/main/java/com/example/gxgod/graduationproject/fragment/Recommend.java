package com.example.gxgod.graduationproject.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gxgod.graduationproject.R;
import com.example.gxgod.graduationproject.activity.AlbumPage;
import com.example.gxgod.graduationproject.activity.SearchPage;
import com.example.gxgod.graduationproject.adapter.AlbumAdapter;
import com.example.gxgod.graduationproject.entry.AlbumEntry;
import com.example.gxgod.graduationproject.service.PhotoService;
import com.wyt.searchbox.SearchFragment;
import com.wyt.searchbox.custom.IOnSearchClickListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/3/14 0014.
 */
public class Recommend extends Fragment{

    private ImageButton openButton;
    private ImageButton searchButton;
    private ViewPager mViewPager;
    private List<ImageView> vpList;
    private RecyclerView recyclerView;
    private TextView titleView;
    private List<AlbumEntry> hotAlbums;
    private final static int vpPageCount = 5;
    private LinearLayout dotLayout;
    private int prePosition=0;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
           switch (msg.what){
               case 0x123:
                   mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
                   handler.sendEmptyMessageDelayed(0x123,3000);
                   break;
           }
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_recommend,container,false);
        mViewPager = (ViewPager) view.findViewById(R.id.recycleViewPager);
        dotLayout = (LinearLayout) view.findViewById(R.id.recycleViewPager_dot);
        recyclerView = (RecyclerView) view.findViewById(R.id.album_list);
        titleView = (TextView) view.findViewById(R.id.recycleViewPager_title);
        openButton = (ImageButton) view.findViewById(R.id.openMenu2);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawerLayout);
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        searchButton = (ImageButton) view.findViewById(R.id.button_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupSearchView();
            }
        });
        initData();
        initViewPager();
        initRecycleView();
        return view;
    }

    //弹出搜索框
    private void popupSearchView() {
        final SearchFragment searchFragment = SearchFragment.newInstance();

        searchFragment.setOnSearchClickListener(new IOnSearchClickListener() {
            @Override
            public void OnSearchClick(String keyword) {
                Intent intent = new Intent(getContext(),SearchPage.class);
                intent.putExtra("keyword",keyword);
                startActivity(intent);
            }
        });
        searchFragment.show(getFragmentManager(),SearchFragment.TAG);
    }

    private void initViewPager() {
        mViewPager.setAdapter(new ViewpagerAdapter());
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                int newPosition = position%vpPageCount;
                if (hotAlbums.size()>0)
                titleView.setText(hotAlbums.get(newPosition).getName());
                dotLayout.getChildAt(prePosition).setEnabled(false);
                dotLayout.getChildAt(newPosition).setEnabled(true);
                prePosition = newPosition;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        int item = Integer.MAX_VALUE/2-(Integer.MAX_VALUE/2)%vpPageCount;
        mViewPager.setCurrentItem(item);
        handler.sendEmptyMessageDelayed(0x123,3000);
    }

    private void initRecycleView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        AlbumAdapter albumAdapter=new AlbumAdapter(getContext());
        recyclerView.setAdapter(albumAdapter);
    }

    private void initData() {
        hotAlbums = new ArrayList<>();
        vpList = new ArrayList<>();
        for (int i=0;i<vpPageCount;i++){
            ImageView view = new ImageView(getContext());
            vpList.add(view);
        }
        loadHotAlbum(vpPageCount);
        for (int i=0;i<vpPageCount;i++){
            View view = new View(getContext());
            view.setBackgroundResource(R.drawable.selector_dot);
            view.setEnabled(false);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(15,15);
            if (i!=0) layoutParams.leftMargin=15;
            view.setLayoutParams(layoutParams);
            dotLayout.addView(view);
        }
    }

    private void loadHotAlbum(int vpPageCount) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://192.168.191.1:8080/project/").addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();
        retrofit.create(PhotoService.class).loadHotAlbums(vpPageCount)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<AlbumEntry>>() {
                    @Override
                    public void call(List<AlbumEntry> albumEntries) {
                        hotAlbums = albumEntries;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        for (int i=0;i<vpList.size();i++)
                            loadViewpagerData(vpList.get(i),i);
                        titleView.setText(hotAlbums.get(prePosition).getName());
                    }
                });

    }

    //需要重写
    private void loadViewpagerData(ImageView view, int i) {
        Glide.with(this).load(hotAlbums.get(i).getUrl()).centerCrop().into(view);
    }


    private class ViewpagerAdapter extends PagerAdapter{
        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container,int position) {
            final int newPosition = position%vpList.size();
            ImageView view=vpList.get(newPosition);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), AlbumPage.class);
                    intent.putExtra("albumId",hotAlbums.get(newPosition).getAlbumId());
                    intent.putExtra("url",hotAlbums.get(newPosition).getUrl());
                    startActivity(intent);
                }
            });
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }
    }

}
