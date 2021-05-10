package com.xz.keybag.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;
import com.xz.keybag.constant.Local;
import com.xz.keybag.utils.ZxingUtils;
import com.xz.keybag.utils.lock.RSA;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import butterknife.BindView;

public class QRCodeActivity extends BaseActivity {

	@BindView(R.id.image_qr)
	ImageView imageQr;

	@Override
	public boolean homeAsUpEnabled() {
		return true;
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_qr_code;
	}

	@Override
	public void initData() {
		hideStatusBar();
		hideBottomMenu();
		String qrSt = getIntent().getStringExtra("qr_code");
		if (TextUtils.isEmpty(qrSt)) {
			return;
		}

		try {
			String secret = RSA.privateDecrypt(qrSt, RSA.getPrivateKey(Local.privateKey));
			Logger.w("解密后密钥:" + secret);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
			sToast("密钥文件已被篡改");
			return;
		}

		Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.lanuch_max);
		Bitmap qrCode = ZxingUtils.createImage(qrSt, 400, 400, logo);
		Glide.with(this).asBitmap().load(qrCode).into(imageQr);

	}

}
