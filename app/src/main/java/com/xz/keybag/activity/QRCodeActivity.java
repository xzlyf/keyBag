package com.xz.keybag.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;
import com.xz.keybag.utils.ZxingUtils;

import butterknife.BindView;

public class QRCodeActivity extends BaseActivity {

	@BindView(R.id.image_qr)
	ImageView imageQr;


	//屏幕亮度
	private float mLight;

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

		mLight = getLight(this);
		Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.lanuch_max);
		Bitmap qrCode = ZxingUtils.createImage(qrSt, 400, 400, logo);
		Glide.with(this).asBitmap().load(qrCode).into(imageQr);

	}

	/**
	 * 二维码报文头判断
	 *
	 * @param qrSt 二维码加密密文
	 */
	public void test(String qrSt) {
		//二维码报文头判断 keybag_secret=RSA密文
		String[] qrArray = qrSt.split("=");
		if (qrArray.length == 0) {
			sToast("非法二维码");
			return;
		}

		if (!qrArray[0].equals("keybag_secret")) {
			sToast("这个二维码没有我想要的信息(ˉ▽ˉ；)...");
			return;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		//把屏幕亮度提高
		setLight(this, 200f);
	}

	@Override
	protected void onPause() {
		super.onPause();
		setLight(this, mLight);

	}

	/**
	 * 获取当前屏幕亮度
	 */
	private float getLight(Activity activity) {
		WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
		return lp.screenBrightness;
	}

	/**
	 * 设置屏幕亮度
	 */
	private void setLight(Activity context, float brightness) {
		WindowManager.LayoutParams lp = context.getWindow().getAttributes();
		lp.screenBrightness = brightness * (1f / 255f);
		context.getWindow().setAttributes(lp);
	}

}
