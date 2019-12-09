package com.xz.keybag.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xz.base.BaseActivity;
import com.xz.keybag.R;
import com.xz.keybag.adapter.KeyAdapter;
import com.xz.keybag.constant.Local;
import com.xz.keybag.entity.KeyEntity;
import com.xz.keybag.sql.SqlManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {


    @BindView(R.id.key_recycler)
    RecyclerView keyRecycler;
    private KeyAdapter keyAdapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };

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
        keyAdapter = new KeyAdapter(mContext);
        keyAdapter.setHandler(handler);
        new ReadDataCommon().start();
    }


    class ReadDataCommon extends Thread {

        @Override
        public void run() {
            super.run();

            Cursor cursor = SqlManager.queryAll(mContext, Local.TABLE_COMMON);
            //如果游标为空则返回false
            if (!cursor.moveToFirst()) {
                //空数据
                return;
            }
            List<KeyEntity> list = new ArrayList<>();
            KeyEntity entity;

            do {

                entity = new KeyEntity();
                entity.setT1(cursor.getString(cursor.getColumnIndex("t1")));
                entity.setT2(cursor.getString(cursor.getColumnIndex("t2")));
                entity.setT3(cursor.getString(cursor.getColumnIndex("t3")));
                entity.setT4(cursor.getString(cursor.getColumnIndex("t4")));
                entity.setT5(cursor.getInt(cursor.getColumnIndex("t5")));
                list.add(entity);

            } while (cursor.moveToNext());


            cursor.close();

        }
    }

}
