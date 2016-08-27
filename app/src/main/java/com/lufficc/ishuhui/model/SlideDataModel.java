package com.lufficc.ishuhui.model;

import java.util.List;

/**
 * Created by lcc_luffy on 2016/1/27.
 */
public class SlideDataModel {
    /**
     * Id : 1
     * title : 《海贼王》1-20卷日版封面，高清大图分享
     * Img : http://img02.ishuhui.com/guanggao/app3.jpg
     * Link : http://www.ishuhui.net/CMS/648
     */
    public List<Slide> list;
    public static class Slide {
        public int Id;
        public String title;
        public String Img;
        public String Link;
    }
}
