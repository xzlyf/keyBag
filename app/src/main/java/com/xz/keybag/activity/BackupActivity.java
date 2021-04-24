package com.xz.keybag.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionSet;

import com.orhanobut.logger.Logger;
import com.xz.keybag.R;
import com.xz.keybag.base.BaseActivity;
import com.xz.keybag.constant.Local;
import com.xz.keybag.custom.SecretInputDialog;
import com.xz.keybag.custom.XOnClickListener;
import com.xz.keybag.sql.EOD;
import com.xz.keybag.sql.SqlManager;
import com.xz.utils.ArrayUtil;
import com.xz.utils.MD5Util;
import com.xz.utils.RandomString;
import com.xz.utils.ViewUtil;
import com.xz.utils.hardware.SystemInfoUtil;
import com.xz.utils.network.NetInfo;
import com.xz.widget.textview.IpEditView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BackupActivity extends BaseActivity {


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
    @BindView(R.id.et_ip)
    IpEditView etIp;
    @BindView(R.id.et_port)
    EditText etPort;
    @BindView(R.id.input_layout)
    LinearLayout inputLayout;
    @BindView(R.id.scroll_msg)
    ScrollView scrollMsg;
    @BindView(R.id.btn_connect)
    Button btnConnect;
    @BindView(R.id.btn_break)
    Button btnBreak;
    @BindView(R.id.img_down)
    ImageView imgDonw;

    //状态栏高度
    private int stateBarHeight;
    //标题栏高度
    private int contentHeight;
    private int isShow = 1;//页面状态  //0隐藏 1展开
    private int isSR = -1;//socket模式  //0发送 1接收 -1未选择
    private final int MSG = 110;
    private final String REGEX_IP = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case MSG:
                    tvMsg.append((String) msg.obj);
                    //始终滚动到底部
                    tvMsg.scrollTo(0, tvMsg.getMeasuredHeight());
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
        btnSend.setOnClickListener(controlListener);
        btnReceive.setOnClickListener(controlListener);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //空检测
                if (etPort.getText().toString().trim().equals("") || etIp.getIp().trim().equals("")) {
                    return;
                }

                //ip检测
                String host = etIp.getIp();
                if (!host.matches(REGEX_IP)) {
                    sToast("ip格式错误");
                    return;
                }
                //端口检测
                int port;
                try {
                    port = Integer.valueOf(etPort.getText().toString().trim());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    sToast("端口异常");
                    return;
                }
                if (!(port > 1024 && port <= 65535)) {
                    sToast("端口范围:1024-65535");
                    return;
                }
                btnConnect.setEnabled(false);
                appendMsg("正在连接" + host + ":" + port);
                new SocketClient(host, port).start();
            }
        });
        //断开按钮
        btnBreak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopSocket();
                btnBreak.setVisibility(View.GONE);
                btnConnect.setEnabled(true);
                btnConnect.setVisibility(View.VISIBLE);
            }
        });

        //默认状态
        btnBreak.setVisibility(View.GONE);
        inputLayout.setVisibility(View.GONE);
        labelTips.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) selectLayout.getLayoutParams();
        lp.height = LinearLayout.LayoutParams.MATCH_PARENT;
        lp.width = LinearLayout.LayoutParams.MATCH_PARENT;
        selectLayout.setLayoutParams(lp);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) selectLayout.getLayoutParams();


        imgDonw.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        Log.d(TAG, "onTouch: x:" + event.getRawX() + "  y:" + event.getY());

                        layoutParams.height = ((int) event.getRawY() - stateBarHeight - contentHeight);
                        selectLayout.setLayoutParams(layoutParams);


                        break;

                    case MotionEvent.ACTION_UP:
                        //需要滑动至屏幕一半才完全展开
                        if (event.getRawY() >= ViewUtil.getScreenSizeV2(BackupActivity.this)[1] / 2) {
                            layoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
                            selectLayout.setLayoutParams(layoutParams);
                            beginDelayedTransition(selectLayout);
                        } else {
                            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                            selectLayout.setLayoutParams(layoutParams);
                            beginDelayedTransition(selectLayout);

                            if (isSR == 0) {
                                showSelectLayout(btnSend);
                            } else if (isShow == 1) {
                                showSelectLayout(btnReceive);
                            }

                            stopSocket();

                        }
                        break;
                }
                return true;
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopSocket();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        contentHeight = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        //状态栏高度
        stateBarHeight = ViewUtil.getStatusBarHeight(mContext);
    }

    /**
     * 发送接收按钮事件
     */
    private View.OnClickListener controlListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isShow == 1) {
                hideSelectLayout(v);
                checkPermission();
            } else if (isShow == 0) {
                stopSocket();
                showSelectLayout(v);
                appendMsg("关闭部署");
            }
        }
    };

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
            inputLayout.setVisibility(View.VISIBLE);
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
            inputLayout.setVisibility(View.GONE);
        }

        labelTips.setVisibility(View.VISIBLE);

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
     * 创建服务
     *
     * @param hasPermission
     */
    private void createSocket(boolean hasPermission) {
        //判断是哪个模式进来的
        if (isSR == 0) {
            if (!hasPermission) {
                btnSend.setText("权限不足");
            } else {
                new SocketServers().start();
            }
        } else if (isSR == 1) {
            if (!hasPermission) {
                btnReceive.setText("权限不足");
            }
        }

    }


    private ServerSocket server;
    private Socket socket;
    private OutputStream out = null;
    private InputStream is = null;

    private final int SEND_SIZE = 128;//限制传输和接收大小

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }


    /**
     * ServerSocket=========================================================================
     */
    private class SocketServers extends Thread {
        LocalDataReadThread getDataThread;
        OutputThread ot;
        InputThread it;
        //默认端口，随机分配
        int[] ports = {29766, 6024, 7096, 8686, 9696, 2324, 6430, 14538};
        //超时
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
            //int port = 45678;//测试固定端口
            try {
                server = new ServerSocket(port);
                server.setSoTimeout(TIME_OUT);//超时时间
                appendMsg("创建成功，等待接入...");
                appendMsg("本机ip：" + (NetInfo.isWifiEnabled(mContext) ? NetInfo.getIpInWifi(mContext) : NetInfo.getIpInGPRS() + "（非WIFI）"));
                appendMsg("端口：" + server.getLocalPort());
                socket = server.accept();
                appendMsg("已接入：" + socket.getInetAddress().getHostAddress());

            } catch (SocketException e) {
                e.printStackTrace();
                appendMsg("超时，将关闭");
                stopSocket();
                return;

            } catch (IOException e) {
                appendMsg("创建失败，3秒后再次创建...");
                SystemClock.sleep(3000);//3秒后再次创建
                doConnect();
                return;
            } catch (Exception e) {
                appendMsg("致命错误：" + e.getMessage());
                return;
            }


            //输出流
            ot = new OutputThread();
            ot.start();
            try {
                ot.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //输入流
            it = new InputThread();
            it.start();
            appendMsg("开始接收数据");
            it.callBack(new ReadCallBack() {
                @Override
                public void respond(String data) {
                    appendMsg("数据：" + data);
                    if (data.equals("ready")) {
                        appendMsg("服务端准备好了！");
                        getDataThread.start();

                    }
                }
            });

            //读取数据
            getDataThread = new LocalDataReadThread();
            getDataThread.callBack(new ReadCallBack() {
                @Override
                public void respond(String Data) {

                    ot.send(Data);
                    SystemClock.sleep(2000);
                    ot.send("over");
                    appendMsg("传输完毕");


                }
            });


        }


    }

    /**
     * 输入流
     */
    private class InputThread extends Thread {
        private ReadCallBack callBackListener;

        @Override
        public void run() {
            super.run();
            try {
                is = socket.getInputStream();
                byte[] buff = new byte[SEND_SIZE];
                int len = 0;
                while ((len = is.read(buff)) != -1) {
//                    appendMsg("数据：" + new String(buff, 0, len));
                    callBackListener.respond(new String(buff, 0, len));
                }

            } catch (IOException e) {
                e.printStackTrace();
                appendMsg("数据接收异常:" + e.getMessage());
            }


        }

        public void callBack(ReadCallBack callBack) {
            this.callBackListener = callBack;
        }
    }


    /**
     * 输出流
     */
    private class OutputThread extends Thread {

        @Override
        public void run() {
            super.run();

            try {
                if (socket != null) {
                    out = socket.getOutputStream();
                }
            } catch (IOException e) {
                e.printStackTrace();
                appendMsg(e.getMessage());
            }


        }


        void send(String data) {

            List<byte[]> base = optByte(data.getBytes());
            try {
                if (base != null) {
                    //分段输出
                    //for (byte[] b : base) {
                    //    out.write(b);
                    //    out.flush();
                    //    SystemClock.sleep(200);
                    //}

                    //分段输出
                    DecimalFormat df = new DecimalFormat("#0.00");
                    int total = base.size();
                    for (int i = 0; i < base.size(); i++) {

                        out.write(base.get(i));
                        out.flush();
                        SystemClock.sleep(200);
                        appendMsg("已传输：" + df.format((((float) i / (float) total) * 100)) + "%");
                    }

                } else {
                    //一次性写入
                    out.write(data.getBytes(Charset.forName("UTF-8")));
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        /**
         * 切割长度
         * 不可操作 SEND_SIZE 长度进行传输
         *
         * @param b
         * @return
         */
        public List<byte[]> optByte(byte[] b) {
            if (b.length > SEND_SIZE) {
                return ArrayUtil.optByte(SEND_SIZE, b);
            }
            return null;
        }


    }

    /**
     * 客户端 client
     */
    private class SocketClient extends Thread {
        final int TIME_OUT = 3 * 10 * 1000;
        String host;
        int port;
        LocalDataWriteThread dataWriteThread = new LocalDataWriteThread();
        InputThread it = new InputThread();
        OutputThread ot = new OutputThread();
        StringBuffer dataCache = new StringBuffer();

        SocketClient(String host, int port) {
            this.host = host;
            this.port = port;
        }

        @Override
        public void run() {
            super.run();
            try {
                socket = new Socket();
                SocketAddress socketAddress = new InetSocketAddress(host, port);
                socket.connect(socketAddress, TIME_OUT);
            } catch (UnknownHostException e) {
                e.getStackTrace();
                appendMsg("连接失败：无法确定主机的IP地址");
                return;
            } catch (IOException e) {
                e.printStackTrace();
                appendMsg("连接断开");
                return;
            }

            appendMsg("连接成功");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnConnect.setVisibility(View.GONE);
                    btnBreak.setVisibility(View.VISIBLE);
                }
            });

            //开启输入流
            it.start();
            appendMsg("开始接收数据");
            it.callBack(new ReadCallBack() {
                @Override
                public void respond(String data) {
                    appendMsg(data);
                    if (data.equals("over")) {
                        dataWriteThread.setResData(dataCache.toString());
                        dataWriteThread.start();
                    } else {
                        dataCache.append(data);
                    }
                }
            });

            dataWriteThread.addCallback(new WriteCallBack() {
                @Override
                public void success(int num) {
                    appendMsg("数据存储完毕");
                    stopSocket();
                    changeKeyDialog();
                }

                @Override
                public void failed() {

                }
            });
            //开启输出流

            ot.start();
            try {
                ot.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //发送必要信息
            ot.send(SystemInfoUtil.getInfo(mContext));
            SystemClock.sleep(1000);
            ot.send("ready");
            try {
                socket.shutdownOutput();
                ot.interrupt();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }


    }


    /**
     * 停止socket服务
     */
    private void stopSocket() {
        //if (!isMainThread()) {
        //    runOnUiThread(new Runnable() {
        //        @Override
        //        public void run() {
        //            stopSocket();
        //        }
        //    });
        //    return;
        //}

        if (socket != null) {
            try {
                socket.shutdownOutput();
                socket.shutdownInput();
                appendMsg("通知输入输出流off");
            } catch (IOException e) {
                e.printStackTrace();
                appendMsg("通知输入输出流error");

            }
        }

        if (is != null) {
            try {
                is.close();
                appendMsg("输入流off");
            } catch (IOException e) {
                e.printStackTrace();
                appendMsg("输入流error");

            }
        }
        if (out != null) {
            try {
                out.close();
                appendMsg("输出流off");

            } catch (IOException e) {
                e.printStackTrace();
                appendMsg("输出流error");
            }
        }
        if (socket != null) {
            try {
                socket.close();
                appendMsg("客户端off");
            } catch (IOException e) {
                e.printStackTrace();
                appendMsg("客户端error");

            }
        }
        if (server != null) {
            try {
                server.close();
                appendMsg("服务端off");
            } catch (IOException e) {
                e.printStackTrace();
                appendMsg("服务端error");

            }
        }

        appendMsg("已释放资源");

        //if (isSR == 1) {
        //    showSelectLayout(btnReceive);
        //} else if (isShow == 0) {
        //    showSelectLayout(btnSend);
        //}
    }


    /**
     * 本地数据读取===============================================================================
     */
    private class LocalDataReadThread extends Thread {
        private ReadCallBack callBackListener;
        private StringBuffer buffer = new StringBuffer();
        //private int num = 0;

        @Override
        public void run() {
            super.run();


            Cursor cursor = SqlManager.queryAll(mContext, Local.TABLE_COMMON);
            //如果游标为空则返回false
            if (!cursor.moveToFirst()) {
                Logger.i(getString(R.string.string_2));
                return;
            }
            String rowSeparator = "#";
            String lineSeparator = "@";
            do {
                buffer.append(cursor.getString(cursor.getColumnIndex("t1")));
                buffer.append(rowSeparator);
                buffer.append(cursor.getString(cursor.getColumnIndex("t2")));
                buffer.append(rowSeparator);
                buffer.append(cursor.getString(cursor.getColumnIndex("t3")));
                buffer.append(rowSeparator);
                buffer.append(cursor.getString(cursor.getColumnIndex("t4")));
                buffer.append(rowSeparator);
                buffer.append(cursor.getString(cursor.getColumnIndex("t5")));
                buffer.append(lineSeparator);
                //num++;
            } while (cursor.moveToNext());
            cursor.close();

            callBackListener.respond(buffer.toString());

            //runOnUiThread(new Runnable() {
            //    @Override
            //    public void run() {
            //        XzTipsDialog xzTipsDialog = new XzTipsDialog.Builder(mContext)
            //                .setContent("共" + num + "条数据，是否确定传输")
            //                .setSubmitOnClickListener("确定", new XOnClickListener() {
            //                    @Override
            //                    public void onClick(int viewId, String s, int position) {
            //                        callBackListener.respond(buffer.toString());
            //
            //                    }
            //                })
            //                .setCancelOnclickListener("取消", new XOnClickListener() {
            //                    @Override
            //                    public void onClick(int viewId, String s, int position) {
            //                        buffer.delete(0, buffer.length());
            //                        appendMsg("取消传输");
            //                    }
            //                })
            //                .create();
            //        xzTipsDialog.setCancelable(false);
            //        xzTipsDialog.show();
            //    }
            //});


        }

        public void callBack(ReadCallBack callBack) {
            this.callBackListener = callBack;
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
     * 向本地写入数据==========================================================================
     */
    private class LocalDataWriteThread extends Thread {
        private String resData;
        private WriteCallBack callBackListener;


        @Override
        public void run() {
            super.run();

            //解析数据
            String[] line = resData.split("@");
            String[][] row = new String[line.length][5];
            for (int i = 0; i < line.length; i++) {

                for (int j = 0; j < 5; j++) {
                    row[i] = line[i].split("#");
                }
            }

            //开始存储数据，不清空原来数据
            ContentValues values = new ContentValues();
            for (int i = 0; i < line.length; i++) {
                values.clear();
                values.put("t1", row[i][0]);
                values.put("t2", row[i][1]);
                values.put("t3", row[i][2]);
                values.put("t4", row[i][3]);
                values.put("t5", row[i][4]);
                SqlManager.insert(mContext, "common", values);//插入数据
            }

            callBackListener.success(line.length);
        }

        void setResData(String resData) {
            this.resData = resData;

        }


        void addCallback(WriteCallBack callBack) {
            this.callBackListener = callBack;
        }

    }

    /**
     * 工具类=================================================================================
     */


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

    private void appendMsg(String msg) {
        Message message = handler.obtainMessage();
        message.what = MSG;
        message.obj = msg + "\n";
        handler.sendMessage(message);
    }

    private SecretInputDialog dialog;

    /**
     * 修改密钥
     */
    private void changeKeyDialog() {
        if (!isMainThread()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    changeKeyDialog();
                }
            });
            return;
        }

        dialog = new SecretInputDialog(mContext);
        dialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        dialog.setOnCancelListener(new XOnClickListener() {
            @Override
            public void onClick(String st, View v) {
                dialog.dismiss();

            }
        });
        dialog.setOnSubmitListener(new XOnClickListener() {
            @Override
            public void onClick(String st, View v) {
                if (st.equals("")) {
                    sToast("无效密钥");
                    return;
                }
                //更新密钥
                ContentValues values = new ContentValues();
                values.put("k1", EOD.encrypt(st, Local.SECRET_KEY));
                values.put("k2", RandomString.getRandomString(16));
                values.put("k3", 0);
                SqlManager.update(mContext, "secret", values, "k1 = ?", new String[]{Local.secret});
                sToast("密钥已修改，重启生效");
                Local.secret = st;
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                dialog.dismiss();

            }
        });
    }

    /**
     * 是否在主线程
     *
     * @return
     */
    private boolean isMainThread() {

        return Looper.getMainLooper() == Looper.myLooper();
    }

    private interface ReadCallBack {
        void respond(String Data);
    }

    private interface WriteCallBack {
        void success(int num);

        void failed();
    }


}
