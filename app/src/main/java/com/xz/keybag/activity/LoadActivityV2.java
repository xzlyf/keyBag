package com.xz.keybag.activity;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.xz.base.BaseActivity;
import com.xz.keybag.R;
import com.xz.keybag.custom.NumberKeyboard;

import butterknife.BindView;

public class LoadActivityV2 extends BaseActivity {

	private int MAX_NUM = 4;

	@BindView(R.id.number_view)
	NumberKeyboard numberView;
	@BindView(R.id.et_pwd)
	TextView etPwd;
	@BindView(R.id.input_layout)
	LinearLayout inputLayout;

	@Override
	public boolean homeAsUpEnabled() {
		return false;
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_load_v2;
	}

	@Override
	public void initData() {

		initView();

	}

	private void initView() {
		numberView.setOnCallBack(new NumberKeyboard.CallBack() {
			@Override
			public void clickNum(String num) {
				if (etPwd.getText().toString().trim().length() >= MAX_NUM) {
					return;
				}
				etPwd.append(num);
			}

			@Override
			public void deleteNum() {
				String pwd = etPwd.getText().toString().trim();
				if (pwd.length() == 0)
					return;
				pwd = pwd.substring(0, pwd.length() - 1);
				etPwd.setText(pwd);
			}
		});
	}

}
