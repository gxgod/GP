package com.example.gxgod.graduationproject.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.gxgod.graduationproject.R;
import com.example.gxgod.graduationproject.activity.AlbumPage;
import com.example.gxgod.graduationproject.activity.MyPhotoPage;
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
 * Created by Administrator on 2017/4/1 0001.
 */
public class TreeListAdapter extends BaseExpandableListAdapter implements ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupClickListener {

    private Retrofit retrofit;
    private List<String> groupList;
    private Context context;
    private List<AlbumEntry> dataList;
    private List<AlbumEntry> collectionList;
    private PopupWindow popupWindow;
    public TreeListAdapter(List<String> groupList, Context context) {
        this.groupList = groupList;
        this.context = context;
        retrofit = new Retrofit.Builder().baseUrl("http://192.168.191.1:8080/project/").addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()).build();
        dataList = new ArrayList<>();
        collectionList = new ArrayList<>();
    }

    private void loadMyAlbumData() {
        String userId =context.getSharedPreferences("info", Context.MODE_PRIVATE).getString("userId", "0");
        retrofit.create(PhotoService.class).loadAlbumData(userId,userId)
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
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        notifyDataSetChanged();
                    }
                });
    }

    private void loadCollectionData() {
        retrofit.create(PhotoService.class).loadCollectionData(context.getSharedPreferences("info", Context.MODE_PRIVATE).getString("userId", "0"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<AlbumEntry>>() {
                    @Override
                    public void call(List<AlbumEntry> albumEntries) {
                        collectionList = albumEntries;
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
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        switch (groupPosition) {
            case 0:
                return 0;
            case 1:
                return dataList.size();
            case 2:
                return collectionList.size();
            default:
                return 0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_group, parent, false);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.tv = (TextView) convertView.findViewById(R.id.list_group_tv);
            groupViewHolder.iv = (ImageView) convertView.findViewById(R.id.list_group_iv);
            convertView.setTag(groupViewHolder);
        } else
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        switch (groupPosition) {
            case 0:
                Glide.with(context).load(R.drawable.list_pic).into(groupViewHolder.iv);
                break;
            case 1:
                Glide.with(context).load(R.drawable.list_album).into(groupViewHolder.iv);
                break;
            case 2:
                Glide.with(context).load(R.drawable.list_collection).into(groupViewHolder.iv);
                break;
        }
        groupViewHolder.tv.setText(groupList.get(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_chilid, parent, false);
            childViewHolder = new ChildViewHolder();
            childViewHolder.iv = (ImageView) convertView.findViewById(R.id.list_child_iv);
            childViewHolder.tv = (TextView) convertView.findViewById(R.id.list_child_tv);
            childViewHolder.ib= (ImageButton) convertView.findViewById(R.id.option_button);
            convertView.setTag(childViewHolder);
        } else
            childViewHolder = (ChildViewHolder) convertView.getTag();
        if (groupPosition == 1) {
            childViewHolder.ib.setVisibility(View.VISIBLE);
            childViewHolder.ib.setFocusable(false);
            setImageButtonOnClick(childViewHolder.ib,childPosition);
            if (dataList.get(childPosition).getUrl().equals("fail"))
                Glide.with(context).load(R.drawable.empty).into(childViewHolder.iv);
            else
                Glide.with(context).load(dataList.get(childPosition).getUrl()).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .override(400, 300).thumbnail(0.1f).into(childViewHolder.iv);
            childViewHolder.tv.setText(dataList.get(childPosition).getName());
        } else if (groupPosition == 2) {
            childViewHolder.ib.setVisibility(View.GONE);
            if (collectionList.get(childPosition).getUrl().equals("fail"))
                Glide.with(context).load(R.drawable.empty).into(childViewHolder.iv);
            else
                Glide.with(context).load(collectionList.get(childPosition).getUrl()).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .override(400, 300).thumbnail(0.1f).into(childViewHolder.iv);
            childViewHolder.tv.setText(collectionList.get(childPosition).getName());
        }
        return convertView;
    }

    private void setImageButtonOnClick(ImageButton ib, final int childPosition) {
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initPopupWindow(childPosition);
            }
        });
    }
    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = ((Activity) context).getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        ((Activity) context).getWindow().setAttributes(lp);
    }
    private void initPopupWindow(final int childPosition) {
        View popupWindowView = LayoutInflater.from(context).inflate(R.layout.tree_popupwindow,null);
        popupWindow = new PopupWindow(popupWindowView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setAnimationStyle(R.style.anim_menu_bottombar);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1.0f);
            }
        });

        popupWindow.showAtLocation(LayoutInflater.from(context).inflate(R.layout.fragment_home,null), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,0,0);
        setBackgroundAlpha(0.5f);
        TextView deleteButton = (TextView) popupWindowView.findViewById(R.id.deleteAlbumButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAlbum(childPosition);
            }
        });
    }

    private void deleteAlbum(final int childPosition) {
        Retrofit retrofit=new Retrofit.Builder().baseUrl("http://192.168.191.1:8080/project/").addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create()).build();
        retrofit.create(PhotoService.class).deleteAlbum(dataList.get(childPosition).getAlbumId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        dataList.remove(childPosition);
                        notifyDataSetChanged();
                        if (popupWindow.isShowing()) popupWindow.dismiss();
                    }
                });

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        if (groupPosition == 1) {
            Intent intent = new Intent(context, AlbumPage.class);
            intent.putExtra("albumId", dataList.get(childPosition).getAlbumId());
            intent.putExtra("url", dataList.get(childPosition).getUrl());
            context.startActivity(intent);
        }else if (groupPosition == 2){
            Intent intent = new Intent(context,AlbumPage.class);
            intent.putExtra("albumId",collectionList.get(childPosition).getAlbumId());
            intent.putExtra("url",collectionList.get(childPosition).getUrl());
            context.startActivity(intent);
        }
        return true;
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        if (groupPosition == 0) {
            Intent intent = new Intent(context, MyPhotoPage.class);
            context.startActivity(intent);
            return true;
        } else if (groupPosition == 1) {
            loadMyAlbumData();
        } else if (groupPosition == 2) {
            loadCollectionData();
        }
        return false;
    }
}

class GroupViewHolder {
    ImageView iv;
    TextView tv;
}

class ChildViewHolder {
    ImageView iv;
    TextView tv;
    ImageButton ib;
}