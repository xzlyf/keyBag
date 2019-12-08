package com.xz.keybag.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class SqlManager {

    private static final String TAG = "SqlManager.class";
    private static final String DB_NAME = "user.db";//数据库名称
    private static final int DB_VERSION = 1;//数据版本
    private static SqlManager mInstance;
    private static SQLiteDatabase db_write;
    private static SQLiteDatabase db_read;

    private SqlManager(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context, DB_NAME, null, DB_VERSION);
        db_write = dbHelper.getWritableDatabase();
        db_read = dbHelper.getReadableDatabase();
    }

    private static SqlManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (SqlManager.class) {
                if (mInstance == null) {
                    mInstance = new SqlManager(context);
                }
            }

        }
        return mInstance;
    }

    /**
     * 插入数据
     *
     * @return 新插入行的行ID，如果发生错误，则为-1
     */
    private long _insert(String table, ContentValues v) {
        return db_write.insert(table, null, v);
    }

    /**
     * 更新数据
     *
     * @return 受影响的行数
     */
    private int _update(String table, ContentValues v, String whereClause, String[] whereArgs) {
        return db_write.update(table, v, whereClause, whereArgs);
    }

    /**
     * 删除数据
     *
     * @return 如果传入一个whereClause，则影响的行数，否则为0。
     * 删除所有行并获得一个count pass“1”作为whereClause。
     */
    private int _delete(String table, String whereClause, String[] whereArgs) {
        return db_write.delete(table, whereClause, whereArgs);
    }

    /**
     * sql语句操作
     *
     * @param sql
     */
    private void _execSQL(String sql) {
        db_write.execSQL(sql);
    }

    /**
     * 查询全部
     *
     * @param table
     * @return
     */
    private Cursor _queryAll(String table) {
        return db_write.query(table, null, null, null, null, null, null);
    }

    /**
     * ===========================================公开方法===========================================
     */
    public static long insert(Context context, String table, ContentValues v) {
        return getInstance(context)._insert(table, v);
    }

    public static int update(Context context, String table, ContentValues values, String whereClause, String[] whereArgs) {
        return getInstance(context)._update(table, values, whereClause, whereArgs);
    }

    public static int delete(Context context, String table, String whereClause, String[] whereArgs) {
        return getInstance(context)._delete(table, whereClause, whereArgs);
    }

    public static Cursor queryAll(Context context, String table) {
        return getInstance(context)._queryAll(table);
    }

    public static void execSql(Context context, String sql) {
        getInstance(context)._execSQL(sql);
    }

    /*

       ***基本使用教程***

    //=============================增
        ContentValues values = new ContentValues();
        values.put("id", "906836");
        values.put("goodsid", "GoodsId");
        values.put("goodsname", "联想笔记本电脑通用键盘保护贴膜");
        values.put("goodslink", "https://detail.tmall.com/item.htm?id=530483020458");
        values.put("actlink", "https://uland.taobao.com/quan/detail?sellerId=2074630256&activityId=38ba9f89b2df4eac9b1d037951d2ef47");
        values.put("imgurl", "https://img.alicdn.com/imgextra/i3/2074630256/TB24QnynpXXXXavXpXXXXXXXXXX_!!2074630256.jpg");
        values.put("actmoney", "1");
        values.put("previos", "6.8");
        values.put("later", "5.8");
        SqlManager.insert(mContext,"cart",values);//插入数据


        //=============================改
        ContentValues values1 = new ContentValues();
        values.put("id", "666666");//需要更新的数据
        //更新数据，更新goodsname为“联想笔记本电脑通用键盘保护贴膜”行的id中的数据
        SqlManager.update(mContext, "cart", values, "goodsname = ?", new String[]{"联想笔记本电脑通用键盘保护贴膜"});


        //=============================删
        //删除id等于666666的行
        SqlManager.delete(mContext,"cart","id=?",new String[]{"666666"});


        //=============================查
        Cursor cursor = SqlManager.queryAll(mContext, "cart");
        //如果游标为空则返回false
        if (cursor.moveToFirst()) {
            do {
                Logger.w(cursor.getString(cursor.getColumnIndex("id")) + "\n" +
                        cursor.getString(cursor.getColumnIndex("goodsid")) + "\n" +
                        cursor.getString(cursor.getColumnIndex("goodsname")) + "\n" +
                        cursor.getString(cursor.getColumnIndex("goodslink")) + "\n" +
                        cursor.getString(cursor.getColumnIndex("actlink")) + "\n" +
                        cursor.getString(cursor.getColumnIndex("imgurl"))
                );
            } while (cursor.moveToNext());
        }
        cursor.close();

     */
}
