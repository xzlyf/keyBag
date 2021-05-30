package com.xz.keybag.activity;

import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;

public class CategoryManagerActivity extends BaseActivity {


	@Override
	public boolean homeAsUpEnabled() {
		return true;
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_category_manager;
	}

	@Override
	public void initData() {
		changeStatusBarTextColor();

	}
}
