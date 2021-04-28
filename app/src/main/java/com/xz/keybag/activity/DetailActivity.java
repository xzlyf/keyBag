package com.xz.keybag.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;
import com.xz.keybag.entity.Datum;
import com.xz.keybag.entity.Project;
import com.xz.keybag.sql.cipher.DBManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends BaseActivity implements View.OnFocusChangeListener, View.OnClickListener {
	@BindView(R.id.name)
	EditText name;
	@BindView(R.id.name_edit)
	ImageView nameEdit;
	@BindView(R.id.user)
	EditText user;
	@BindView(R.id.user_edit)
	ImageView userEdit;
	@BindView(R.id.pwd)
	EditText pwd;
	@BindView(R.id.pwd_edit)
	ImageView pwdEdit;
	@BindView(R.id.remark)
	EditText remark;
	@BindView(R.id.remark_edit)
	ImageView remarkEdit;
	@BindView(R.id.end_time)
	TextView endTime;
	@BindView(R.id.back)
	ImageView back;
	@BindView(R.id.submit)
	TextView submit;
	private Project project;
	private DBManager db;

	@Override
	public boolean homeAsUpEnabled() {
		return true;
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_detail;
	}

	@Override
	public void initData() {
		project = (Project) getIntent().getSerializableExtra("model");
		if (project == null) {
			sToast(getString(R.string.string_22));
			return;
		}
		db = DBManager.getInstance(this);
		name.setText(project.getDatum().getProject());
		user.setText(project.getDatum().getAccount());
		pwd.setText(project.getDatum().getPassword());
		remark.setText(project.getDatum().getRemark());
		endTime.setText(project.getUpdateDate());
		name.setOnFocusChangeListener(this);
		user.setOnFocusChangeListener(this);
		pwd.setOnFocusChangeListener(this);
		remark.setOnFocusChangeListener(this);
		back.setOnClickListener(this);
		submit.setOnClickListener(this);
		if (isNightMode()) {
			back.setColorFilter(getResources().getColor(R.color.icons));
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
			case R.id.name:
				showEditorImg(hasFocus, nameEdit);
				break;
			case R.id.user:
				showEditorImg(hasFocus, userEdit);

				break;
			case R.id.pwd:
				showEditorImg(hasFocus, pwdEdit);

				break;
			case R.id.remark:
				showEditorImg(hasFocus, remarkEdit);

				break;
		}
	}

	private void showEditorImg(boolean b, ImageView view) {
		if (b) {
			view.setVisibility(View.INVISIBLE);
		} else {
			view.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ButterKnife.bind(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.back:
				finish();
				break;
			case R.id.submit:
				submit();
				break;
		}
	}

	private void submit() {


		Datum datum = new Datum();
		datum.setProject(name.getText().toString().trim());
		datum.setAccount(user.getText().toString().trim());
		datum.setPassword(pwd.getText().toString().trim());
		datum.setRemark(remark.getText().toString().trim());
		//datum.setCategory(mCategorySt);//todo 加入修改分类
		if (datum.isEmpty()) {
			finish();
			return;
		}
		if (TextUtils.isEmpty(datum.getProject())) {
			sToast("请输入名称");
			return;
		}
		if (TextUtils.isEmpty(datum.getAccount())) {
			sToast("请输入账号");
			return;
		}
		if (TextUtils.isEmpty(datum.getPassword())) {
			sToast("请输入密码");
			return;
		}
		project.setDatum(datum);
		db.updateProject(project.getId(), project);
		sToast("修改成功");

	}

	/**
	 * 检查是否存在空
	 *
	 * @param arg
	 * @return
	 */
	private boolean checkEmpty(String... arg) {
		for (String s : arg) {
			if (s == null || s.equals("")) {
				return false;
			}
		}
		return true;
	}

}
