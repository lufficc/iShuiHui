package com.lufficc.ishuhui.model;

import com.lufficc.ishuhui.manager.RetrofitManager;
import com.lufficc.ishuhui.utils.PtrUtil;
import com.lufficc.ishuhui.utils.SecurityUtil;

import org.greenrobot.eventbus.EventBus;

import okhttp3.Headers;
import retrofit2.Callback;

/**
 * Created by lcc_luffy on 2016/1/28.
 */
public class User {
    public static final String COOKIE_KEY = "Cookie";
    private static final String USER_KEY_ID = "USER_KEY_ID";
    private static final String USER_KEY_EMAIL = "USER_KEY_EMAIL";
    private static final String USER_KEY_NICKNAME = "USER_KEY_NICKNAME";
    private static User instance;
    private String Set_Cookie = null;

    public String getId() {
        return id;
    }

    public String getNickName() {
        return NickName;
    }

    public String getEmail() {
        return email;
    }

    private String id;
    private String email;
    private String NickName;

    private User(String set_Cookie) {
        Set_Cookie = set_Cookie;
    }

    public static User getInstance() {
        if (instance == null) {
            synchronized (User.class) {
                if (instance == null) {
                    instance = new User(PtrUtil.getInstance().getString(COOKIE_KEY, null));
                    instance.email = PtrUtil.getInstance().getString(USER_KEY_EMAIL, null);
                    instance.id = PtrUtil.getInstance().getString(USER_KEY_ID, null);
                    instance.NickName = PtrUtil.getInstance().getString(USER_KEY_NICKNAME, null);
                }
            }
        }
        return instance;
    }

    public boolean isLogin() {
        return Set_Cookie != null;
    }

    public static void login(String email, String password, final LoginCallBack loginCallBack) {
        RetrofitManager.api()
                .login(email, SecurityUtil.createMd5(password), String.valueOf(2))
                .enqueue(new Callback<LoginModel>() {
                    @Override
                    public void onResponse(retrofit2.Call<LoginModel> call, retrofit2.Response<LoginModel> response) {
                        LoginModel result = response.body();
                        int ErrCode = -2;
                        try {
                            ErrCode = Integer.parseInt(result.ErrCode);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        if (ErrCode == 0) {
                            Headers headers = response.headers();
                            PtrUtil.getInstance().start()
                                    .put(User.COOKIE_KEY, headers.get("Set-Cookie"))
                                    .put(USER_KEY_EMAIL, result.Return.Email)
                                    .put(USER_KEY_ID, result.Return.Id)
                                    .put(USER_KEY_NICKNAME, result.Return.NickName)
                                    .commit();
                            getInstance().notifyLogin();
                            if (loginCallBack != null)
                                loginCallBack.onSuccess();
                            EventBus.getDefault().post(getInstance());
                        } else {
                            if (loginCallBack != null)
                                loginCallBack.onFailure(result.ErrMsg);
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<LoginModel> call, Throwable t) {
                        if (loginCallBack != null)
                            loginCallBack.onFailure(t.getMessage());
                    }
                });
    }

    public String getCookie() {
        return Set_Cookie;
    }

    private void notifyLogin() {
        instance = null;
    }

    public void logout()
    {
        instance = null;
        this.Set_Cookie = null;
        this.email = null;
        PtrUtil.getInstance()
                .start()
                .remove(COOKIE_KEY)
                .remove(USER_KEY_EMAIL)
                .remove(USER_KEY_ID)
                .remove(USER_KEY_NICKNAME)
                .commit();
        EventBus.getDefault().post(new EventLogout());
    }

    public static class EventLogout
    {

    }
    public interface LoginCallBack {
        void onSuccess();

        void onFailure(String msg);
    }
}
