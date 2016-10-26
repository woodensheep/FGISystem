/**
 * Copyright 2008-2012. 重庆富邦科技发展有限公司, Inc. All rights reserved.
 * <a>http://www.3gcq.cn</a>
 */
package com.fubangty.FGISystem.gdims.base;



import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

/**
 * 基础activity,提供一些日志记录等基本方法
 * @author liyun create 2012-5-27
 * @version 0.1
 *
 */
public class BaseActivity extends Activity {
	
	protected String TAG = this.getClass().getSimpleName();
	protected Context context = this;
	protected LocationListener listener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//强制为竖屏
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * activity跳转
	 * @param activity
	 */
	public void forward(Class<? extends Activity> activity) {
		Intent intent = new Intent(context, activity);
		startActivity(intent);
	}
	
	/**
	 * activity跳转
	 * @param activity 目标activity
	 * @param bundle  绑定的值
	 */
	public void forward(Class<? extends Activity> activity,Bundle bundle){
		Intent intent = new Intent(this, activity);
		intent.putExtras(bundle);
		startActivity(intent);
	}
	
	/**
	 * 提示框
	 * @param text 提示内容
	 * @param time 持续时间
	 */
	public void showToast(CharSequence text,int time){
		Toast.makeText(context, text, time ).show();
	}
	
	/**
	 * 提示框
	 * @param text 提示内容
	 */
	public void showToast(CharSequence text){
		showToast(text,Toast.LENGTH_SHORT);
	}
	
	/**
	 * 信息提示对话框
	 * @param text 提示内容
	 */
	public void alert(String text){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("信息").setMessage(text).setPositiveButton("确定", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).create().show();
	}
	
	/**
	 * 确认信息提示框
	 * @param text
	 * @param listener
	 */
	public void confirm(String text,OnClickListener listener){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(text).setIcon(R.drawable.ic_dialog_info).setPositiveButton("确定",listener).setNegativeButton("取消", listener).create().show();
	}
	
	private LocationListener getLocationListener(){
    	return new LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderEnabled(String provider) {
				showToast("当前使用的是"+provider);
				if (provider.equals(LocationManager.GPS_PROVIDER)) {
					//GPS provider
				}
				if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
					//network provider
				}
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				showToast(provider+"定位服务不可用");
				if (provider.equals(LocationManager.GPS_PROVIDER)) {
					//("定位提供：GPS不可用");
				}
				if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
					//("定位提供:WIFI不可用");
				}
				
			}
			
			@Override
			public void onLocationChanged(Location location) {
				if(location!=null){
//					if (provider.equals(LocationManager.GPS_PROVIDER)) {
//						loc_info.setText("定位提供：GPS("+location.getExtras().getInt("satellites")+")");
//					}
//					if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
//						loc_info.setText("定位提供:WIFI(粗略定位)"+location.getExtras().getInt("satellites"));
//					}
					Double la = location.getLatitude()*1E6;
					Double lo = location.getLongitude()*1E6;
				}else{
					//loc_info.setText("信号太弱无法定位");
				}
			}
		};
    }
}
