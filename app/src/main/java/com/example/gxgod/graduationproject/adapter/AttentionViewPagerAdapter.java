package com.example.gxgod.graduationproject.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Administrator on 2017/5/5 0005.
 */
public class AttentionViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> dataList;
    private int type;

    public AttentionViewPagerAdapter(FragmentManager fm, List<Fragment> dataList, int type) {
        super(fm);
        this.type = type;
        this.dataList = dataList;
    }

    @Override
    public Fragment getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                if (type == 0)
                    return "我的关注";
                else
                    return "ta的关注";
            default:
                if (type == 0)
                    return "我的粉丝";
                else
                    return "ta的粉丝";
        }
    }
}
