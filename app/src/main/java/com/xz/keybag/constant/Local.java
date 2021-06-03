package com.xz.keybag.constant;

import com.xz.keybag.entity.Admin;

public class Local {

	public static final String TABLE_COMMON = "common";//表名
	public static final String TABLE_SECRET = "secret";//表名
	public static final String TABLE_ACC = "dbase";//表名

	public static final String PACKAGE_NAME = "com.xz.keybag";
	public static final String WeChat = "小泽干货铺";
	public static final String WeChat_URL = "http://weixin.qq.com/r/SziRicLEyp_1rYbq921Z";

	public static final String DEFAULT_SLOGAN = "你个大头虾\n成日忘记密码";//默认slogan
	public static String SLOGAN = "";//当前slogan

	//二维码协议头
	public static final String PROTOCOL_SPLIT = "@";//分割符号
	//二维码密钥传输协议:格式：RSA(头协议@DES(secret))
	public static final String PROTOCOL_QR_SECRET = "keybag_secret";
	//二维码数据搬家协议:格式：RSA(头协议@ip@port)
	public static final String PROTOCOL_QR_SHARD = "keybag_shard";

	//密钥长度必须超过或等于8位
	public static final String SECRET_PWD = "xiaoze66";
	public static final String SECRET_KEY = "xzlyf666";
	public static final String DEFAULT = "0000";
	public static String secret;//密钥


	//handler标识

	//Shard标识
	public static final String SHARD_BOOLEAN_MODE = "day_or_night";
	public static final String SHARD_SLOGAN = "slogan";

	//Request参数
	public static final int REQ_QR_CODE = 11002; // // 打开扫描界面请求码
	public static final int REQ_MAKE_PWD = 11003; // // 打开随机密码生成界面

	//Activity启动模式
	public static final int START_MODE_LOGIN_MODE = 0x14764185;
	public static final int START_MODE_RANDOM = 0x14764186;

	//EXTRA标识
	public static final String INTENT_EXTRA_KEY_QR_SCAN = "qr_scan_result";
	public static final String INTENT_EXTRA_QR_CODE = "qr_code";
	public static final String INTENT_EXTRA_RANDOM = "random";


	//参数标识
	public static final String FINGERPRINT_STATE_OPEN = "fingerprint";//开启指纹登录
	public static final String FINGERPRINT_STATE_CLOSE = "shutdown_f";//关闭指纹登录
	public static final String FINGERPRINT_STATE_NONSUPPORT = "no_f";//设备不知指纹
	public static final String PASSWORD_STATE_NULL = "no_password";
	public static final String PASSWORD_STATE_SUCCESS = "success_password";
	public static final String PASSWORD_STATE_ERROR = "error_query_pwd";
	public static final String CONFIG_FORGET_OPEN = "open";//开启密码防忘记
	public static final String CONFIG_FORGET_SHUT = "shut";//关闭密码防忘记
	public static final String CONFIG_PUBLIC_PWD_OPEN = "open";//密码明文显示开启
	public static final String CONFIG_PUBLIC_PWD_SHUT = "shut";//密码明文显示关闭

	//user
	public static Admin mAdmin;

	//二维码传输统一RSA加密密钥对
	public static final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCYnaV760mA_Tg-YkJy7Ql9NGjqQg31rRePAAxLx6yvYWrnZpgHWQFufjipUSMv7JZjWjiAVuTD-5HIw1Y1j2zHJBIe_CxoNbOw7cmSHcMD6x0XxHcXfg1q31MBTX5pCAZwtI8S5gwRclBSi1KMCKPhJZDyWS7nOy_8zM8OefliewIDAQAB";
	public static final String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJidpXvrSYD9OD5iQnLtCX00aOpCDfWtF48ADEvHrK9haudmmAdZAW5-OKlRIy_slmNaOIBW5MP7kcjDVjWPbMckEh78LGg1s7DtyZIdwwPrHRfEdxd-DWrfUwFNfmkIBnC0jxLmDBFyUFKLUowIo-ElkPJZLuc7L_zMzw55-WJ7AgMBAAECgYBRSt_kEx9zGu7DpBlbGFIOpEUiSw8ehxXecRsbnn-oZ87G9coTV-DAV3GdFwAUPgSZZWxlSGPQ1QcBlrMsEnrRAQl673iZZMI-OT89SytymJei7HEP42BgyeBIaasHAYpzb9d64GXDzHoP11YrXbEBZKd7c8h7P1P1P42Jsl2eYQJBANqzEyX8yxrKVm7RG_8Gh_QglAtsROmW0kVUvodoV3_pXapGxyGU4bRfh2tMJo9era3nh5KaUrK3Qbwtb8hd2zECQQCypTRiFJdeKTolHJEeU9bJvsdSMw7qYP2nPJl701C70zSnc5CXg8bDY-rcZZBAC6I0G5x8jtpGUOCG_kKjj9VrAkEAn1VmNv_k7YDK1fPNZNDaThdKJMRdtJ1oVpC2OyrezNc1oW_tdl7kzafnnlmdokiFWrTbRgjfBUuUPcgQr7cJkQJAHCcc0jgcgqJMn3yZAgaEWtbN4o5ZU2Zlku1h4rmyh2cJbToxFy-VK0WbxY_b47vxm_Sd_KMmkt48BAodxljscQJATLZs0xAvUIVVjOI5KJFg8WVMdkOlraN0gu4hCyCckmgc-MRGmDkm_koFpr3Qu9JpvLn9UJQI6E5ITorcVdHNOw";
	public static final String desKey = "jaZrjLhA";

	public static class User {
		public static String loginPwd = "";
	}
}
