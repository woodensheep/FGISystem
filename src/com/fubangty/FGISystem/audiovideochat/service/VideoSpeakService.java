package com.fubangty.FGISystem.audiovideochat.service;

import com.bairuitech.anychat.AnyChatBaseEvent;
import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.fubangty.FGISystem.audiovideochat.activity.SpeakActivity;
import com.fubangty.FGISystem.audiovideochat.activity.VideoActivity;
import com.fubangty.FGISystem.audiovideochat.common.Constants;
import com.fubangty.FGISystem.audiovideochat.global.Global;
import com.fubangty.FGISystem.audiovideochat.http.CallHttpBiz;
import com.fubangty.FGISystem.audiovideochat.http.ConnectionUrl;
import com.fubangty.FGISystem.audiovideochat.http.MyCallback;
import com.fubangty.FGISystem.audiovideochat.parsjson.PushMsgJson;
import com.fubangty.FGISystem.audiovideochat.utils.ServiceUtil;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class VideoSpeakService extends Service
		implements AnyChatBaseEvent, MyCallback {

	public AnyChatCoreSDK anyChatSDK;
	private String ip;
	private String name;
	private int port;
	private int roomid;
	private String id;
	private final int LOCALVIDEOAUTOROTATION = 1; // 本地视频自动旋转控制
	private MyBroadReceiver mBroadcastReceiver;

	public VideoSpeakService() {
		Log.d("VideoSpeakService", "打开了音视频通话");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Global.stop_service);
		mBroadcastReceiver = new MyBroadReceiver();
		registerReceiver(mBroadcastReceiver, filter);
		Log.e("VideoSpeakService", "启动了服务.....");
		
	}

	private void InitConnectMsg(Intent intent) {
		SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
		//拿到缓存文件
		ip=ConnectionUrl.IP;
		port=Integer.valueOf(ConnectionUrl.PORT);
		roomid = intent.getIntExtra("roomID", 1036); //默认是视频的房间号
//
		if (PushMsgJson.type == 1) {
			name = sp.getString("name", "");
		}
		if (PushMsgJson.type == 2) {
			name = sp.getString("phoneNum", "");
		}
	}

	private void InitSDK() {
		if (anyChatSDK == null) {
			anyChatSDK = AnyChatCoreSDK.getInstance(this);
			anyChatSDK.SetBaseEvent(this);
			anyChatSDK.InitSDK(android.os.Build.VERSION.SDK_INT, 0);
			AnyChatCoreSDK.SetSDKOptionInt(
					AnyChatDefine.BRAC_SO_LOCALVIDEO_AUTOROTATION,
					LOCALVIDEOAUTOROTATION);
		}
	}

	private String targetid;
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("VideoSpeakService", "onStartCommand().....");
		InitSDK();
		InitConnectMsg(intent);
		anyChatSDK.Connect(ip, port);
		anyChatSDK.Login(name, "123");
		Log.e("服务中数据信息", "name:"+name);
		
		if (intent != null) {
			targetid = intent.getStringExtra("UserID");
			id = intent.getStringExtra("id");
			System.out.println("id:" + id);
			doRequest();
		}
		return START_NOT_STICKY;
	}

	private void doRequest() {
		CallHttpBiz call = new CallHttpBiz(this);
		RequestParams params = new RequestParams();
		params.addBodyParameter("id", id);
		System.out.println("cancleid:" + id);
		// call.PostData(1, ConnectionUrl.URL_CANCLE, params, this);
		call.PostData(1, ConnectionUrl.URL_CANCLE, params, this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		System.out.println("VideoSpeakService.stop");
		anyChatSDK.LeaveRoom(-1);
		anyChatSDK.Logout();
		anyChatSDK.Release();
		unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}

	@Override
	public void OnAnyChatConnectMessage(boolean bSuccess) {
		if (!bSuccess) {
			Log.d("VideoSpeakService", "连接失败......");
			Toast.makeText(getApplicationContext(), "网络不通畅，请稍等！", Toast.LENGTH_SHORT).show();
		} else {
			Log.d("VideoSpeakService", "连接成功......");
		}
	}

	@Override
	public void OnAnyChatLoginMessage(int dwUserId, int dwErrorCode) {

		if (dwErrorCode == 0) {
			Log.d("VideoSpeakService", "登陆成功......");
			anyChatSDK.EnterRoom(roomid, "");
		} else {
			Log.d("VideoSpeakService", "登陆失败......" + dwErrorCode);
			Log.d("VideoSpeakService", "dwUserId......" + dwUserId);
		}
	}

	@Override
	public void OnAnyChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {
		if (dwErrorCode == 0) {
			Log.d("VideoSpeakService", "房间进入成功......");
		} else {
			Log.d("VideoSpeakService", "房间进入失败......");
		}
	}
	
	boolean isUserOnline;
	@Override
	public void OnAnyChatOnlineUserMessage(int dwUserNum, int dwRoomId) {
		Log.e("VideoSpeakService", "OnAnyChatOnlineUserMessage......");
		int[] userID = anyChatSDK.GetRoomOnlineUsers(roomid);
		System.out.println("roomid:" + roomid);
		String[] sId = new String[userID.length];
		System.out.println("sid.len:" + sId.length);
		
		for (int index = 0; index < userID.length; ++index) {
			sId[index] = String.valueOf(userID[index]);
			System.out.println("sid:" + sId[index]);
		}
		for (int index = 0; index < sId.length; ++index) {
			if (targetid.equals(sId[index])) { //邀请id在线
				isUserOnline = true;
			}
		}

		if (isUserOnline) {
			Intent intent = new Intent();
			intent.putExtra("UserID", targetid);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			if (PushMsgJson.type == 1) {
				intent.setClass(this, VideoActivity.class);
			} else if (PushMsgJson.type == 2) {
				intent.setClass(this, SpeakActivity.class);
			}
			Log.e("进入房间成功", "开启通话界面。。。。。。");
			startActivity(intent);
		} else {
			Toast.makeText(VideoSpeakService.this, "targetId:" + targetid,
					Toast.LENGTH_LONG).show();
			Toast.makeText(VideoSpeakService.this, "会话邀请已过期",
					Toast.LENGTH_LONG).show();
			doRequest();
		}
	}

	@Override
	public void OnAnyChatUserAtRoomMessage(int dwUserId, boolean bEnter) {
	}

	@Override
	public void OnAnyChatLinkCloseMessage(int dwErrorCode) {

	}

	private class MyBroadReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Global.stop_service)) {
				if(ServiceUtil.isServiceRunning(getApplicationContext(), Constants.POI_SERVICE)){
					Intent i=new Intent(VideoSpeakService.this,VideoSpeakService.class);
					stopService(i);
				}
				Log.e("MyBroadReceiver", "收到stop service广播");
			}
		}
	}

	@Override
	public void getJson(int type, String data) {
		System.out.println("返回数据：" + data);
	}

	@Override
	public void onFailure(HttpException arg0, String arg1) {
		
	};
}
