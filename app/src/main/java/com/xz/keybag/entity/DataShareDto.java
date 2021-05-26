package com.xz.keybag.entity;

/**
 * @author czr
 * @email czr2001@outlook.com
 * @date 2021/5/19
 */
public class DataShareDto {
	private int code;
	private Object t;

	public DataShareDto(int code, Object t) {
		this.code = code;
		this.t = t;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public Object getT() {
		return t;
	}

	public void setT(Object t) {
		this.t = t;
	}
}
