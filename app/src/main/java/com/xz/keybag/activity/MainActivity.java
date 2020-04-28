package com.xz.keybag.activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.xz.base.BaseActivity;
import com.xz.base.OnItemClickListener;
import com.xz.base.utils.PreferencesUtilV2;
import com.xz.dialog.event.NegativeOnClickListener;
import com.xz.dialog.event.PositiveOnClickListener;
import com.xz.dialog.imitate.AppleInputDialog;
import com.xz.dialog.imitate.UpdateDialog;
import com.xz.keybag.R;
import com.xz.keybag.adapter.KeyAdapter;
import com.xz.keybag.constant.Local;
import com.xz.keybag.entity.KeyEntity;
import com.xz.keybag.entity.SecretEntity;
import com.xz.keybag.entity.UpdateEntity;
import com.xz.keybag.sql.EOD;
import com.xz.keybag.sql.SqlManager;
import com.xz.utils.MD5Util;
import com.xz.utils.PackageUtil;
import com.xz.utils.RandomString;
import com.xz.utils.SpacesItemDecorationVertical;
import com.xz.utils.SystemUtil;
import com.xz.utils.ThreadUtil;
import com.xz.utils.network.OkHttpClientManager;
import com.xz.widget.dialog.XOnClickListener;
import com.xz.widget.dialog.XzInputDialog;
import com.xz.widget.textview.SearchEditView;

