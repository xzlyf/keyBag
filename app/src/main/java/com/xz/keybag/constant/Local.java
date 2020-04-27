package com.xz.keybag.constant;

public class Local {

    public static final String TABLE_COMMON = "common";//表名
    public static final String TABLE_SECRET = "secret";//表名
    public static final String TABLE_ACC = "dbase";//表名

    //密钥长度必须超过或等于8位
    public static final String SECRET_PWD = "xiaoze66";
    public static final String SECRET_KEY = "xzlyf666";
    public static final String DEFAULT = "0000";
    public static String secret;//密钥

    //handler标识
    public static final int CODE_1 = 101;
    public static final int CODE_2 = 102;
    public static final int CODE_3 = 103;
    public static final int CODE_4 = 104;
    public static final int CODE_5 = 105;


    //更新服务器
    public static String NET_GET_UPDATE = "http://192.168.1.72:28080/keybag/update.json";


    //Shard标识
    public static final String SHARD_BOOLEAN_MODE = "day_or_night";
    public static final String SHARD_SERVER_URL = "server_url";

    public static class User {
        public static String loginPwd = "";

    }
}
