/**
 * Copyright 2008-2012. 重庆富邦科技发展有限公司, Inc. All rights reserved.
 * <a>http://www.3gcq.cn</a>
 */
package com.fubangty.FGISystem.gdims.util;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;


/**
 * @author liyun create 2012-6-11
 * @version 0.1
 *
 */
public class GobalApplication extends Application{
	
	private String ipAddress ;
	
	private String port;
	
	private String mobile;
	
	private Map<String, String> cache ;
	
	public void setCache(Map<String, String> cache) {
		this.cache = cache;
	}
	
	public Map<String, String> getCache() {
		if(cache==null){
			cache = new HashMap<String, String>();
		}
		return cache;
	}
	
	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getMobile() {
		return mobile;
	}
	
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

}
