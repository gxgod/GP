package com.example.gxgod.graduationproject.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gxgod.graduationproject.R;
import com.example.gxgod.graduationproject.adapter.PhotoViewAdapter;

/**
 * Created by gxgod on 2017/2/10.
 */
public class MyPhotoFragment extends Fragment {


    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private PhotoViewAdapter mPhotoViewAdapter;
    private String url;
    private int type;
    public static MyPhotoFragment newInstance(String url,int type){
        MyPhotoFragment myPhotoFragment = new MyPhotoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url",url);
        bundle.putInt("type",type);
        myPhotoFragment.setArguments(bundle);
        return myPhotoFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        url = bundle.getString("url");
        type = bundle.getInt("type");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myphoto, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.photo_recycleView);
        mLayoutManager = new GridLayoutManager(getContext(), 2){
            @Override
            public boolean canScrollVertically() {
                return super.canScrollVertically();
            }
        };
        mRecyclerView.setLayoutManager(mLayoutManager);
        mPhotoViewAdapter = new PhotoViewAdapter(getContext(),url,type);
        mRecyclerView.setAdapter(mPhotoViewAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int lastVisibleItem = 0;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if ((newState == RecyclerView.SCROLL_STATE_IDLE) && (mPhotoViewAdapter.getLoadState() == false) && (lastVisibleItem + 1 == mPhotoViewAdapter.getItemCount())) {
                    mPhotoViewAdapter.loadImage();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        return view;
    }
}
