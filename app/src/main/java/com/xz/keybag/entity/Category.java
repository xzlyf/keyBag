package com.xz.keybag.entity;

/**
 * @author czr
 * @email czr2001@outlook.com
 * @date 2021/4/25
 */
public class Category {
	/**
	 * 标签名称
	 */
	private String name;
	/**
	 * 标签id
	 */
	private String id;

	public Category(String name, String id) {
		this.name = name;
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
