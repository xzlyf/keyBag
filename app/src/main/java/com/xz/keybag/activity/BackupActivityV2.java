package com.xz.keybag.activity;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;
import com.xz.keybag.sql.cipher.DBManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class BackupActivityV2 extends BaseActivity {


	@BindView(R.id.tv_back)
	ImageView tvBack;
	@BindView(R.id.pie_chart)
	PieChart pieChart;


	public static final int HANDLER_REFRESH_PIE_DATA = 0x123456;
	private DBManager db;

	private Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(@NonNull Message msg) {
			switch (msg.what) {
				case HANDLER_REFRESH_PIE_DATA:
					PieData pieData = (PieData) msg.obj;
					pieChart.setData(pieData);
					pieChart.invalidate();
					break;
			}
			return true;
		}
	});

	@Override
	public boolean homeAsUpEnabled() {
		return true;
	}

	@Override
	public int getLayoutResource() {
		return R.layout.activity_backup_v2;
	}

	@Override
	public void initData() {
		changeStatusBarTextColor();
		db = DBManager.getInstance(this);
		initPieChart();
	}

	/**
	 * 初始化图表
	 */
	private void initPieChart() {
		new PieChartDataRead().start();
	}


	private class PieChartDataRead extends Thread {
		@Override
		public void run() {
			//查询得到项目数据
			Map<String, Integer> map = db.queryProjectState();
			//总条数
			int total = map.size();
			//填装进pie实体
			List<PieEntry> pieList = new ArrayList<>();
			for (Map.Entry<String, Integer> entry : map.entrySet()) {
				pieList.add(new PieEntry(entry.getValue(), entry.getKey() + " " + entry.getValue() + "条"));
			}

			PieDataSet dataSet = new PieDataSet(pieList, "数据总览" + total + "条");
			ArrayList<Integer> colors = new ArrayList<Integer>();
			colors.add(Color.rgb(205, 205, 205));
			colors.add(Color.rgb(114, 188, 223));
			colors.add(Color.rgb(255, 123, 124));
			colors.add(Color.rgb(57, 135, 200));
			colors.add(Color.rgb(57, 135, 20));
			colors.add(Color.rgb(77, 105, 20));
			colors.add(Color.rgb(107, 142, 35));
			colors.add(Color.rgb(175, 238, 238));
			colors.add(Color.rgb(250, 235, 215));
			//数据颜色
			dataSet.setColors(colors);
			//设置饼状图之间的距离
			dataSet.setSliceSpace(3f);
			// 选中态多出的长度
			dataSet.setSelectionShift(10f);
			//设置连接线的颜色
			//dataSet.setValueLineColor(Color.LTGRAY);
			// 连接线在饼状图外面
			//dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);


			PieData pieData = new PieData(dataSet);
			pieData.setDrawValues(true);
			pieData.setValueFormatter(new PercentFormatter());
			pieData.setValueTextSize(12f);
			pieData.setValueFormatter(new PercentFormatter(pieChart));

			// 获取pieCahrt图列
			Legend l = pieChart.getLegend();
			l.setEnabled(true);                    //是否启用图列（true：下面属性才有意义）
			l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
			l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
			l.setOrientation(Legend.LegendOrientation.VERTICAL);
			l.setForm(Legend.LegendForm.DEFAULT); //设置图例的形状
			l.setFormSize(10);                      //设置图例的大小
			l.setFormToTextSpace(10f);              //设置每个图例实体中标签和形状之间的间距
			l.setDrawInside(false);
			l.setWordWrapEnabled(true);              //设置图列换行(注意使用影响性能,仅适用legend位于图表下面)
			l.setXEntrySpace(10f);                  //设置图例实体之间延X轴的间距（setOrientation = HORIZONTAL有效）
			l.setYEntrySpace(8f);                  //设置图例实体之间延Y轴的间距（setOrientation = VERTICAL 有效）
			l.setYOffset(0f);                      //设置比例块Y轴偏移量
			l.setTextSize(12f);                      //设置图例标签文本的大小
			l.setTextColor(Color.parseColor("#ff9933"));//设置图例标签文本的颜色


			Description description = new Description();
			description.setText("");
			pieChart.setDescription(description);
			//内圆大小
			pieChart.setHoleRadius(50f);
			pieChart.setTransparentCircleRadius(50f);
			//显示成百分比
			pieChart.setUsePercentValues(true);
			//饼状图中间的文字
			pieChart.setCenterText("Store");
			//不显示文字
			pieChart.setDrawEntryLabels(false);


			//回到主线程
			Message msg = mHandler.obtainMessage();
			msg.what = HANDLER_REFRESH_PIE_DATA;
			msg.obj = pieData;
			mHandler.sendMessage(msg);
		}
	}

}
