package com.xz.keybag.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;

import com.xz.keybag.R;
import com.xz.keybag.base.BaseRecyclerAdapter;
import com.xz.keybag.base.BaseRecyclerViewHolder;
import com.xz.keybag.entity.Category;

import java.util.HashMap;
import java.util.List;

/**
 * @author czr
 * @email czr2001@outlook.com
 * @date 2021/4/24
 */
public class CategoryEditAdapter extends BaseRecyclerAdapter<Category> {
	private HashMap<Integer, Boolean> isCheckedMap;// 用来记录是否被选中

	public CategoryEditAdapter(Context context, List<Category> list) {
		super(context);
		mList = list;
		isCheckedMap = new HashMap<>();
		for (int i = 0; i < list.size(); i++) {
			isCheckedMap.put(i, false);
		}
	}

	public HashMap<Integer, Boolean> getCheckMap() {
		return isCheckedMap;
	}

	/**
	 * 清除所有item的选中状态
	 */
	public void clearAllCheck() {
		for (int i = 0; i < mList.size(); i++) {
			isCheckedMap.put(i, false);
		}
	}

	@Override
	protected void showViewHolder(BaseRecyclerViewHolder holder, int position) {
		ViewHolder viewHolder = (ViewHolder) holder;
		viewHolder.cb.setText(mList.get(position).getName());
	}

	@Override
	protected BaseRecyclerViewHolder createNewViewHolder(ViewGroup parent, int viewType) {
		return new ViewHolder(mInflater.inflate(R.layout.item_simple_txt_checkbox, parent, false));
	}

	private class ViewHolder extends BaseRecyclerViewHolder {
		private CheckBox cb;

		ViewHolder(@NonNull View itemView) {
			super(itemView);
			cb = itemView.findViewById(R.id.checkbox);
			cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					isCheckedMap.put(getLayoutPosition(), isChecked);
				}
			});
		}
	}
}
