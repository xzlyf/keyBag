package com.xz.keybag.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.xz.keybag.constant.Local;

/**
 * @author czr
 * @email czr2001@outlook.com
 * @date 2021/6/10
 */
public class MainActivity extends Activity {
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startActivity(new Intent(MainActivity.this, LoginActivity.class)
				.putExtra("mode", Local.START_MODE_LOGIN_MODE));
		finish();
	}
}
