package com.example.gxgod.graduationproject.service;

import com.example.gxgod.graduationproject.entry.DiscussEntry;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Administrator on 2017/4/28 0028.
 */
public interface DiscussService {

    @GET("discussService/getdiscusscount/{albumId}")
    Observable<String>  getDiscussCount(@Path("albumId")String albumId);

    @POST("discussService/sendDiscuss")
    Observable<String> sendDiscussToServer(@Query("albumId")int albumId,@Query("userId")String userId,@Query("content")String s);

    @POST("discussService/loadDiscussData")
    Observable<List<DiscussEntry>> loadDiscussData(@Query("albumId")int albumId);
}
