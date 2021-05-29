package com.xz.keybag.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.xz.keybag.R;

/**
 * @author czr
 * @email czr2001@outlook.com
 * @date 2021/5/30
 * TODO 集成到MyApp中
 * 带图标的文本框
 */
public class IconTextView extends TextView {
	private Bitmap bitmap;
	private Rect src = new Rect();//原图区域
	private Rect target = new Rect();//目标区域
	private float iconPaddingStart;


	public IconTextView(Context context) {
		this(context, null);
	}

	public IconTextView(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public IconTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.IconTextView);
		int iconSrc = ta.getResourceId(R.styleable.IconTextView_icon_src, 0);
		iconPaddingStart = ta.getDimension(R.styleable.IconTextView_icon_paddingStart, 0);
		ta.recycle();

		if (iconSrc != 0) {
			bitmap = BitmapFactory.decodeResource(getResources(), iconSrc);
		}

	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (bitmap != null) {

			src.left = 0;
			src.top = 0;
			src.right = bitmap.getWidth();
			src.bottom = bitmap.getHeight();
			int textHeight = (int) getTextSize();
			target.left = 0;

			//计算图像复制区域的纵坐标，
			target.top = (int) (((getMeasuredHeight() - getTextSize()) / 2) + 1);

			target.bottom = target.top + textHeight;
			target.right = (int) (textHeight * ((float) bitmap.getWidth() / bitmap.getHeight()));

			//图标padding偏移量
			canvas.translate(iconPaddingStart, 0);
			//绘制
			canvas.drawBitmap(bitmap, src, target, getPaint());
			//向右移动TextView的的距离
			canvas.translate(target.right + 2, 0);
		}

		super.onDraw(canvas);
	}
}
