package com.lufficc.ishuhui.model;

import java.util.List;

/**
 * Created by lcc_luffy on 2016/2/13.
 */
public class PictureModel {

    /**
     * error : false
     * results : [{"who":"张涵宇","publishedAt":"2016-02-04T07:14:01.791Z","desc":"2.4","type":"福利","url":"http://ww4.sinaimg.cn/large/7a8aed7bgw1f0k67eluxej20fr0m8whw.jpg","used":true,"objectId":"56af61d8a633bd0257d289ce","createdAt":"2016-02-01T13:47:04.494Z","updatedAt":"2016-02-04T07:14:02.520Z"},{"who":"张涵宇","publishedAt":"2016-02-03T04:32:45.907Z","desc":"2.3","type":"福利","url":"http://ww2.sinaimg.cn/large/7a8aed7bgw1f0k6706308j20vg18gqfl.jpg","used":true,"objectId":"56af61c6c24aa800547f2a51","createdAt":"2016-02-01T13:46:46.600Z","updatedAt":"2016-02-03T04:32:47.023Z"},{"who":"张涵宇","publishedAt":"2016-02-02T03:57:06.977Z","desc":"2.2","type":"福利","url":"http://ww1.sinaimg.cn/large/7a8aed7bgw1f0k66sk2qbj20rs130wqf.jpg","used":true,"objectId":"56af61b28ac2470053a4e8a4","createdAt":"2016-02-01T13:46:26.071Z","updatedAt":"2016-02-02T03:57:07.794Z"},{"who":"张涵宇","publishedAt":"2016-02-01T03:49:41.430Z","desc":"2.1","type":"福利","url":"http://ww1.sinaimg.cn/large/7a8aed7bgw1f0ixu5rmtcj20hs0qojv5.jpg","used":true,"objectId":"56adfa08a341310052d5ac2e","createdAt":"2016-01-31T12:11:52.570Z","updatedAt":"2016-02-01T03:49:42.111Z"},{"who":"张涵宇","publishedAt":"2016-01-29T04:18:12.684Z","desc":"1.29","type":"福利","url":"http://ww4.sinaimg.cn/large/7a8aed7bjw1f0f9fkzu78j20f00qo0xl.jpg","used":true,"objectId":"56a9c84f816dfa0059566791","createdAt":"2016-01-28T07:50:39.415Z","updatedAt":"2016-01-29T04:18:14.968Z"},{"who":"张涵宇","publishedAt":"2016-01-28T04:44:09.773Z","desc":"1.28","type":"福利","url":"http://ww3.sinaimg.cn/large/7a8aed7bjw1f0e4suv1tgj20hs0qo0w5.jpg","used":true,"objectId":"56a87eda816dfa00594ca59a","createdAt":"2016-01-27T08:24:58.116Z","updatedAt":"2016-01-28T04:44:10.341Z"},{"who":"张涵宇","publishedAt":"2016-01-27T05:14:00.966Z","desc":"1.27","type":"福利","url":"http://ww2.sinaimg.cn/large/7a8aed7bjw1f0cw7swd9tj20hy0qogoo.jpg","used":true,"objectId":"56a715498ac2470055233aa0","createdAt":"2016-01-26T06:42:17.769Z","updatedAt":"2016-01-27T05:14:01.666Z"},{"who":"张涵宇","publishedAt":"2016-01-26T04:02:34.316Z","desc":"1.26","type":"福利","url":"http://ww2.sinaimg.cn/large/7a8aed7bjw1f0buzmnacoj20f00liwi2.jpg","used":true,"objectId":"56a5e769816dfa005aa27c38","createdAt":"2016-01-25T09:14:17.609Z","updatedAt":"2016-01-26T04:02:34.897Z"},{"who":"张涵宇","publishedAt":"2016-01-25T06:59:09.001Z","desc":"1.25","type":"福利","url":"http://ww1.sinaimg.cn/large/7a8aed7bjw1f0bifjrh39j20v018gwtj.jpg","used":true,"objectId":"56a581922e958a00517a093c","createdAt":"2016-01-25T01:59:46.062Z","updatedAt":"2016-01-25T06:59:09.566Z"},{"who":"张涵宇","publishedAt":"2016-01-22T05:14:47.832Z","desc":"1.22","type":"福利","url":"http://ww4.sinaimg.cn/large/7a8aed7bjw1f082c0b6zyj20f00f0gnr.jpg","used":true,"objectId":"56a1933ea34131005273e41f","createdAt":"2016-01-22T02:26:06.396Z","updatedAt":"2016-01-22T05:14:49.253Z"}]
     */

    public boolean error;
    /**
     * who : 张涵宇
     * publishedAt : 2016-02-04T07:14:01.791Z
     * desc : 2.4
     * type : 福利
     * url : http://ww4.sinaimg.cn/large/7a8aed7bgw1f0k67eluxej20fr0m8whw.jpg
     * used : true
     * objectId : 56af61d8a633bd0257d289ce
     * createdAt : 2016-02-01T13:47:04.494Z
     * updatedAt : 2016-02-04T07:14:02.520Z
     */

    public List<ResultsEntity> results;

    public static class ResultsEntity {
        public String who;
        public String publishedAt;
        public String desc;
        public String type;
        public String url;
        public boolean used;
        public String objectId;
        public String createdAt;
        public String updatedAt;
    }
}
