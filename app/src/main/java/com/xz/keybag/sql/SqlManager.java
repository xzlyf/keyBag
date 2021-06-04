package com.xz.keybag.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class SqlManager {
    public static final String SECRET_PWD = "xiaoze66";
    public static final String SECRET_KEY = "xzlyf666";
    public static final String DEFAULT = "0000";
    public static final String TABLE_COMMON = "common";//表名
    public static final String TABLE_SECRET = "secret";//表名
    public static final String TABLE_ACC = "dbase";//表名
    private static final String TAG = "SqlManager.class";
    private static final String DB_NAME = "user.db";//数据库名称
    private static final int DB_VERSION = 9;//数据版本
    private static SqlManager mInstance;
    private static SQLiteDatabase db_write;
    private static SQLiteDatabase db_read;

    private SqlManager(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context, DB_NAME, null, DB_VERSION);
        db_write = dbHelper.getWritableDatabase();
        //db_read = dbHelper.getReadableDatabase();
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
     * 删除所有数据
     *
     * @return 如果传入一个whereClause，则影响的行数，否则为0。
     * 删除所有行并获得一个count pass“1”作为whereClause。
     */
    private int _deleteAll(String table) {
        return db_write.delete(table, null, null);
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

    public static int deleteAll(Context context, String table) {
        return getInstance(context)._deleteAll(table);
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
        SqlManager.insert(mContext,"cart",values);//插入数据


        //=============================改
        ContentValues values1 = new ContentValues();
        values.put("id", "666666");//需要更新的数据
        SqlManager.update(mContext, "cart", values, "goodsname = ?", new String[]{"xxxxxxxxxxxx"});


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
                        cursor.getString(cursor.getColumnIndex("imgurl"))
                );
            } while (cursor.moveToNext());
        }
        cursor.close();

     */
}
