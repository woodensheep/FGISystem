package com.fubangty.FGISystem.deffend.activity;

import com.fubangty.FGISystem.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * 日志已上报和未上报界面
 * 
 * @author lemon
 *
 */
public class HeaderActivity extends Activity {
	
	private Context context;
	private Button btnWeekly;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_header);
		btnWeekly=(Button) findViewById(R.id.btn_header_weekly);
		context = this;
		setListener();
	}

	private void setListener() {
		btnWeekly.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, WeeklyActivity.class);
				startActivity(intent);
			}
		});
		
	}

}
