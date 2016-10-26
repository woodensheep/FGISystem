package com.fubangty.FGISystem.audiovideochat.parsjson;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

public class AreaIdJson {
	public int status = -999;
	public String msg;
	public String areaid;
	public Context context;
	
	public AreaIdJson(Context context){
		this.context = context;
	}
	
	/*
	 * 解析json数据
	 */
	public void parserData(String datas){
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(datas);
			status = jsonObject.optInt("status");
			if(status == 200){
				JSONObject josn= (JSONObject)jsonObject.getJSONObject("data");
				areaid = josn.getString("specified_num");
			}else{
				msg = jsonObject.optString("message");
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			}
			System.out.println("状态："+status);
			System.out.println("返回的消息："+msg);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
}
