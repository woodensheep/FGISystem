/**
 * Copyright 2008-2012. 重庆富邦科技发展有限公司, Inc. All rights reserved.
 * <a>http://www.3gcq.cn</a>
 */
package com.fubangty.FGISystem.gdims.entity;

/**
 * 宏观巡查
 * @author liyun create 2012-5-28
 * @version 0.1
 *
 */
public class Macro {
	
	private Integer id;
	private String macroName;
	private String picture;
	private String value;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getMacroName() {
		return macroName;
	}
	public void setMacroName(String macroName) {
		this.macroName = macroName;
	}
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	

}
