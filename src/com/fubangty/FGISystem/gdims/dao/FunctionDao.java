/**
 * Copyright 2008-2012. 重庆富邦科技发展有限公司, Inc. All rights reserved.
 * <a>http://www.3gcq.cn</a>
 */
package com.fubangty.FGISystem.gdims.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fubangty.FGISystem.gdims.util.DataBaseHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author liyun create 2012-7-7
 * @version 0.1
 *
 */
public class FunctionDao {
	
	private DataBaseHelper dataBase;
	private static final String TABLE_NAME = "tab_config";
	
	public FunctionDao(Context context) {
		dataBase = new DataBaseHelper(context);
	}
	
	/**
	 * 查询该手机号码所配置的号码
	 * @return
	 */
	public Map<String, String> queryForFunction(){
		SQLiteDatabase db = dataBase.getReadableDatabase();
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		
		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
		Map<String, String> map = new HashMap<String, String>();
		while(cursor.moveToNext()){
			map.put("report", cursor.getString(cursor.getColumnIndex("report")));
			map.put("context", cursor.getString(cursor.getColumnIndex("context")));
		}
		cursor.close();
		db.close();
		return map;
	}
	
	public void close(){
		if(dataBase!=null){
			dataBase.close();
		}
		
	}
}
