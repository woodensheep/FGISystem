package com.fubangty.FGISystem.activity;

import com.fubangty.FGISystem.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * <font color="green">用户注册界面</font>
 * @ClassName RegisterActivity
 * @author Administrator
 * @date 2016年9月19日 下午2:19:42
 *
 * @version
 */
public class RegisterActivity extends Activity implements OnClickListener{
	private TextView title_content;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_register);
		initView();
	}

	//初始化控件
	private void initView() {
		title_content = (TextView) findViewById(R.id.title_content);
		title_content.setText(R.string.zhuce);
		
	}

	//点击事件
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			default :
				break;
		}
	}
	
}
