package com.fubangty.FGISystem.deffend.activity;

import com.fubangty.FGISystem.R;
import com.fubangty.FGISystem.deffend.http.CallHttpBiz;
import com.fubangty.FGISystem.deffend.http.ConnectionUrl;
import com.fubangty.FGISystem.deffend.http.MyCallback;
import com.fubangty.FGISystem.deffend.parsjson.LoginJson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

/**
 * 注册界面
 * @author lemon
 *
 */
public class RegisterActivity extends Activity implements MyCallback {
	private EditText etName;
	private EditText etPassword;
	private RadioButton rbHeader;
	private RadioButton rbSearcher;
	private Button btnRegister;
	private Button btnCancle;
	private String phoneId;
	//权限
	private String userType;
	private Context context;
	//电话号码
	private String phoneNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		context=this;
		SharedPreferences shared = context.getSharedPreferences("defendInfo", Context.MODE_PRIVATE);
		phoneId=shared.getString("phoneID", 0+"");
		initViews();
		setListener();
	}

	private void initViews() {
		etName = (EditText) findViewById(R.id.et_register_username);
		etPassword = (EditText) findViewById(R.id.et_register_password);
		btnRegister = (Button) findViewById(R.id.btn_2_register);
		btnCancle = (Button) findViewById(R.id.btn_cancle);
		rbHeader=(RadioButton) findViewById(R.id.rb_header);
		rbSearcher=(RadioButton) findViewById(R.id.rb_searcher);
	}

	private void setListener() {
		btnRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(etName.getText().toString().trim().equals("")|etPassword.getText().toString().trim().equals("")|((!rbHeader.isChecked())&&(!rbSearcher.isChecked()))){
					Toast.makeText(getApplicationContext(), "请输入完整信息", Toast.LENGTH_LONG).show();
				}else{
					if (rbHeader.isChecked()) {
						userType=1+"";
					}else{
						userType=2+"";
					}
					sendMessage(1);
				}
			}
		});

		btnCancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		
	}

	/**
	 * 向后台发送请求
	 * 
	 * post请求, type (1表示post,0表示get), URL 目标地址, mRequestParams 集合对象 callback
	 */
	public void sendMessage(int type) {
		System.out.println("进入了sendMessage()方法");
		CallHttpBiz call = new CallHttpBiz(this);
		RequestParams params = new RequestParams();
		params.addBodyParameter("phoneNum", etName.getText().toString().trim()); // 手机号码(登录名)
		params.addBodyParameter("userPwd", etPassword.getText().toString().trim()); // 密码
		params.addBodyParameter("phoneID", phoneId); // 手机唯一标识IMEI
		params.addBodyParameter("userType",userType);
		Log.d("limeng", "userType:"+userType);
		call.PostData(2, new ConnectionUrl().REGISTER, params, this);
	}

	@Override
	public void getJson(int type, String data) {
		switch (type) {
		case 2:
			// 判断返回数据state是否为200
			LoginJson loginJson = new LoginJson(context);
			if (loginJson.parserData(data)) {
				//
				finish();
			}
			break;

		default:
			break;
		}

	}

	@Override
	public void onFailure(HttpException exception) {
		Toast.makeText(getApplicationContext(), "注册失败！请检查网络配置或服务器", Toast.LENGTH_LONG).show();

	}

}
