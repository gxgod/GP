package com.example.gxgod.graduationproject.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gxgod.graduationproject.R;
import com.example.gxgod.graduationproject.activity.MyAlbumPage;
import com.example.gxgod.graduationproject.activity.ShowPage;
import com.example.gxgod.graduationproject.entry.PhotoEntry;
import com.example.gxgod.graduationproject.service.PhotoService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.Result;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/2/23 0023.
 */
public class PhotoViewAdapter extends RecyclerView.Adapter<MyViewHolder>{

    private PopupWindow popupWindow;
    private Context context;
    private List<String> dataList;
    private int start;
    private int count;
    private boolean isLoading;
    private boolean isAllLoad;
    private String url;
    private int type;
    public PhotoViewAdapter(Context context,String url,int type) {
        this.context = context;
        this.url = url;
        this.type = type;
        dataList = new ArrayList<>();
        start = 0;
        count = 10;
        loadImage();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0)
            view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);
        else
            view = LayoutInflater.from(context).inflate(R.layout.loading, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view, viewType);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if (position + 1 != getItemCount() || isAllLoad == true) {
            Glide.with(context).load(dataList.get(position)).placeholder(R.color.gray).centerCrop().thumbnail( 0.1f )
                    .into(holder.imageView);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ShowPage.class);
                    intent.putExtra("id",position);
                    intent.putExtra("data",(ArrayList)dataList);
                    context.startActivity(intent);
                }
            });

            if (type == 0)
            holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    initPopupWindow(position);
                    return false;
                }
            });
        }else{
            Glide.with(context).load(R.drawable.loading).into(holder.imageView);
        }
    }

    private void initPopupWindow(final int position) {
            final View popupWindowView = LayoutInflater.from(context).inflate(R.layout.popupwindow, null);
            popupWindow = new PopupWindow(popupWindowView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setAnimationStyle(R.style.anim_menu_bottombar);
            popupWindow.showAtLocation(LayoutInflater.from(context).inflate(R.layout.activity_myphotopage,null), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,0,0);
            TextView deleteButton= (TextView) popupWindowView.findViewById(R.id.deleteButton);
            deleteButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    deleteItem(position);
                    popupWindow.dismiss();
                    return true;
                }
            });
            TextView addButton = (TextView) popupWindowView.findViewById(R.id.addButton);
            addButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popupWindow.dismiss();
                    Intent intent = new Intent(context,MyAlbumPage.class);
                    intent.putExtra("url",dataList.get(position));
                    context.startActivity(intent);
                    return true;
                }
            });
    }

    private void deleteItem(final int position) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://192.168.191.1:8080/project/imageService/").addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create()).build();
        retrofit.create(PhotoService.class).deletePhoto(dataList.get(position))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        notifyItemRemoved(position);
                        dataList.remove(position);
                        start--;
                        notifyItemRangeChanged(0, getItemCount());
                        notifyDataSetChanged();
                        Toast.makeText(context,"删除成功",Toast.LENGTH_SHORT).show();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(context,"删除失败",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        if (isAllLoad == true)
            return dataList.size();
        else
            return dataList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount() && isAllLoad == false)
            return 1;
        return 0;
    }

    public void loadImage() {
        changeLoadState();
        final int initCount = dataList.size();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        PhotoService photoService = retrofit.create(PhotoService.class);

        Observer<PhotoEntry.Result> observer = new Observer<PhotoEntry.Result>() {
            @Override
            public void onCompleted() {
                if (dataList.size() == initCount) isAllLoad = true;
                notifyDataSetChanged();
                changeLoadState();
                start+=count;
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(PhotoEntry.Result result) {
                Log.i("info",result.getUrl());
                dataList.add(result.getUrl());
            }
        };

        photoService.getPhoto(count, start)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(new Func1<PhotoEntry, Observable<PhotoEntry.Result>>() {
                    @Override
                    public Observable<PhotoEntry.Result> call(PhotoEntry photoEntry) {
                        return Observable.from(photoEntry.getResults());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if ((position + 1 == getItemCount()) && (isAllLoad == false))
                    return 2;
                else
                    return 1;
            }
        });
    }

    public boolean getLoadState() {
        return isLoading;
    }

    private void changeLoadState() {
        if (isLoading == true) isLoading = false;
        else isLoading = true;
    }

}

class MyViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView;

    public MyViewHolder(View itemView, int viewType) {
        super(itemView);
        if (viewType == 0)
            imageView = (ImageView) itemView.findViewById(R.id.image_photo);
        else
            imageView = (ImageView) itemView.findViewById(R.id.image_loading);
    }
}
