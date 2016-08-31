package com.lufficc.ishuhui.manager;

import io.realm.Realm;

/**
 * Created by lufficc on 2016/8/30.
 */
public class RealmManager {
    private static Realm realm = Realm.getDefaultInstance();

    public static Realm realm() {
        return realm;
    }

    private RealmManager() {
    }
}
