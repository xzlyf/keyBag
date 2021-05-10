package com.xz.keybag.activity;

import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;

public class ModifyActivity extends BaseActivity {

	@Override
	public boolean homeAsUpEnabled() {
		return true;
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_modify;
	}

	@Override
	public void initData() {
		// TODO: 2021/5/10 zxing扫描二维码
	}
}
