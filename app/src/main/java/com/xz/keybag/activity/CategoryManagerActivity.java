package com.xz.keybag.activity;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xz.keybag.R;
import com.xz.keybag.adapter.CategoryEditAdapter;
import com.xz.keybag.base.BaseActivity;
import com.xz.keybag.custom.UnifyEditView;
import com.xz.keybag.entity.Category;
import com.xz.keybag.sql.DBManager;
import com.xz.keybag.utils.UUIDUtil;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class CategoryManagerActivity extends BaseActivity {
	@BindView(R.id.category_view)
	RecyclerView recycler;
	@BindView(R.id.tv_category)
	UnifyEditView tvCategory;

	private DBManager db;
	private CategoryEditAdapter adapter;
	private List<Category> mList;
	private AlertDialog dialog;

	@Override
	public boolean homeAsUpEnabled() {
		return true;
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_category_manager;
	}

	@Override
	public void initData() {
		changeStatusBarTextColor();
		db = DBManager.getInstance(this);
		initRecycler();
	}


	private void initRecycler() {
		//分类标签
		mList = db.queryCategory();
		adapter = new CategoryEditAdapter(mContext, mList);
		recycler.setLayoutManager(new LinearLayoutManager(mContext));
		recycler.setAdapter(adapter);
	}

	@OnClick({R.id.tv_back, R.id.tv_add, R.id.tv_delete})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.tv_back:
				finish();
				break;
			case R.id.tv_add:
				createCategory();
				break;
			case R.id.tv_delete:
				deleteCategory();
				break;
		}
	}

	private void createCategory() {
		String category = tvCategory.getText().toString().trim();
		if (TextUtils.isEmpty(category)) {
			sToast("请输入标签名");
			return;
		}
		Category c = new Category(category, UUIDUtil.getStrUUID());
		db.insertCategory(c);
		tvCategory.setText("");
		adapter.refreshSingle(c);

	}

	private void deleteCategory() {
		boolean has = false;
		for (HashMap.Entry<Integer, Boolean> entry : adapter.getCheckMap().entrySet()) {
			if (entry.getValue()) {
				has = true;
			}
		}
		if (!has) {
			return;
		}

		if (dialog == null) {
			dialog = new AlertDialog.Builder(mContext)
					.setMessage("是否删除已选的标签\n删除标签不会删除已关联标签的密码")
					.setNegativeButton("否", null)
					.setPositiveButton("是", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							for (HashMap.Entry<Integer, Boolean> entry : adapter.getCheckMap().entrySet()) {
								if (entry.getValue()) {
									db.deleteCategory(mList.get(entry.getKey()).getId());
									mList.remove((int) entry.getKey());
								}
							}
							adapter.clearAllCheck();
							adapter.superRefresh(db.queryCategory());
						}
					})
					.create();
		}
		dialog.show();
	}

}
