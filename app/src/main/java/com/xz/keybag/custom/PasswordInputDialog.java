package com.xz.keybag.custom;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.xz.keybag.R;
import com.xz.keybag.base.BaseDialog;
import com.xz.keybag.constant.Local;
import com.xz.utils.KeyBoardUtil;

public class PasswordInputDialog extends BaseDialog {
	private PassDialogListener mOnClickListener;
	private EditText mEtInput;
	private SlidingVerification mVerifyProgress;
	private TextView mTvTop;
	private int MAX_NUM = 4;
	private TextView mTitle;
	private TextView mContent;
	private TextView mCancel;
	private PassDialogListener mCancelListener;

	public PasswordInputDialog(Context context) {
		super(context);
	}

	@Override
	protected int getLayoutResource() {
		return R.layout.dialog_pasword_input;
	}

	@Override
	protected void initData() {
		initView();
	}

	private void initView() {
		mEtInput = findViewById(R.id.et_input);
		mTvTop = findViewById(R.id.tv_top);
		mTitle = findViewById(R.id.tv_title);
		mContent = findViewById(R.id.tv_content);
		mCancel = findViewById(R.id.tv_cancel);
		mVerifyProgress = findViewById(R.id.verify_progress);
		mVerifyProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

				if (seekBar.getProgress() >= seekBar.getMax()) {
					if (TextUtils.isEmpty(mEtInput.getText().toString().trim())) {
						seekBar.setThumb(mContext.getResources().getDrawable(R.mipmap.seekbar_thumb));
						seekBar.setThumbOffset(0);
						seekBar.setProgress(0);
						Toast.makeText(mContext, "密码不能为空", Toast.LENGTH_SHORT).show();
						return;
					}
					if (mEtInput.getText().toString().trim().length() < Local.PWD_COUNT) {
						seekBar.setThumb(mContext.getResources().getDrawable(R.mipmap.seekbar_thumb));
						seekBar.setThumbOffset(0);
						seekBar.setProgress(0);
						Toast.makeText(mContext, "密码需要满足" + Local.PWD_COUNT + "位", Toast.LENGTH_SHORT).show();
						return;
					}
					seekBar.setThumb(mContext.getResources().getDrawable(R.mipmap.check_pass));
					seekBar.setThumbOffset(seekBar.getMax() + 50);
					seekBar.setProgress(seekBar.getMax());
					seekBar.setEnabled(false);
					mTvTop.setVisibility(View.VISIBLE);
					mEtInput.setEnabled(false);
					if (mOnClickListener != null) {
						mOnClickListener.onClick(PasswordInputDialog.this, mEtInput.getText().toString().trim());
					}

				} else {
					seekBar.setProgress(0);
				}
			}
		});

		mEtInput.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == MAX_NUM) {
					mEtInput.clearFocus();
					KeyBoardUtil.hideKeyBoard(mEtInput);
				}
			}
		});

		mCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mCancelListener!=null){
					mCancelListener.onClick(PasswordInputDialog.this,"");
				}
			}
		});

	}

	public void setTitle(String title) {
		if (mTitle != null) {
			mTitle.setText(title);
		}
	}

	public void setContent(String content) {
		if (mContent != null) {
			mContent.setText(content);
		}
	}

	public void cleanStatus() {
		if (mVerifyProgress != null) {
			mVerifyProgress.setThumb(mContext.getResources().getDrawable(R.mipmap.seekbar_thumb));
			mVerifyProgress.setThumbOffset(0);
			mVerifyProgress.setProgress(0);
			mVerifyProgress.setEnabled(true);
			mTvTop.setVisibility(View.GONE);
			mEtInput.setEnabled(true);
		}
	}

	/**
	 * 设置取消按钮，并监听剪辑时间
	 * 不设置不显示
	 *
	 * @param tx       按钮名字
	 * @param listener 监听事件
	 */
	public void setOnCancelLickListener( String tx, PassDialogListener listener) {
		if (mCancel == null) {
			return;
		}
		mCancel.setVisibility(View.VISIBLE);
		mCancel.setFocusable(true);
		mCancel.setClickable(true);
		mCancel.setText(tx);
		mCancelListener = listener;
	}

	public void setOnClickListener(PassDialogListener listener) {
		mOnClickListener = listener;
	}

	public interface PassDialogListener {
		void onClick(PasswordInputDialog dialog, String st);
	}
}
