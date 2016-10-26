package com.fubangty.FGISystem.gdims.dao;

import com.fubangty.FGISystem.gdims.util.DataBaseHelper;

import android.content.Context;

public  abstract class BaseDao {
	
	private DataBaseHelper dataBaseHelper;
	
	public BaseDao(Context context) {
		dataBaseHelper = new DataBaseHelper(context);
	}
	
	
	public abstract void close();
	
	
	public abstract Object doInData();
	
	
	public abstract void call();
	
}
