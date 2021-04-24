package com.xz.keybag.custom;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xz.keybag.R;
import com.xz.keybag.adapter.AppListAdapter;
import com.xz.keybag.base.BaseDialog;
import com.xz.keybag.utils.AppInfoUtils;
import com.xz.utils.SpacesItemDecorationVertical;

/**
 * @author czr
 * @email czr2001@outlook.com
 * @date 2021/4/24
 */
public class AppListDialog extends BaseDialog {
	private RecyclerView itemRecycler;
	private AppListAdapter mAdapter;

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

		mAdapter = new AppListAdapter(mContext);
		itemRecycler = findViewById(R.id.item_recycler);
		itemRecycler.setLayoutManager(new LinearLayoutManager(mContext));
		itemRecycler.addItemDecoration(new SpacesItemDecorationVertical(20));
		itemRecycler.setAdapter(mAdapter);
		mAdapter.refresh(AppInfoUtils.getAllApp(mContext,true));
	}
}
