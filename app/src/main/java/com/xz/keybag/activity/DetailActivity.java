package com.xz.keybag.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xz.keybag.R;
import com.xz.keybag.adapter.CategoryAdapter;
import com.xz.keybag.base.BaseActivity;
import com.xz.keybag.base.OnItemClickListener;
import com.xz.keybag.custom.UnifyEditView;
import com.xz.keybag.entity.Category;
import com.xz.keybag.entity.Datum;
import com.xz.keybag.entity.Project;
import com.xz.keybag.sql.cipher.DBManager;
import com.xz.utils.SpacesItemDecorationHorizontal;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class DetailActivity extends BaseActivity {
	@BindView(R.id.name)
	UnifyEditView name;
	@BindView(R.id.user)
	UnifyEditView user;
	@BindView(R.id.pwd)
	UnifyEditView pwd;
	@BindView(R.id.remark)
	UnifyEditView remark;
	@BindView(R.id.update_time)
	TextView updateTime;
	@BindView(R.id.create_time)
	TextView createTime;
	@BindView(R.id.back)
	ImageView back;
	@BindView(R.id.submit)
	TextView submit;
	@BindView(R.id.category_view)
	RecyclerView categoryView;
	private Project project;
	private DBManager db;
	private CategoryAdapter categoryAdapter;
	private String mCategorySt;

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
		initCategory();
		name.setText(project.getDatum().getProject());
		user.setText(project.getDatum().getAccount());
		pwd.setText(project.getDatum().getPassword());
		remark.setText(project.getDatum().getRemark());
		updateTime.setText(project.getUpdateDate());
		createTime.setText(project.getCreateDate());
		if (isNightMode()) {
			back.setColorFilter(getResources().getColor(R.color.icons));
		}
	}


	/**
	 * 加载分类标签
	 */
	private void initCategory() {
		List<Category> list = db.queryCategory();
		categoryAdapter = new CategoryAdapter(mContext);
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
		categoryView.setLayoutManager(linearLayoutManager);
		categoryView.addItemDecoration(new SpacesItemDecorationHorizontal(20));
		categoryView.setAdapter(categoryAdapter);
		categoryAdapter.refresh(list);
		categoryAdapter.setOnItemClickListener(new OnItemClickListener<Category>() {
			@Override
			public void onItemClick(View view, int position, Category model) {
				mCategorySt = model.getName();
			}

			@Override
			public void onItemLongClick(View view, int position, Category model) {

			}
		});
	}


	@OnClick({R.id.back, R.id.submit})
	public void onViewClick(View v) {

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
		datum.setCategory(mCategorySt);
		datum.setRemark(remark.getText().toString().trim());
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


}
