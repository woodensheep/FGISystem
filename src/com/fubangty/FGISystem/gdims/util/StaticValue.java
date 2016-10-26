/**
 * Copyright 2008-2012. 重庆富邦科技发展有限公司, Inc. All rights reserved.
 * <a>http://www.3gcq.cn</a>
 */
package com.fubangty.FGISystem.gdims.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fubangty.FGISystem.R;

import android.os.Environment;


/**
 * @author liyun create 2012-6-9
 * @version 0.1
 *
 */
public class StaticValue {
	
	public static List<Map<String, Object>> bbarItem = new ArrayList<Map<String,Object>>();
	
	public final static String IP_ADDRESS="10.0.2.2";
	
	public final static String PORT ="8080";
	
	public final static String CONTEXT = "receive";
	
	public final static String CONFIG_NAME ="config.txt";
	/**
	 * 保存宏观观测照片位置，必须有存储卡
	 */
	public final static String MACRO_PIC_PATH =Environment.getExternalStorageDirectory().toString()+"/macro/";
	/**
	 * 保存监测点照片位置，必须有存储卡
	 */
	public final static String MONITOR_PIC_PATH = Environment.getExternalStorageDirectory().toString() + "/monitor/";
	/**
	 * 保存灾情速报的照片
	 */
	public final static String REPORT_PIC_PATH = Environment.getExternalStorageDirectory().toString()+ "/report/";
	
	static {
		Map<String, Object> item1 = new HashMap<String, Object>();
		item1.put("ItemImage", R.drawable.ic_menu_back);
		item1.put("ItemText", "返回");

		Map<String, Object> item2 = new HashMap<String, Object>();
		item2.put("ItemImage", R.drawable.ic_menu_home);
		item2.put("ItemText", "主页");

		Map<String, Object> item3 = new HashMap<String, Object>();
		item3.put("ItemImage", R.drawable.ic_menu_upload);
		item3.put("ItemText", "上传");

		bbarItem.add(item1);
		bbarItem.add(item2);
		bbarItem.add(item3);
	}
	/**
	 * 生成一个毫秒级的序列号
	 * @return
	 */
	public static String getSerialNo(){
		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String serialNo = sd.format(new Date());
		return serialNo;
	}
	

}
