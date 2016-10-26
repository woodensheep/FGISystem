/**
 * Copyright 2008-2012. 重庆富邦科技发展有限公司, Inc. All rights reserved.
 * <a>http://www.3gcq.cn</a>
 */
package com.fubangty.FGISystem.gdims.dao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.fubangty.FGISystem.gdims.exception.GdimsException;
import com.fubangty.FGISystem.gdims.util.DataBaseHelper;

/**
 * 初始化数据
 * 
 * @author liyun create 2012-6-5
 * @version 0.1
 */
public class InitDataDao {

	private DataBaseHelper dataBase;
	private static final String[] TABLES = { "tab_monitor", "tab_config", "tab_disaster","tab_country","tab_town" };

	public InitDataDao(Context context) {
		dataBase = new DataBaseHelper(context);
	}

	public void initData() {
		SQLiteDatabase db = dataBase.getWritableDatabase();
		for (String table : TABLES) {
			db.execSQL("delete from " + table);
		}
		db.close();
	}

	/**
	 * 根据服务器传回的json字符串初始化功能配置表
	 * 
	 * @param json
	 *            {"report":"群测群防监测,灾险情速报","context":"发生时间,乡,村,组,地点,经度..."}
	 * @throws JSONException
	 * @throws GdimsException 
	 */
	public void initConfig(String json) throws JSONException, GdimsException {

		String table = "tab_config";
		ContentValues values = new ContentValues();
		JSONObject object = new JSONObject(json);
		String result = object.getString("result");
		String infos = object.getString("info");
		if ("1".equals(result)) {
			JSONObject info = new JSONObject(infos);
			values.put("report", info.getString("report"));
			values.put("context", info.getString("context"));
			SQLiteDatabase db = dataBase.getWritableDatabase();
			db.insert(table, null, values);
			db.close();
		} else {
			String error = object.getString("info");
			if (error == null) {
				error = "未分配该号码的监测点信息";
			}
			throw new GdimsException(error);
		}

	}

