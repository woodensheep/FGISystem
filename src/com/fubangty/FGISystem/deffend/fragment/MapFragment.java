package com.fubangty.FGISystem.deffend.fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.fubangty.FGISystem.R;
import com.fubangty.FGISystem.deffend.util.ServiceUtil;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MapFragment extends Fragment {

	private TextView tvLocation;
	private MapView mMapView;
	private TextView tvReport;
	private BaiduMap mBaiduMap;
	private LocationClient mLocClient;
	private MyLocationListenner myListener;
	private Context context;
	private MsgReceiver msgReceiver;
	SharedPreferences shared;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		SDKInitializer.initialize(getActivity().getApplicationContext());
		context = getActivity();
		View v = inflater.inflate(R.layout.fragment_map, null);
		mMapView = (MapView) v.findViewById(R.id.mapview);
		tvLocation = (TextView) v.findViewById(R.id.tv_location);
		tvReport = (TextView) v.findViewById(R.id.tv_map_report);
		shared = context.getSharedPreferences("defendInfo", Context.MODE_PRIVATE);
		//动态注册广播接收器  
        msgReceiver = new MsgReceiver();  
        IntentFilter intentFilter = new IntentFilter();  
        intentFilter.addAction("com.fubangty.deffend.fragment.RECEIVER");  
        context.registerReceiver(msgReceiver, intentFilter);  
		// setViews();
		initMap();
		
		setListener();
		return v;
	}

	/** 
     * 广播接收器 接受定位信息
     * @author len 
     * 
     */  
    public class MsgReceiver extends BroadcastReceiver{  
  
        @Override  
        public void onReceive(Context context, Intent intent) {  
        	BDLocation location = intent.getParcelableExtra("location");
        	LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
        	MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
        	// 设置初始大小
        	mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(18).build()));
        	mBaiduMap.animateMapStatus(u);
        	MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			tvLocation.setText(
					"纬度：" + location.getLatitude() + "， " + "经度：" + location.getLongitude() + "\n" + getSystemTime());
			if (location.getLocationDescribe() != null) {
				shared.edit().putString("Latitude", location.getLatitude()+"").commit();
				shared.edit().putString("LocationDescribe", location.getLocationDescribe()).commit();
				shared.edit().putString("Longitude", location.getLongitude()+"").commit();
				shared.edit().putString("LocationTime", getSystemTime()).commit();
				tvLocation.append("\n" + location.getLocationDescribe());

			} else {
				tvLocation.append("\n" + "网络不通畅");
			}
        }
    }  

	private void setListener() {
		tvReport.setOnClickListener(new NoDoubleClickListener() {

			@Override
			void onNoDoubleClick(View v) {
				Log.d("limeng", "mapframent:"+"click");
				getOpen();
			}
		});
	}

	/**
	 * 注册闹钟定时器
	 */
	public void getOpen() {
		if (!ServiceUtil.isServiceRunning(context, "com.fubangty.FGISystem.deffend.service.BackGroundService")) {
			ServiceUtil.invokeTimerPOIService(context);// 启动定时器
			Toast.makeText(context, "定位开启中。。。", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(context, "定位已经开启！！！", Toast.LENGTH_SHORT).show();
			System.out.println("service正在在运行...");
		}
	}

	// 获取当前时间
	private String getSystemTime() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
		String date = sDateFormat.format(new Date(System.currentTimeMillis()));
		return date;
	}

	private void initMap() {
		mBaiduMap = mMapView.getMap();
		// 普通地图
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		Double Latitude=Double.valueOf(shared.getString("Latitude", "0"));
		Double Longitude=Double.valueOf(shared.getString("Longitude", "0"));
		String LocationDescribe=shared.getString("LocationDescribe", "");
		String LocationTime=shared.getString("LocationTime", "");
		LatLng ll = new LatLng(Latitude, Longitude);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
		// 设置初始大小
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(18).build()));
		mBaiduMap.animateMapStatus(u);
		tvLocation.setText(
				"纬度：" + Latitude + "， " + "经度：" + Longitude + "\n"+LocationTime);
		if (!LocationDescribe.equals("")) {
			tvLocation.append("\n" + LocationDescribe);

		} else {
			tvLocation.append("\n" + "网络不通畅");
		}
		//initLocation();
	};

	/**
	 * 定位
	 */
	private void initLocation() {
		// 定位初始化
		mLocClient = new LocationClient(getActivity().getApplicationContext());
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
		option.setScanSpan(1000);
		// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setLocationNotify(true);
		// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationDescribe(true);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}

	@Override
	public void onDestroy() {
		//mLocClient.unRegisterLocationListener(myListener);
		//myListener = null;
		//mLocClient.stop();
		//mMapView.onDestroy();
		super.onDestroy();
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		private boolean isFirstLoc = true;

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude()).longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			tvLocation.setText(
					"纬度：" + location.getLatitude() + "， " + "经度：" + location.getLongitude() + "\n" + getSystemTime());
			if (location.getLocationDescribe() != null) {

				tvLocation.append("\n" + location.getLocationDescribe());

			} else {
				tvLocation.append("\n" + "网络不通畅");
			}
			mLocClient.unRegisterLocationListener(myListener);
			myListener = null;
			mLocClient.stop();
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				// 设置初始大小
				mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(18).build()));
				mBaiduMap.animateMapStatus(u);

			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	// 防止短时间内多次点击
	public abstract class NoDoubleClickListener implements OnClickListener {

		public static final int MIN_CLICK_DELAY_TIME = 10000;
		private long lastClickTime = 0;

		@Override
		public void onClick(View v) {
			long currentTime = Calendar.getInstance().getTimeInMillis();
			if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
				lastClickTime = currentTime;
				onNoDoubleClick(v);
			} else {
				Toast.makeText(context, "不要重复点击！！！", Toast.LENGTH_SHORT).show();
			}
		}

		abstract void onNoDoubleClick(View v);
	}
}
