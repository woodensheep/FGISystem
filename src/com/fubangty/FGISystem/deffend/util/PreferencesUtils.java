package com.fubangty.FGISystem.deffend.util;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferencesUtils {
	
	private final static String PhoneId="PhoneId";
	private SharedPreferences share;
	
	public PreferencesUtils(SharedPreferences share) {
		super();
		this.share = share;
	}


	public static void save(Context context,String SharedPreferencesName,Map<String, String> logMap) {
		SharedPreferences preferences = context.getSharedPreferences(SharedPreferencesName, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("PhoneId", logMap.get("name"));
		editor.commit();
	}
	
	public String getIMEI(){
		return share.getString("PhoneId", "");
	}
	
	public void setIMEI(String IMEI){
		share.edit().putString("PhoneId",IMEI).commit();
	}

}
