package com.xz.keybag.custom;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.orhanobut.logger.Logger;
import com.xz.keybag.R;
import com.xz.keybag.adapter.AppListAdapter;
import com.xz.keybag.base.BaseDialog;
import com.xz.keybag.base.OnItemClickListener;
import com.xz.keybag.entity.AppInfo;
import com.xz.keybag.utils.AppInfoUtils;
import com.xz.utils.SpacesItemDecorationVertical;

import java.util.ArrayList;
import java.util.List;

/**
 * @author czr
 * @email czr2001@outlook.com
 * @date 2021/4/24
 */
public class AppListDialog extends BaseDialog {
	private RecyclerView itemRecycler;
	private AppListAdapter mAdapter;
	private ProgressBar mProgressBar;
	private Handler mHandler;
	private ReadThread readThread;

	public AppListDialog(Context context) {
		super(context);
	}

	@Override
	protected int getLayoutResource() {
		return R.layout.dialog_applist;
	}

	@Override
	protected void initData() {
		initView();
		mHandler = new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(@NonNull Message msg) {
				return true;
			}
		});

		readThread = new ReadThread(mContext);
		readThread.start();
	}

	private void initView() {
		setCancelable(true);
		Window window = getWindow();
		if (window != null) {
			window.setBackgroundDrawableResource(com.xz.xzwidget.R.color.transparent);
			WindowManager.LayoutParams lp = window.getAttributes();
			DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
			lp.height = (int) (dm.heightPixels * 0.5);
			lp.width = (int) (dm.widthPixels * 0.8);
			lp.dimAmount = 0.2f;
			window.setAttributes(lp);
		}

		mProgressBar = findViewById(R.id.bar_total);
		mAdapter = new AppListAdapter(mContext);
		itemRecycler = findViewById(R.id.item_recycler);
		itemRecycler.setLayoutManager(new LinearLayoutManager(mContext));
		itemRecycler.addItemDecoration(new SpacesItemDecorationVertical(20));
		itemRecycler.setAdapter(mAdapter);
		//mAdapter.refresh(AppInfoUtils.getAllApp(mContext, true));

	}


	public void setOnItemClickListener(OnItemClickListener<AppInfo> listener) {
		mAdapter.setOnItemClickListener(listener);
	}

	public void stopThread() {
		if (readThread.isAlive()) {
			readThread.stopThread();
		}
	}

	/**
	 * 使用子线程读取应用列表并显示图标
	 * 防止一下子加载过多图标
	 */
	private class ReadThread extends Thread {
		private Context mContext;
		private boolean isStop = false;

		ReadThread(Context context) {
			mContext = context;
		}

		@Override
		public void run() {
			List<AppInfo> allApp = AppInfoUtils.getAllApp(mContext, true);
			int total = allApp.size();
			for (int i = 0; i < allApp.size(); i++) {
				if (isStop) {
					break;
				}
				int finalI = i;
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						int curr = (int) (((float) finalI / (float) total) * 100);
						mProgressBar.setProgress(curr);
						mAdapter.refreshSingle(allApp.get(finalI));
					}
				});
				SystemClock.sleep(80);
			}
			if (!isStop) {
				//隐藏进度条
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mProgressBar.setProgress(100);
						mProgressBar.setVisibility(View.INVISIBLE);
					}
				});
			}

		}

		void stopThread() {
			isStop = true;
		}
	}

}
