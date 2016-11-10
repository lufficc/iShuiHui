package com.lufficc.ishuhui.activity.preview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;

import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;

/**
 * Created by lufficc on 2016/11/10.
 */

public class BackgroundCacheStuffer extends SpannedCacheStuffer {
    // 通过扩展SimpleTextCacheStuffer或SpannedCacheStuffer个性化你的弹幕样式
    final Paint paint = new Paint();

    @Override
    public void measure(BaseDanmaku danmaku, TextPaint paint, boolean fromWorkerThread) {
        //  danmaku.padding = 20;  // 在背景绘制模式下增加padding
        super.measure(danmaku, paint, fromWorkerThread);
    }

    private static final int BLACK_COLOR = 0xb2009688;//黑色 普通
    public static int DANMU_PADDING = 8;
    private int DANMU_PADDING_INNER = 7;
    private int DANMU_RADIUS = 15;//圆角半径

    @Override
    public void drawBackground(BaseDanmaku danmaku, Canvas canvas, float left, float top) {
        paint.setAntiAlias(true);
        paint.setColor(BLACK_COLOR);//黑色 普通
        //由于该库并没有提供margin的设置，所以我这边试出这种方法：将danmaku.padding也就是内间距设置大一点，并在这里的RectF中设置绘制弹幕的位置，就可以形成类似margin的效果
        canvas.drawRoundRect(new RectF(left + DANMU_PADDING_INNER - 6, top + DANMU_PADDING_INNER - 6
                        , left + danmaku.paintWidth - DANMU_PADDING_INNER + 6,
                        top + danmaku.paintHeight - DANMU_PADDING_INNER + 6),//+6 主要是底部被截得太厉害了，+6是增加padding的效果
                DANMU_RADIUS, DANMU_RADIUS, paint);
    }
}
