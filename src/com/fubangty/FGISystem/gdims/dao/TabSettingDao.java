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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author liyun create 2012-7-18
 * @version 0.1
 *
 */
public class TabSettingDao {

	private DataBaseHelper dataBaseHelper;
	
	private String table = "tab_setting";
	
	public TabSettingDao(Context context) {
		dataBaseHelper = new DataBaseHelper(context);
	}
	
	/**
	 * 
	 * @return
	 */
	public Map<String, String> initConfig(){
		
		List<Map<String, String>> list = queryForList();
		if(list.size()>0){
			Map<String, String> params = list.get(0);
			return params;
		}
		
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Map<String, String>> queryForList(){
		
		SQLiteDatabase database = dataBaseHelper.getReadableDatabase();
		
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		
		Cursor cursor = database.query(table, null, null, null, null, null, null);
		while(cursor.moveToNext()){
			Map<String, String> parames = new HashMap<String, String>();
			parames.put("ipAddress", cursor.getString(cursor.getColumnIndex("ip_address")));
			parames.put("port", ""+cursor.getInt(cursor.getColumnIndex("port")));
			parames.put("mobile", cursor.getString(cursor.getColumnIndex("mobile")));
			parames.put("id", cursor.getString(cursor.getColumnIndex("id")));
			list.add(parames);
		}
		cursor.close();
		database.close();
		
		return list;
	}
	/**
	 * 
	 * @param ipAddress
	 * @param port
	 * @param url
	 */
	public void saveSetting(String ipAddress,String port, String mobile){
		List<Map<String, String>> list = queryForList();
		SQLiteDatabase database = dataBaseHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("port", port);
		values.put("ip_address", ipAddress);
		values.put("mobile", mobile);
		
		if(list.size()>0){
			String id = list.get(0).get("id");
			database.update(table, values, "id=?", new String[]{id});
		}else{
			database.insert(table, null, values);
		}
		
		database.close();
		
	}
	
	public void close(){
		if(dataBaseHelper!=null){
			dataBaseHelper.close();
		}
	}

}
