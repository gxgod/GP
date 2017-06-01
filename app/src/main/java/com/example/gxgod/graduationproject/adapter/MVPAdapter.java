package com.example.gxgod.graduationproject.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by gxgod on 2017/2/10.
 */
public class MVPAdapter extends FragmentPagerAdapter{

    private List<Fragment> datalist;
    private String[] titleList;
    public MVPAdapter(FragmentManager fm, List<Fragment> datalist, String[] titleList) {
        super(fm);
        this.datalist = datalist;
        this.titleList = titleList;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList[position];
    }

    @Override
    public Fragment getItem(int position) {
        return datalist.get(position);
    }
    @Override
    public int getCount() {
        return datalist.size();
    }
}
