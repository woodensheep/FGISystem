package com.fubangty.FGISystem.audiovideochat.receiver;

import com.fubangty.FGISystem.audiovideochat.common.Constants;
import com.fubangty.FGISystem.audiovideochat.utils.ServiceUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {

		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) || // 开机启动监测
				intent.getAction().equals(Intent.ACTION_USER_PRESENT)) { // 屏幕解锁监测
			if (!ServiceUtil.isServiceRunning(context, Constants.POI_SERVICE)) {
				ServiceUtil.invokeTimerPOIService(context);
			}
		}
	}

}
