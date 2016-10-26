/**
 * Copyright 2008-2012. 重庆富邦科技发展有限公司, Inc. All rights reserved.
 * <a>http://www.3gcq.cn</a>
 */
package com.fubangty.FGISystem.gdims.util;


import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * @author liyun create 2012-6-28
 * @version 0.1
 * 
 */
public class Utils {

	/**
	 * 判断是否有存储卡
	 * 
	 * @return
	 */
	public static boolean existSDcard() {
		return (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState()));
	}

	/**
	 * 判断是否有网络存在
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {

		boolean isConnection = false;
		ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); // mobile
																													// 3G
																													// Data
																													// Network
		State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		// wifi
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		// 判断3G
		if (mobile == State.CONNECTED || mobile == State.CONNECTING) {
			isConnection = true;
		}
		if (wifi == State.CONNECTED || wifi == State.CONNECTING) {
			isConnection = true;
		}
		// isConnection =
		// conMan.getActiveNetworkInfo().isConnectedOrConnecting();

		return isConnection;
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWIFIAvailable(Context context) {

		boolean flag = false;

		ConnectivityManager cwjManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cwjManager.getActiveNetworkInfo() != null) {
			flag = cwjManager.getActiveNetworkInfo().isAvailable();
		}

		return flag;

	}

	/**
	 * 判断手机运营商
	 * 
	 * @param context
	 * @return
	 */
	public static String getOperators(Context context) {
		TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		String imsi = telManager.getSubscriberId();
		if (imsi != null) {
			if (imsi.startsWith("46000") || imsi.startsWith("46002")) {// 因为移动网络编号46000下的IMSI已经用完，所以虚拟了一个46002编号，134/159号段使用了此编号
				// 中国移动
			} else if (imsi.startsWith("46001")) {
				// 中国联通
			} else if (imsi.startsWith("46003")) {
				// 中国电信
			}
		}
		return null;
	}

	/**
	 * 判断是否有3G或者WIFI网络
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isUMSorWIFI(Context context) {
		ConnectivityManager mConnectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		// 检查网络连接，如果无网络可用，就不需要进行连网操作等
		NetworkInfo info = mConnectivity.getActiveNetworkInfo();

		if (info == null || !mConnectivity.getBackgroundDataSetting()) {
			return false;
		}

		// 判断网络连接类型，只有在3G或wifi里进行一些数据更新。
		int netType = info.getType();
		int netSubtype = info.getSubtype();

		if (netType == ConnectivityManager.TYPE_WIFI) {
			return info.isConnected();
		} else if (netType == ConnectivityManager.TYPE_MOBILE && netSubtype == TelephonyManager.NETWORK_TYPE_UMTS && !mTelephony.isNetworkRoaming()) {
			return info.isConnected();
		} else {
			return false;
		}
	}

	/**
	 * 判断是否有sim卡
	 * 
	 * @param context
	 * @return
	 */
	public static boolean hasSIM(Context context) {
		try {
			TelephonyManager mgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

			return TelephonyManager.SIM_STATE_READY == mgr.getSimState();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	/**
	 * 获取SIM卡电话号码
	 * 
	 * @param context
	 * @return
	 */
	public static String getNumber(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String number = tm.getLine1Number();
		return number;
	}

	public static String getImsi(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = tm.getSubscriberId();
		return imsi;
	}
	
	/**
	 * 获取手机IMEI信息
	 * @param context
	 * @return
	 */
	public static String getImei(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tm.getDeviceId();
		return imei;
	}
	
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public static int getVerCode(Context context) {
		int verCode = -1;
		try {
			verCode = context.getPackageManager().getPackageInfo("com.fbty.gdims.mobile", 0).versionCode;
		} catch (NameNotFoundException e) {
			Log.e("version", e.getMessage());
		}
		return verCode;
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public static String getVerName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo("com.fbty.gdims.mobile", 0).versionName;
		} catch (NameNotFoundException e) {
			Log.e("version", e.getMessage());
		}
		return verName;
	}
}
