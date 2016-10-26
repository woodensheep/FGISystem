/**
 * Copyright 2008-2012. 重庆富邦科技发展有限公司, Inc. All rights reserved.
 * <a>http://www.3gcq.cn</a>
 */
package com.fubangty.FGISystem.gdims.util;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

/**
 * @author liyun create 2012-6-19
 * @version 0.1
 * 
 */
public class LocationHelper {

	private LocationManager manager;

	private Context context;

	private LocationHandler handler;

	private String netWorkProvider ;
	
	private String gpsProvider;

	private long minTime = 10000;

	private int minDistance = 0;
	//判断当前是否任在使用网络定位，用于判断是否remove listener
	private boolean isNetWork;
	
	private MyListener netWorkListener;
	
	private MyListener gpsListener;

	public LocationHelper(Context context, LocationHandler handler) {
		this.context = context;
		this.handler = handler;
	}

	class MyListener implements LocationListener {

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		@Override
		public void onProviderEnabled(String provider) {
			Toast.makeText(context, provider + "启用成功", Toast.LENGTH_SHORT).show();
		}

		@Override
		public void onProviderDisabled(String provider) {
			//当GPS不可以用的时候才提醒
			if(provider.equals(LocationManager.GPS_PROVIDER)){
				Toast.makeText(context, "当前" + provider + "不可用,请在设置里面开启", Toast.LENGTH_SHORT).show();
			}
			
		}

		@Override
		public void onLocationChanged(Location location) {
			
			if (location != null) {
//				Double la = location.getLatitude() * 1E6;
//				Double lo = location.getLongitude() * 1E6;
//				Toast.makeText(context, "获取经纬度成功,经度:" + la + "|纬度:" + lo,2000).show();
				if(location.getProvider().equals(LocationManager.NETWORK_PROVIDER)){
					if(netWorkListener!=null){
						manager.removeUpdates(netWorkListener);
						isNetWork = false;
					}
					
				}	
				
				handler.onLocationChanged(location);
			}
		}
	};

	private Listener status = new Listener() {

		@Override
		public void onGpsStatusChanged(int event) {
			GpsStatus status = manager.getGpsStatus(null);
			switch (event) {
				case GpsStatus.GPS_EVENT_FIRST_FIX:
					Toast.makeText(context, status.getTimeToFirstFix() + " time", 2000).show();
					break;
				case GpsStatus.GPS_EVENT_SATELLITE_STATUS:

					Toast.makeText(context, status.getMaxSatellites(), 2000).show();
					break;
				case GpsStatus.GPS_EVENT_STARTED:
					Toast.makeText(context, "GPS_EVENT_STARTED", 2000).show();
					break;

				case GpsStatus.GPS_EVENT_STOPPED:
					Toast.makeText(context, "GPS_EVENT_STOPPED", 2000).show();
					break;
			}

		}
	};
	
	/**
	 * 初始化location manager
	 */
	public void initManager() {
		// Acquire a reference to the system Location Manager
		manager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
		
		
		boolean wifi = Utils.isWIFIAvailable(context);
		
		
		if(netWorkProvider== null){
			//provider = LocationManager.GPS_PROVIDER;
//			Criteria criteria = new Criteria();
//	        criteria.setAccuracy(Criteria.ACCURACY_FINE);
//	        criteria.setAltitudeRequired(false);
//	        criteria.setBearingRequired(false);
//	        criteria.setCostAllowed(true);
//	        criteria.setPowerRequirement(Criteria.POWER_LOW);
//	        provider = manager.getBestProvider(criteria, true);
			
			if(wifi){ // 如果有网络可以先使用wifi定位
				netWorkProvider = LocationManager.NETWORK_PROVIDER;
				//先进行一次粗略的定位
				netWorkListener =  new MyListener();
				manager.requestLocationUpdates(netWorkProvider, minTime, minDistance, netWorkListener);
				isNetWork = true;
			}
			
		}
		//同时启用GPS定位,如果GPS不可用调至GPS开关设置页面
		if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			
			Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if(location!=null){
				handler.lastLocation(location);
			}
			
			gpsListener  = new MyListener();
			manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, gpsListener);
		}else{
			Toast.makeText(context, "请先打开GPS定位设置", 2000 ).show();
			context.startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));

		}
		
		
	}

	/**
	 * 关闭GPS服务
	 */
	public void shutDownManger() {
		if (manager != null) {
			if(isNetWork){
				manager.removeUpdates(netWorkListener);
			}
			manager.removeUpdates(gpsListener);
			if(status!=null){
				manager.removeGpsStatusListener(status);
			}
			manager = null;
		}

	}


	public long getMinTime() {
		return minTime;
	}

	public void setMinTime(long minTime) {
		this.minTime = minTime;
	}

	public int getMinDistance() {
		return minDistance;
	}

	public void setMinDistance(int minDistance) {
		this.minDistance = minDistance;
	}
	
	public interface LocationHandler {

		/**
		 * 当GPS有信号时,按照设置的时间和变化的位置回调此方法
		 * 
		 * @param location
		 *            当前GPS信息,经度纬度高度时间等
		 */
		public void onLocationChanged(Location location);

		/**
		 * 获取的上一次GPS缓存的信息,可根据回调的时间来判断此数据是否有效
		 * @param location GPS缓存的信息
		 * 
		 */
		public void lastLocation(Location location);
		/**
		 * 从开启GPS到第一次获取GPS信号
		 * @param status GPS状态,可以通过次函数获得一些状态信息
		 */
		public void onGpsEventFix(GpsStatus status);
		
		/**
		 * GPS卫星状态发生改变时回调此函数
		 * @param status GPS状态,可以通过次函数获得一些状态信息
		 */
		public void onGpsSatelliteStatus(GpsStatus status);
	}

}

