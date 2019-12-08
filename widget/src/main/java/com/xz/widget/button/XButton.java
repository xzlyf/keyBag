package com.xz.widget.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

import com.xz.xzwidget.R;

public class XButton extends AppCompatButton {
    public XButton(Context context) {
        this(context, null);
    }

    public XButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, R.attr.buttonStyle);

        //获取attr数据
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.XButton);
        int radius = typedArray.getInteger(R.styleable.XButton_buttonRadius, 16);
        int color = typedArray.getColor(R.styleable.XButton_backgroundColor, Color.parseColor("#8FBC8F"));
        boolean isFill = typedArray.getBoolean(R.styleable.XButton_isFill, true);
        int strokeWidth = typedArray.getInteger(R.styleable.XButton_buttonStrokeWidth, 2);
        int textColor = typedArray.getColor(R.styleable.XButton_buttonTextColor, Color.WHITE);
        int which = typedArray.getInt(R.styleable.XButton_whiceCirc, -1);
        boolean isReversal = typedArray.getBoolean(R.styleable.XButton_isReversal,false);

        //释放资源
        typedArray.recycle();

        //如果不等于-1表示用户设置了单边圆角
        if (which == -1) {
            setRoundRectDrawable(radius, color, isFill, strokeWidth);
        } else {
            switch (which) {
                case 0:
                    setTopLeftRoundRectDrawable(isReversal, radius, color, isFill, strokeWidth);
                    break;
                case 1:
                    setTopRightRoundRectDrawable(isReversal, radius, color, isFill, strokeWidth);
                    break;
                case 2:
                    setBottomLeftRoundRectDrawable(isReversal, radius, color, isFill, strokeWidth);
                    break;
                case 3:
                    setBottomRightRoundRectDrawable(isReversal, radius, color, isFill, strokeWidth);
                    break;
                default:
                    setRoundRectDrawable(radius, color, isFill, strokeWidth);
                    break;

            }
        }
        setTextColor(textColor);

    }

    /**
     * 设置四个圆角
     *
     * @param radius      半径
     * @param color       颜色
     * @param isFill      是否填充
     * @param strokeWidth 描边
     * @return
     */
    public void setRoundRectDrawable(int radius, int color, boolean isFill, int strokeWidth) {
        //左上、右上、右下、左下的圆角半径
        float[] radiu = {radius, radius, radius, radius, radius, radius, radius, radius};
        rectDrawable(radiu, color, isFill, strokeWidth);
    }

    /**
     * 设置左上角圆角
     *
     * @param reversal    反转--左上角为执教其余为圆角（即反转）
     * @param radius      半径
     * @param color       颜色
     * @param isFill      是否填充
     * @param strokeWidth 描边
     */
    public void setTopLeftRoundRectDrawable(boolean reversal, int radius, int color, boolean isFill, int strokeWidth) {
        float[] radiu;
        if (reversal) {
            radiu = new float[]{0, 0, radius, radius, radius, radius, radius, radius,};
        } else {
            radiu = new float[]{radius, radius, 0, 0, 0, 0, 0, 0};
        }
        rectDrawable(radiu, color, isFill, strokeWidth);
    }

    /**
     * 设置右上角圆角
     *
     * @param reversal    反转--右上角为执教其余为圆角（即反转）
     * @param radius      半径
     * @param color       颜色
     * @param isFill      是否填充
     * @param strokeWidth 描边
     */
    public void setTopRightRoundRectDrawable(boolean reversal, int radius, int color, boolean isFill, int strokeWidth) {
        float[] radiu;
        if (reversal) {
            radiu = new float[]{radius, radius, 0, 0, radius, radius, radius, radius,};
        } else {
            radiu = new float[]{0, 0, radius, radius, 0, 0, 0, 0};
        }
        rectDrawable(radiu, color, isFill, strokeWidth);
    }

    /**
     * 设置右下角圆角
     *
     * @param reversal    反转--右下角为执教其余为圆角（即反转）
     * @param radius      半径
     * @param color       颜色
     * @param isFill      是否填充
     * @param strokeWidth 描边
     */
    public void setBottomRightRoundRectDrawable(boolean reversal, int radius, int color, boolean isFill, int strokeWidth) {
        float[] radiu;
        if (reversal) {
            radiu = new float[]{radius, radius, radius, radius, 0, 0, radius, radius,};
        } else {
            radiu = new float[]{0, 0, 0, 0, radius, radius, 0, 0};
        }
        rectDrawable(radiu, color, isFill, strokeWidth);
    }

    /**
     * 设置左下角圆角
     *
     * @param reversal    反转--左下角为执教其余为圆角（即反转）
     * @param radius      半径
     * @param color       颜色
     * @param isFill      是否填充
     * @param strokeWidth 描边
     */
    public void setBottomLeftRoundRectDrawable(boolean reversal, int radius, int color, boolean isFill, int strokeWidth) {
        float[] radiu;
        if (reversal) {
            radiu = new float[]{radius, radius, radius, radius, radius, radius, 0, 0,};
        } else {
            radiu = new float[]{0, 0, 0, 0, 0, 0, radius, radius};
        }
        rectDrawable(radiu, color, isFill, strokeWidth);
    }

    /**
     * 绘制圆角Drawable
     *
     * @param radiu
     * @param color
     * @param isFill
     * @param strokeWidth
     */
    private void rectDrawable(float[] radiu, int color, boolean isFill, int strokeWidth) {
        //GradientDrawable是shape的动态实现，可通过动态的获取控件的shape获取实例并进行修改
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadii(radiu);
        drawable.setColor(isFill ? color : Color.WHITE);
        drawable.setStroke(isFill ? 0 : strokeWidth, color);
        setBackgroundDrawable(drawable);
    }

}
