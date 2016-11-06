package com.lufficc.ishuhui.model;

import com.orm.dsl.Table;

import java.io.Serializable;

/**
 * Created by lufficc on 2016/8/25.
 */

@Table
public class Comic implements Serializable {
    public Long getId() {
        return id;
    }

    public Long id;

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
    public int page;
}
