package com.xz.keybag.entity;

/**
 * 用户全局配置
 */
public class AdminConfig {
	private String id;
	private String forgetPass;//密码防忘记
	private long loginTimestamp;
	private long unlockTimestamp;
	private String publicPwd;//密码明文显示
	private String loginSwitch;//开启/关闭密码登录


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getForgetPass() {
		return forgetPass;
	}

	public void setForgetPass(String forgetPass) {
		this.forgetPass = forgetPass;
	}

	public long getLoginTimestamp() {
		return loginTimestamp;
	}

	public void setLoginTimestamp(long loginTimestamp) {
		this.loginTimestamp = loginTimestamp;
	}

	public long getUnlockTimestamp() {
		return unlockTimestamp;
	}

	public void setUnlockTimestamp(long unlockTimestamp) {
		this.unlockTimestamp = unlockTimestamp;
	}

	public String getPublicPwd() {
		return publicPwd;
	}

	public void setPublicPwd(String publicPwd) {
		this.publicPwd = publicPwd;
	}

	public String getLoginSwitch() {
		return loginSwitch;
	}

	public void setLoginSwitch(String loginSwitch) {
		this.loginSwitch = loginSwitch;
	}
}