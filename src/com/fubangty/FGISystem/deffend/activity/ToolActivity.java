package com.fubangty.FGISystem.deffend.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.fubangty.FGISystem.R;

public class ToolActivity extends Activity {
	private ImageView ibback;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tool);
		ibback= (ImageView) findViewById(R.id.iv_tool_back);
		ibback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
