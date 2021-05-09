package com.xz.keybag.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @author czr
 * @email czr2001@outlook.com
 * @date 2021/4/26
 */
public class Datum implements Serializable {

	/**
	 * project : 应用名/软件名
	 * account : 账号
	 * password : 密码
	 * pw_history : ["历史密码_1","历史密码_2"]
	 * remark : 备注信息
	 * category : 分类
	 */

	private String project;
	private String account;
	private String password;
	private String remark;
	private String category;
	private List<String> pw_history;

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<String> getPw_history() {
		return pw_history;
	}

	public void setPw_history(List<String> pw_history) {
		this.pw_history = pw_history;
	}

	public boolean isEmpty() {
		return project.equals("") && account.equals("") && password.equals("") && remark.equals("");
	}

	@Override
	public String toString() {
		return "Datum{" +
				"project='" + project + '\'' +
				", account='" + account + '\'' +
				", password='" + password + '\'' +
				", remark='" + remark + '\'' +
				", category='" + category + '\'' +
				", pw_history=" + pw_history +
				'}';
	}
}