	/**
	 * 初始化灾害点信息表
	 * 
	 * @param json
	 *            返回的是一个JSON数组[{"legalR":100,"unifiedNumber":"500101010001",
	 *            "name":"2","disasterType":null,"longitude":6,"latitude":0,
	 *            "macroscopicPhenomenon":"墙上新裂缝,小型崩塌,新泉水新湿地,树木歪斜,动物异常,"}]
	 * @throws JSONException
	 * @throws GdimsException 
	 */
	public void initDisaster(String json) throws JSONException, GdimsException {
		String table = "tab_disaster";

		// JSONArray array = new JSONArray(json);
		JSONObject object = new JSONObject(json);
		String result = object.getString("result");
		String infos = object.getString("info");
		if ("1".equals(result)) {
			JSONArray array = new JSONArray(infos);
			SQLiteDatabase db = dataBase.getWritableDatabase();
			// db.beginTransaction(); 用事务的方式批量提交,但如果数量比较小也可以不开
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				ContentValues values = new ContentValues();
				values.put("dis_name", obj.getString("name"));
				values.put("dis_no", obj.getString("unifiedNumber"));
				values.put("longitude", obj.getString("longitude"));
				values.put("latitude", obj.getString("latitude"));
				values.put("legal_r", obj.getDouble("legalR"));
				values.put("dis_type", obj.getString("disasterType"));
				values.put("macro_context", obj.getString("macroscopicPhenomenon"));
				db.insert(table, null, values);

			}
			// db.setTransactionSuccessful();
			// db.endTransaction();
			db.close();
		} else {
			String error = object.getString("info");
			if (error == null) {
				error = "未分配该号码的监测点信息";
			}
			throw new GdimsException(error);
		}

	}

	/**
	 * 初始化监测信息表
	 * 
	 * @param json
	 *            [{"monAngle":181,"monPointName":"滑坡正北方1号点","unifiedNumber":
	 *            "500101010001"
	 *            ,"ypoint":25000,"monPointNumber":"5001010100010101",
	 *            "instrumentConstant"
	 *            :0,"dimension":"","monDirection":36,"instrumentNumber"
	 *            :"","monType"
	 *            :1,"legalR":150,"longitude":"","latitude":"","xpoint"
	 *            :50000,"monContent":"裂缝宽度","monPointLocation":"滑坡正北方"}]
	 * @throws JSONException
	 * @throws GdimsException 
	 */
	public void initMonitor(String json) throws JSONException, GdimsException {
		String table = "tab_monitor";
		JSONObject object = new JSONObject(json);
		String result = object.getString("result");
		String infos = object.getString("info");
		if ("1".equals(result)) {
			JSONArray array = new JSONArray(infos);
			SQLiteDatabase db = dataBase.getWritableDatabase();
			db.beginTransaction(); // 用事务的方式批量提交,但如果数量比较小也可以不开
			try {
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					ContentValues values = new ContentValues();
					values.put("monitor_name", obj.getString("monPointName"));
					values.put("disaster_no", obj.getString("unifiedNumber"));
					values.put("monitor_no", obj.getString("monPointNumber"));
					values.put("longitude", obj.getString("xpoint"));
					values.put("latitude", obj.getString("ypoint"));
					values.put("monitor_address", obj.getString("monPointLocation"));
					values.put("monitor_type", obj.getString("monType"));
					values.put("legal_r", obj.getDouble("legalR"));
					values.put("dimension", obj.getString("dimension"));
					values.put("monitor_content", obj.getString("monContent"));
					db.insert(table, null, values);
				}
				db.setTransactionSuccessful();// 必须显示的设置该方法,否则事务不会提交
			} finally {
				db.endTransaction();
				db.close();
			}

		} else {
			String error = object.getString("info");
			if (error == null) {
				error = "未分配该号码的监测点信息";
			}
			throw new GdimsException(error);
		}

	}
	
	/**
	 * 初始化村社表
	 * @param json
	 * @throws JSONException
	 * @throws GdimsException 
	 */
	public void initCountry(String json) throws JSONException, GdimsException {
		String table = "tab_country";
		JSONObject object = new JSONObject(json);
		String result = object.getString("result");
		String infos = object.getString("info");
		if ("1".equals(result)) {
			JSONArray array = new JSONArray(infos);
			SQLiteDatabase db = dataBase.getWritableDatabase();
			db.beginTransaction(); // 用事务的方式批量提交,但如果数量比较小也可以不开
			try {
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					ContentValues values = new ContentValues();
					values.put("town", obj.getString("adminNumber"));
					values.put("name", obj.getString("name"));

					db.insert(table, null, values);
				}
				db.setTransactionSuccessful();// 必须显示的设置该方法,否则事务不会提交
			} finally {
				db.endTransaction();
				db.close();
			}

		}else{
			String error = object.getString("info");
			if (error == null) {
				error = "初始村社信息失败，请稍后再试";
			}
			throw new GdimsException(error);
		}

	}
	
	/**
	 * 
	 * @param json
	 * @throws JSONException
	 * @throws GdimsException 
	 */
	public void initTown(String json) throws JSONException, GdimsException {
		String table = "tab_town";
		JSONObject object = new JSONObject(json);
		String result = object.getString("result");
		String infos = object.getString("info");
		if ("1".equals(result)) {
			JSONArray array = new JSONArray(infos);
			SQLiteDatabase db = dataBase.getWritableDatabase();
			db.beginTransaction(); // 用事务的方式批量提交,但如果数量比较小也可以不开
			try {
				for (int i = 0; i < array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					ContentValues values = new ContentValues();
					values.put("name", obj.getString("name"));
					values.put("num", obj.getInt("id"));
					db.insert(table, null, values);
				}
				db.setTransactionSuccessful();// 必须显示的设置该方法,否则事务不会提交
			} finally {
				db.endTransaction();
				db.close();
			}

		}else{
			String error = object.getString("info");
			if (error == null) {
				error = "初始乡镇信息失败，请稍后再试";
			}
			throw new GdimsException(error);
		}

	}

	public void close() {
		dataBase.close();
	}

}
