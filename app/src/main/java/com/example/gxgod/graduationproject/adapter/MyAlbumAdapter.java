package com.example.gxgod.graduationproject.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gxgod.graduationproject.R;
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
 * Created by Administrator on 2017/4/18 0018.
 */
public class MyAlbumAdapter extends RecyclerView.Adapter<MyAlbumHolder> {

    private Context context;
    private List<AlbumEntry> dataList;
    private Retrofit retrofit;
    private String picUrl;
    private PopupWindow popupWindow;

    public MyAlbumAdapter(Context context, String picUrl) {
        this.context = context;
        this.picUrl = picUrl;
        dataList = new ArrayList<>();
        retrofit = new Retrofit.Builder().baseUrl("http://192.168.191.1:8080/project/").addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();
        loadData();
    }

    private void loadData() {
        String userId = context.getSharedPreferences("info", Context.MODE_PRIVATE).getString("userId", "0");
        retrofit.create(PhotoService.class)
                .loadAlbumData(userId,userId)
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
                        Log.i("info", "load data error");
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        notifyDataSetChanged();
                    }
                });
    }

    @Override
    public MyAlbumHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_myalbum, parent, false);
        MyAlbumHolder myAlbumHolder = new MyAlbumHolder(view);
        return myAlbumHolder;
    }

    @Override
    public void onBindViewHolder(MyAlbumHolder holder, final int position) {
        if (position == 0) {
            holder.tv.setText("新建图册");
            Glide.with(context).load(R.drawable.addalbum).into(holder.iv);
            holder.v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createNewAlbum();
                }
            });
        } else {
            holder.tv.setText(dataList.get(position - 1).getName());
            if (!dataList.get(position - 1).getUrl().equals("fail"))
                Glide.with(context).load(dataList.get(position - 1).getUrl()).into(holder.iv);
            else
                Glide.with(context).load(R.drawable.empty).into(holder.iv);
            holder.v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addPicIntoAlbum(dataList.get(position - 1).getAlbumId());
                }
            });
        }
    }

    private void addPicIntoAlbum(int id) {
        retrofit.create(PhotoService.class).addPictureIntoAlbum(id, picUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        if (s.equals("yes"))
                            Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(context, "该图片已添加", Toast.LENGTH_SHORT).show();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(context, "添加失败", Toast.LENGTH_SHORT).show();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        ((Activity) context).finish();
                    }
                });
    }

    private void createNewAlbum() {
        final String userId = context.getSharedPreferences("info", Context.MODE_PRIVATE).getString("userId", "0");
        View view = LayoutInflater.from(context).inflate(R.layout.createalbum, null);
        Button button = (Button) view.findViewById(R.id.createAlbum_button);
        final EditText editText = (EditText) view.findViewById(R.id.createAlbum_et);
        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.createAlbum_checkbox);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        backgroundAlpha(0.5f);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editText.getText().toString();
                boolean type = checkBox.isChecked();
                if (name.equals("")) Toast.makeText(context, "未输入图册名", Toast.LENGTH_SHORT).show();
                else {

                    retrofit.create(PhotoService.class).createNewAlbum(userId, name, type)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<String>() {
                                @Override
                                public void call(String s) {
                                    int id = Integer.parseInt(s);
                                    popupWindow.dismiss();
                                    addPicIntoAlbum(Integer.parseInt(s));
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    Toast.makeText(context, "创建失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        popupWindow.showAtLocation(LayoutInflater.from(context).inflate(R.layout.activity_myalbum, null), Gravity.CENTER, 0, 0);
    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        ((Activity) context).getWindow().setAttributes(lp);
    }

    @Override
    public int getItemCount() {
        return 1 + dataList.size();
    }
}

class MyAlbumHolder extends RecyclerView.ViewHolder {

    TextView tv;
    ImageView iv;
    View v;

    public MyAlbumHolder(View itemView) {
        super(itemView);
        v = itemView;
        iv = (ImageView) itemView.findViewById(R.id.myAlbum_iv);
        tv = (TextView) itemView.findViewById(R.id.myAlbum_tv);
    }
}
