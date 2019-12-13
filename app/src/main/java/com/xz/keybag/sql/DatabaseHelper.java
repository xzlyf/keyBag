package com.xz.keybag.sql;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String CREATE_COMMON = "create table common (" +
            "t1 text," +
            "t2 text," +
            "t3 text," +
            "t4 text," +
            "t5 text)";

    private static final String CREATE_SECRET = "create table secret(" +
            "k1 text," +
            "k2 text," +
            "k3 text)";
    private static final String CREATE_DBASE = "create table dbase(" +
            "p1 text," +
            "p2 text," +
            "p3 text)";


    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }


    /**
     * 初始化工作
     * 初始化表，没有就重新创建一张
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_COMMON);
        db.execSQL(CREATE_SECRET);
        db.execSQL(CREATE_DBASE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