import java.io.IOException;
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
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_search)
    SearchEditView etSearch;
    @BindView(R.id.switch_mode)
    Switch modeSwitch;


    private KeyAdapter keyAdapter;
    private List<KeyEntity> mList;
    private boolean isNight;//日渐模式false 夜间模式true
    private boolean isNet = false;//正在进行网络操作

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case Local.CODE_1:
                    //刷新列表
                    keyAdapter.superRefresh(mList);
                    break;
                case Local.CODE_2:
                    //更新数据
                    disLoading();
                    UpdateEntity entity = (UpdateEntity) msg.obj;
                    if (PackageUtil.getVersionCode(mContext) >= entity.getCode()) {
                        sToast("当前为最新版本");
                        break;
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(mContext, "存储权限未获取", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                    String path = mContext.getExternalFilesDir("update").getAbsolutePath() + "/";
                    String fileName = "kb.apk";
                    UpdateDialog dialog = new UpdateDialog.Builder(mContext)
                            .setContent(entity.getMsg())
                            .setVersionName("v" + entity.getVersion())
                            .setDownload(entity.getLink(), fileName, path, new UpdateDialog.UpdateListener() {
                                @Override
                                public void onSuccess(String path) {

                                }

                                @Override
                                public void onFailed(String err) {
                                    sToast(err);
                                }
                            })
                            .setInstallOnClickListener(new UpdateDialog.InstallListener() {
                                @Override
                                public void install(String path) {
                                    SystemUtil.newInstallAppIntent(mContext, path);
                                }
                            })
                            .create();
                    dialog.show();
                    break;
                case Local.CODE_3:
                    //请求失败
                    disLoading();
                    sToast("服务器异常");
                    break;
            }
            return true;
        }
    });


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
        initState();
        initView();
        initRecycler();


    }

    private void initState() {
        isNight = PreferencesUtilV2.getBoolean(Local.SHARD_BOOLEAN_MODE, false);
        modeSwitch.setChecked(isNight);
        if (isNight) {
            if (!isNightMode()) {
                changeMode(true);
            }
        }
        modeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                changeMode(isChecked);
            }
        });

        Local.NET_GET_UPDATE = PreferencesUtilV2.getString(Local.SHARD_SERVER_URL, Local.NET_GET_UPDATE);

    }

    private void initView() {

        tvAdd.setOnClickListener(this);
        tvMenu.setOnClickListener(this);
        findViewById(R.id.btn_1).setOnClickListener(this);
        findViewById(R.id.btn_2).setOnClickListener(this);
        findViewById(R.id.btn_3).setOnClickListener(this);
        findViewById(R.id.btn_4).setOnClickListener(this);
        Button btn5 = findViewById(R.id.btn_5);
        btn5.setOnClickListener(this);
        btn5.setOnLongClickListener(onLongClickListener);

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

    @Override
    protected void changeMode(boolean isNight) {
        super.changeMode(isNight);
        modeSwitch.setChecked(isNight);
        PreferencesUtilV2.putBoolean(Local.SHARD_BOOLEAN_MODE, isNight);
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

    private View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            XzInputDialog dialog = new XzInputDialog.Builder(mContext)
                    .setHint("服务器更新地址")
                    .setMinLine(3)
                    .setMaxLine(3)
                    .setSubmitOnClickListener("确定", new XOnClickListener() {
                        @Override
                        public void onClick(int viewId, String s, int position) {
                            Local.NET_GET_UPDATE = s;
                            PreferencesUtilV2.putString(Local.SHARD_SERVER_URL, Local.NET_GET_UPDATE);
                            sToast("修改完成");
                        }
                    })
                    .create();
            dialog.show();
            return true;
        }
    };

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.tv_menu:
                drawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.tv_add:
                drawerLayout.closeDrawer(Gravity.LEFT);
                startActivity(new Intent(MainActivity.this, AddActivity.class));
                break;
            case R.id.btn_1:
                drawerLayout.closeDrawer(Gravity.LEFT);
                startActivity(new Intent(MainActivity.this, SecretActivity.class));
                break;
            case R.id.btn_2:
                drawerLayout.closeDrawer(Gravity.LEFT);
                startActivity(new Intent(MainActivity.this, LoadActivity.class).putExtra("mode", 1));
                break;
            case R.id.btn_3:
                drawerLayout.closeDrawer(Gravity.LEFT);
                startActivity(new Intent(MainActivity.this, BackupActivity.class));
                break;
            case R.id.btn_4:
                drawerLayout.closeDrawer(Gravity.LEFT);

                AppleInputDialog dialog = new AppleInputDialog.Builder(mContext)
                        .setTitle("请输入密码")
                        .setInputLines(1)
                        .setContent("请输入启动密码，用以格式化数据库。")
                        .setHint("请输入")
                        .setPositiveOnClickListener("确定", new PositiveOnClickListener() {
                            @Override
                            public void OnClick(View v, String t) {
                                if (MD5Util.getMD5(t).equals(Local.User.loginPwd)) {
                                    SqlManager.deleteAll(mContext, Local.TABLE_COMMON);
                                    sToast("删除完成");
                                } else {
                                    sToast("密码核对错误");
                                }
                            }
                        })
                        .setNegativeOnClickListener("取消", new NegativeOnClickListener() {
                            @Override
                            public void OnClick(View v, String t) {

                            }
                        })
                        .create();
                dialog.show();
                break;
            case R.id.btn_5:
                if (isNet) {
                    sToast("请勿频繁操作");
                    return;
                }
                checkUpdate();
                break;


        }


    }

    /**
     * 检查更新
     */
    private void checkUpdate() {
        isNet = true;
        showLoading("正在加载");
        ThreadUtil.runInThread(new Runnable() {
            @Override
            public void run() {
                try {
                    String request = OkHttpClientManager.getAsString(Local.NET_GET_UPDATE);
                    Logger.w(request);
                    Gson gson = new Gson();
                    UpdateEntity entity = gson.fromJson(request, UpdateEntity.class);

                    Message message = Message.obtain();
                    message.obj = entity;
                    message.what = Local.CODE_2;
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(Local.CODE_3);
                }
                isNet = false;
            }
        });

    }

    private long backOccTime;

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            long i = System.currentTimeMillis();
            if (i - backOccTime > 2000) {
                sToast("再次点击退出");
                backOccTime = i;
            } else {
                super.onBackPressed();
            }
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
                values.put("k2", EOD.encrypt(RandomString.getRandomString(16),Local.SECRET_KEY));
                values.put("k3", EOD.encrypt(String.valueOf(new Random().nextInt(64)),Local.SECRET_KEY));
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
