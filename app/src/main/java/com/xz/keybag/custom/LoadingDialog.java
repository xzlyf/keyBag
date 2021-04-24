package com.xz.keybag.custom;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.xz.keybag.R;
import com.xz.keybag.base.BaseDialog;


/**
 * @author czr
 * @email czr2001@outlook.com
 * @date 2021/1/4
 */
public class LoadingDialog extends BaseDialog {
	private boolean canExit;
	private LoadingView loadingView;
	private TextView tipsView;
	private String tips;

	private LoadingDialog(Context context) {
		this(context, 0);
	}

	private LoadingDialog(Context context, int themeResId) {
		super(context, themeResId);
	}

	@Override
	protected int getLayoutResource() {
		return R.layout.dialog_loading;
	}

	@Override
	protected void initData() {
		loadingView = findViewById(R.id.loading_view);
		tipsView = findViewById(R.id.tv_tips);
		setCancelable(canExit);
		setCanceledOnTouchOutside(canExit);
		if (tips != null) {
			tipsView.setText(tips);
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
		//释放动画内存
		loadingView.setVisibility(View.INVISIBLE);
	}


	public static class Builder {
		LoadingDialog dialog;

		public Builder(Context context) {
			dialog = new LoadingDialog(context);

		}

		public Builder canExit(boolean canExit) {
			dialog.canExit = canExit;
			return this;
		}

		public Builder setTips(String tips) {
			dialog.tips = tips;
			return this;
		}

		public LoadingDialog build() {
			dialog.create();
			return dialog;
		}


	}
}
