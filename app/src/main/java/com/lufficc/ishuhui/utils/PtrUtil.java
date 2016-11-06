package com.lufficc.ishuhui.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.lufficc.ishuhui.config.App;

import java.util.Map;
import java.util.Set;

/**
 * Created by lcc_luffy on 2016/1/26.
 */
public class PtrUtil {
    private SharedPreferences sharedPreferences;
    private static PtrUtil instance;

    private PtrUtil() {
        sharedPreferences = App.getInstance().getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
    }

    public static PtrUtil getInstance() {
        if (instance == null) {
            synchronized (PtrUtil.class) {
                if (instance == null)
                    instance = new PtrUtil();
            }
        }
        return instance;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public PreferencesEditor start() {
        return new PreferencesEditor(sharedPreferences.edit());
    }

    public String getString(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }

    public int getInt(String key, int defValue) {
        return sharedPreferences.getInt(key, defValue);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return sharedPreferences.getBoolean(key, defValue);
    }

    public long getLong(String key, long defValue) {
        return sharedPreferences.getLong(key, defValue);
    }

    public float getFloat(String key, float defValue) {
        return sharedPreferences.getFloat(key, defValue);
    }

    public Map<String, ?> getAll() {
        return sharedPreferences.getAll();
    }

    public Set<String> getStringSet(String key, Set<String> defValue) {
        return sharedPreferences.getStringSet(key, defValue);
    }

    public class PreferencesEditor {
        private SharedPreferences.Editor editor;

        public PreferencesEditor(SharedPreferences.Editor editor) {
            this.editor = editor;
        }

        public PreferencesEditor put(String key, String value) {
            editor.putString(key, value);
            return this;
        }

        public PreferencesEditor put(String key, boolean value) {
            editor.putBoolean(key, value);
            return this;
        }
        public  PreferencesEditor remove(String key){
            editor.remove(key);
            return this;
        }

        public PreferencesEditor put(String key, int value) {
            editor.putInt(key, value);
            return this;
        }

        public PreferencesEditor put(String key, long value) {
            editor.putLong(key, value);
            return this;
        }

        public PreferencesEditor put(String key, float value) {
            editor.putFloat(key, value);
            return this;
        }

        public PreferencesEditor put(String key, Set<String> value) {
            editor.putStringSet(key, value);
            return this;
        }

        public boolean commit() {
            return editor.commit();
        }
        public void apply(){
            editor.apply();
        }
    }

}
