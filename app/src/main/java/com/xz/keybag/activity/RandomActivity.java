package com.xz.keybag.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;
import com.xz.keybag.constant.Local;
import com.xz.utils.CopyUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;

public class RandomActivity extends BaseActivity {

	@BindView(R.id.tv_back)
	ImageView tvBack;
	@BindView(R.id.cb_1)
	CheckBox cb1;
	@BindView(R.id.cb_2)
	CheckBox cb2;
	@BindView(R.id.cb_3)
	CheckBox cb3;
	@BindView(R.id.cb_4)
	CheckBox cb4;
	@BindView(R.id.tv_make)
	Button tvMake;
	@BindView(R.id.tv_keep)
	Button tvKeep;
	@BindView(R.id.tv_pre)
	TextView tvPre;
	@BindView(R.id.seek_len)
	SeekBar seekBar;
	@BindView(R.id.et_len)
	TextView etLen;
	private String[] upperCase = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
	private String[] lowerCase = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
	private String[] numberCase = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
	private String[] charCase = {"!", "@", "#", "$", "%", "-"};
	//当前选中的字符集
	private List<String> charList;
	private Random mRandom;
	private CopyUtil copyUtil;
	private int mode;//启动模式

	@Override
	public boolean homeAsUpEnabled() {
		return true;
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_random;
	}

	@Override
	public void initData() {
		if (getIntent() != null) {
			mode = getIntent().getIntExtra("mode", 0);
			if (mode == Local.START_MODE_RANDOM) {
				tvKeep.setVisibility(View.VISIBLE);
			}
		}
		changeStatusBarTextColor();
		copyUtil = new CopyUtil(mContext);
		mRandom = new Random();
		charList = new ArrayList<>();

		seekBar.setMax(255);
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (progress <= 1) {
					etLen.setText("1");
					return;
				}
				etLen.setText(String.valueOf(progress));

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});
	}


	@OnClick({R.id.tv_back, R.id.tv_make, R.id.tv_copy, R.id.tv_keep})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.tv_back:
				finish();
				break;
			case R.id.tv_make:
				doIt();
				break;
			case R.id.tv_copy:
				copyUtil.copyToClicp(tvPre.getText().toString().trim());
				Snackbar.make(view, "已复制", Snackbar.LENGTH_SHORT).show();
				break;
			case R.id.tv_keep:
				saveAndKeep();
				break;
		}
	}

	private void doIt() {
		charList.clear();
		if (cb1.isChecked()) {
			Collections.addAll(charList, upperCase);
		}
		if (cb2.isChecked()) {
			Collections.addAll(charList, lowerCase);
		}
		if (cb3.isChecked()) {
			Collections.addAll(charList, numberCase);
		}
		if (cb4.isChecked()) {
			Collections.addAll(charList, charCase);
		}

		if (charList.size() == 0) {
			sToast("请至少选择一个字符集");
			return;
		}
		int len;
		try {
			len = Integer.parseInt(etLen.getText().toString().trim());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			len = 8;
		}
		tvPre.setText(getRandom(len, charList));
	}

	/**
	 * @param len      字符串生成长度
	 * @param charList 随机生成的字符集
	 */
	public String getRandom(int len, @NonNull List<String> charList) {
		int size = charList.size();
		StringBuilder sBuff = new StringBuilder();
		for (int i = 0; i < len; i++) {
			sBuff.append(charList.get(mRandom.nextInt(size)));
		}
		return sBuff.toString();
	}


	private void saveAndKeep() {
		Intent intent = new Intent();
		intent.putExtra(Local.INTENT_EXTRA_RANDOM, tvPre.getText().toString().trim());
		setResult(RESULT_OK, intent);
		finish();
	}
}
