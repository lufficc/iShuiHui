package com.lufficc.ishuhui.model;

import java.io.Serializable;

/**
 * Created by lufficc on 2016/8/25.
 */

public class Comic implements Serializable{
    public int Id;
    public String Title;
    public String FrontCover;
    public String RefreshTime;
    public String RefreshTimeStr;
    public String Explain;
    public String SerializedState;
    public String Author;
    public int LastChapterNo;
    public int ClassifyId;
    public boolean Recommend;
    public int Copyright;
    public Chapter LastChapter;
}
