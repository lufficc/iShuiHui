package com.lufficc.ishuhui.model;

import com.orm.dsl.Ignore;
import com.orm.dsl.Table;

import java.io.Serializable;

/**
 * Created by lufficc on 2016/8/25.
 */
@Table
public class Chapter implements Serializable {
    @Ignore
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

    public Long getId() {
        return id;
    }
    public Long id;
    public int page;
}
