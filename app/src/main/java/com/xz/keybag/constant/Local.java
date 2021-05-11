package com.xz.keybag.constant;

import com.xz.keybag.entity.Admin;

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
	public static String NET_GET_UPDATE = "http://192.168.0.233:28080/keybag/update.json";


	//Shard标识
	public static final String SHARD_BOOLEAN_MODE = "day_or_night";
	public static final String SHARD_SERVER_URL = "server_url";

	//Request参数
	public static final int REQ_QR_CODE = 11002; // // 打开扫描界面请求码
	public static final int REQ_PERM_CAMERA = 11003; // 打开摄像头
	public static final int REQ_PERM_EXTERNAL_STORAGE = 11004; // 读写文件

	//EXTRA标识
	public static final String INTENT_EXTRA_KEY_QR_SCAN = "qr_scan_result";

	//user
	public static Admin mAdmin;

	//二维码传输统一RSA加密密钥对
	public static final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCYnaV760mA_Tg-YkJy7Ql9NGjqQg31rRePAAxLx6yvYWrnZpgHWQFufjipUSMv7JZjWjiAVuTD-5HIw1Y1j2zHJBIe_CxoNbOw7cmSHcMD6x0XxHcXfg1q31MBTX5pCAZwtI8S5gwRclBSi1KMCKPhJZDyWS7nOy_8zM8OefliewIDAQAB";
	public static final String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJidpXvrSYD9OD5iQnLtCX00aOpCDfWtF48ADEvHrK9haudmmAdZAW5-OKlRIy_slmNaOIBW5MP7kcjDVjWPbMckEh78LGg1s7DtyZIdwwPrHRfEdxd-DWrfUwFNfmkIBnC0jxLmDBFyUFKLUowIo-ElkPJZLuc7L_zMzw55-WJ7AgMBAAECgYBRSt_kEx9zGu7DpBlbGFIOpEUiSw8ehxXecRsbnn-oZ87G9coTV-DAV3GdFwAUPgSZZWxlSGPQ1QcBlrMsEnrRAQl673iZZMI-OT89SytymJei7HEP42BgyeBIaasHAYpzb9d64GXDzHoP11YrXbEBZKd7c8h7P1P1P42Jsl2eYQJBANqzEyX8yxrKVm7RG_8Gh_QglAtsROmW0kVUvodoV3_pXapGxyGU4bRfh2tMJo9era3nh5KaUrK3Qbwtb8hd2zECQQCypTRiFJdeKTolHJEeU9bJvsdSMw7qYP2nPJl701C70zSnc5CXg8bDY-rcZZBAC6I0G5x8jtpGUOCG_kKjj9VrAkEAn1VmNv_k7YDK1fPNZNDaThdKJMRdtJ1oVpC2OyrezNc1oW_tdl7kzafnnlmdokiFWrTbRgjfBUuUPcgQr7cJkQJAHCcc0jgcgqJMn3yZAgaEWtbN4o5ZU2Zlku1h4rmyh2cJbToxFy-VK0WbxY_b47vxm_Sd_KMmkt48BAodxljscQJATLZs0xAvUIVVjOI5KJFg8WVMdkOlraN0gu4hCyCckmgc-MRGmDkm_koFpr3Qu9JpvLn9UJQI6E5ITorcVdHNOw";

	public static class User {
		public static String loginPwd = "";
	}
}
