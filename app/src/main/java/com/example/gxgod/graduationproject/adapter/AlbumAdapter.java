package com.example.gxgod.graduationproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
 * Created by Administrator on 2017/3/15 0015.
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumViewHolder> {
    private Context context;
    private List<AlbumEntry> dataList;
    public AlbumAdapter(Context context){
        this.context = context;
        dataList = new ArrayList<>();
        loadData();
    }

    private void loadData() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://192.168.191.1:8080/project/").addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();
        retrofit.create(PhotoService.class).loadHotAlbums(20)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<AlbumEntry>>() {
                    @Override
                    public void call(List<AlbumEntry> albumEntries) {
                        dataList = albumEntries;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        notifyDataSetChanged();
                    }
                });
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_album,parent,false);
        AlbumViewHolder albumViewHolder = new AlbumViewHolder(view);
        return albumViewHolder;
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder holder, final int position) {
        if (dataList.size()>0 && !dataList.get(position%dataList.size()).getUrl().equals("fail")) {
            Glide.with(context).load(dataList.get(position % dataList.size()).getUrl()).centerCrop().into(holder.iv);
            holder.tv.setText(dataList.get(position%dataList.size()).getName());
            holder.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AlbumPage.class);
                    intent.putExtra("albumId",dataList.get(position%dataList.size()).getAlbumId());
                    intent.putExtra("url",dataList.get(position%dataList.size()).getUrl());
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return 20;
    }
}
class AlbumViewHolder extends RecyclerView.ViewHolder{
    ImageView iv;
    TextView tv;
    CardView cv;
    public AlbumViewHolder(View itemView) {
        super(itemView);
        iv = (ImageView) itemView.findViewById(R.id.album_images);
        tv = (TextView) itemView.findViewById(R.id.album_text);
        cv = (CardView) itemView.findViewById(R.id.album_card);
    }
}