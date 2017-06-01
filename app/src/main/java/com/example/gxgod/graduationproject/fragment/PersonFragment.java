package com.example.gxgod.graduationproject.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gxgod.graduationproject.R;
import com.example.gxgod.graduationproject.adapter.PersonListAdapter;
import com.example.gxgod.graduationproject.view.DividerItemDecoration;

/**
 * Created by Administrator on 2017/5/8 0008.
 */
public class PersonFragment extends Fragment {

    private int type;
    private String userId;
    private String id;
    private RecyclerView recycleView;
    public static PersonFragment newInstance(int type, String userId,String id) {
        Bundle args = new Bundle();
        args.putInt("type",type);
        args.putString("userId",userId);
        args.putString("id",id);
        PersonFragment fragment = new PersonFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle  =getArguments();
        type = bundle.getInt("type");
        userId = bundle.getString("userId");
        id = bundle.getString("id");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_person,container,false);
        recycleView = (RecyclerView) view.findViewById(R.id.person_list);
        recycleView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recycleView.setAdapter(new PersonListAdapter(getContext(),type,userId,id));
        recycleView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        return view;
    }
}
