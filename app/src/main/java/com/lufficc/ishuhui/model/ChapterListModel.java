package com.lufficc.ishuhui.model;

/**
 * Created by lcc_luffy on 2016/1/23.
 */
public class ChapterListModel {
    public String ErrCode;
    public String ErrMsg;
    public String Costtime;
    public boolean IsError;
    public String Message;

    public ReturnEntity Return;

    public static class ReturnEntity {
        public int ListCount;
        public int PageSize;
        public int PageIndex;
        public Comic ParentItem;
        public java.util.List<Chapter> List;
    }
}

