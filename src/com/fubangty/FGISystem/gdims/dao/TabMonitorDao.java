/**
 * Copyright 2008-2012. 重庆富邦科技发展有限公司, Inc. All rights reserved.
 * <a>http://www.3gcq.cn</a>
 */
package com.fubangty.FGISystem.gdims.dao;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fubangty.FGISystem.gdims.entity.Node;
import com.fubangty.FGISystem.gdims.entity.Tree;
import com.fubangty.FGISystem.gdims.util.DataBaseHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author liyun create 2012-6-4
 * @version 0.1
 *
 */
public class TabMonitorDao {
	
	
	private static final String TAB_NAME = "tab_monitor";
	private DataBaseHelper dataBase;
	
	public TabMonitorDao(Context context){
		
		dataBase = new DataBaseHelper(context);
		
	}
	
	public List<Tree> queryForList(){
		
		SQLiteDatabase db = dataBase.getReadableDatabase();
		String sql ="SELECT * FROM tab_disaster td,tab_monitor  tm where td.dis_no = tm.disaster_no";
		Cursor  cursor = db.rawQuery(sql, null);
		
		
		
		
		Map<String, Tree> map = new HashMap<String, Tree>();
		
		
		String sql1 ="SELECT * FROM tab_disaster";
		Cursor  cursor1 = db.rawQuery(sql1, null);
		
		
		while(cursor1.moveToNext()){
			String disName = cursor1.getString(cursor.getColumnIndex("dis_name"));
			String disNo = cursor1.getString(cursor.getColumnIndex("dis_no"));
			Tree tree = map.get(disNo)==null? new Tree(disName,disNo): map.get(disNo);
			
			map.put(disNo, tree);
		}
		
		
		while(cursor.moveToNext()){
			String disName = cursor.getString(cursor.getColumnIndex("dis_name"));
			String disNo = cursor.getString(cursor.getColumnIndex("dis_no"));
			String miName = cursor.getString(cursor.getColumnIndex("monitor_name"));
			String miNo = cursor.getString(cursor.getColumnIndex("monitor_no"));
			Tree tree = map.get(disNo)==null? new Tree(disName,disNo): map.get(disNo);
			Node node = new Node();
			node.setName(miName);
			node.setValue(miNo);
			node.setTitle(disNo);
			tree.getChilds().add(node);
			map.put(disNo, tree);
		}
		
		
		cursor1.close();
		cursor.close();
		db.close();
		
		return new ArrayList<Tree>(map.values());
	}
	
	/**
	 * 
	 * @param monitorNo
	 * @return
	 */
	public Map<String, String> queryForMap(String monitorNo,String disNo){
		
		SQLiteDatabase db = dataBase.getReadableDatabase();
		String sql ="SELECT * FROM tab_disaster td,tab_monitor  tm where td.dis_no = tm.disaster_no and tm.monitor_no =? and tm.disaster_no=?";
		Cursor  cursor = db.rawQuery(sql, new String[]{monitorNo,disNo});
		Map<String, String> map = new HashMap<String, String>();
		while(cursor.moveToNext()){
			map.put("dis_name", cursor.getString(cursor.getColumnIndex("dis_name")));
			map.put("dis_no", cursor.getString(cursor.getColumnIndex("dis_no")));
			map.put("longitude", cursor.getString(cursor.getColumnIndex("longitude")));
			map.put("latitude", cursor.getString(cursor.getColumnIndex("latitude")));
			map.put("monitor_name", cursor.getString(cursor.getColumnIndex("monitor_name")));
			map.put("monitor_no", cursor.getString(cursor.getColumnIndex("monitor_no")));
			map.put("monitor_type", cursor.getString(cursor.getColumnIndex("monitor_type")));
			map.put("dimension", cursor.getString(cursor.getColumnIndex("dimension")));
			map.put("monitor_content", cursor.getString(cursor.getColumnIndex("monitor_content")));
			map.put("legal_r", cursor.getString(cursor.getColumnIndex("legal_r")));
			
		}
		
		cursor.close();
		db.close();
		
		return map;
	}
	
	public void close(){
		dataBase.close();
	}

}
