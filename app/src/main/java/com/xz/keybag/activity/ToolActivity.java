package com.xz.keybag.activity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;
import com.xz.keybag.utils.lock.SHA256;
import com.xz.utils.CopyUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class ToolActivity extends BaseActivity {

	@BindView(R.id.et_source)
	EditText etSource;
	@BindView(R.id.tv_number)
	TextView tvNumber;
	@BindView(R.id.tv_pre)
	TextView tvPre;
	@BindView(R.id.spinner)
	Spinner spinner;

	private CopyUtil copyUtil;
	private String[] algorithmArray = {"SHA256", "DES", "RSA"};

	@Override
	public boolean homeAsUpEnabled() {
		return true;
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_tool;
	}

	@Override
	public void initData() {
		changeStatusBarTextColor();
		copyUtil = new CopyUtil(this);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, algorithmArray);
		spinner.setAdapter(adapter);
		etSource.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				tvNumber.setText(String.format("%s/1024", s.toString().length()));
			}
		});
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (position == 1) {

				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}


	@OnClick({R.id.tv_back, R.id.tv_make, R.id.tv_copy, R.id.tv_paste, R.id.tv_copy_clip})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.tv_back:
				finish();
				break;
			case R.id.tv_make:
				doIt();
				break;
			case R.id.tv_copy:
				copyUtil.copyToClicp(etSource.getText().toString());
				Snackbar.make(view, "已复制", Snackbar.LENGTH_SHORT).show();
				break;
			case R.id.tv_paste:
				etSource.setText(copyUtil.getClicp());
				Snackbar.make(view, "已粘贴", Snackbar.LENGTH_SHORT).show();
				break;
			case R.id.tv_copy_clip:
				copyUtil.copyToClicp(tvPre.getText().toString());
				Snackbar.make(view, "已复制", Snackbar.LENGTH_SHORT).show();
				break;
		}
	}

	private void doIt() {
		String st = etSource.getText().toString();
		if (TextUtils.isEmpty(st))
			return;
		tvPre.setText(SHA256.getSHA256(st));
	}
}
