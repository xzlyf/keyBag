package com.xz.keybag.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;
import com.xz.keybag.constant.Local;
import com.xz.keybag.custom.UnifyEditView;
import com.xz.keybag.sql.DBManager;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginPassActivity extends BaseActivity {

	@BindView(R.id.tv_back)
	ImageView tvBack;
	@BindView(R.id.tv_save)
	TextView tvSave;
	@BindView(R.id.ue_old)
	UnifyEditView ueOld;
	@BindView(R.id.ue_new)
	UnifyEditView ueNew;
	@BindView(R.id.ue_retry)
	UnifyEditView ueRetry;


	private DBManager db;

	@Override
	public boolean homeAsUpEnabled() {
		return true;
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_login_pass;
	}

	@Override
	public void initData() {
		db = DBManager.getInstance(this);

	}


	@OnClick({R.id.tv_back, R.id.tv_save})
	public void onViewClicked(View v) {
		switch (v.getId()) {
			case R.id.tv_back:
				finish();
				break;
			case R.id.tv_save:
				updatePwd();
				break;
		}
	}

	/**
	 * 保存密码
	 */
	private void updatePwd() {
		String old = ueOld.getText().toString().trim();
		String newPwd = ueNew.getText().toString().trim();
		String retryPwd = ueRetry.getText().toString().trim();
		if (TextUtils.isEmpty(old)) {
			sToast("旧的密码不可为空");
			return;
		}
		if (TextUtils.isEmpty(newPwd)) {
			sToast("新的密码不可为空");
			return;
		}
		if (TextUtils.isEmpty(retryPwd)) {
			sToast("重复密码不可为空");
			return;
		}
		if (newPwd.length() < Local.PWD_COUNT) {
			sToast("密码需要满足" + Local.PWD_COUNT + "位");
			return;
		}
		if (!retryPwd.equals(newPwd)) {
			sToast("重复密码和新密码不一致");
			return;
		}
		int state = db.updateLogin(old, retryPwd);
		if (state != 1) {
			sToast("修改失败,可能是旧密码验证失败");
			return;
		}
		sToast("修改成功");
		ueOld.setText("");
		ueNew.setText("");
		ueRetry.setText("");

	}
}
