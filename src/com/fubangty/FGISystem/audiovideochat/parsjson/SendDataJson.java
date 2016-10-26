package com.fubangty.FGISystem.audiovideochat.parsjson;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

/**
 * <font color="green">解析服务器返回的数据</font>
 * @ClassName FristJson
 * @author 樊艳红
 * @date 2016年4月6日 下午7:30:44
 *
 * @version
 */
public class SendDataJson {
	public int status = -999;
	public String msg;
	public Context context;
	
	public SendDataJson(Context context){
		this.context = context;
	}
	
	/* 解析json数据 */
	public void parserData(String data){
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(data);
			status = jsonObject.optInt("status");
			msg = jsonObject.optString("message");
			if(status == 200){
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			}
			System.out.println("状态："+status);
			System.out.println("返回的消息："+msg);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
}
