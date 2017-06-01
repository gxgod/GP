package com.example.gxgod.graduationproject.service;

import com.example.gxgod.graduationproject.entry.AlbumEntry;
import com.example.gxgod.graduationproject.entry.PhotoEntry;
import com.example.gxgod.graduationproject.entry.ReportedEntry;

import java.util.List;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Administrator on 2017/2/23 0023.
 */
public interface PhotoService {
    @GET("{start}/{count}")
    Observable<PhotoEntry> getPhoto(@Path("count") int count, @Path("start") int start);

    @POST("deleteimage")
    Observable<String> deletePhoto(@Query("path") String path);

    @POST("albumService/addPictureIntoAlbum")
    Observable<String> addPictureIntoAlbum(@Query("id") int id,@Query("url") String url);

    @POST("albumService/createNewAlbum")
    Observable<String> createNewAlbum(@Query("userId") String id,@Query("name")String name,@Query("public") boolean type);

    @GET("albumService/loadAlbumData/{userId}/{id}")
    Observable<List<AlbumEntry>> loadAlbumData(@Path("userId") String userId,@Path("id") String id);

    @GET("albumService/loadCollectionData/{userId}")
    Observable<List<AlbumEntry>> loadCollectionData(@Path("userId")String id);

    @GET("albumService/loadDetailAlbumData/{albumId}/{userId}")
    Observable<AlbumEntry> loadDetailAlbumData(@Path("albumId") int albumId,@Path("userId") String userId);

    @GET("albumService/changeCollectState/{albumId}/{userId}/{type}")
    Observable<String> changeCollectState(@Path("albumId")int albumId,@Path("userId")int userId,@Path("type")int type);

    @GET("albumService/loadHotAlbums/{count}")
    Observable<List<AlbumEntry>> loadHotAlbums(@Path("count")int count);

    @GET("albumService/deleteAlbum/{albumId}")
    Observable<String> deleteAlbum(@Path("albumId")int id);

    @POST("albumService/searchKeyword")
    Observable<List<AlbumEntry>> searchKeyword(@Query("keyword") String keyword);

    @POST("albumService/loadReportedAlbum")
    Observable<List<ReportedEntry>> loadReportedAlbum();

    @GET("albumService/reportAlbum/{albumId}/{userId}")
    Observable<String> reportAlbum(@Path("userId")String userId,@Path("albumId")int albumId);

    @GET("albumService/deleteReportInfo/{albumId}/{type}")
    Observable<String> deleteReportInfo(@Path("albumId")int albumId,@Path("type")int type);
}
