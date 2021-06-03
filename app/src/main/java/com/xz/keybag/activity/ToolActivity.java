package com.xz.keybag.activity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;
import com.xz.keybag.custom.UnifyEditView;
import com.xz.keybag.utils.lock.DES;
import com.xz.keybag.utils.lock.RSA;
import com.xz.keybag.utils.lock.SHA256;
import com.xz.utils.CopyUtil;

import java.util.Map;

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
	@BindView(R.id.des_layout)
	LinearLayout desLayout;
	@BindView(R.id.ue_des)
	UnifyEditView ueDes;
	@BindView(R.id.tv_des)
	TextView tvDes;
	@BindView(R.id.rsa_layout)
	LinearLayout rsaLayout;
	@BindView(R.id.ue_rsa_public)
	UnifyEditView ueRsaPublic;
	@BindView(R.id.ue_rsa_private)
	UnifyEditView ueRsaPrivate;
	@BindView(R.id.tv_rsa)
	TextView tvRsa;

	private CopyUtil copyUtil;
	private String[] algorithmArray = {"SHA256", "DES", "RSA"};
	private int type; //加密模式 0-sha256 1-des 2-rsa

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
		nonType();
		spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, algorithmArray));
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
				nonType();
				switch (position) {
					case 0:
						shaType();
						break;
					case 1:
						desType();
						break;
					case 2:
						rsaType();
						break;
					default:
						nonType();
						break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}


	@OnClick({R.id.tv_back, R.id.tv_make, R.id.tv_copy, R.id.tv_paste, R.id.tv_copy_clip
			, R.id.tv_des, R.id.tv_rsa})
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
			case R.id.tv_des:
				ueDes.setText(DES.getKey());
				break;
			case R.id.tv_rsa:
				Map<String, String> keys = RSA.createKeys(1024);
				if (keys != null) {
					ueRsaPublic.setText(keys.get("publicKey"));
					ueRsaPrivate.setText(keys.get("privateKey"));
				}
				break;
		}
	}

	private void doIt() {
		String st = etSource.getText().toString();
		if (TextUtils.isEmpty(st))
			return;
		String tar = null;
		switch (type) {
			case 0:
				//sha256
				tar = SHA256.getSHA256(st);
				break;
			case 1:
				//des
				String secret = ueDes.getText().toString().trim();
				if (TextUtils.isEmpty(secret)) {
					sToast("请先输入一个密钥");
					return;
				}
				try {
					tar = DES.encryptor(st, secret);
				} catch (Exception e) {
					tar = e.getMessage();
				}
				break;
			case 2:
				//rsa
				String publicKey = ueRsaPublic.getText().toString().trim();
				String privateKey = ueRsaPrivate.getText().toString().trim();
				if (TextUtils.isEmpty(publicKey)) {
					sToast("请先输入一个公钥");
					return;
				}
				if (TextUtils.isEmpty(privateKey)) {
					sToast("请先输入一个私钥");
					return;
				}
				try {
					tar = RSA.publicEncrypt(st, RSA.getPublicKey(publicKey));
				} catch (Exception e) {
					tar = e.getMessage();
				}
				break;
		}
		tvPre.setText(tar);
	}

	/**
	 * 不选择任何模式
	 */
	private void nonType() {
		type = -1;
		desLayout.setVisibility(View.GONE);
		rsaLayout.setVisibility(View.GONE);
	}

	/**
	 * sha256模式
	 */
	private void shaType() {
		type = 0;

	}

	/**
	 * des模式
	 */
	private void desType() {
		type = 1;
		desLayout.setVisibility(View.VISIBLE);
		ueDes.setText(DES.getKey());

	}

	/**
	 * rsa模式
	 */
	private void rsaType() {
		type = 2;
		rsaLayout.setVisibility(View.VISIBLE);
		Map<String, String> keys = RSA.createKeys(512);
		if (keys != null) {
			ueRsaPublic.setText(keys.get("publicKey"));
			ueRsaPrivate.setText(keys.get("privateKey"));
		}
	}
}
