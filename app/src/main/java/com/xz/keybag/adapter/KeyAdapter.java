package com.xz.keybag.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;
import com.xz.keybag.R;
import com.xz.keybag.base.BaseRecyclerAdapter;
import com.xz.keybag.base.BaseRecyclerViewHolder;
import com.xz.keybag.constant.Local;
import com.xz.keybag.entity.Datum;
import com.xz.keybag.entity.Project;
import com.xz.keybag.sql.cipher.DBManager;
import com.xz.utils.CopyUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class KeyAdapter extends BaseRecyclerAdapter<Project> {


	private List<Project> filterDatas;
	private CopyUtil copyUtil;
	private DBManager db;
	private AdapterCallback mListener;

	public KeyAdapter(Context context) {
		super(context);
		this.filterDatas = mList;
		copyUtil = new CopyUtil(context);
		db = DBManager.getInstance(context);
	}

	@Override
	public int getItemCount() {
		return filterDatas.size();
	}


	@Override
	protected void showViewHolder(BaseRecyclerViewHolder holder, int position) {
		ViewHolder viewHolder = (ViewHolder) holder;
		Project entity = filterDatas.get(position);
		viewHolder.name.setText(entity.getDatum().getProject());
		viewHolder.userId.setText(String.format("账号：%s", entity.getDatum().getAccount()));
		if (TextUtils.equals(Local.mAdmin.getConfig().getPublicPwd(), Local.CONFIG_PUBLIC_PWD_OPEN)) {
			viewHolder.userPsw.setText("密码：******");
		} else {
			viewHolder.userPsw.setText(String.format("密码：%s", entity.getDatum().getPassword()));
		}
		viewHolder.tvCategory.setText(entity.getDatum().getCategory());


	}

	@Override
	protected BaseRecyclerViewHolder createNewViewHolder(ViewGroup parent, int viewType) {
		return new ViewHolder(mInflater.inflate(R.layout.item_key, parent, false));
	}

	/**
	 * 设置以分类标签为过滤器
	 */
	public void setFilterByCategory(String category) {
		if (category.isEmpty()) {
			//没有过滤的内容，则使用源数据
			filterDatas = mList;
		} else {
			List<Project> filteredList = new ArrayList<>();

			//toLowerCase统一设置为小写，这样就忽略大小写了
			for (int i = 0; i < mList.size(); i++) {
				if (mList.get(i).getDatum().getCategory().contains(category)) {
					filteredList.add(mList.get(i));
				}
			}
			filterDatas = filteredList;
		}
		notifyDataSetChanged();
	}

	/**
	 * 清除过滤器
	 */
	public void clearFilter() {
		filterDatas = mList;
		notifyDataSetChanged();
	}


	/**
	 * 过滤器哦
	 *
	 * @return
	 */
	public Filter getFilter() {
		return new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				String charString = constraint.toString();
				if (charString.isEmpty()) {
					//没有过滤的内容，则使用源数据
					filterDatas = mList;
				} else {
					List<Project> filteredList = new ArrayList<>();

					//toLowerCase统一设置为小写，这样就忽略大小写了
					for (int i = 0; i < mList.size(); i++) {
						if (mList.get(i).getDatum().getProject().toLowerCase().contains(charString.toLowerCase())) {
							filteredList.add(mList.get(i));
						}
					}

					filterDatas = filteredList;
				}

				FilterResults filterResults = new FilterResults();
				filterResults.values = filterDatas;
				return filterResults;
			}

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
				filterDatas = (List<Project>) results.values;
				notifyDataSetChanged();
			}
		};
	}

	public void setAdapterCallBack(AdapterCallback listener) {
		mListener = listener;
	}

	private AlertDialog mAffirmDialog;

	/**
	 * 显示删除前确认对话框
	 */
	private void affirmDialog(int position) {
		if (mAffirmDialog != null) {
			mAffirmDialog.cancel();
		}
		mAffirmDialog = new AlertDialog.Builder(mContext)
				.setMessage("确定删除吗")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						db.deleteProject(filterDatas.get(position).getId());
						Project project = filterDatas.get(position);
						filterDatas.remove(project);
						mList.remove(project);//也要删除源数据中的项目
						notifyDataSetChanged();
						Toast.makeText(mContext, getString(R.string.string_8), Toast.LENGTH_SHORT).show();
						if (mListener != null) {
							mListener.closeMenu();
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (mListener != null) {
							mListener.closeMenu();
						}
						dialog.dismiss();
					}
				})
				.create();
		mAffirmDialog.show();
	}

	class ViewHolder extends BaseRecyclerViewHolder implements View.OnClickListener {
		@BindView(R.id.name)
		TextView name;
		@BindView(R.id.user_id)
		TextView userId;
		@BindView(R.id.user_psw)
		TextView userPsw;
		@BindView(R.id.tv_category)
		TextView tvCategory;

		ViewHolder(@NonNull View itemView) {
			super(itemView);

		}


		@OnClick({R.id.layout_1, R.id.delete, R.id.copy_account, R.id.copy_pwd, R.id.item_menu, R.id.share})
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.layout_1:
					if (mOnItemClickListener != null) {
						mOnItemClickListener.onItemClick(v, getLayoutPosition(), filterDatas.get(getLayoutPosition()));
					}
					break;
				case R.id.copy_account:
					copyUtil.copyToClicp(mList.get(getLayoutPosition()).getDatum().getAccount());
					Snackbar.make(v, getString(R.string.string_6), Snackbar.LENGTH_SHORT).show();
					break;
				case R.id.copy_pwd:
					copyUtil.copyToClicp(mList.get(getLayoutPosition()).getDatum().getPassword());
					Snackbar.make(v, getString(R.string.string_7), Snackbar.LENGTH_SHORT).show();
					break;
				case R.id.delete:
					int position = getLayoutPosition();
					affirmDialog(position);
					break;
				case R.id.item_menu:
					if (mListener != null) {
						mListener.openMenu();
					}
					break;
				case R.id.share:
					Datum datum = mList.get(getLayoutPosition()).getDatum();
					String shareSt = "这里是[钥匙包]密码分享\n" +
							"标题：" + datum.getProject() + "\n" +
							"账号：" + datum.getAccount() + "\n" +
							"密码：" + datum.getPassword() + "\n" +
							"备注：" + datum.getRemark();
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_SEND);
					intent.setType("text/plain");
					intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
					intent.putExtra(Intent.EXTRA_TEXT, shareSt);
					intent = Intent.createChooser(intent, "分享");
					mListener.closeMenu();
					mContext.startActivity(intent);
					//copyUtil.copyToClicp(shareSt);
					//Snackbar.make(v, "已复制", Snackbar.LENGTH_SHORT).show();
					//if (mListener != null) {
					//	mListener.closeMenu();
					//}
					break;
			}

		}
	}


	public interface AdapterCallback {
		void closeMenu();

		void openMenu();
	}

}
