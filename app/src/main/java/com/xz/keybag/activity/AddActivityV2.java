package com.xz.keybag.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;
import com.xz.keybag.custom.AppListDialog;
import com.xz.keybag.custom.UnifyEditView;
import com.xz.keybag.entity.AppInfo;
import com.xz.keybag.utils.AppInfoUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddActivityV2 extends BaseActivity {


	@BindView(R.id.tv_back)
	ImageView tvBack;
	@BindView(R.id.tv_title)
	TextView tvTitle;
	@BindView(R.id.tv_save)
	TextView tvSave;
	@BindView(R.id.ue_project)
	UnifyEditView ueProject;
	@BindView(R.id.ue_account)
	UnifyEditView ueAccount;
	@BindView(R.id.ue_pwd)
	UnifyEditView uePwd;
	@BindView(R.id.ue_remark)
	UnifyEditView ueRemark;

	private AppListDialog appListDialog;

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
		initView();
	}

	private void initView() {
		ueProject.setLabelOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showAppListDialog();
			}
		});
	}

	@OnClick({R.id.tv_back, R.id.tv_save})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.tv_back:
				break;
			case R.id.tv_save:
				break;
		}
	}

	private void showAppListDialog() {
		if (appListDialog == null) {
			appListDialog = new AppListDialog(mContext);
			appListDialog.create();
		}
		appListDialog.show();
	}
}
