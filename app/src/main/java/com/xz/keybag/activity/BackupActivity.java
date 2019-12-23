package com.xz.keybag.activity;

import android.database.Cursor;
import android.os.Bundle;

import com.orhanobut.logger.Logger;
import com.xz.base.BaseActivity;
import com.xz.keybag.R;
import com.xz.keybag.constant.Local;
import com.xz.keybag.sql.SqlManager;
import com.xz.utils.MD5Util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class BackupActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);
    }

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
        new ReadThread().start();

    }


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
            if (md5Name==null){
                Logger.w("MD5计算失败");
                return null;
            }
            String newName = getCacheDir().getAbsolutePath()+File.separator+md5Name+".txt";
            file.renameTo(new File(newName));
            return md5Name;

        }

    }

}
