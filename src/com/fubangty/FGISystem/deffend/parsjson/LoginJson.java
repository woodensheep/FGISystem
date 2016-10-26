package com.fubangty.FGISystem.deffend.parsjson;

import org.json.JSONException;
import org.json.JSONObject;

import com.fubangty.FGISystem.deffend.util.ToastUtils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
/**
 * 上传后，服务端返回的数据解析
 * @author lemon
 *
 */
public class LoginJson {
	public int status = -999;
	public String msg;
	public String areaid;
	public Context context;
	
	public LoginJson(Context context){
		this.context = context;
	}
	
	/*
	 * 解析json数据
	 */
	public Boolean parserData(String datas){
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(datas);
			Log.d("limeng", "jsonObject:"+jsonObject.toString());
			status = jsonObject.optInt("status");
			System.out.println("DATAS"+datas);
			if(status == 200){
				msg = jsonObject.optString("message");
				ToastUtils.setToast(context, msg, Toast.LENGTH_SHORT);
				Log.d("message-0:", msg);
				return true;
			}else{
				msg = jsonObject.optString("message");
				Log.d("message-1:", msg);
				ToastUtils.setToast(context, msg, Toast.LENGTH_SHORT);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
