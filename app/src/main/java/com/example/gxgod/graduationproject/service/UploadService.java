package com.example.gxgod.graduationproject.service;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Administrator on 2017/4/11 0011.
 */
public interface UploadService {

    @POST("project/file/upload")
    @Multipart
    Observable<String> uploadFile(@Part MultipartBody.Part file,@Part("userId") String userId);

    @POST("file/uploadUserPhoto")
    @Multipart
    Observable<String> uploadUserPhoto(@Part MultipartBody.Part file, @Part("userId")String userId);
}
