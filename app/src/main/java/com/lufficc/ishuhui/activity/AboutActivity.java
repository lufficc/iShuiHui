package com.lufficc.ishuhui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.lufficc.ishuhui.R;

import butterknife.BindView;

public class AboutActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.version)
    TextView versionTv;

    @BindView(R.id.lufficc)
    TextView lufficc;

    @BindView(R.id.github)
    TextView github;
    public static void about(Context context)
    {
        context.startActivity(new Intent(context,AboutActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = "Version: "+info.versionName;
            versionTv.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        github.setOnClickListener(this);
        lufficc.setOnClickListener(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.lufficc:
                openUrl("https://lufficc.com/");
                break;
            case R.id.github:
                openUrl("https://github.com/lufficc/IShuHui");
                break;
        }
    }

    private void openUrl(String url)
    {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
