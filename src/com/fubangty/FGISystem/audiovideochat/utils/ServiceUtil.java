package com.fubangty.FGISystem.audiovideochat.utils;

import java.util.List;

import com.fubangty.FGISystem.audiovideochat.common.Constants;
import com.fubangty.FGISystem.audiovideochat.service.MyService;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ServiceUtil {

	// 判断service是否处于运行状态
	public static boolean isServiceRunning(Context context, String className) {
		boolean isRunning = false;
		
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceInfos = activityManager
				.getRunningServices(Constants.RETRIVE_SERVICE_COUNT);

		if (null == serviceInfos || serviceInfos.size() < 1) {
			return false;
		}

		for (int i = 0; i < serviceInfos.size(); i++) {
			if (serviceInfos.get(i).service.getClassName().contains(className)) {
				isRunning = true;
				break;
			}
		}
		Log.d("Tag", className + " isRunning =  " + isRunning);
		return isRunning;
	}

	// 启动定时器
	public static void invokeTimerPOIService(Context context) {
		Log.e("Tag", "启动定时任务....");
		PendingIntent alarmSender = null;
		Intent starti = new Intent(context, MyService.class);
		try {
			alarmSender = PendingIntent.getService(context, 0, starti,
					PendingIntent.FLAG_UPDATE_CURRENT);
		} catch (Exception e) {
			Log.d("Tag", "定时任务启动失败 " + e.toString());
		}
		AlarmManager am = (AlarmManager) context
				.getSystemService(Activity.ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
				Constants.ELAPSED_TIME, alarmSender);
	}

	// 解除定时器
	public static void cancleAlarmManager(Context context) {
		Log.e("Tag", "取消定时任务... ");
		Intent endi = new Intent(context, MyService.class);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0,
				endi, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarm = (AlarmManager) context
				.getSystemService(Activity.ALARM_SERVICE);
		alarm.cancel(pendingIntent);
	}

}
