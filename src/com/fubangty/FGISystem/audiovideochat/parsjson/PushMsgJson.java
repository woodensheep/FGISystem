package com.fubangty.FGISystem.audiovideochat.parsjson;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fubangty.FGISystem.audiovideochat.entity.Message;

import android.util.Log;

public class PushMsgJson {
	public static int type;
	
	public static List<Message> doParse(String json){
		List<Message> lists = new ArrayList<Message>(); 
		try {
			JSONObject jsons = new JSONObject(json);
			JSONArray array = jsons.getJSONArray("data");
			Log.d("解析推送会议数据", array.length()+"");
			
			for(int i=0; i<array.length(); i++){
				Message entity = new Message();
				JSONObject jObject = array.getJSONObject(i);
				type = jObject.getInt("push_type");
				entity.setId(jObject.getInt("id"));
				entity.setInvite_man(jObject.getString("invite_man"));
				entity.setInvite_userId(jObject.getInt("invite_userId"));
				entity.setPhone_ids(jObject.getString("phone_ids"));
				entity.setPush_msg(jObject.getString("push_msg"));
				entity.setPush_time(jObject.getString("push_time"));
				entity.setPush_type(type);
				entity.setRoom_id(jObject.getInt("room_id"));
				entity.setArea_id(jObject.getString("area_id"));
				lists.add(entity);				
			}	
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return lists;
	}

}
