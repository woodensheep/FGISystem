package com.fubangty.FGISystem.deffend.util;

import java.text.SimpleDateFormat;
import java.util.List;

import com.fubangty.FGISystem.deffend.service.BackGroundService;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class ServiceUtil {
	
	//判断service是否处于运行状态
	public static boolean isServiceRunning(Context context, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceInfos = activityManager.getRunningServices(100);

        if(null == serviceInfos || serviceInfos.size() < 1) {
            return false;
        }

        for(int i = 0; i < serviceInfos.size(); i++) {
        	Log.d("limeng", i+":"+serviceInfos.get(i).service.getClassName());
            if(serviceInfos.get(i).service.getClassName().contains(className)) {
                isRunning = true;
                break;
            }
        }
        Log.e("Tag", className + " isRunning =  " + isRunning);
        System.out.println("当前类："+className+"，当前运行状态："+isRunning);
        return isRunning;
    }

	//启动定时器
    public static void invokeTimerPOIService(Context context){
        Log.e("Tag", "启动定时任务...." );
        PendingIntent alarmSender = null;
        Intent starti= new Intent(context, BackGroundService.class);
        try {
            alarmSender =  PendingIntent.getService(context, 0, starti,
    				PendingIntent.FLAG_CANCEL_CURRENT);
        } catch (Exception e) {
            Log.e("Tag", "定时任务启动失败 " + e.toString());
        }
        System.out.println("到闹钟服务了");
        System.out.println("闹钟服务时间："+new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(System.currentTimeMillis()));
        AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),120* 1000, alarmSender);
    }

    
    //解除定时器
    public static void cancleAlarmManager(Context context){
        Log.e("Tag", "取消定时任务... ");
        Intent endi = new Intent(context,BackGroundService.class);
        PendingIntent pendingIntent= PendingIntent.getService(context, 0, endi,
				PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarm=(AlarmManager)context.getSystemService(Activity.ALARM_SERVICE);
        alarm.cancel(pendingIntent);
    }

}
