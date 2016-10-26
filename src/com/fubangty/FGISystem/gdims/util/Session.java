/**
 * Copyright 2008-2012. 重庆富邦科技发展有限公司, Inc. All rights reserved.
 * <a>http://www.3gcq.cn</a>
 */
package com.fubangty.FGISystem.gdims.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liyun create 2012-5-31
 * @version 0.1
 *
 */
public class Session {
	
	private static Map<String, Object> map = new ConcurrentHashMap<String, Object>();
	
	
	public static Object getEntity(String key,boolean remove){
		
		if(remove){
			return map.remove(key);
		}
		return  map.get(key);
	}
	
	
	public static void putObjct(String key, Object value){
		map.put(key, value);
	}

}
