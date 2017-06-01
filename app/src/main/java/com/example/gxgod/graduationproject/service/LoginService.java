package com.example.gxgod.graduationproject.service;

import com.example.gxgod.graduationproject.entry.UserEntry;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Administrator on 2017/3/9 0009.
 */
public interface LoginService {
    @GET("project/loginService/login/{username}/{password}")
    Observable<UserEntry> getResult(@Path("username") String username, @Path("password") String password);
    @GET("project/loginService/register/{username}/{password}")
    Observable<String> registerResult(@Path("username") String username, @Path("password") String password);

    @GET("loginService/attention/{userId}/{type}")
    Observable<List<UserEntry>> loadUserAttentionData(@Path("userId")String userId,@Path("type")int type);

    @GET("loginService/attentionCount/{userId}")
    Observable<UserEntry> loadUserAttentionCount(@Path("userId")String userId);

    @GET("loginService/attentionUser/{observer}/{observed}/{type}")
    Observable<String> attentionUser(@Path("observer")String a,@Path("observed")String b,@Path("type")int type);

    @GET("loginService/isAttention/{observer}/{observed}")
    Observable<String> isAttention(@Path("observer")String a,@Path("observed")String b);
}
