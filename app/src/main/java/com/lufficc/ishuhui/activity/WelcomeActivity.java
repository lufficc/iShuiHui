package com.lufficc.ishuhui.activity;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ViewPropertyAnimatorCompatSet;
import android.view.Window;
import android.widget.TextView;

import com.lufficc.ishuhui.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class WelcomeActivity extends AppCompatActivity {

    final long ANIMATION_DURATION = 700;
    final long SIGNED_IN_DELAY = 2500;

    @BindView(R.id.text_view_app_name)
    TextView textViewAppName;
    @BindView(R.id.text_welcome)
    TextView textViewSlogan;
    private Unbinder unbinder;
    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        unbinder = ButterKnife.bind(this);
        mHandler = new Handler();

        // Animate UI in
        animateTextViews();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                openMainActivity();
            }
        }, SIGNED_IN_DELAY);
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        ColorDrawable gradientDrawable = new ColorDrawable();
        gradientDrawable.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
        window.setBackgroundDrawable(gradientDrawable);
        window.setFormat(PixelFormat.RGBA_8888);
    }
    // Animations

    private void animateTextViews() {
        final int TRANSLATE_Y = 100;
        textViewAppName.setAlpha(0f);
        textViewAppName.setTranslationY(TRANSLATE_Y);
        textViewSlogan.setAlpha(0f);
        ViewPropertyAnimatorCompat appNameAnimator = ViewCompat.animate(textViewAppName)
                .alpha(1)
                .setDuration(ANIMATION_DURATION)
                .translationYBy(-TRANSLATE_Y);
        ViewPropertyAnimatorCompat appSloganAnimator = ViewCompat.animate(textViewSlogan)
                .alpha(1)
                .setDuration(ANIMATION_DURATION);
        ViewPropertyAnimatorCompatSet animatorSet = new ViewPropertyAnimatorCompatSet();
        animatorSet.playSequentially(appNameAnimator, appSloganAnimator);
    }
}
