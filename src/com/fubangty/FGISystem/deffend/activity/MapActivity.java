package com.fubangty.FGISystem.deffend.activity;

import com.fubangty.FGISystem.R;
import com.fubangty.FGISystem.deffend.service.BackGroundService;
import com.fubangty.FGISystem.deffend.util.ServiceUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * 后台定位取消界面，通过Notification进入
 * @author lemon
 *
 */
public class MapActivity extends Activity {

	private Button btncanclemap;
	private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		context=this;
		btncanclemap = (Button) findViewById(R.id.btn_map_cancle);
		setlistener();
	}

	private void setlistener() {
		btncanclemap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog();
			}
		});
	}

	/**
	 * dialog显示判断删除与否，根据id号删除
	 */
	private void dialog() {
		// 先new出一个监听器，设置好监听
		DialogInterface.OnClickListener dialogOnclicListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case Dialog.BUTTON_POSITIVE:
					Log.d("limeng", "userType:");
					Log.d("limeng", "MapActivity-finish");
					cancle();
					break;
				case Dialog.BUTTON_NEGATIVE:
					break;
				}
			}
		};
		// dialog参数设置
		AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this); // 先得到构造器
		builder.setTitle("提示"); // 设置标题
		builder.setMessage("是否取消定位?"); // 设置内容
		builder.setIcon(R.drawable.main_map_pressed);// 设置图标，图片id即可
		builder.setPositiveButton("确认", dialogOnclicListener);
		builder.setNegativeButton("取消", dialogOnclicListener);
		builder.create().show();
	}

	private void cancle() {
		Log.d("limeng", "cancle"+ServiceUtil.isServiceRunning(context, "com.fubangty.FGISystem.deffend.service.BackGroundService"));
		// 取消上传service 
		if (ServiceUtil.isServiceRunning(context, "com.fubangty.FGISystem.deffend.service.BackGroundService")) {
			ServiceUtil.cancleAlarmManager(context);// 取消定时器
			Intent service = new Intent(context, BackGroundService.class);
			context.stopService(service);
			finish();
//			NotificationManager mNotificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE); ;
//			mNotificationManager.cancel(12346);
		}

	}
}
