package com.xz.keybag.activity;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;
import com.xz.keybag.constant.Local;
import com.xz.keybag.utils.AppInfoUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class AboutActivity extends BaseActivity {

	@BindView(R.id.tv_back)
	ImageView tvBack;
	@BindView(R.id.image_launch)
	ImageView imageLaunch;
	@BindView(R.id.tv_version)
	TextView tvVersion;

	@Override
	public boolean homeAsUpEnabled() {
		return true;
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_about;
	}

	@Override
	public void initData() {
		changeStatusBarTextColor();
		//获取app图标
		Drawable appIcon = AppInfoUtils.getAppIcon(mContext, Local.PACKAGE_NAME);
		if (appIcon != null) {
			imageLaunch.setImageDrawable(appIcon);
		}
		//获取版本
		String appVersion = AppInfoUtils.getAppVersion(mContext, Local.PACKAGE_NAME);
		if (appVersion != null) {
			tvVersion.setText(String.format("v%s", appVersion));
		}

		String[] appPermission = AppInfoUtils.getAppPermission(mContext, Local.PACKAGE_NAME);
		for (String s : appPermission) {
			Logger.d("权限：" + s);
		}
	}


	@OnClick(R.id.tv_back)
	public void onViewClicked() {
		finish();
	}
}
