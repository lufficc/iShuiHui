package com.lufficc.ishuhui.manager;

import com.litesuits.orm.LiteOrm;
import com.lufficc.ishuhui.config.App;

/**
 * Created by lufficc on 2016/11/6.
 */

public class Orm {
    private static LiteOrm liteOrm;

    public static LiteOrm getLiteOrm() {
        if (liteOrm == null) {
            synchronized (Orm.class) {
                if (liteOrm == null) {
                    liteOrm = LiteOrm.newSingleInstance(App.getInstance(), "comic.db");
                    liteOrm.setDebugged(true);
                }
            }
        }
        return liteOrm;
    }

}
