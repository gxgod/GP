package com.example.gxgod.graduationproject.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gxgod.graduationproject.R;
import com.example.gxgod.graduationproject.adapter.AttentionListAdapter;
import com.example.gxgod.graduationproject.view.DividerItemDecoration;

/**
 * Created by Administrator on 2017/5/5 0005.
 */
public class AttentionFragment extends Fragment {

    private int type;
    private RecyclerView recyclerView;
    private String userId;
    public static AttentionFragment newInstance(int type,String userId) {
        Bundle args = new Bundle();
        args.putInt("type",type);
        args.putString("userId",userId);
        AttentionFragment fragment = new AttentionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        type = bundle.getInt("type");
        userId = bundle.getString("userId");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attention,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.attention_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setAdapter(new AttentionListAdapter(getContext(),type,userId));
    }
}
