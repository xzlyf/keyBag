package com.xz.keybag.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.orhanobut.logger.Logger;
import com.xz.keybag.R;
import com.xz.keybag.base.BaseRecyclerAdapter;
import com.xz.keybag.base.BaseRecyclerViewHolder;
import com.xz.keybag.entity.Category;

/**
 * @author czr
 * @email czr2001@outlook.com
 * @date 2021/4/24
 */
public class CategoryAdapter extends BaseRecyclerAdapter<Category> {
	private View mSelectView;
	private boolean isOne = true;

	public CategoryAdapter(Context context) {
		super(context);
	}

	@Override
	protected void showViewHolder(BaseRecyclerViewHolder holder, int position) {
		ViewHolder viewHolder = (ViewHolder) holder;
		viewHolder.mName.setText(mList.get(position).getName());
	}

	@Override
	protected BaseRecyclerViewHolder createNewViewHolder(ViewGroup parent, int viewType) {
		return new ViewHolder(mInflater.inflate(R.layout.item_category, parent, false));
	}

	private class ViewHolder extends BaseRecyclerViewHolder {
		private TextView mName;

		ViewHolder(@NonNull View itemView) {
			super(itemView);
			mName = itemView.findViewById(R.id.tv_key);
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mSelectView != null) {
						mSelectView.setEnabled(true);
					}
					mSelectView = v;
					mSelectView.setEnabled(false);
					if (mOnItemClickListener != null) {
						mOnItemClickListener.onItemClick(v, getLayoutPosition(), mList.get(getLayoutPosition()));
					}
				}
			});
			//默认选中第一个
			if (isOne) {
				mSelectView = mName;
				mSelectView.setEnabled(false);
				if (mOnItemClickListener != null) {
					mOnItemClickListener.onItemClick(mName, 0, mList.get(0));
				}
				isOne = false;
			}
		}
	}
}
