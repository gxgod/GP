package com.example.gxgod.graduationproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gxgod.graduationproject.R;
import com.example.gxgod.graduationproject.activity.AlbumPage;
import com.example.gxgod.graduationproject.entry.AlbumEntry;
import com.example.gxgod.graduationproject.service.PhotoService;

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
 * Created by Administrator on 2017/5/15 0015.
 */
public class SearchListAdapter extends RecyclerView.Adapter<SearchListViewHolder> {
    private Context context;
    private Retrofit retrofit;
    private List<AlbumEntry> dataList;
    private final static String url = "http://192.168.191.1:8080/project/";

    public SearchListAdapter(Context context, String keyword) {
        this.context = context;
        dataList = new ArrayList<>();
        retrofit = new Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create()).addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        loadData(keyword);
    }

    @Override
    public SearchListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0)
            view = LayoutInflater.from(context).inflate(R.layout.item_search, parent, false);
        else
            view = LayoutInflater.from(context).inflate(R.layout.item_search_empty, parent, false);
        SearchListViewHolder viewHolder = new SearchListViewHolder(view,viewType);
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if (dataList.size() == 0)
            return 1;
        else
            return 0;
    }

    @Override
    public void onBindViewHolder(SearchListViewHolder holder, final int position) {
       if (getItemViewType(position)==0){
           if (dataList.get(position).getUrl().equals("fail"))
               Glide.with(context).load(R.drawable.empty).asBitmap().into(holder.iv);
           else
               Glide.with(context).load(dataList.get(position).getUrl()).asBitmap().into(holder.iv);
           holder.tv.setText(dataList.get(position).getName());
           holder.v.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent intent = new Intent(context, AlbumPage.class);
                   intent.putExtra("url",dataList.get(position).getUrl());
                   intent.putExtra("albumId",dataList.get(position).getAlbumId());
                   context.startActivity(intent);
               }
           });
       }else{
           holder.tv.setText("没能找到合适的结果");
       }
    }

    @Override
    public int getItemCount() {
        if (dataList.size() > 0)
            return dataList.size();
        else
            return 1;
    }

    //根据关键词加载数据
    public void loadData(String keyword) {
        retrofit.create(PhotoService.class)
                .searchKeyword(keyword)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<AlbumEntry>>() {
                    @Override
                    public void call(List<AlbumEntry> albumEntries) {
                        dataList = albumEntries;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(context, "出错啦!", Toast.LENGTH_SHORT).show();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        notifyDataSetChanged();
                    }
                });
    }
}

class SearchListViewHolder extends RecyclerView.ViewHolder {
    ImageView iv;
    TextView tv;
    View v;
    public SearchListViewHolder(View itemView, int viewType) {
        super(itemView);
        if (viewType == 0) {
            v = itemView;
            iv = (ImageView) itemView.findViewById(R.id.search_iv);
            tv = (TextView) itemView.findViewById(R.id.search_tv);
        }else{
            tv = (TextView) itemView.findViewById(R.id.search_tv_empty);
        }
    }
}