package com.fubangty.FGISystem.deffend.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtils {
	
	
	public static void setToast(Context context ,String msg,int time){
		Toast toast = Toast.makeText(context,
				msg, time);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}
}
