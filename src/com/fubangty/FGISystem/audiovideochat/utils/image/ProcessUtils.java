package com.fubangty.FGISystem.audiovideochat.utils.image;

import android.app.ProgressDialog;
import android.content.Context;

public class ProcessUtils {
	
	static ProgressDialog progress;
	
	public static void showProcess(Context activity,String msg){
		if(progress==null){
			progress = new ProgressDialog(activity);
			progress.setCanceledOnTouchOutside(false);
			progress.setMessage(msg);
			progress.show();
		} else{
			closeProcess();
		}
	}

	public static void closeProcess(){
		if(progress!=null){
			progress.cancel();
			progress = null;
			System.gc();
		}
	}
}
