package com.xz.keybag.activity;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;
import com.xz.keybag.constant.Local;
import com.xz.keybag.utils.AppInfoUtils;
import com.xz.utils.CopyUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class AboutActivity extends BaseActivity {

	@BindView(R.id.tv_back)
	ImageView tvBack;
	@BindView(R.id.image_launch)
	ImageView imageLaunch;
	@BindView(R.id.tv_version)
	TextView tvVersion;

	private CopyUtil copyUtil;

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
		copyUtil = new CopyUtil(mContext);
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

	}


	@OnClick(R.id.tv_back)
	public void onViewClicked() {
		finish();
	}


	@OnClick(R.id.tv_jump)
	public void onViewClick(View v) {
		toWeChat();
	}

	public void toWeChat() {
		try {
			copyUtil.copyToClicp(Local.WeChat);
			sToast(Local.WeChat + "已复制到粘贴板");
			Intent intent = new Intent(Intent.ACTION_MAIN);
			ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setComponent(cmp);
			startActivity(intent);
		} catch (Exception e) {
			if (e instanceof ActivityNotFoundException) {
				sToast("未找到微信，请手动前往搜索");
			} else {
				e.printStackTrace();
			}
		}

	}
}
