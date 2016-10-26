package com.fubangty.FGISystem.deffend.activity;

import com.fubangty.FGISystem.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * 添加小组成员
 * @author lemon
 *
 */
public class MemberActivity extends Activity {

	/** 姓名 **/
	private EditText etMemberName;
	/** 巡查情况 **/
	private EditText etXunchaSituation;
	/** 监测工作 **/
	private EditText etMonitor;
	/** 培训、指导等其它方面工作 **/
	private EditText etOthersWork;
	/** 确定 **/
	private Button btnCommit;
	/** 取消 **/
	private Button btnCancle;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_member);
		context=this;
		initViews();
		setListener();
	}

	private void initViews() {
		etMemberName = (EditText) findViewById(R.id.et_member_name);
		etXunchaSituation = (EditText) findViewById(R.id.et_xuncha_situation);
		etMonitor = (EditText) findViewById(R.id.et_monitor);
		etOthersWork = (EditText) findViewById(R.id.et_others_work);
		btnCommit = (Button) findViewById(R.id.btn_commit);
		btnCancle = (Button) findViewById(R.id.btn_cancle);
		Intent intent = getIntent();
		etMemberName.setText(intent.getStringExtra("member_name"));
		etXunchaSituation.setText(intent.getStringExtra("xuncha_situation"));
		etMonitor.setText(intent.getStringExtra("monitor"));
		etOthersWork.setText(intent.getStringExtra("others_work"));
	}

	private void setListener() {
		
		btnCommit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 数据是使用Intent返回
				Intent intent = new Intent();
				if(isEmpty(etMemberName)||isEmpty(etXunchaSituation)||isEmpty(etMonitor)||isEmpty(etOthersWork)){
					Toast.makeText(context, "请将信息填写完整！！！", Toast.LENGTH_SHORT).show();
				}else{
				// 把返回数据存入Intent
				intent.putExtra("member_name", etMemberName.getText().toString().trim());
				intent.putExtra("xuncha_situation", etXunchaSituation.getText().toString().trim());
				intent.putExtra("monitor", etMonitor.getText().toString().trim());
				intent.putExtra("others_work", etOthersWork.getText().toString().trim());
				// 设置返回数据
				MemberActivity.this.setResult(0, intent);
				// 关闭Activity
				MemberActivity.this.finish();
				}
			}
		});

		btnCancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 数据是使用Intent返回
				Intent intent = new Intent();
				// 设置返回数据
				MemberActivity.this.setResult(-1, intent);
				MemberActivity.this.finish();
			}
		});
	}

	//判断输入框是否为空
	private Boolean isEmpty(EditText editText) {
		String string=editText.getText().toString().trim();
		if (string == null || string.length() == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 监听返回键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			// 数据是使用Intent返回
			Intent intent = new Intent();
			// 设置返回数据
			MemberActivity.this.setResult(-1, intent);
			MemberActivity.this.finish();
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
	
}
