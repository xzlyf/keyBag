package com.xz.keybag.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.xz.keybag.sql.DBManager;
import com.xz.utils.SpacesItemDecorationHorizontal;
import com.xz.utils.SpacesItemDecorationVertical;
import com.xz.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class HomeActivity extends BaseActivity {


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
	SearchView etSearch;
	@BindView(R.id.switch_mode)
	Switch modeSwitch;
	@BindView(R.id.category_view)
	RecyclerView categoryRecycler;
	@BindView(R.id.tv_login_date)
	TextView tvLoginTime;
	@BindView(R.id.tv_slogan)
	TextView tvSlogan;


	private DBManager db;
	private KeyAdapter keyAdapter;
	private CategoryAdapter categoryAdapter;
	private List<Project> mListProject;
	private List<Category> mListCategory;
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
		return R.layout.activity_home;
	}

	@Override
	public void initData() {
		db = DBManager.getInstance(this);
		initState();
		initView();
		initRecycler();
	}

	private void initState() {
		long loginTime;
		try {
			loginTime = Long.parseLong(Local.mAdmin.getLastLoginTime());
			tvLoginTime.setText(String.format("上次登录：\n%s", TimeUtil.getSimMilliDate("yyyy年MM月dd日 HH:mm:ss", loginTime)));
		} catch (NumberFormatException e) {
			tvLoginTime.setText("上次登录：异常");
		}
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
		etSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				keyAdapter.getFilter().filter(newText);
				return true;
			}
		});
	}

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
		refreshCategory();
		updateSlogan();
	}

	private void initRecycler() {
		mListProject = new ArrayList<>();
		keyAdapter = new KeyAdapter(mContext);
		keyAdapter.setHandler(handler);
		keyRecycler.setLayoutManager(new LinearLayoutManager(mContext));
		keyRecycler.addItemDecoration(new SpacesItemDecorationVertical(20));
		keyRecycler.setAdapter(keyAdapter);
		keyAdapter.setOnItemClickListener(new OnItemClickListener<Project>() {
			@Override
			public void onItemClick(View view, int position, Project model) {
				startActivity(new Intent(HomeActivity.this, DetailActivity.class).putExtra("model", model));
			}

			@Override
			public void onItemLongClick(View view, int position, Project model) {

			}
		});
		keyAdapter.setAdapterCallBack(new KeyAdapter.AdapterCallback() {
			@Override
			public void closeMenu() {
				//删除后自动关闭菜单，否则会移动到下一个
				keyRecycler.closeMenu();
			}

			@Override
			public void openMenu() {
				keyRecycler.openMenu();
			}
		});

		//分类标签
		categoryAdapter = new CategoryAdapter(mContext);
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
		categoryRecycler.setLayoutManager(linearLayoutManager);
		categoryRecycler.addItemDecoration(new SpacesItemDecorationHorizontal(20));
		categoryRecycler.setAdapter(categoryAdapter);
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

	private void refreshCategory() {
		mListCategory = db.queryCategory();
		mListCategory.add(0, new Category(CATEGORY_ALL, "1"));
		categoryAdapter.superRefresh(mListCategory);
	}

	private void updateSlogan() {
		String slogan = PreferencesUtilV2.getString(Local.SHARD_SLOGAN, Local.DEFAULT_SLOGAN);
		tvSlogan.setText(slogan);
	}

	@OnClick({R.id.tv_menu, R.id.tv_add, R.id.tv_secret, R.id.tv_move, R.id.tv_category
			, R.id.tv_about, R.id.tv_random, R.id.tv_tool, R.id.tv_share})
	public void onViewClick(View v) {

		switch (v.getId()) {
			case R.id.tv_menu:
				drawerLayout.openDrawer(GravityCompat.START);
				return;
			case R.id.tv_add:
				startActivity(new Intent(HomeActivity.this, AddActivity.class));
				break;
			case R.id.tv_secret:
				startActivity(new Intent(HomeActivity.this, LoginActivity.class)
						.putExtra("mode", Local.START_MODE_SECRET_SETTING));
				break;
			case R.id.tv_move:
				startActivity(new Intent(HomeActivity.this, BackupActivity.class));
				break;
			case R.id.tv_category:
				startActivity(new Intent(HomeActivity.this, CategoryManagerActivity.class));
				break;
			case R.id.tv_about:
				startActivity(new Intent(HomeActivity.this, AboutActivity.class));
				break;
			case R.id.tv_random:
				startActivity(new Intent(HomeActivity.this, RandomActivity.class));
				break;
			case R.id.tv_tool:
				startActivity(new Intent(HomeActivity.this, ToolActivity.class));
				break;
			case R.id.tv_share:
				Intent share_intent = new Intent();
				share_intent.setAction(Intent.ACTION_SEND);
				share_intent.setType("text/plain");
				share_intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
				share_intent.putExtra(Intent.EXTRA_TEXT, "[钥匙包]是一款省心安全简洁的密码记录App,拥有有好的使用界面和一些小工具。欢迎关注[" + Local.WeChat + "]订阅号获取最新版本下载。");
				share_intent = Intent.createChooser(share_intent, "分享");
				startActivity(share_intent);
				break;
		}
		drawerLayout.closeDrawer(GravityCompat.START);

	}


	private long backOccTime;

	@Override
	public void onBackPressed() {
		if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
			drawerLayout.closeDrawer(GravityCompat.START);
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
			mListProject = db.queryProject();
			//反转列表
			Collections.reverse(mListProject);

			//刷新主页
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					//刷新列表
					keyAdapter.superRefresh(mListProject);
				}
			});
		}

	}

}
