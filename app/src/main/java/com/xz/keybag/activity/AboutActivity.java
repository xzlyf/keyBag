package com.xz.keybag.activity;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

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

		//todo 反馈
		//留言 POST
		//http://cc.ys168.com/f_ht/ajcx/lyd.aspx?cz=lytj&pdgk=1&pdgly=0&pdzd=0&tou=1&yzm=undefined&_dlmc=xzlyf&_dlmm=
		//body : sm留言人,nr内容

	}


	@OnClick(R.id.tv_back)
	public void onViewClicked() {
		finish();
	}
}
