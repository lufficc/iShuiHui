package com.lufficc.ishuhui.model;

/**
 * Created by lufficc on 2016/8/26.
 */

public class FirLatestModel {

    /**
     * name : fir.im
     * version : 1.0
     * changelog : 更新日志
     * versionShort : 1.0.5
     * build : 6
     * installUrl : http://download.fir.im/v2/app/install/xxxxxxxxxxxxxxxxxxxx?download_token=xxxxxxxxxxxxxxxxxxxxxxxxxxxx
     * install_url : http://download.fir.im/v2/app/install/xxxxxxxxxxxxxxxx?download_token=xxxxxxxxxxxxxxxxxxxxxxxxxxxx
     * update_url : http://fir.im/fir
     * binary : {"fsize":6446245}
     */

    public String name;
    public String version;
    public String changelog;
    public String versionShort;
    public int build;
    public String installUrl;
    public String install_url;
    public String update_url;
    /**
     * fsize : 6446245
     */

    public Binary binary;

    public static class Binary {
        public int fsize;
    }

    @Override
    public String toString() {
        return "FirLatestModel{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", changelog='" + changelog + '\'' +
                ", versionShort='" + versionShort + '\'' +
                ", build='" + build + '\'' +
                ", installUrl='" + installUrl + '\'' +
                ", install_url='" + install_url + '\'' +
                ", update_url='" + update_url + '\'' +
                ", binary=" + binary.fsize +
                '}';
    }
}
