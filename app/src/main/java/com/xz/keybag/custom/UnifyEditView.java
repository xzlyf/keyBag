package com.xz.keybag.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.xz.keybag.R;

/**
 * @author czr
 * @email czr2001@outlook.com
 * @date 2021/4/24
 * TODO 集成到MyApp中
 */
public class UnifyEditView extends LinearLayout {

	private TextView mLabel;
	private EditText mInput;

	public UnifyEditView(Context context) {
		this(context, null);
	}

	public UnifyEditView(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public UnifyEditView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context, attrs);
	}

	private void initView(Context context, AttributeSet attrs) {

		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.UnifyEditView);
		//主属性
		int mode = ta.getInt(R.styleable.UnifyEditView_mode, 0);
		//获取标签属性
		AttrsByLabel labelAttrs = new AttrsByLabel();
		labelAttrs.setColor(ta.getColor(R.styleable.UnifyEditView_labelColor, context.getColor(R.color.mainTextColor)));
		labelAttrs.setSize(ta.getDimensionPixelSize(R.styleable.UnifyEditView_labelSize, 16));
		labelAttrs.setText(ta.getString(R.styleable.UnifyEditView_label));
		//获取EditView属性
		AttrsByEdit editAttrs = new AttrsByEdit();
		editAttrs.setHint(ta.getString(R.styleable.UnifyEditView_hint));
		editAttrs.setHintColor(ta.getColor(R.styleable.UnifyEditView_hintColor, Color.parseColor("#757575")));
		editAttrs.setText(ta.getString(R.styleable.UnifyEditView_text));
		editAttrs.setTextColor(ta.getColor(R.styleable.UnifyEditView_textColor, Color.parseColor("#212121")));
		editAttrs.setTextSize(ta.getDimensionPixelSize(R.styleable.UnifyEditView_textSize, 16));
		editAttrs.setLines(ta.getInt(R.styleable.UnifyEditView_lines, 1));
		editAttrs.setMaxLines(ta.getInt(R.styleable.UnifyEditView_maxLines, 1));
		ta.recycle();

		setPadding(20, 50, 20, 50);
		if (mode == 1) {
			//垂直模式
			setOrientation(VERTICAL);
		} else {
			//水平模式
			setOrientation(HORIZONTAL);
		}
		addView(addTextView(context, labelAttrs));
		addView(addEditView(context, editAttrs, mode));

	}

	private View addTextView(Context context, AttrsByLabel labelAttrs) {
		mLabel = new TextView(context);
		mLabel.setPadding(30, 0, 30, 0);
		mLabel.setText(labelAttrs.getText());
		mLabel.setTextSize(labelAttrs.getSize());
		mLabel.setTextColor(labelAttrs.getColor());
		mLabel.setTextColor(context.getColor(R.color.mainTextColor));
		return mLabel;
	}

	private View addEditView(Context context, AttrsByEdit editAttrs, int mode) {
		LinearLayout.LayoutParams lp;
		mInput = new EditText(context);
		if (mode == 1) {
			//垂直模式
			lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1.0f);
			mInput.setGravity(Gravity.START);
			mInput.setPadding(30, 30, 30, 0);
		} else {
			//水平模式
			lp = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
			mInput.setGravity(Gravity.END);
			mInput.setPadding(30, 0, 30, 0);
		}
		mInput.setLayoutParams(lp);
		mInput.setBackgroundColor(Color.TRANSPARENT);
		mInput.clearFocus();
		mInput.setTextSize(editAttrs.getTextSize());
		mInput.setTextColor(editAttrs.getTextColor());
		mInput.setHint(editAttrs.getHint());
		mInput.setLines(editAttrs.getLines());
		mInput.setMaxLines(editAttrs.getMaxLines());
		return mInput;
	}

	/**
	 * =================公开方法====================
	 */
	public Editable getText() {
		return mInput.getText();
	}

	public void setText(String st) {
		mInput.setText(st);
	}

	public void setLabelOnClickListener(View.OnClickListener listener) {
		mLabel.setClickable(true);
		mLabel.setFocusable(true);
		mLabel.setOnClickListener(listener);
	}

	private static class AttrsByLabel {
		private String text;
		private int size;
		private int color;

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public int getSize() {
			return size;
		}

		public void setSize(int size) {
			this.size = size;
		}

		public int getColor() {
			return color;
		}

		public void setColor(int color) {
			this.color = color;
		}
	}

	private static class AttrsByEdit {
		private String text;
		private String hint;
		private int textSize;
		private int textColor;
		private int hintColor;
		private int lines;
		private int maxLines;

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public String getHint() {
			return hint;
		}

		public void setHint(String hint) {
			this.hint = hint;
		}

		public int getTextSize() {
			return textSize;
		}

		public void setTextSize(int textSize) {
			this.textSize = textSize;
		}

		public int getTextColor() {
			return textColor;
		}

		public void setTextColor(int textColor) {
			this.textColor = textColor;
		}

		public int getHintColor() {
			return hintColor;
		}

		public void setHintColor(int hintColor) {
			this.hintColor = hintColor;
		}

		public int getLines() {
			return lines;
		}

		public void setLines(int lines) {
			this.lines = lines;
		}

		public int getMaxLines() {
			return maxLines;
		}

		public void setMaxLines(int maxLines) {
			this.maxLines = maxLines;
		}
	}
}
