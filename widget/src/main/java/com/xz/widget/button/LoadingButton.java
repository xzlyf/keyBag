package com.xz.widget.button;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;

public class LoadingButton extends XButton {
    private int mWidth = 0;
    private int mHeight = 0;
    private Paint paint;
    private Rect rect;
    private boolean isLoading = false;

    public LoadingButton(Context context) {
        this(context, null);
    }

    public LoadingButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取控件宽高
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mWidth = getWidth();
                mHeight = getHeight();
            }
        });

        //GradientDrawable drawable = new GradientDrawable();

        rect = new Rect(0, 0, 0, 0);
        paint = new Paint();
        paint.setColor(Color.parseColor("#90EE90"));
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(3);


    }

    /**
     * 更新数值
     * 接收0-100更新数值
     *
     * @param value
     */
    public void updateValue(int value) {
        if (value < 0 || value > 100) {
            return;
        }
        //float f = (float) value / 100;
        //int i = (int) (mWidth * f);
        rect.union(0, 0, (int) (mWidth * ((float) value / 100)), mHeight);
        postInvalidate();
    }

    /**
     * 设置加载中进度条的颜色
     *
     * @param color
     */
    public void setOverColor(int color) {
        paint.setColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(rect, paint);
        super.onDraw(canvas);

    }
}
