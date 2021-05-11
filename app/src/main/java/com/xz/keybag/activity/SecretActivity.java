package com.xz.keybag.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;
import com.xz.keybag.constant.Local;
import com.xz.keybag.utils.lock.RSA;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import butterknife.BindView;
import butterknife.OnClick;

public class SecretActivity extends BaseActivity {


	@BindView(R.id.tv_back)
	ImageView tvBack;
	@BindView(R.id.tv_title)
	TextView tvTitle;
	@BindView(R.id.banner)
	ImageView bannerView;
	@BindView(R.id.tv_examine)
	TextView tvExamine;
	@BindView(R.id.tv_change)
	TextView tvChange;
	@BindView(R.id.tv_share)
	TextView tvShare;

	@Override
	public boolean homeAsUpEnabled() {
		return true;
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_secret;
	}

	@Override
	public void initData() {
		if (Local.mAdmin == null) {
			sToast("请先初始化");
			return;
		}
		changeStatusBarTextColor();
		Glide.with(this).asGif().load(R.drawable.animaiton_unlock).into(bannerView);
	}

	@OnClick({R.id.tv_back, R.id.tv_examine, R.id.tv_change, R.id.tv_share})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.tv_back:
				finish();
				break;
			case R.id.tv_examine:
				break;
			case R.id.tv_change:
				startActivity(new Intent(SecretActivity.this, ModifyActivity.class));
				break;
			case R.id.tv_share:
				String secret = Local.mAdmin.getDes();
				if (TextUtils.isEmpty(secret)) {
					sToast("密钥文件已被篡改");
					return;
				}
				//二维码传输协议:keybag_secret=RSA密文
				StringBuilder qrSt = new StringBuilder();
				try {
					qrSt.append("keybag_secret");
					qrSt.append("=");
					qrSt.append(RSA.publicEncrypt(secret, RSA.getPublicKey(Local.publicKey)));
				} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
					e.printStackTrace();
					sToast("密钥文件已被损坏");
					return;
				}
				startActivity(new Intent(SecretActivity.this, QRCodeActivity.class)
						.putExtra("qr_code", qrSt.toString()));
				break;
		}
	}
}
