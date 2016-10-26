package com.fubangty.FGISystem.deffend.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.model.LatLng;
import com.fubangty.FGISystem.R;
import com.fubangty.FGISystem.deffend.activity.MapActivity;
import com.fubangty.FGISystem.deffend.http.CallHttpBiz;
import com.fubangty.FGISystem.deffend.http.MyCallback;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;

/**
 * 开启后台定位，上传经纬度的Service
 * @version
 */
public class BackGroundService extends Service implements MyCallback {
	private LocationClient mLocClient;
	private MyLocationListenner myListener;

	@Override
	public void onCreate() {
		// 置为前台
		sendToFace();
		System.out.println("置为前台...");
		Toast.makeText(getApplicationContext(), "后台定位开始...", 0).show();
		initLocation();
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	/**
	 * 置为前台，提高进程优先级，除非内存极度不够的时候才会被杀死
	 */
	public void sendToFace() {
		Intent notificationIntent = new Intent(this, MapActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		Notification noti = new Notification.Builder(this).setContentTitle("地勘助手定位！")
				.setContentText("定位系统正在运行...关闭请点击进入!").setSmallIcon(R.drawable.logo).setContentIntent(pendingIntent)
				.build();
		startForeground(12346, noti);
	}

	/**
	 * 向后台发送请求
	 * 
	 * post请求, type (1表示post,0表示get), URL 目标地址, mRequestParams 集合对象 callback
	 * 传递this，底层已经实现了接口回调 type：经纬度类型，0代表是手机GPS；1代表百度定位
	 */
	public void sendMessage(int type, double lon, double lat) {
		System.out.println("进入了sendMessage()方法");
		SharedPreferences shared = getApplication().getSharedPreferences("config_file", Context.MODE_PRIVATE);
		CallHttpBiz call = new CallHttpBiz(this);
		RequestParams params = new RequestParams();
		params.addBodyParameter("phoneNum", shared.getString("phoneNum", "0"));
		params.addBodyParameter("lon", lon + ""); //经度
		params.addBodyParameter("lat", lat + ""); //纬度
		call.PostData(2, com.fubangty.FGISystem.http.ConnectionUrl.URL_POSITION, params, this);
//		call.PostData(2, new ConnectionUrl().UPMAP, params, this);
	}

	/**
	 * 访问服务器成功后的回调方法
	 */
	@Override
	public void getJson(int type, String data) {
		switch (type) {
		case 2:
			try {
				JSONArray message = new JSONObject(data).getJSONArray("data");
				JSONObject json = message.getJSONObject(0);
				String status = json.getString("status");
				String msg = json.getString("message");
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 访问服务器失败后的回调方法
	 */
	@Override
	public void onFailure(HttpException exception) {
		Toast.makeText(getApplicationContext(), "上传失败，请检查服务器！", Toast.LENGTH_SHORT).show();
		Log.d("limeng", "上传失败！服务器无法访问");
	}

	/**
	 * 系统kill service时都没机会执行onDestroy
	 */
	@Override
	public void onDestroy() {
		System.out.println("BackGroundService被系统回收");
		mLocClient.unRegisterLocationListener(myListener);
		myListener = null;
		mLocClient.stop();
		// Intent localIntent = new Intent();
		// localIntent.setClass(this, BackGroundService.class); //
		// 销毁时重新启动Service
		// System.out.println("销毁时重新启动Service");
		// this.startService(localIntent);
	}

	private void initMap() {
		initLocation();
	};

	/**
	 * 定位
	 */
	private void initLocation() {
		// 定位初始化
		mLocClient = new LocationClient(getApplication());
		myListener = new MyLocationListenner();
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		// 高精度定位模式：这种定位模式下，会同时使用网络定位和GPS定位，优先返回最高精度的定位结果；
		// 低功耗定位模式：这种定位模式下，不会使用GPS，只会使用网络定位（Wi-Fi和基站定位）；
		// 仅用设备定位模式：这种定位模式下，不需要连接网络，只使用GPS进行定位，这种模式下不支持室内环境的定位。
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000*60*1);
		// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setLocationNotify(false);
		// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationDescribe(true);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}

	// 获取当前时间
	private String getSystemTime() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
		String date = sDateFormat.format(new Date(System.currentTimeMillis()));
		return date;
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		private boolean isFirstLoc = true;

		@Override
		public void onReceiveLocation(BDLocation location) {

			if (location.getLocationDescribe() != null) {
				Intent intent = new Intent("com.fubangty.deffend.fragment.RECEIVER");
				// 发送Action为com.example.communication.RECEIVER的广播
				intent.putExtra("location", location);
				sendBroadcast(intent);
				sendMessage(2, location.getLongitude(), location.getLatitude());
				// tvLocation.append("\n" + location.getLocationDescribe());
			} else {
				// tvLocation.append("\n" +"网络不通畅");
			}

			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

}
