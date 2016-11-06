package com.lufficc.ishuhui.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lufficc.ishuhui.model.ChapterListModel;
import com.lufficc.ishuhui.model.ComicsModel;
import com.lufficc.ishuhui.model.LoginModel;
import com.lufficc.ishuhui.model.RegisterModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by lufficc on 2016/8/25.
 */

public interface Api {
    @GET("/ComicBooks/GetAllBook")
    Call<ComicsModel> getComics(
            @Query("ClassifyId") String classify,
            @Query("PageIndex") int page);

    @GET("/ComicBooks/GetChapterList")
    Call<ChapterListModel> getComicChapters(
            @Query("id") String id,
            @Query("PageIndex") int page);

    @GET("/ComicBooks/GetLastChapterForBookIds")
    Call<ChapterListModel> getLateComicChapters(
            @Query("idJson") String ids,
            @Query("PageIndex") int page);

    @POST("/Subscribe")
    Call<JsonObject> subscribe(
            @Query("bookid") String bookId,
            @Query("isSubscribe") String isSubscribe,
            @Query("fromtype") int fromType);


    @POST("/UserCenter/Regedit")
    Call<RegisterModel> register(
            @Query("Email") String email,
            @Query("Password") String password,
            @Query("FromType") int fromType);

    @POST("/UserCenter/Login")
    Call<LoginModel> login(
            @Query("Email") String bookId,
            @Query("Password") String isSubscribe,
            @Query("FromType") String fromType);

    @GET("/ComicBooks/GetSubscribe")
    Call<ComicsModel> getSubscribedComics();


    @POST("/ComicBooks/GetAllBook")
    Call<ComicsModel> search(@Query("Title") String keyword);

}
