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
 * @author liyun create 2012-6-9
 * @version 0.1
 *
 */
public class MacroDao {
	
	private DataBaseHelper dataBase;
	private static final String TABLE_NAME = "tab_disaster";

	public MacroDao(Context context) {
		dataBase = new DataBaseHelper(context);
	}
	
	public List<Map<String, String>> queryForMacro(String disNo){
		SQLiteDatabase db = dataBase.getReadableDatabase();
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		
		String selection = null;
		String[] selectionArgs = null;
		
		if(disNo!=null){
			selection = "dis_no = ?";
			selectionArgs = new String[]{disNo};
		}
		
		Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);
		while(cursor.moveToNext()){
			Map<String, String> map = new HashMap<String, String>();
			map.put("disNo", cursor.getString(cursor.getColumnIndex("dis_no")));
			map.put("disName", cursor.getString(cursor.getColumnIndex("dis_name")));
			map.put("content", cursor.getString(cursor.getColumnIndex("macro_context")));
			map.put("legalR", cursor.getString(cursor.getColumnIndex("legal_r")));
			map.put("longitude", cursor.getString(cursor.getColumnIndex("longitude")));
			map.put("latitude", cursor.getString(cursor.getColumnIndex("latitude")));
			list.add(map);
		}
		
		cursor.close();
		db.close();
		
		return list;
	}
	

	
	public void close(){
		dataBase.close();
	}
}
