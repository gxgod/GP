package com.example.gxgod.graduationproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gxgod.graduationproject.R;
import com.example.gxgod.graduationproject.activity.AlbumPage;
import com.example.gxgod.graduationproject.entry.ReportedEntry;
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
 * Created by Administrator on 2017/5/28 0028.
 */
public class AdminListAdapter extends RecyclerView.Adapter<AdminListViewHolder> {
    private Context context;
    private Retrofit retrofit;
    private List<ReportedEntry> dataList;
    private static final String baseUrl="http://192.168.191.1:8080/project/";
    public AdminListAdapter(Context context){
        this.context = context;
        retrofit = new Retrofit.Builder().baseUrl(baseUrl).
                addConverterFactory(GsonConverterFactory.create()).
                addConverterFactory(ScalarsConverterFactory.create()).
                addCallAdapterFactory(RxJavaCallAdapterFactory.create()).
                build();
        dataList = new ArrayList<>();
        loadData();
    }

    private void loadData() {
        retrofit.create(PhotoService.class)
                .loadReportedAlbum()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<ReportedEntry>>() {
                    @Override
                    public void call(List<ReportedEntry> reportedEntries) {
                        dataList = reportedEntries;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(context,"加载异常",Toast.LENGTH_SHORT).show();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        notifyDataSetChanged();
                    }
                });
    }

    @Override
    public AdminListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin,parent,false);
        AdminListViewHolder adminListViewHolder = new AdminListViewHolder(view);
        return adminListViewHolder;
    }

    @Override
    public void onBindViewHolder(AdminListViewHolder holder, final int position) {
        final String url =dataList.get(position).getAlbumEntry().getUrl();
        if (url.equals("fail"))
            Glide.with(context).load(R.drawable.empty).into(holder.ib);
        else
            Glide.with(context).load(url).centerCrop().into(holder.ib);
        holder.tv1.setText("图册名:"+dataList.get(position).getAlbumEntry().getName());
        holder.tv2.setText("创建者:"+dataList.get(position).getAlbumEntry().getUserName());
        holder.tv3.setText("举报者:"+dataList.get(position).getReporterName());
        holder.ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AlbumPage.class);
                intent.putExtra("albumId",dataList.get(position).getAlbumEntry().getAlbumId());
                intent.putExtra("url",url);
                context.startActivity(intent);
            }
        });

        holder.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(position,0);
            }
        });
        holder.no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem(position,1);
            }
        });
    }
    private void deleteItem(final int position, int type){
        Retrofit temp = new Retrofit.Builder().baseUrl("http://192.168.191.1:8080/project/")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create()).build();
        temp.create(PhotoService.class)
                .deleteReportInfo(dataList.get(position).getAlbumEntry().getAlbumId(),type)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.i("info",s);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.i("info","fail");
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        notifyItemRemoved(position);
                        dataList.remove(position);
                        notifyItemRangeChanged(0,getItemCount());
                        notifyDataSetChanged();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}

class AdminListViewHolder extends RecyclerView.ViewHolder{
    ImageButton ib,ok,no;
    TextView tv1,tv2,tv3;
    public AdminListViewHolder(View itemView) {
        super(itemView);
        ib= (ImageButton) itemView.findViewById(R.id.admin_iv);
        ok = (ImageButton) itemView.findViewById(R.id.ok);
        no = (ImageButton) itemView.findViewById(R.id.no);
        tv1= (TextView) itemView.findViewById(R.id.admin_tv1);
        tv2= (TextView) itemView.findViewById(R.id.admin_tv2);
        tv3= (TextView) itemView.findViewById(R.id.admin_tv3);
    }
}
