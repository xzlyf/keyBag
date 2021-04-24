package com.xz.keybag.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.xz.keybag.R;
import com.xz.keybag.base.BaseRecyclerAdapter;
import com.xz.keybag.base.BaseRecyclerViewHolder;
import com.xz.keybag.entity.AppInfo;

/**
 * @author czr
 * @email czr2001@outlook.com
 * @date 2021/4/24
 */
public class AppListAdapter extends BaseRecyclerAdapter<AppInfo> {
	public AppListAdapter(Context context) {
		super(context);
	}

	@Override
	protected void showViewHolder(BaseRecyclerViewHolder holder, int position) {
		AppInfo appInfo = getDatas().get(position);
		ViewHolder viewHolder = (ViewHolder) holder;
		viewHolder.mIcon.setImageDrawable(appInfo.getIcon());
		viewHolder.mName.setText(appInfo.getAppName());
	}

	@Override
	protected BaseRecyclerViewHolder createNewViewHolder(ViewGroup parent, int viewType) {
		return new ViewHolder(mInflater.inflate(R.layout.item_applist, parent, false));
	}

	private static class ViewHolder extends BaseRecyclerViewHolder {
		private ImageView mIcon;
		private TextView mName;

		ViewHolder(@NonNull View itemView) {
			super(itemView);
			mIcon = itemView.findViewById(R.id.img_icon);
			mName = itemView.findViewById(R.id.tv_name);
		}
	}
}
