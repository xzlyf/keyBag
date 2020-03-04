package com.xz.keybag.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionSet;

import com.orhanobut.logger.Logger;
import com.xz.base.BaseActivity;
import com.xz.keybag.R;
import com.xz.keybag.constant.Local;
import com.xz.keybag.sql.SqlManager;
import com.xz.utils.MD5Util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;
import java.util.Scanner;

import butterknife.BindView;

public class BackupActivity extends BaseActivity implements View.OnClickListener {


    @BindView(R.id.select_layout)
    LinearLayout selectLayout;
    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.btn_receive)
    Button btnReceive;
    @BindView(R.id.label_tips)
    TextView labelTips;
    @BindView(R.id.tv_msg)
    TextView tvMsg;

    private int isShow = 1;//页面状态  //0隐藏 1展开
    private int isSR = -1;//socket模式  //0发送 1接收 -1未选择
    private final int MSG = 110;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG:
                    tvMsg.append((String) msg.obj);
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
        return R.layout.activity_backup;
    }

    @Override
    public void initData() {
        initView();
    }

    private void initView() {
        btnSend.setOnClickListener(this);
        btnReceive.setOnClickListener(this);
        //始终保持滚动到底部
        ScrollView scrollView = findViewById(R.id.scroll_msg);
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);

        //默认状态
        labelTips.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) selectLayout.getLayoutParams();
        lp.height = LinearLayout.LayoutParams.MATCH_PARENT;
        lp.width = LinearLayout.LayoutParams.MATCH_PARENT;
        selectLayout.setLayoutParams(lp);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                if (isShow == 1) {
                    hideSelectLayout(v);
                    checkPermission();
                } else if (isShow == 0) {
                    showSelectLayout(v);
                }
                break;
            case R.id.btn_receive:
                if (isShow == 1) {
                    hideSelectLayout(v);
                    checkPermission();
                } else if (isShow == 0) {
                    showSelectLayout(v);
                }
                break;
        }
    }

    /**
     * 隐藏选择界面
     */
    private void hideSelectLayout(View v) {
        isShow = 0;
        labelTips.setVisibility(View.GONE);

        if (v.getId() == R.id.btn_send) {
            isSR = 0;//切换模式
            btnReceive.setVisibility(View.GONE);
        } else if (v.getId() == R.id.btn_receive) {
            isSR = 1;//切换模式
            btnSend.setVisibility(View.GONE);
        }
        v.setBackgroundResource(R.drawable.btn_rect_3);
        ((Button) v).setText("正在部署...");

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) selectLayout.getLayoutParams();
        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        selectLayout.setLayoutParams(layoutParams);
        //设置动画
        beginDelayedTransition(selectLayout);
    }

    /**
     * 显示选择界面
     *
     * @param v
     */
    private void showSelectLayout(View v) {
        isShow = 1;//切换状态
        isSR = -1;//切换模式

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) selectLayout.getLayoutParams();
        layoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
        selectLayout.setLayoutParams(layoutParams);
        //设置动画
        beginDelayedTransition(selectLayout);

        if (v.getId() == R.id.btn_send) {
            btnSend.setBackgroundResource(R.drawable.btn_rect_1);
            btnSend.setText("发送");
            btnReceive.setVisibility(View.VISIBLE);
        } else if (v.getId() == R.id.btn_receive) {
            btnReceive.setBackgroundResource(R.drawable.btn_rect_2);
            btnReceive.setText("接收");
            btnSend.setVisibility(View.VISIBLE);
        }

        labelTips.setVisibility(View.VISIBLE);

    }

    /**
     * 播放动画
     *
     * @param view
     */
    private void beginDelayedTransition(ViewGroup view) {

        TransitionSet mSet = new AutoTransition();
        //设置动画持续时间
        mSet.setDuration(300);
        mSet.setInterpolator(new DecelerateInterpolator(0.8f));
        // 开始播放
        TransitionManager.beginDelayedTransition(view, mSet);
    }

    /**
     * 检查权限
     */
    private void checkPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.READ_PHONE_STATE}, 119);
            } else {
                createSocket(true);
            }
        } else {
            createSocket(true);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 119) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //成功
                createSocket(true);
            } else {
                //失败
                createSocket(false);
            }

        }
    }


    /**
     * 创建serverSocket
     *
     * @param hasPermission
     */
    private void createSocket(boolean hasPermission) {
        if (!hasPermission) {
            //判断是哪个模式进来的
            if (isSR == 0) {
                btnSend.setText("权限不足");
                //showSelectLayout(btnSend);
            } else if (isSR == 1) {
                btnReceive.setText("权限不足");
                //showSelectLayout(btnReceive);
            }
            return;
        }

        new SendServers().start();
    }


    private ServerSocket server;
    private Socket socket;

    /**
     * ServerSocket=========================================================================
     */
    private class SendServers extends Thread {
        //默认端口，随机分配
        int[] ports = {5555, 6666, 7777, 8888, 9999, 29766, 6024, 7096, 8686, 9696, 2324, 6430, 14538};

        //无连接超时
        final int TIME_OUT = 5 * 60 * 1000;

        /**
         * 使用 socket.shutdownInput可以通知服务器关闭输入流
         */

        @Override
        public void run() {
            super.run();
            doConnect();
        }


        private void doConnect() {
            appendMsg("开始创建服务端");
            Random random = new Random();
            int port = ports[random.nextInt(ports.length - 1)];
            try {
                server = new ServerSocket(port);
                server.setSoTimeout(TIME_OUT);//超时时间
                appendMsg("创建成功，等待接入...");
                appendMsg("本机ip：" + server.getLocalSocketAddress());
                appendMsg("端口：" + server.getLocalPort());
                socket = server.accept();
                appendMsg("已接入：" + socket.getInetAddress().getHostAddress());
                new InputThread().start();
                new OutputThread().start();
            } catch (SocketException e) {
                appendMsg("超时，将关闭");

            } catch (IOException e) {
                appendMsg("创建失败，3秒后再次创建...");
                SystemClock.sleep(3000);//3秒后再次创建
                doConnect();
            }

        }


    }

    private class InputThread extends Thread {
        @Override
        public void run() {
            super.run();
            InputStream is = null;
            try {
                is = socket.getInputStream();
                byte[] buff = new byte[1024];
                int len = 0;
                appendMsg("---开始接收数据---");
                while ((len = is.read(buff)) != -1) {
                    appendMsg("数据：" + new String(buff, 0, len));
                }
            } catch (IOException e) {
                e.printStackTrace();
                appendMsg("数据接收异常:" + e.getMessage());
            }


            try {
                if (is != null) {
                    is.close();
                    appendMsg("接收关闭");
                }
            } catch (IOException e) {
                appendMsg("输入流关闭异常");
                e.printStackTrace();
            }


        }
    }

    private class OutputThread extends Thread {
        @Override
        public void run() {
            super.run();
            OutputStream out = null;
            try {
                if (socket != null) {
                    out = socket.getOutputStream();
                    String in = "Hello world";
                    out.write(("server saying: " + in).getBytes());
                    out.flush();// 清空缓存区的内容
                    //try {
                    //    socket.shutdownOutput();
                    //    out.close();
                    //} catch (IOException e) {
                    //    e.printStackTrace();
                    //}
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 本地数据读取===============================================================================
     */
    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();

            if (Local.secret == null) {
                Logger.w(getString(R.string.string_25));
                return;
            }
            Cursor cursor = SqlManager.queryAll(mContext, Local.TABLE_COMMON);
            //如果游标为空则返回false
            if (!cursor.moveToFirst()) {
                Logger.i(getString(R.string.string_2));
                return;
            }
            String rowSeparator = "#";
            String lineSeparator = "@";
            StringBuilder builder = new StringBuilder();
            do {
                builder.append(cursor.getString(cursor.getColumnIndex("t1")));
                builder.append(rowSeparator);
                builder.append(cursor.getString(cursor.getColumnIndex("t2")));
                builder.append(rowSeparator);
                builder.append(cursor.getString(cursor.getColumnIndex("t3")));
                builder.append(rowSeparator);
                builder.append(cursor.getString(cursor.getColumnIndex("t4")));
                builder.append(rowSeparator);
                builder.append(cursor.getString(cursor.getColumnIndex("t5")));
                builder.append(lineSeparator);
            } while (cursor.moveToNext());
            cursor.close();
            Logger.w(builder.toString());

            try {
                save(builder);
                Logger.w("成功");
            } catch (IOException e) {
                e.printStackTrace();
                Logger.w("失败");
            }

        }

        private String save(StringBuilder builder) throws IOException {
            String fileName = System.currentTimeMillis() + ".txt";
            String path = getCacheDir().getAbsolutePath() + File.separator + fileName;
            File file = new File(path);
            if (file.exists()) { // 如果已存在,删除旧文件
                file.delete();
            }
            file = new File(path);
            file.createNewFile();
            PrintWriter pfp = new PrintWriter(file, "UTF-8"); // 设置输出文件的编码为utf-8

            pfp.print(builder.toString());
            pfp.flush();
            pfp.close();

            String md5Name = MD5Util.getFileMD5(file);
            if (md5Name == null) {
                Logger.w("MD5计算失败");
                return null;
            }
            String newName = getCacheDir().getAbsolutePath() + File.separator + md5Name + ".txt";
            file.renameTo(new File(newName));
            return md5Name;

        }

    }


    /**
     * 工具类=================================================================================
     */


    private void appendMsg(String msg) {
        Message message = handler.obtainMessage();
        message.what = MSG;
        message.obj = msg + "\n";
        handler.sendMessage(message);
    }

}
