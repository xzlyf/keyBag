package com.xz.keybag.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;

import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;

public class AddActivityV2 extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_v2);
	}

	@Override
	public boolean homeAsUpEnabled() {
		return true;
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_add_v2;
	}

	@Override
	public void initData() {
	}
}
