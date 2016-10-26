
package com.fubangty.FGISystem.gdims.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;

import com.fubangty.FGISystem.R;
import com.fubangty.FGISystem.gdims.base.BaseActivity;
import com.fubangty.FGISystem.gdims.util.GobalApplication;
import com.fubangty.FGISystem.gdims.util.UpdateManager;

/**
 * @author liqingsong
 * @version 0.1
 * 
 */
public class MainActivity extends BaseActivity implements OnClickListener {
	
	private String SYNC_VERSION;
	private LinearLayout linear1,linear2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		System.out.println("进入主页面。。。。。");
		linear1=(LinearLayout) findViewById(R.id.liner1);
		linear2=(LinearLayout) findViewById(R.id.liner2);
		linear1.setOnClickListener(this);
		linear2.setOnClickListener(this);

		
		GobalApplication gobal = (GobalApplication)getApplication();
		String ipAddress = gobal.getIpAddress();
		String port = gobal.getPort();
		
//		SYNC_VERSION = "http://"+ipAddress+":"+port+"/meteor/down/version.json";
//		SYNC_VERSION = "http://192.168.212.52:8080/system/haveNewVersion.do";
		
//		UpdateManager update = new UpdateManager(context);
//		update.checkVersion(SYNC_VERSION);
//		update.setApkUrl("http://"+ipAddress+":"+port+"/meteor/down/");
//		update.setApkUrl("192.168.212.52:8080/system/downloadApk.do");
	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.liner1:
			forward(TreeMenuActivity.class);
			
			break;
		case R.id.liner2:
			forward(ReportActivity.class);
			
			break;

		default:
			break;
		}
		
	}

}
