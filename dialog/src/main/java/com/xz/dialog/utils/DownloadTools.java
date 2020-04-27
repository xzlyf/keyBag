package com.xz.dialog.utils;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 文件下载类
 *
 * @author czr
 * @date 2020/4/10
 */
public class DownloadTools extends AsyncTask<Void, Integer, String> {
    private final String TAG = "DownloadTools";

    //下载地址
    private String remoteUrl;
    //存储绝对路径，包含文件名和拓展的路径
    private String localPath;
    private DownloadCallback mCallback;


    /**
     * 开始执行下载
     * 提供下载地址和存储地址
     *
     * @param remoteUrl
     * @param localPath
     */
    public void start(@NonNull String remoteUrl, @NonNull String localPath, DownloadCallback callback) {
        mCallback = callback;
        this.remoteUrl = remoteUrl;
        this.localPath = localPath;
        execute();
    }

    public void cancel() {
        if (!isCancelled()) {
            cancel(true);
        }
    }


    /**
     * 下载前操作，还在主线程
     */
    @Override
    protected void onPreExecute() {
        mCallback.onInit();
        super.onPreExecute();
    }

    /**
     * 后台操作 通过publishProgress更新ui
     *
     * @param voids
     * @return
     */
    @Override
    protected String doInBackground(Void... voids) {
        File file = new File(localPath);
        Log.d(TAG, "doInBackground: " + file.getParentFile().toString());
        if (file.isDirectory()) {
            return "Path is Directory";
        }
        if (!file.getParentFile().exists()){
            file.mkdirs();
        }
        if (file.exists()) {
            file.delete();
        }

        int TIME_OUT = 30 * 1000;
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;

        try {
            URL url = new URL(remoteUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(TIME_OUT);
            connection.setReadTimeout(TIME_OUT);
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "HTTP Status-Code not as 200";
            }

            //文件大小
            int fileLength = connection.getContentLength();
            input = connection.getInputStream();
            output = new FileOutputStream(file);

            byte[] data = new byte[2048];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                if (isCancelled()) {
                    input.close();
                    return "Download is Cancel";
                }
                total += count;
                if (fileLength > 0)
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return "error:" + e.getMessage();
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

        return "success";
    }

    /**
     * ui更新
     *
     * @param values
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        mCallback.onUpdate(values[0]);
    }

    /**
     * 结果
     *
     * @param string
     */
    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        Log.d(TAG, "onPostExecute: ");
        if (string.equals("success")) {
            mCallback.onSuccess(localPath);
        } else {
            //下载失败自动删除未完成文件
            File file = new File(localPath);
            if (file.exists()) {
                file.delete();
            }
            mCallback.onError(string);
        }
    }


    /**
     * 主动取消
     */
    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.d(TAG, "onCancelled: ");
    }


    public interface DownloadCallback {
        void onInit();

        void onSuccess(String path);

        void onError(String err);

        void onUpdate(int i);
    }
}
