package com.xz.dialog.imitate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;

import com.xz.dialog.R;
import com.xz.dialog.base.BaseDialog;
import com.xz.dialog.utils.DownloadTools;
import com.xz.dialog.utils.SystemUtils;

import java.io.File;

/**
 * @author czr
 * @date 2020/4/7
 * <p>
 * 仿原生对话框
 */
public class UpdateDialog extends BaseDialog {

    private int titleBackgroundRes;//标题背景资源
    private String versionName;//版本号
    private String content;//内容
    private UpdateListener callback;

    private ImageView titleBg;
    private ImageView tvClose;
    private TextView tvVersion;
    private TextView tvContent;
    private TextView tvProgress;
    private TextView tvDownload;
    private TextView tvInstall;
    private Typeface tf;
    private String remoteUrl;
    private String localPath;
    private InstallListener mInstallListener;


    public UpdateDialog(Context context) {
        super(context);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_update;
    }


    @Override
    protected void initView() {
        titleBg = findViewById(R.id.title_bg);
        tvClose = findViewById(R.id.tv_close);
        tvVersion = findViewById(R.id.tv_version);
        tvContent = findViewById(R.id.tv_content);
        tvProgress = findViewById(R.id.tv_progress);
        tvDownload = findViewById(R.id.tv_download);
        tvInstall = findViewById(R.id.tv_install);
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                cancel();
            }
        });
        tvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDownload();
            }
        });
        tvInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInstallListener != null) {
                    mInstallListener.install(localPath);
                }
            }
        });
    }


    @Override
    protected void initData() {
        tvInstall.setVisibility(View.GONE);
        tvClose.setColorFilter(Color.WHITE);
        tvProgress.setVisibility(View.GONE);
        tvDownload.setVisibility(View.VISIBLE);
        titleBackgroundRes = R.drawable.bg_update;
        versionName = "";
        content = "";
        tf = Typeface.createFromAsset(mContext.getAssets(), "fonts/bitsybutton_v2.ttf");
        tvProgress.setTypeface(tf, Typeface.BOLD);

    }

    @Override
    public void show() {
        super.show();
        refreshView();
    }

    /**
     * 刷新视图
     */
    private void refreshView() {
        titleBg.setBackgroundResource(titleBackgroundRes);
        if (tvVersion.equals("")) {
            tvVersion.setVisibility(View.GONE);
        } else {
            tvVersion.setText(versionName);
        }
        tvContent.setText(content);
    }

    /**
     * 执行下载
     */
    private void doDownload() {
        DownloadTools downloadTools = new DownloadTools();
        downloadTools.start(remoteUrl, localPath, new DownloadTools.DownloadCallback() {

            @Override
            public void onInit() {
                tvDownload.setVisibility(View.GONE);
                tvProgress.setVisibility(View.VISIBLE);
                tvInstall.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(String path) {
                callback.onSuccess(path);
                tvProgress.setVisibility(View.GONE);
                tvInstall.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(String err) {
                tvDownload.setVisibility(View.VISIBLE);
                tvProgress.setVisibility(View.GONE);
                callback.onFailed(err);
            }

            @Override
            public void onUpdate(int i) {
                tvProgress.setText(i + "%");
            }
        });
    }


    public static class Builder {
        private UpdateDialog dialog;

        public Builder(Context context) {
            dialog = new UpdateDialog(context);
            dialog.create();
        }

        /**
         * 设置标题背景资源，仅标题，非全局背景
         *
         * @param res
         * @return
         */
        public Builder setTitleBackground(int res) {
            dialog.titleBackgroundRes = res;
            return this;
        }

        /**
         * 设置版本号  示例：v1.0.0
         *
         * @param ver
         * @return
         */
        public Builder setVersionName(String ver) {
            dialog.versionName = ver;
            return this;
        }

        /**
         * 设置更新内容
         *
         * @param content
         * @return
         */
        public Builder setContent(String content) {
            dialog.content = content;
            return this;
        }

        /**
         * 设置下载
         *
         * @param remoteUrl 下载地址
         * @param fileName  文件名
         * @param localPath 文件路径
         * @param callback  回调
         * @return
         */
        @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        public Builder setDownload(String remoteUrl, String fileName, String localPath, UpdateListener callback) {
            dialog.remoteUrl = remoteUrl;
            dialog.localPath = localPath + fileName;
            dialog.callback = callback;
            return this;
        }


        /**
         * 安装按钮监听
         *
         * @param listener
         * @return
         */
        public Builder setInstallOnClickListener(InstallListener listener) {
            dialog.mInstallListener = listener;
            return this;
        }


        public UpdateDialog create() {
            return dialog;
        }
    }

    public interface UpdateListener {
        //成功返回文件地址
        void onSuccess(String path);

        //失败放回原因
        void onFailed(String err);
    }

    public interface InstallListener {
        void install(String path);
    }
}
