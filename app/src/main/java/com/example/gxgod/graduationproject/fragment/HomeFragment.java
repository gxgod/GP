package com.example.gxgod.graduationproject.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.gxgod.graduationproject.R;
import com.example.gxgod.graduationproject.activity.SubmitPage;
import com.example.gxgod.graduationproject.adapter.TreeListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/31 0031.
 */
public class HomeFragment extends Fragment{

    private  ExpandableListView expandableListView;
    private TreeListAdapter treeListAdapter;
    private List<String> groupList;
    private ImageView addButton;
    private ImageButton openButton;
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=LayoutInflater.from(getContext()).inflate(R.layout.fragment_home,container,false);
        initData();
        openButton= (ImageButton) view.findViewById(R.id.openMenu);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawerLayout);
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        expandableListView = (ExpandableListView) view.findViewById(R.id.home_menu);
        treeListAdapter = new TreeListAdapter(groupList,getContext());
        expandableListView.setAdapter(treeListAdapter);
        expandableListView.setOnGroupClickListener(treeListAdapter);
        expandableListView.setGroupIndicator(null);
        expandableListView.setOnChildClickListener(treeListAdapter);
        addButton = (ImageView) view.findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), SubmitPage.class);
                startActivity(intent);
            }
        });
        return view;
    }
    private void initData(){
        groupList=new ArrayList<>();
        groupList.add("我的图片");
        groupList.add("我的相册");
        groupList.add("我收藏的相册");
    }

}

