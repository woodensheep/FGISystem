package com.fubangty.FGISystem.deffend.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DailyLogDBHelper extends SQLiteOpenHelper{

	public static final String CREATE_NEWS = "create table dailylog ("  
            + "id integer primary key autoincrement, "  
            + "name text, "  
            + "time text, "  
            + "company text,"  
            + "district_county text,"  
            + "township text,"  
            + "daily_work text,"  
            + "emergency_handling text)";  
	
	public DailyLogDBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL(CREATE_NEWS);
	}
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
