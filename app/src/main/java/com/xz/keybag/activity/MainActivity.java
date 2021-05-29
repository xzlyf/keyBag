package com.xz.keybag.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xz.dialog.event.NegativeOnClickListener;
import com.xz.dialog.event.PositiveOnClickListener;
import com.xz.dialog.imitate.AppleInputDialog;
import com.xz.keybag.R;
import com.xz.keybag.adapter.CategoryAdapter;
import com.xz.keybag.adapter.KeyAdapter;
import com.xz.keybag.base.BaseActivity;
import com.xz.keybag.base.OnItemClickListener;
import com.xz.keybag.base.utils.PreferencesUtilV2;
import com.xz.keybag.constant.Local;
import com.xz.keybag.custom.SlideRecyclerView;
import com.xz.keybag.entity.Category;
import com.xz.keybag.entity.Project;
import com.xz.keybag.sql.SqlManager;
import com.xz.keybag.sql.cipher.DBManager;
import com.xz.utils.MD5Util;
import com.xz.utils.SpacesItemDecorationHorizontal;
import com.xz.utils.SpacesItemDecorationVertical;
import com.xz.widget.textview.SearchEditView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {


	@BindView(R.id.key_recycler)
	SlideRecyclerView keyRecycler;
	@BindView(R.id.tv_menu)
	ImageView tvMenu;
	@BindView(R.id.tv_add)
	ImageView tvAdd;
	@BindView(R.id.drawer_layout)
	DrawerLayout drawerLayout;
	@BindView(R.id.tv_title)
	TextView tvTitle;
	@BindView(R.id.et_search)
	SearchEditView etSearch;
	@BindView(R.id.switch_mode)
	Switch modeSwitch;
	@BindView(R.id.category_view)
	RecyclerView categoryRecycler;


	private DBManager db;
	private KeyAdapter keyAdapter;
	private CategoryAdapter categoryAdapter;
	private List<Project> mList;
	private boolean isNight;//日渐模式false 夜间模式true
	private static final String CATEGORY_ALL = "所有";

	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(@NonNull Message msg) {
			return true;
		}
	});


	@Override
	public boolean homeAsUpEnabled() {
		return false;
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_main;
	}

	@Override
	public void initData() {
		db = DBManager.getInstance(this);
		initState();
		initView();
		initRecycler();
	}

	private void initState() {
		isNight = PreferencesUtilV2.getBoolean(Local.SHARD_BOOLEAN_MODE, false);
		modeSwitch.setChecked(isNight);
		if (isNight) {
			if (!isNightMode()) {
				changeMode(true);
			}
		}
		modeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				changeMode(isChecked);
			}
		});


	}

	private void initView() {
		etSearch.addTextChangeListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				keyAdapter.getFilter().filter(s);
			}
		});


	}

	@Override
	protected void changeMode(boolean isNight) {
		super.changeMode(isNight);
		modeSwitch.setChecked(isNight);
		PreferencesUtilV2.putBoolean(Local.SHARD_BOOLEAN_MODE, isNight);
	}


	/**
	 * 每次回到主页重新读取数据库
	 */
	@Override
	protected void onResume() {
		super.onResume();
		new ReadDataCommon().start();
	}

	private void initRecycler() {
		mList = new ArrayList<>();
		keyAdapter = new KeyAdapter(mContext);
		keyAdapter.setHandler(handler);
		keyRecycler.setLayoutManager(new LinearLayoutManager(mContext));
		keyRecycler.addItemDecoration(new SpacesItemDecorationVertical(20));
		keyRecycler.setAdapter(keyAdapter);
		keyAdapter.setOnItemClickListener(new OnItemClickListener<Project>() {
			@Override
			public void onItemClick(View view, int position, Project model) {
				startActivity(new Intent(MainActivity.this, DetailActivity.class).putExtra("model", model));
			}

			@Override
			public void onItemLongClick(View view, int position, Project model) {

			}
		});
		keyAdapter.setOnDeleteClickListener(new com.xz.keybag.custom.XOnClickListener() {
			@Override
			public void onClick(String st, View v) {
				//删除后自动关闭菜单，否则会移动到下一个
				keyRecycler.closeMenu();
			}
		});

		//分类标签
		List<Category> list = db.queryCategory();
		list.add(0, new Category(CATEGORY_ALL, "1"));
		categoryAdapter = new CategoryAdapter(mContext);
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
		categoryRecycler.setLayoutManager(linearLayoutManager);
		categoryRecycler.addItemDecoration(new SpacesItemDecorationHorizontal(20));
		categoryRecycler.setAdapter(categoryAdapter);
		categoryAdapter.refresh(list);
		categoryAdapter.setOnItemClickListener(new OnItemClickListener<Category>() {
			@Override
			public void onItemClick(View view, int position, Category model) {
				if (model.getName().equals(CATEGORY_ALL)) {
					keyAdapter.clearFilter();
				} else {
					keyAdapter.setFilterByCategory(model.getName());
				}
			}

			@Override
			public void onItemLongClick(View view, int position, Category model) {

			}
		});
	}


	@OnClick({R.id.tv_menu, R.id.tv_add, R.id.tv_secret, R.id.tv_move})
	public void onViewClick(View v) {

		switch (v.getId()) {
			case R.id.tv_menu:
				drawerLayout.openDrawer(GravityCompat.START);
				return;
			case R.id.tv_add:
				startActivity(new Intent(MainActivity.this, AddActivity.class));
				break;
			case R.id.tv_secret:
				startActivity(new Intent(MainActivity.this, LoginActivity.class).putExtra("mode", Local.INTENT_EXTRA_LOGIN_MODE));
				break;
			case R.id.tv_move:
				startActivity(new Intent(MainActivity.this, BackupActivity.class));
				break;
		}
		drawerLayout.closeDrawer(GravityCompat.START);

	}


	private long backOccTime;

	@Override
	public void onBackPressed() {
		if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
			drawerLayout.closeDrawer(Gravity.LEFT);
		} else {
			long i = System.currentTimeMillis();
			if (i - backOccTime > 2000) {
				sToast("再次点击退出");
				backOccTime = i;
			} else {
				super.onBackPressed();
			}
		}
	}

	/**
	 * 异步数据读取线程类
	 */
	private class ReadDataCommon extends Thread {
		@Override
		public void run() {
			super.run();
			mList = db.queryProject();
			//反转列表
			Collections.reverse(mList);

			//刷新主页
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					//刷新列表
					keyAdapter.superRefresh(mList);
				}
			});
		}

	}

}
