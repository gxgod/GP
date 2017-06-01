package com.example.gxgod.graduationproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.gxgod.graduationproject.R;
import com.example.gxgod.graduationproject.activity.PersonPage;
import com.example.gxgod.graduationproject.entry.UserEntry;
import com.example.gxgod.graduationproject.service.LoginService;
import com.makeramen.roundedimageview.RoundedImageView;

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
 * Created by Administrator on 2017/5/5 0005.
 */
public class AttentionListAdapter extends RecyclerView.Adapter<AttentionListViewHolder> {
    private Context context;
    private int type;
    private String userId;
    private List<UserEntry> dataList;
    private Retrofit retrofit;

    public AttentionListAdapter(Context context, int type, String userId) {
        this.context = context;
        this.type = type;
        this.userId = userId;
        dataList = new ArrayList<>();
        loadData();
    }

    private void loadData() {
        retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.191.1:8080/project/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        retrofit.create(LoginService.class)
                .loadUserAttentionData(userId, type)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<UserEntry>>() {
                    @Override
                    public void call(List<UserEntry> userEntries) {
                        dataList = userEntries;
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
    public AttentionListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_attention, parent, false);
        AttentionListViewHolder viewHolder = new AttentionListViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AttentionListViewHolder holder, final int position) {
        if (dataList.get(position).getUserUrl().equals(""))
            Glide.with(context).load(R.drawable.person).into(holder.iv);
        else
            Glide.with(context).load(dataList.get(position).getUserUrl()).into(holder.iv);
        holder.tv.setText(dataList.get(position).getUserName());
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PersonPage.class);
                intent.putExtra("userId",dataList.get(position).getUserId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}

class AttentionListViewHolder extends RecyclerView.ViewHolder {

    RoundedImageView iv;
    TextView tv;
    View v;

    public AttentionListViewHolder(View itemView) {
        super(itemView);
        v = itemView;
        iv = (RoundedImageView) itemView.findViewById(R.id.attention_iv);
        tv = (TextView) itemView.findViewById(R.id.attention_tv);
    }
}