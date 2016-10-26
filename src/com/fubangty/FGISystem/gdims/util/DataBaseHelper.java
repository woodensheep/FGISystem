package com.fubangty.FGISystem.gdims.util;


import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

//DatabaseHelper作为一个访问SQLite的助手类，提供两个方面的功能，
//第一，getReadableDatabase(),getWritableDatabase()可以获得SQLiteDatabse对象，通过该对象可以对数据库进行操作
//第二，提供了onCreate()和onUpgrade()两个回调函数，允许我们在创建和升级数据库时，进行自己的操作

public class DataBaseHelper extends SQLiteOpenHelper {

	private static final int VERSION = 1;
	private static final String DB_NAME = "gdims";

	private String Tab;

	// 在SQLiteOepnHelper的子类当中，必须有该构造函数
	public DataBaseHelper(Context context, String name, CursorFactory factory, int version) {

		// 必须通过super调用父类当中的构造函数
		super(context, name, factory, version);
	}

	public DataBaseHelper(Context context, String name, int version) {
		this(context, name, null, version);
	}

	public DataBaseHelper(Context context, String name) {
		this(context, name, VERSION);
		Log.d("databasehelp", name);
		Resources r = context.getResources();
	}

	public DataBaseHelper(Context context) {
		this(context, DB_NAME, VERSION);
	}

	// 该函数是在第一次创建数据库的时候执行,实际上是在第一次得到SQLiteDatabse对象的时候，才会调用这个方法
	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("databasehelp", db.getPath());
		//execSQL函数用于执行SQL语句 根据表明name创建，可以利用发射动态创建，目前先写死测试创建数据库
		db.execSQL("create table tab_monitor (id  integer unique  PRIMARY KEY AUTOINCREMENT , disaster_no varchar(100), monitor_name varchar(100), monitor_no varchar(100),longitude varchar(100),latitude varchar(100),monitor_address varchar(100),monitor_type varchar(100),legal_r  double(20),dimension varchar(50),monitor_content varchar(100));");
		db.execSQL("create table tab_config (id integer unique PRIMARY KEY AUTOINCREMENT , report varchar(250), context varchar(550));");
		db.execSQL("create table tab_disaster(id integer unique  PRIMARY KEY AUTOINCREMENT ,dis_name varchar(100),dis_no varchar(100),longitude varchar(50),latitude  varchar(50),legal_r  double(20),dis_type varchar(32),macro_context varchar(256));");
		db.execSQL("create table tab_macro_data(id integer unique PRIMARY KEY AUTOINCREMENT,dis_no varchar(50),num integer,content varchar(150),file varchar(150));");
		db.execSQL("create table tab_setting(id integer unique PRIMARY KEY AUTOINCREMENT,ip_address varchar(250),port integer,mobile varchar(250));");
		db.execSQL("create table tab_country(id integer unique PRIMARY KEY AUTOINCREMENT,name varchar(250),town integer);");
		db.execSQL("create table tab_town(id integer unique PRIMARY KEY AUTOINCREMENT,num integer,name varchar(250));");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		 db.execSQL("DROP TABLE IF EXISTS tab_monitor");   
		 db.execSQL("DROP TABLE IF EXISTS tab_config");   
		 db.execSQL("DROP TABLE IF EXISTS tab_disaster");
		 db.execSQL("DROP TABLE IF EXISTS tab_macro_data");
		 db.execSQL("DROP TABLE IF EXISTS tab_setting");
		 db.execSQL("DROP TABLE IF EXISTS tab_country");
		 db.execSQL("DROP TABLE IF EXISTS tab_town");
		 onCreate(db);
	}

}
