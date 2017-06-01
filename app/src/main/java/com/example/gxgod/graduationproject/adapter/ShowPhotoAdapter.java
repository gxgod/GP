package com.example.gxgod.graduationproject.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gxgod.graduationproject.R;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by Administrator on 2017/3/9 0009.
 */
public class ShowPhotoAdapter extends PagerAdapter {

    private List dataList;
    private Context context;
    private List<PhotoView> imageViews;
    public ShowPhotoAdapter(List dataList, Context context){
        this.dataList = dataList;
        this.context = context;
        imageViews = new ArrayList<>();
        for (int i=0;i<20;i++){
            PhotoView view = new PhotoView(context);
            view.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageViews.add(view);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView view = imageViews.get(position%20);
        Glide.with(context).load(dataList.get(position)).into(view);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(imageViews.get(position%20));
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
