package com.fubangty.FGISystem.audiovideochat.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * <font color="green">网络相关辅助类</font>
 * @ClassName NetUtils
 * @author Administrator
 * @date 2016年7月20日 上午10:36:11
 *
 * @version
 */
public class NetUtils {

	private NetUtils() {
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	/* 判断网络是否连接 */
	public static boolean isConnected(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (null != manager) {
			NetworkInfo info = manager.getActiveNetworkInfo();
			if (null != info && info.isConnected()) {
				if (info.getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	/* 打开网络设置界面 */
	public static void openSetting(Activity activity) {
		Intent intent = null;
		if(android.os.Build.VERSION.SDK_INT > 10){ //Android 3.0以上
			intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
		} else{
			intent = new Intent();
			ComponentName cm = new ComponentName("com.android.settings",
					"com.android.settings.Settings");
			intent.setComponent(cm);
			intent.setAction("android.intent.action.VIEW");
		}
		activity.startActivity(intent);
	}
}
