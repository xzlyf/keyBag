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
import com.xz.keybag.custom.AppListDialog;
import com.xz.keybag.custom.UnifyEditView;
import com.xz.keybag.entity.AppInfo;
import com.xz.keybag.entity.Category;
import com.xz.keybag.entity.Datum;
import com.xz.keybag.sql.cipher.DBManager;
import com.xz.utils.SpacesItemDecorationHorizontal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class AddActivity extends BaseActivity {


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
	@BindView(R.id.category_view)
	RecyclerView categoryView;

	private AppListDialog appListDialog;
	private CategoryAdapter categoryAdapter;
	private String mCategorySt;
	private DBManager db;

	@Override
	public boolean homeAsUpEnabled() {
		return true;
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_add;
	}

	@Override
	public void initData() {
		db = DBManager.getInstance(mContext);
		initView();
		initCategory();
	}

	private void initView() {
		ueProject.setLabelOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showAppListDialog();
			}
		});


	}

	/**
	 * 加载分类标签
	 */
	private void initCategory() {
		//todo  sql读取分类标签

		List<Category> list = new ArrayList<>();
		list.add(new Category("App", "a219dha8h12jhjkh1"));
		list.add(new Category("网站", "1233445ckjwu34ng"));
		list.add(new Category("邮箱", "1sc521f6asdf4489da"));

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


	@OnClick({R.id.tv_back, R.id.tv_save})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.tv_back:
				finish();
				break;
			case R.id.tv_save:
				saveAndExit();
				break;
		}
	}

	private void showAppListDialog() {
		if (appListDialog == null) {
			appListDialog = new AppListDialog(mContext);
			appListDialog.create();
			appListDialog.setOnItemClickListener(new OnItemClickListener<AppInfo>() {
				@Override
				public void onItemClick(View view, int position, AppInfo model) {
					appListDialog.dismiss();
					ueProject.setText(model.getAppName());
				}

				@Override
				public void onItemLongClick(View view, int position, AppInfo model) {

				}
			});
		}
		appListDialog.show();
	}

	/**
	 * 保存并退出
	 */
	private void saveAndExit() {
		Datum datum = new Datum();
		datum.setProject(ueProject.getText().toString().trim());
		datum.setAccount(ueAccount.getText().toString().trim());
		datum.setPassword(uePwd.getText().toString().trim());
		datum.setRemark(ueRemark.getText().toString().trim());
		datum.setCategory(mCategorySt);
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
		if (TextUtils.isEmpty(datum.getAccount())) {
			sToast("请输入密码");
			return;
		}
		db.insertProject(datum);
		sToast("已保存");
		finish();
	}


	@Override
	protected void onDestroy() {
		if (appListDialog != null) {
			appListDialog.stopThread();
		}
		super.onDestroy();
	}

}
