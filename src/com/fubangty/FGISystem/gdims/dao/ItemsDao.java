/**
 * Copyright 2008-2012. 重庆富邦科技发展有限公司, Inc. All rights reserved.
 * <a>http://www.3gcq.cn</a>
 */
package com.fubangty.FGISystem.gdims.dao;

import java.util.ArrayList;
import java.util.List;

import com.fubangty.FGISystem.gdims.util.DataBaseHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author liyun create 2012-8-27
 * @version 0.1
 *
 */
public class ItemsDao {
	
	private DataBaseHelper helper;
	
	public ItemsDao(Context context){
		helper = new DataBaseHelper(context);
	}
	
	public String[] getTownItems(){
		
		String table = "tab_town";
		
		SQLiteDatabase database = helper.getReadableDatabase();
		Cursor cursor = database.query(table, null, null, null, null, null, null);
		List<String> list = new ArrayList<String>();
		while(cursor.moveToNext()){
			list.add(cursor.getString(cursor.getColumnIndex("name")));
		}
		
		cursor.close();
		database.close();
		
		return list.toArray(new String[list.size()]);
		
	}
	
	public String[] getCountryItems(String town){
		
		SQLiteDatabase database = helper.getReadableDatabase();
		String sql = "select t1.name from tab_country t1,tab_town t2 where t1.town=t2.num and t2.name =?";
		String[] selectionArgs = {town};
		Cursor cursor = database.rawQuery(sql, selectionArgs);
		
		List<String> list = new ArrayList<String>();
		while(cursor.moveToNext()){
			list.add(cursor.getString(cursor.getColumnIndex("name")));
		}
		
		cursor.close();
		database.close();
		
		return list.toArray(new String[list.size()]);
		
	}
	
	
	
	public void close(){
		
		if(helper!=null){
			helper.close();
		}
		
	}

}
