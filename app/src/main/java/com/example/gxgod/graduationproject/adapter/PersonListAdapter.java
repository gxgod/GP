package com.example.gxgod.graduationproject.adapter;

import android.content.Context;
import android.content.Intent;
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
 * Created by Administrator on 2017/5/8 0008.
 */
public class PersonListAdapter extends RecyclerView.Adapter<PersonListViewHolder> {

    private Context context;
    private List<AlbumEntry> dataList;
    private String userId,id;
    private int type;
    public PersonListAdapter(Context context, int type, String userId,String id){
        this.context = context;
        this.type = type;
        this.userId = userId;
        this.id = id;
        dataList = new ArrayList<>();
        loadData(type);
    }

    private void loadData(int type) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://192.168.191.1:8080/project/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        if (type == 0) {
            retrofit.create(PhotoService.class)
                    .loadAlbumData(userId,id)
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
                            notifyDataSetChanged();;
                        }
                    });
        }else{
            retrofit.create(PhotoService.class)
                    .loadCollectionData(userId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Action1<List<AlbumEntry>>() {
                        @Override
                        public void call(List<AlbumEntry> albumEntries) {
                            dataList=albumEntries;
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
    }
    @Override
    public PersonListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_person,parent,false);
        PersonListViewHolder viewHolder = new PersonListViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PersonListViewHolder holder, final int position) {
        if (dataList.get(position).getUrl().equals("fail"))
            Glide.with(context).load(R.drawable.empty).fitCenter().into(holder.iv);
        else
            Glide.with(context).load(dataList.get(position).getUrl()).centerCrop().into(holder.iv);
        holder.tv.setText(dataList.get(position).getName());
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AlbumPage.class);
                intent.putExtra("albumId",dataList.get(position).getAlbumId());
                intent.putExtra("url",dataList.get(position).getUrl());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
class PersonListViewHolder extends RecyclerView.ViewHolder{
    View v;
    TextView tv;
    ImageView iv;
    public PersonListViewHolder(View itemView) {
        super(itemView);
        v=itemView;
        tv= (TextView) itemView.findViewById(R.id.person_item_tv);
        iv= (ImageView) itemView.findViewById(R.id.person_item_iv);
    }
}