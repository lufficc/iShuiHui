package com.lufficc.ishuhui.constants;

/**
 * Created by lcc_luffy on 2016/1/22.
 */
public class API {
    public static final String APP_HTTP = "http://www.ishuhui.net";

    public static final String GET_NEW_BOOK = "http://www.ishuhui.net/ComicBooks/GetLastChapterForBookIds?idJson=[1,2,3]";

    public static final String URL_IMG_CHAPTER = "http://www.ishuhui.net/ReadComicBooksToIso/";


    /**
     * 订阅漫画
     * isSubscribe  true/false
     * bookid       Id
     */

    public static final String SUBSCRIBE = "http://www.ishuhui.net/Subscribe";

    /**
     * 获取用户订阅的漫画
     */
    public static final String GET_SUBSCRIBE_BOOK = "http://www.ishuhui.net/ComicBooks/GetSubscribe";
    /**
     * 获取具体漫画的章节列表
     * id           漫画的Id
     * PageIndex    获取第几页的数据
     */
    public static final String GET_COMIC_BOOK_DATA = "http://www.ishuhui.net/ComicBooks/GetChapterList";

    /**
     * 获取某一分类30条记录
     * ClassifyId   分类标识，0热血，1国产，2同人，3鼠绘
     * Size         每次获取的消息条数，最大值为30
     * PageIndex    获取第几页的数据
     * Title        搜索动漫数据,URLDecoder.decode(Title, "UTF-8")
     */
    public static final String GET_BOOK_BY_PARAM = "http://www.ishuhui.net/ComicBooks/GetAllBook";
    /**
     * 获取幻灯片接口
     */
    public static final String GET_SLIDE_DATA = "http://two.ishuhui.com/imgs.html";
    public static final String URL_USER_LOGIN = "http://www.ishuhui.net/UserCenter/Login";
    public static final String URL_USER_REGISTER = "http://www.ishuhui.net/UserCenter/Regedit";

}
