package com.lufficc.ishuhui.model;

/**
 * Created by lcc_luffy on 2016/1/27.
 */
public class LoginModel {

    /**
     * ErrCode : 0
     * ErrMsg : 0
     * Return : {"Id":62158,"NickName":null,"Email":"528360256@qq.com","Phone":null,"RegFromType":2,"Avatar":"/Content/images/touxi.jpg"}
     * Costtime : 16
     * IsError : false
     * Message : null
     */

    public String ErrCode;
    public String ErrMsg;
    public ReturnEntity Return;
    public String Costtime;
    public boolean IsError;
    public Object Message;
    /**
     * Id : 62158
     * NickName : null
     * Email : 528360256@qq.com
     * Phone : null
     * RegFromType : 2
     * Avatar : /Content/images/touxi.jpg
     */

    public static class ReturnEntity {
        public String Id;
        public String NickName;
        public String Email;
        public Object Phone;
        public int RegFromType;
        public String Avatar;
    }
}
