/**
 * Copyright 2008-2012. 重庆富邦科技发展有限公司, Inc. All rights reserved.
 * <a>http://www.3gcq.cn</a>
 */
package com.fubangty.FGISystem.gdims.dialog;

import android.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/**
 * @author liyun create 2012-6-24
 * @version 0.1
 *
 */
public class MyAlert {
	
	private Context context;
	
	public MyAlert(Context context){
		this.context = context;
	}
	
	public void alert(CharSequence message){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setPositiveButton("确定", new OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
			
		}).setMessage(message).create().show();
	}
	

}
