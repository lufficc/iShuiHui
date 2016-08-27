package com.lufficc.ishuhui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;

import com.lufficc.ishuhui.R;
import com.lufficc.ishuhui.model.RegisterModel;
import com.lufficc.ishuhui.model.User;
import com.lufficc.ishuhui.manager.RetrofitManager;
import com.lufficc.ishuhui.utils.SecurityUtil;

import java.lang.reflect.Method;

import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import shem.com.materiallogin.MaterialLoginView;
import shem.com.materiallogin.MaterialLoginViewListener;

public class LoginActivity extends BaseActivity implements MaterialLoginViewListener {

    @BindView(R.id.login)
    MaterialLoginView materialLoginView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.login));
        materialLoginView.setListener(this);
    }

    public static void login(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void onRegister(TextInputLayout registerUser, TextInputLayout registerPass, TextInputLayout registerPassRep) {
        String email = registerUser.getEditText().getText().toString();
        String password = registerPass.getEditText().getText().toString();
        String password_confirm = registerPassRep.getEditText().getText().toString();
        if (TextUtils.isEmpty(email)) {
            registerUser.setError(getString(R.string.error_field_required));
            registerUser.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password) || !equals(password, password_confirm)) {
            registerPass.setError(getString(R.string.error_password));
            registerPass.requestFocus();
            return;
        }
        register(email,password);

    }

    public void register(String email, String password) {
        RetrofitManager.api()
                .register(email, SecurityUtil.createMd5(password), 2)
                .enqueue(new Callback<RegisterModel>() {
                    @Override
                    public void onResponse(Call<RegisterModel> call, Response<RegisterModel> response) {
                        if (response.isSuccessful()) {
                            if (!response.body().IsError) {
                                toast("注册成功");
                                try {
                                    Method method = materialLoginView.getClass().getDeclaredMethod("animateLogin");
                                    method.setAccessible(true);
                                    method.invoke(materialLoginView);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                toast("注册失败," + response.body().ErrMsg);
                            }
                        } else {
                            toast("注册失败," + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<RegisterModel> call, Throwable t) {
                        toast("注册失败," + t.getMessage());
                    }
                });
    }

    private boolean equals(Object a, Object b) {
        return (a == null) ? (b == null) : a.equals(b);
    }

    @Override
    public void onLogin(TextInputLayout loginUser, TextInputLayout loginPass) {
        String email = loginUser.getEditText().getText().toString();
        String password = loginPass.getEditText().getText().toString();
        if (TextUtils.isEmpty(email)) {
            loginUser.setError(getString(R.string.error_field_required));
            loginUser.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            loginPass.setError(getString(R.string.error_field_required));
            loginPass.requestFocus();
            return;
        }
        login(email, password);
    }

    private void login(String email, String password) {
        User.login(email, password, new User.LoginCallBack() {
            @Override
            public void onSuccess() {
                toast("登录成功");
                finish();
            }

            @Override
            public void onFailure(String msg) {
                toast(msg);
            }
        });
    }
}
