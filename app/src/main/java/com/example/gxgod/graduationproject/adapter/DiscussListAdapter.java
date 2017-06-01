package com.example.gxgod.graduationproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.gxgod.graduationproject.R;
import com.example.gxgod.graduationproject.activity.PersonPage;
import com.example.gxgod.graduationproject.entry.DiscussEntry;
import com.example.gxgod.graduationproject.service.DiscussService;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/5/2 0002.
 */
public class DiscussListAdapter extends RecyclerView.Adapter<DiscussListViewHolder> {

    private Retrofit retrofit;
    private Context context;
    private int albumId;
    private List<DiscussEntry> dataList;

    public DiscussListAdapter(Context context, Retrofit retrofit, int albumId) {
        this.context = context;
        this.retrofit = retrofit;
        this.albumId = albumId;
        dataList = new ArrayList<>();
        LoadData();
    }

    private void LoadData() {
        retrofit.create(DiscussService.class)
                .loadDiscussData(albumId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<DiscussEntry>>() {
                    @Override
                    public void call(List<DiscussEntry> discussEntries) {
                        dataList = discussEntries;
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

    public void addData(String content, String username, String time,String url) {
        DiscussEntry discussEntry = new DiscussEntry();
        discussEntry.setContent(content);
        discussEntry.setUsername(username);
        discussEntry.setDate(time);
        discussEntry.setUrl(url);
        dataList.add(0, discussEntry);
        notifyDataSetChanged();
    }

    @Override
    public DiscussListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_discuss, parent, false);
        DiscussListViewHolder viewHolder = new DiscussListViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DiscussListViewHolder holder, final int position) {
        holder.usernameTv.setText(dataList.get(position).getUsername());
        holder.contentTv.setText(dataList.get(position).getContent());
        holder.discussIdTv.setText("#" + (getItemCount() - position) + " " + dataList.get(position).getDate());
        if ((dataList.get(position).getUrl() == null)||(dataList.get(position).getUrl().equals("")))
            Glide.with(context).load(R.drawable.person).into(holder.iv);
        else
            Glide.with(context).load(dataList.get(position).getUrl()).into(holder.iv);
        holder.iv.setOnClickListener(new View.OnClickListener() {
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

class DiscussListViewHolder extends RecyclerView.ViewHolder {
    TextView usernameTv;
    TextView discussIdTv;
    TextView contentTv;
    RoundedImageView iv;

    public DiscussListViewHolder(View itemView) {
        super(itemView);
        usernameTv = (TextView) itemView.findViewById(R.id.discuss_username);
        discussIdTv = (TextView) itemView.findViewById(R.id.discuss_number);
        contentTv = (TextView) itemView.findViewById(R.id.discuss_content);
        iv = (RoundedImageView) itemView.findViewById(R.id.discuss_image);
    }
}