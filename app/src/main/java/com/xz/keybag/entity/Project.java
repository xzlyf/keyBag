package com.xz.keybag.entity;

import java.io.Serializable;

/**
 * @author czr
 * @email czr2001@outlook.com
 * @date 2021/4/27
 */
public class Project implements Serializable {
	private String id;
	private Datum datum;
	private String updateDate;
	private String createDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Datum getDatum() {
		return datum;
	}

	public void setDatum(Datum datum) {
		this.datum = datum;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
}
