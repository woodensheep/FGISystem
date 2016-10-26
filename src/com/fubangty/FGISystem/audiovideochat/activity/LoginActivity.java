package com.fubangty.FGISystem.audiovideochat.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.fubangty.FGISystem.R;
import com.fubangty.FGISystem.audiovideochat.dialog.SetNameDialog;
import com.fubangty.FGISystem.audiovideochat.http.CallHttpBiz;
import com.fubangty.FGISystem.audiovideochat.http.ConnectionUrl;
import com.fubangty.FGISystem.audiovideochat.http.MyCallback;
import com.fubangty.FGISystem.audiovideochat.utils.NetUtils;
import com.fubangty.FGISystem.audiovideochat.utils.image.ProcessUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * <font color="green">用户登录、注册界面</font>
 * @ClassName LoginActivity
 * @author Administrator
 * @date 2016年7月27日 下午1:52:27
 *
 * @version
 */
public class LoginActivity extends Activity implements OnClickListener, MyCallback {
	private EditText etMobile;// 登录的号码
	private Button btnSign, btnGetCode;// 登录按钮,获取验证码按钮
	private TextView tvRegister;// 注册按钮
	private String registPhone, code, IMEI;// 注册填写的号码
	private SetNameDialog dialog;
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_chat);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		//获取手机IMEI
		TelephonyManager mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		IMEI = mTm.getDeviceId();
		
		initView();
	}

	private void initView() {
		etMobile = (EditText) findViewById(R.id.et_login_mobile);
		btnSign = (Button) findViewById(R.id.btn_login_submit);
		tvRegister = (TextView) findViewById(R.id.tv_login_register);
		btnSign.setOnClickListener(this);
		tvRegister.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login_submit:
			// 登录请求，提交手机号和手机IMEI
			String phoneNumber = etMobile.getText().toString().trim();
			if(!TextUtils.isEmpty(phoneNumber)){
				ProcessUtils.showProcess(LoginActivity.this, "正在登录，请稍候……");
				CallHttpBiz call = new CallHttpBiz(this);
				String url = ConnectionUrl.URL_LOGIN;
				RequestParams mRequestParams = new RequestParams();
				mRequestParams.addBodyParameter("phoneNumber",phoneNumber);
				mRequestParams.addBodyParameter("phoneId",IMEI);
				System.out.println("登录请求"+IMEI+","+phoneNumber);
				call.PostData(6, url, mRequestParams, this);
			} else{
				Toast.makeText(LoginActivity.this, "请输入手机号码！",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.tv_login_register:
			registDialog();// 显示注册对话框
			break;
		case R.id.btn_dialog_register:
			// 注册请求，提交手机号、IMEI、验证码
			code = dialog.etCode.getText().toString().trim();
			registPhone = dialog.etPhone.getText().toString().trim();
			if (!TextUtils.isEmpty(registPhone) && !TextUtils.isEmpty(code)) {
				CallHttpBiz call = new CallHttpBiz(this);
				String url = ConnectionUrl.URL_REGISTER;
				RequestParams mRequestParams=new RequestParams();
				mRequestParams.addBodyParameter("phoneNumber",registPhone );
				mRequestParams.addBodyParameter("phoneId", IMEI);
				mRequestParams.addBodyParameter("captcha",code );
				call.PostData(5, url, mRequestParams, this);
			}else{
				Toast.makeText(this, "信息填写有误！", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.btn_register_getcode:
			// 获取验证码请求
			registPhone = dialog.etPhone.getText().toString().trim();
			if (!TextUtils.isEmpty(registPhone)) {
				if(NetUtils.isConnected(getApplication())){
					btnGetCode.setClickable(false);
					CallHttpBiz call = new CallHttpBiz(this);
					String url = ConnectionUrl.URL_REGISTER;
					RequestParams mRequestParams = new RequestParams();
					mRequestParams.addBodyParameter("phoneNumber", registPhone);
					call.PostData(4, url, mRequestParams, this);
				} else{
					Toast.makeText(this, "无网络连接，请先设置！", Toast.LENGTH_SHORT).show();
					NetUtils.openSetting(LoginActivity.this);
				}
			} else {
				Toast.makeText(this, "号码不能为空！", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				btnGetCode.setText("已发送  " + msg.obj);
				break;
			case 1:
				btnGetCode.setText("获取");
				btnGetCode.setClickable(true);
				break;
			}
		};
	};

	private void countDown() {
		new Thread() {
			int count = 60;
			public void run() {
				for (int i = 0; i < 60; i++) {
					count--;
					Message msg = handler.obtainMessage(0, count);
					handler.sendMessage(msg);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				handler.sendEmptyMessage(1);
			};
		}.start();
	}

	/*
	 * 注册对话框
	 */
	private void registDialog() {
		dialog = new SetNameDialog(this, this);
		btnGetCode = dialog.getcode;
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
		Window win = dialog.getWindow();
		win.getDecorView().setPadding(0, 0, 0, 0);
		WindowManager.LayoutParams lp = win.getAttributes();
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		win.setAttributes(lp);
	}

	@Override
	public void getJson(int type, String data) {
		switch (type) {
		case 4://获取验证码成功
			try {
				JSONObject object = new JSONObject(data);
				String msg = object.getString("message");
				String state = object.getString("status");
				if(state.equals("200")){
					Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
					countDown();
				}else{
					Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
					handler.sendEmptyMessage(1);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		case 5://注册成功
			try {
				JSONObject object = new JSONObject(data);
				String msg = object.getString("message");
				String state = object.getString("status");
				if(state.equals("200")){
					Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					etMobile.setText(registPhone);
				}else{
					Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		case 6://登录成功
			try {
				ProcessUtils.closeProcess();
				
				JSONObject object = new JSONObject(data);
				String msg = object.getString("message");
				String state = object.getString("status");
				if(state.equals("200")){
					Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
					sp.edit().putString("areaId", object.getString("area_id")).commit();
					sp.edit().putString("name", object.getString("name")).commit();
					sp.edit().putBoolean("isLogin", true).commit();
					String number = etMobile.getText().toString().trim();
					sp.edit().putString("phoneNum", number).commit();
					System.out.println(sp.getString("areaId", "登录成功！"));
//					Intent intent = new Intent(this,SelectActivity.class);
//					startActivity(intent);
					finish();
				}else{
					Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		}
	}

	@Override
	public void onFailure(HttpException exception, String str) {
		System.out.println("onFailure请求失败！");
		ProcessUtils.closeProcess();
		Toast.makeText(this, "请求失败，请检查网络或服务器！", Toast.LENGTH_SHORT).show();
		handler.sendEmptyMessage(1);
	}
}
