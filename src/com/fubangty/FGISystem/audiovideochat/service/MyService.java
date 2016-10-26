package com.fubangty.FGISystem.audiovideochat.service;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.fubangty.FGISystem.R;
import com.fubangty.FGISystem.audiovideochat.activity.ReceiveVideoActivity;
import com.fubangty.FGISystem.audiovideochat.entity.Message;
import com.fubangty.FGISystem.audiovideochat.http.CallHttpBiz;
import com.fubangty.FGISystem.audiovideochat.http.ConnectionUrl;
import com.fubangty.FGISystem.audiovideochat.http.MyCallback;
import com.fubangty.FGISystem.audiovideochat.parsjson.PushMsgJson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
	private CallHttpBiz biz;
	private NotificationManager manager;
	private static final int SERVICE_FOR_TASK = 1000;

	private String id;// 每条信息的id
	private String push_msg;
	private String push_userid;
	private String push_time;
	private String invite_man;
	private int room_id;
	public static List<Message> list;
	private SharedPreferences sp;
	protected String phone_id;
	private String phoneNum;
	private String areaId;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("tag", "MyService().....");
		init();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("MyService startCommand", "执行了MyService方法");
		doRequest();
		return START_STICKY;
	}

	private void init() {
		biz = new CallHttpBiz(this);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		phoneNum = sp.getString("phoneNum", "");
		areaId = sp.getString("areaId", "");
//		phoneNum = "18680934191";
//		areaId = "106";
		manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}

	public void doRequest() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = new java.util.Date();
		String time = format.format(date);

		Log.d("tag", "doRequest...开始执行：" + time);
		RequestParams params = new RequestParams();
		params.addBodyParameter("phoneNumber", phoneNum);
		params.addBodyParameter("areaId", areaId);
		System.out.println(phoneNum + "," + areaId);
		biz.PostData(1, ConnectionUrl.URL_HASVIDEO, params, new MyCallback() {
			@Override
			public void getJson(int type, String data) {
				if (data != null) {
					list = PushMsgJson.doParse(data);
					System.out.println(data);
					System.out.println("list.len:" + list.size());
					
					if (PushMsgJson.type==1 && list.size()>0) { // 视频
							id = list.get(0).getId() + "";
							push_msg = list.get(0).getPush_msg();
							push_time = list.get(0).getPush_time();
							push_userid = list.get(0).getInvite_userId() + "";
							room_id = list.get(0).getRoom_id();
							invite_man = list.get(0).getInvite_man();
							Log.d("收到消息了", "弹出通知栏"+id);
							Log.d("数据信息", "id:"+id+",UserId:"+push_userid+",roomId:"+room_id+".......");

							Intent intent = new Intent();
							intent.setClass(getApplicationContext(), ReceiveVideoActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra("MsgList", (Serializable) list);
							if(!id.equals(sp.getString("id", "-1"))){
								startActivity(intent);
//								sendNofiy();
							}
							sp.edit().putString("id", id).commit();
							System.out.println("保存的id："+sp.getString("id", "-1"));
					} else if (PushMsgJson.type==2 && list.size()>0) { // 音频
							id = list.get(0).getId() + "";
							push_msg = list.get(0).getPush_msg();
							push_time = list.get(0).getPush_time();
							push_userid = list.get(0).getInvite_userId() + "";
							room_id = list.get(0).getRoom_id();
							phone_id = list.get(0).getPhone_ids();
							Log.d("收到消息了", "弹出通知栏");
							Intent intent = new Intent();
							intent.setClass(getApplicationContext(), ReceiveVideoActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra("MsgList", (Serializable) list);
							if(!id.equals(sp.getString("id", "-1"))){
								startActivity(intent);
//								sendNofiy();
							}
							sp.edit().putString("id", id).commit();
							System.out.println("保存的id："+sp.getString("id", "-1"));
					} // else if结束
				} else {
					Log.d("getJson", "返回data为空！");
				} // 最外层if结束
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				System.out.println("arg0:" + arg0 + " arg1:" + arg1);
			}
		});

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
	}

	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void sendNofiy() {
		Log.d("tag", "消息通知设置");
		Notification.Builder builder = new Notification.Builder(this);
		// 设置图标
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setContentText("内容:" + push_msg + "时间:" + push_time);
		builder.setTicker("你有新的消息");
		builder.setDefaults(Notification.DEFAULT_SOUND);

		if (PushMsgJson.type == 1) {// 视频
			Intent i = new Intent(this, VideoSpeakService.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Log.d("数据信息", "id:"+id+",UserId:"+push_userid+",roomId:"+room_id+".......");
			i.putExtra("id", id);
			i.putExtra("UserID", push_userid);
			i.putExtra("roomID", room_id);
			i.putExtra("inviteMan", invite_man);
			
			PendingIntent pi = PendingIntent.getService(this, 100, i, PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(pi);
			Notification ntf = builder.build();
			ntf.flags = Notification.FLAG_AUTO_CANCEL;
			manager.notify(SERVICE_FOR_TASK, ntf);
			return;
		}

		if (PushMsgJson.type == 2) {// 音频
			Intent i = new Intent(this, VideoSpeakService.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			i.putExtra("id", id);
			i.putExtra("UserID", push_userid);
			i.putExtra("roomID", room_id);

			PendingIntent pi = PendingIntent.getService(this, 100, i, PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(pi);
			Notification ntf = builder.build();
			ntf.flags = Notification.FLAG_AUTO_CANCEL;
			manager.notify(SERVICE_FOR_TASK, ntf);
		} // if结束

	} // sendNofiy()结束

}
