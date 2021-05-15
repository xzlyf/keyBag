package com.xz.keybag.entity;

/**
 * @author czr
 * @email czr2001@outlook.com
 * @date 2021/5/15
 */
public class LoginConfig {
	private long loginTimestamp;
	private long lastUnlockTime;
	private String forgetPass;


	public long getLoginTimestamp() {
		return loginTimestamp;
	}

	public void setLoginTimestamp(long loginTimestamp) {
		this.loginTimestamp = loginTimestamp;
	}

	public long getLastUnlockTime() {
		return lastUnlockTime;
	}

	public void setLastUnlockTime(long lastUnlockTime) {
		this.lastUnlockTime = lastUnlockTime;
	}

	public String getForgetPass() {
		return forgetPass;
	}

	public void setForgetPass(String forgetPass) {
		this.forgetPass = forgetPass;
	}
}
