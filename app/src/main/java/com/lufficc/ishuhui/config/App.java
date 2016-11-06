package com.lufficc.ishuhui.config;

import android.app.Application;
import android.util.TypedValue;

import com.lufficc.ishuhui.R;
import com.orm.SugarContext;


/**
 * Created by lcc_luffy on 2016/1/25.
 */
public class App extends Application {
    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        SugarContext.init(this);
        instance = this;
    }

    public static App getInstance() {
        return instance;
    }

    public int getColorPrimary() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        SugarContext.terminate();
    }
}
