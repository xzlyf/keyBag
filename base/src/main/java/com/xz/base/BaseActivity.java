package com.xz.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.orhanobut.logger.Logger;
import com.xz.base.utils.ToastUtil;
import com.xz.widget.dialog.XOnClickListener;
import com.xz.widget.dialog.XzLoadingDialog;
import com.xz.widget.dialog.XzTipsDialog;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity implements BaseView {
    protected final String TAG = this.getClass().getSimpleName();

    protected Activity mContext;
    private XzLoadingDialog xzLoadingDialog;
    private XzTipsDialog xzTipsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutResource());
        ButterKnife.bind(this);
        mContext = this;
        //设置是否开启返回homeAsUp按钮
        if (homeAsUpEnabled()) {
            ActionBar bar = getSupportActionBar();
            if (bar != null) {
                bar.setHomeButtonEnabled(true);
                bar.setDisplayHomeAsUpEnabled(true);

            }
        }

        //申请权限
        initPermission();


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //结束Activity&从集合中移除
        BaseApplication.getInstance().finishActivity(this);
    }

    public abstract boolean homeAsUpEnabled();

    public abstract int getLayoutResource();

    public abstract void initData();

    /**
     * 申请权限
     */
    public void initPermission() {
        if (Local.flag == -1) {
            Local.flag = 0;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                boolean noPermission = false;
                //权限列表在Local全局类里，需要增加权限去local里增加
                for (String s : Local.permission) {
                    if (ContextCompat.checkSelfPermission(this, s) != PackageManager.PERMISSION_GRANTED) {
                        //没有权限
                        noPermission = true;
                        Logger.w(s);
                    }
                }

                if (noPermission) {

                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setTitle("温馨提示")
                            .setMessage("请允许我使用以下权限，以确保程序正确运行！")
                            .setPositiveButton("同意", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(BaseActivity.this, Local.permission, 456);
                                }
                            })
                            .setNegativeButton("不同意", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    lToast("程序可能出现数据保存不成功、闪退等异常!");
                                }
                            })
                            .create();

                    dialog.show();
                }else {
                    initData();
                }

            } else {
                initData();
            }

        } else {
            initData();
        }
    }

    /**
     * 权限结果回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 456) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    //成功
                } else {
                    //失败
                }
            }
            //给不给权限都给进了
            initData();

        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:  //id不要写错，前面要加android
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void showLoading(String text) {
        if (xzLoadingDialog == null) {
            xzLoadingDialog = new XzLoadingDialog.Builder(this)
                    .setCancelEnable(true)
                    .setCancelText(text)
                    .setCancelOnClickListener(new XOnClickListener() {
                        @Override
                        public void onClick(int viewId, String s, int position) {

                        }
                    })
                    .create();
        }

        xzLoadingDialog.show();
    }

    @Override
    public void disLoading() {
        if (xzLoadingDialog != null) {
            xzLoadingDialog.dismiss();
        }
    }

    @Override
    public void sToast(String text) {
        ToastUtil.Shows(this, text);
    }

    @Override
    public void lToast(String text) {
        ToastUtil.Shows_LONG(this, text);
    }

    @Override
    public void sDialog(String title, String msg) {
        if (xzTipsDialog == null) {
            xzTipsDialog = new XzTipsDialog.Builder(this)
                    .setTitle(title)
                    .setContent(msg)
                    .setCancelOnclickListener("好的", new XOnClickListener() {
                        @Override
                        public void onClick(int viewId, String s, int position) {
                            dDialog();
                        }
                    })
                    .create();
            xzTipsDialog.setCancelable(false);
            xzTipsDialog.setCanceledOnTouchOutside(false);
        }
        xzTipsDialog.show();
    }

    @Override
    public void dDialog() {
        if (xzTipsDialog != null || xzTipsDialog.isShowing()) {
            xzTipsDialog.dismiss();
            xzTipsDialog = null;
        }
    }

    /**
     * 点击编辑框外隐藏软键盘并清除焦点
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    /**
     * 是否应该隐藏键盘
     *
     * @param v
     * @param event
     * @return
     */
    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                v.clearFocus();
                return true;
            }
        }
        return false;
    }

}
