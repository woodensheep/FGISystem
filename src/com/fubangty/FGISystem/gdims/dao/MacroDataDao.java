/**
 * Copyright 2008-2012. 重庆富邦科技发展有限公司, Inc. All rights reserved.
 * <a>http://www.3gcq.cn</a>
 */
package com.fubangty.FGISystem.gdims.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fubangty.FGISystem.gdims.entity.MacroData;
import com.fubangty.FGISystem.gdims.util.DataBaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author liyun create 2012-7-14
 * @version 0.1
 *
 */
public class MacroDataDao {
	
	private DataBaseHelper dataBase;
	private String table ="tab_macro_data";
	
	public MacroDataDao(Context context) {
		dataBase = new DataBaseHelper(context);
	}
	
	/**
	 * 存储宏观观测数据
	 * @param list
	 */
	public void saveMacroData(List<MacroData> list){
		SQLiteDatabase db = dataBase.getWritableDatabase();
		db.beginTransaction();
		
		for(MacroData data : list ){
			ContentValues values = new ContentValues();
			values.put("dis_no", data.getDisNo());
			values.put("num", data.getNum());
			values.put("file", data.getFile());
			values.put("content", data.getContent());
			db.insert(table, null, values);
		}
		
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}
	
	/**
	 * 删除对应灾害点编号的宏观观测数据
	 * @param disNo
	 */
	public void deleteMacroData(String disNo){
		SQLiteDatabase db = dataBase.getWritableDatabase();
		int row = db.delete(table, "dis_no=?", new String[]{disNo});
		db.close();
	}
	
	/**
	 * 
	 * @param disNo
	 * @return
	 */
	public List<MacroData> queryList(String disNo){
		SQLiteDatabase db = dataBase.getReadableDatabase();
		List<MacroData> list = new ArrayList<MacroData>();
		Cursor cursor = db.query(table, null, "dis_no=?", new String[]{disNo}, null, null, null);
		while(cursor.moveToNext()){
			MacroData data = new MacroData();
			data.setContent(cursor.getString(cursor.getColumnIndex("content")));
			data.setDisNo(cursor.getString(cursor.getColumnIndex("dis_no")));
			data.setFile(cursor.getString(cursor.getColumnIndex("file")));
			data.setNum(cursor.getInt(cursor.getColumnIndex("num")));
			list.add(data);
		}
		
		cursor.close();
		db.close();
		return list;
	}
	
	public void close(){
		dataBase.close();
	}


}
