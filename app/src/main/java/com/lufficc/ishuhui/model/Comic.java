package com.lufficc.ishuhui.model;


import com.litesuits.orm.db.annotation.Default;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;

/**
 * Created by lufficc on 2016/8/25.
 */

@Table("comcis")
public class Comic implements Serializable {

    @PrimaryKey(AssignType.BY_MYSELF)
    public int Id;

    public String Title;
    public String FrontCover;
    public String RefreshTime;
    public String RefreshTimeStr;
    public String Explain;
    public String SerializedState;
    public String Author;
    public int LastChapterNo;
    public String ClassifyId;
    public boolean Recommend;
    public int Copyright;
    public Chapter LastChapter;
    public int page;

    @Default("false")
    public boolean isSubscribe;
}
