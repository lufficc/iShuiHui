package com.lufficc.ishuhui.model;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;

/**
 * Created by lufficc on 2016/8/25.
 */
@Table("chapters")
public class Chapter implements Serializable {
    @PrimaryKey(AssignType.BY_MYSELF)
    public String Id;
    public int page;
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
