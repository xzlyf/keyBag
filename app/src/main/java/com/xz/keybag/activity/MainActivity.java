package com.xz.keybag.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.orhanobut.logger.Logger;
import com.xz.base.BaseActivity;
import com.xz.base.OnItemClickListener;
import com.xz.keybag.R;
import com.xz.keybag.adapter.KeyAdapter;
import com.xz.keybag.constant.Local;
import com.xz.keybag.entity.KeyEntity;
import com.xz.keybag.entity.SecretEntity;
import com.xz.keybag.sql.EOD;
import com.xz.keybag.sql.SqlManager;
import com.xz.utils.RandomString;
import com.xz.utils.SpacesItemDecorationVertical;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.key_recycler)
    RecyclerView keyRecycler;
    @BindView(R.id.tv_menu)
    ImageView tvMenu;
    @BindView(R.id.tv_add)
    ImageView tvAdd;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    private KeyAdapter keyAdapter;
    private List<KeyEntity> mList;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Local.CODE_1:
                    //刷新列表
                    keyAdapter.superRefresh(mList);
                    break;
            }
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

        initView();
        initRecycler();


    }

    /**
     * 每次回到主页重新读取数据库
     */
    @Override
    protected void onResume() {
        super.onResume();
        new ReadDataCommon().start();
    }

    private void initRecycler() {
        mList = new ArrayList<>();
        keyAdapter = new KeyAdapter(mContext);
        keyAdapter.setHandler(handler);
        keyRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        keyRecycler.addItemDecoration(new SpacesItemDecorationVertical(20));
        keyRecycler.setAdapter(keyAdapter);
        keyAdapter.setOnItemClickListener(new OnItemClickListener<KeyEntity>() {
            @Override
            public void onItemClick(View view, int position, KeyEntity model) {
                if (position == 0) {
                    //显示

                } else if (position == 1) {
                    //隐藏
                }
            }

            @Override
            public void onItemLongClick(View view, int position, KeyEntity model) {

            }
        });
    }


    private void initView() {

        tvAdd.setOnClickListener(this);
        tvMenu.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.tv_menu:
                drawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.tv_add:
                startActivity(new Intent(MainActivity.this, AddActivity.class));
                break;

        }


    }


    /**
     * 异步数据读取线程类
     */
    private class ReadDataCommon extends Thread {

        @Override
        public void run() {
            super.run();

            String secret = getSecret();
            Local.secret = secret;
            if (secret == null) {
                Logger.i("密钥初始化");
                return;
            }

            Cursor cursor = SqlManager.queryAll(mContext, Local.TABLE_COMMON);
            //如果游标为空则返回false
            if (!cursor.moveToFirst()) {
                Logger.i("无存档");
                return;
            }
            KeyEntity entity;
            mList.clear();
            do {
                entity = new KeyEntity();
                entity.setT1(EOD.decrypt(cursor.getString(cursor.getColumnIndex("t1")), secret));
                entity.setT2(EOD.decrypt(cursor.getString(cursor.getColumnIndex("t2")), secret));
                entity.setT3(EOD.decrypt(cursor.getString(cursor.getColumnIndex("t3")), secret));
                entity.setT4(EOD.decrypt(cursor.getString(cursor.getColumnIndex("t4")), secret));
                entity.setT5(EOD.decrypt(cursor.getString(cursor.getColumnIndex("t5")), secret));

                mList.add(entity);

            } while (cursor.moveToNext());
            cursor.close();
            //反转列表
            Collections.reverse(mList);
            Message message = handler.obtainMessage();
            message.what = Local.CODE_1;
            handler.sendMessage(message);

        }

        /**
         * 获取密钥
         */
        private String getSecret() {
            Cursor cursor = SqlManager.queryAll(mContext, Local.TABLE_SECRET);
            //如果游标为空则返回false
            if (!cursor.moveToFirst()) {
                //用户第一次运行创建一个随机密钥
                ContentValues values = new ContentValues();
                values.put("k1", RandomString.getRandomString(8, true));
                values.put("k2", RandomString.getRandomString(16));
                values.put("k3", 0);
                SqlManager.insert(mContext, "secret", values);//插入数据
                return null;
            }
            SecretEntity entity = new SecretEntity();
            entity.setK1(cursor.getString(cursor.getColumnIndex("k1")));
            entity.setK2(cursor.getString(cursor.getColumnIndex("k2")));
            entity.setK3(cursor.getString(cursor.getColumnIndex("k3")));
            cursor.close();

            return entity.getK1();
        }
    }

}
