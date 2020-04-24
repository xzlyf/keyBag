package com.xz.keybag.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.xz.utils.MD5Util;
import com.xz.utils.RandomString;
import com.xz.utils.SpacesItemDecorationVertical;
import com.xz.widget.dialog.XOnClickListener;
import com.xz.widget.dialog.XzInputDialog;
import com.xz.widget.textview.SearchEditView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.key_recycler)
    RecyclerView keyRecycler;
    @BindView(R.id.tv_menu)
    ImageView tvMenu;
    @BindView(R.id.tv_add)
    ImageView tvAdd;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.btn_1)
    Button btn_1;
    @BindView(R.id.btn_2)
    Button btn_2;
    @BindView(R.id.btn_3)
    Button btn_3;
    @BindView(R.id.btn_4)
    Button btn_4;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_search)
    SearchEditView etSearch;


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

    private void initView() {

        tvAdd.setOnClickListener(this);
        tvMenu.setOnClickListener(this);
        btn_1.setOnClickListener(this);
        btn_2.setOnClickListener(this);
        btn_3.setOnClickListener(this);
        btn_4.setOnClickListener(this);

        etSearch.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                keyAdapter.getFilter().filter(s);
            }
        });


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
                startActivity(new Intent(MainActivity.this, DetailActivity.class)
                        .putExtra("model", model));
            }

            @Override
            public void onItemLongClick(View view, int position, KeyEntity model) {

            }
        });
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
            case R.id.btn_1:
                startActivity(new Intent(MainActivity.this, SecretActivity.class));
                break;
            case R.id.btn_2:
                startActivity(new Intent(MainActivity.this, LoadActivity.class).putExtra("mode", 1));
                break;
            case R.id.btn_3:
                startActivity(new Intent(MainActivity.this, BackupActivity.class));
                break;
            case R.id.btn_4:
                XzInputDialog dialog = new XzInputDialog.Builder(mContext)
                        .setTitle("请输入密码")
                        .setHint("清空需要密码权限，请输入软件进入时的密码用以格式化数据库。")
                        .setMinLine(3)
                        .setSubmitOnClickListener("确定", new XOnClickListener() {
                            @Override
                            public void onClick(int viewId, String s, int position) {

                                if (MD5Util.getMD5(s).equals(Local.User.loginPwd)) {
                                    SqlManager.deleteAll(mContext, Local.TABLE_COMMON);
                                    sToast("删除完成");
                                } else {
                                    sToast("密码核对错误");
                                }

                            }
                        })
                        .create();
                dialog.show();
                break;


        }


    }

    private long backOccTime;
    @Override
    public void onBackPressed() {
        long i = System.currentTimeMillis();
        if (i - backOccTime > 2000) {
            sToast("再次点击退出");
            backOccTime = i;
        } else {
            super.onBackPressed();
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
                Logger.i(getString(R.string.string_1));
                return;
            }

            Cursor cursor = SqlManager.queryAll(mContext, Local.TABLE_COMMON);
            //如果游标为空则返回false
            if (!cursor.moveToFirst()) {
                Logger.i(getString(R.string.string_2));
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
                String secret = RandomString.getRandomString(8, true);
                ContentValues values = new ContentValues();
                values.put("k1", EOD.encrypt(secret, Local.SECRET_KEY));
                values.put("k2", RandomString.getRandomString(16));
                values.put("k3", new Random().nextInt(64));
                SqlManager.insert(mContext, "secret", values);//插入数据
                return secret;
            }
            SecretEntity entity = new SecretEntity();
            entity.setK1(EOD.decrypt(cursor.getString(cursor.getColumnIndex("k1")), Local.SECRET_KEY));
            entity.setK2(EOD.decrypt(cursor.getString(cursor.getColumnIndex("k2")), Local.SECRET_KEY));
            entity.setK3(EOD.decrypt(cursor.getString(cursor.getColumnIndex("k3")), Local.SECRET_KEY));
            cursor.close();

            return entity.getK1();
        }
    }

}
