package com.xz.keybag.entity;

/**
 * 用户全局配置
 */
public class AdminConfig {
	private String id;
	private String forgetPass;//密码防忘记
	private long loginTimestamp;
	private long unlockTimestamp;

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
}