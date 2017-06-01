package com.example.gxgod.graduationproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.gxgod.graduationproject.R;
import com.example.gxgod.graduationproject.activity.CachePage;
import com.example.gxgod.graduationproject.activity.ShowPage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/17 0017.
 */
public class CacheAdapter extends RecyclerView.Adapter<CacheViewHolder> {
    private Context context;
    private List<String> dataList;
    public CacheAdapter(Context context, List<String> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public CacheViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_photo,parent,false);
        CacheViewHolder viewHolder = new CacheViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CacheViewHolder holder, final int position) {
        Glide.with(context).load(dataList.get(position)).centerCrop().into(holder.iv);
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowPage.class);
                intent.putExtra("id",position);
                intent.putExtra("data",(ArrayList)dataList);
                intent.putExtra("hideButton",1);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}

class CacheViewHolder extends RecyclerView.ViewHolder{

    ImageView iv;
    public CacheViewHolder(View itemView) {
        super(itemView);
        iv = (ImageView) itemView.findViewById(R.id.image_photo);
    }
}
