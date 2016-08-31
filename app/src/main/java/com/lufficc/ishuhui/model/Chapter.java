package com.lufficc.ishuhui.model;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by lufficc on 2016/8/25.
 */

public class Chapter extends RealmObject implements Serializable{
    public String Id;
    public String Title;
    public String FrontCover;
    public String Sort;
    public String Images;
    public String RefreshTimeStr;
    public int ChapterNo;
    public String LastChapterNo;
    public String BookId;
    public String RefreshTime;
    public String PostUser;
    public int Reel;
    public int ChapterType;
    public String CreateTime;
}
