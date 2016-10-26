package com.fubangty.FGISystem.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources.Theme;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fubangty.FGISystem.R;
import com.fubangty.FGISystem.audiovideochat.http.CallHttpBiz;
import com.fubangty.FGISystem.audiovideochat.http.MyCallback;
import com.fubangty.FGISystem.gdims.base.BaseActivity;
import com.fubangty.FGISystem.gdims.dao.InitDataDao;
import com.fubangty.FGISystem.gdims.dao.TabSettingDao;
import com.fubangty.FGISystem.gdims.dialog.MyAlert;
import com.fubangty.FGISystem.gdims.dialog.SetIPDialog;
import com.fubangty.FGISystem.gdims.exception.GdimsException;
import com.fubangty.FGISystem.gdims.util.GobalApplication;
import com.fubangty.FGISystem.gdims.util.Utils;
import com.fubangty.FGISystem.http.ConnectionUrl;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;

/**
 * <font color="green">用户登录界面</font>
 * @ClassName LoginActivity
 * @author Administrator
 * @date 2016年9月19日 下午2:19:26
 *
 * @version
 */
public class LoginActivity extends BaseActivity implements OnClickListener, MyCallback {
	private ProgressDialog pd;
	private InitDataDao dataDao;
	private String ipAddress="192.168.3.123"; //183.230.108.112
	private String port="8080"; //80
	private String imei;
	private int role_type; //用户类型
	private String roleStr;
	private String[] roles = new String[]{"群测群防人员", "驻守地质队员"};
	private String SYNC_MONITOR;
	private String SYNC_MACRO;
	private String SYNC_FUNCTION;
	private String SYNC_TOWN;
	private String SYNC_COUNTRY;
	private String SYNC_VERSION;
	private String phoneNum, pswd;
	private EditText et_user,et_pswd;
	private Button btn_selectRole,btn_login;
	private TextView title_content,tv_type;
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_login);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		initView();
		selectRoleDialog();
	}

	//弹出用户类型选择对话框
	private void selectRoleDialog() {
		roleStr = "群测群防人员";
		AlertDialog.Builder builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle("请选择用户类型");
		//默认选中"群测群防人员"
		builder.setSingleChoiceItems(roles, 0, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				roleStr = roles[which];
			}
		});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(roleStr.equals("群测群防人员")){
					role_type = 0;
				} else if(roleStr.equals("驻守地质队员")){
					role_type = 1;
				}
				tv_type.setText(roleStr);
				System.out.println("选择的用户类型:"+role_type);
			}
		});
		builder.setCancelable(false);
		builder.create().show();
	}

	//初始化控件
	private void initView() {
		et_user = (EditText) findViewById(R.id.et_login_user);
		et_pswd = (EditText) findViewById(R.id.et_login_pswd);
		btn_login = (Button) findViewById(R.id.btn_login);
		btn_selectRole = (Button) findViewById(R.id.btn_selectRole);
		title_content = (TextView) findViewById(R.id.title_content);
		tv_type = (TextView) findViewById(R.id.tv_type);
		btn_login.setOnClickListener(this);
		btn_selectRole.setOnClickListener(this);
		title_content.setText(R.string.denglu);
		
		et_user.setText(sp.getString("phoneNum", ""));
		if(sp.getInt("roletype", -1) == 0){
			tv_type.setText("群测群防人员");
		} else if(sp.getInt("roletype", -1) == 1){
			tv_type.setText("驻守地质队员");
		}
	}

	//点击事件
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_login : //登录
				phoneNum = et_user.getText().toString().trim();
				pswd = et_pswd.getText().toString().trim();
				if(isEditTextEmpty()){
					sendMessage(role_type, ConnectionUrl.URL_LOGIN);
				}
				break;
			case R.id.btn_selectRole :
				selectRoleDialog();
				break;
			default :
				break;
		}
	}
	
	//判断输入框内容是否为空
	private boolean isEditTextEmpty(){
		if(TextUtils.isEmpty(et_user.getText()) || TextUtils.isEmpty(et_pswd.getText())){
			Toast.makeText(getApplicationContext(), "手机号或密码不能为空！", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(roleStr.equals("无")){
			Toast.makeText(getApplicationContext(), "必须选择用户类型！", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	/**
	 * post方式向后台发送请求
	 * 
	 * type 每次请求的标记, URL 目标地址, mRequestParams 集合对象, callback 回调
	 */
	public void sendMessage(int type, String url){
		System.out.println("进入了sendMessage()方法");
		System.out.println("手机号："+phoneNum+"，密码："+pswd+"，类型："+role_type);
		CallHttpBiz call = new CallHttpBiz(this);
		RequestParams params = new RequestParams();
		params.addBodyParameter("mobile", phoneNum);
		params.addBodyParameter("password", pswd);
		params.addBodyParameter("roleId", role_type+"");
		call.PostData(type, url, params, this);
	}
	
	private void getGobal() {
		TabSettingDao dao = new TabSettingDao(context);
		dao.saveSetting(ipAddress,port,phoneNum);
		dao.close();
		Map<String, String> map = initConfig();
		imei = Utils.getImei(context);
		ipAddress = map.get("ipAddress");
		port = map.get("port");
		phoneNum = map.get("mobile");
		GobalApplication gobal = (GobalApplication) getApplication();
		gobal.setIpAddress(ipAddress);
		gobal.setMobile(phoneNum);
		gobal.setPort(port);
		SYNC_MONITOR = "http://" + ipAddress + ":" + port + "/data_service/findMonitor.do";
		SYNC_MACRO = "http://" + ipAddress + ":" + port + "/data_service/findMacro.do";
		SYNC_FUNCTION = "http://" + ipAddress + ":" + port + "/data_service/findFunCfg.do";
		SYNC_TOWN = "http://" + ipAddress + ":" + port + "/data_service/findTownsInfo.do";
	}

	/**
	 * @return
	 */
	private Map<String, String> initConfig() {
		TabSettingDao dao = new TabSettingDao(context);
		Map<String, String> map = dao.initConfig();
		dao.close();
		return map;
	}

	/**
	 * 进入系统时候异步从服务上同步数据
	 */
	private AsyncTask asyncHttpTask = new AsyncTask() {
		private HttpClient client;

		@Override
		protected Object doInBackground(Object... params) {

			dataDao.initData();
			String json1;
			try {
				json1 = loadData(SYNC_FUNCTION);
				dataDao.initConfig(json1);
				publishProgress("40", "配置数据初始成功");

				String json2 = loadData(SYNC_MACRO);
				dataDao.initDisaster(json2);
				publishProgress("60", "灾害点数据初始成功");

				String json3 = loadData(SYNC_MONITOR);
				dataDao.initMonitor(json3);
				Log.d(TAG, json3);
				publishProgress("80", "监测点数据初始成功");

				// String json4 = loadData(SYNC_COUNTRY);
				// dataDao.initCountry(json4);
				// publishProgress("70","村社信息初始化成功");

				String json5 = loadData(SYNC_TOWN);
				dataDao.initTown(json5);
				publishProgress("80", "乡镇信息初始化成功");

			} catch (IOException e) {
				return "服务器未响应请检查配置或稍后再试";
			} catch (JSONException e) {
				return "未找到该号码的灾害点信息";
			} catch (GdimsException e) {
				return e.getMessage();
			}
			return "success";
		}

		protected void onPostExecute(Object result) {
			dataDao.close();
			client.getConnectionManager().shutdown();
			pd.incrementProgressBy(100);
			pd.dismiss();
			if ("success".equals(result)) {
				finish();
				forward(MainActivity.class);
			} else {
				MyAlert alert = new MyAlert(context);
				alert.alert(result.toString());
			}
		}

		protected void onPreExecute() {
			// 初始化进度条
			pd = new ProgressDialog(context);
			// 设置进度条风格，风格为长形，有刻度的
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			// 设置ProgressDialog 标题
			// pd.setTitle("初始化数据");
			// 设置ProgressDialog 提示信息
			pd.setMessage("正在准备登录");
			pd.setIndeterminate(false);
			pd.show();

			client = new DefaultHttpClient();
			dataDao = new InitDataDao(LoginActivity.this);
		}

		@Override
		protected void onProgressUpdate(Object... values) {
			int diff = Integer.parseInt(values[0].toString());
			Log.d(TAG, diff + "%");
			pd.setProgress(diff);
			pd.setMessage(values[1].toString());
		}

		private String loadData(String url) throws IOException, GdimsException {
			Log.d(TAG, "请求地址为:" + url);
			HttpParams params = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(params, 10000);
			HttpConnectionParams.setSoTimeout(params, 10000);
			HttpPost request = new HttpPost(url);
			request.setParams(params);

			BasicNameValuePair bvp = new BasicNameValuePair("mobile", phoneNum);
			BasicNameValuePair imv = new BasicNameValuePair("imei", imei);
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(bvp);
			list.add(imv);

			request.setEntity(new UrlEncodedFormEntity(list));
			HttpResponse response = client.execute(request);

			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					String result = EntityUtils.toString(resEntity);
					Log.d(TAG, "返回数据为:" + result);
					return result;
				}
			} else {
				int state = response.getStatusLine().getStatusCode();
				throw new GdimsException("远程服务器出现" + state + "错误");
			}
			return null;
		}
	};

	protected void onDestroy() {
		GobalApplication gobal = (GobalApplication) getApplication();
		Map<String, String> cache = gobal.getCache();
		if (cache.size() != 0) {
			cache.clear();
		}
		super.onDestroy();
	}

	/**
	 * 访问服务器成功后的回调方法
	 */
	@Override
	public void getJson(int type, String data) {
		switch (type) {
			case 0 : //群测群防类型返回数据
				try {
					JSONObject jsonObject = new JSONObject(data);
					String status = jsonObject.getString("status");
					JSONArray message = jsonObject.getJSONArray("message");
					
					if (status.equals("200")){
						JSONObject json = message.getJSONObject(0);
						String areaid = json.getString("area_id");
						String name = json.getString("name");
						
						//将返回的区县ID和用户名存入share中
						sp.edit().putString("areaId", areaid).commit(); 
						sp.edit().putString("name", name).commit();
						sp.edit().putString("phoneNum", phoneNum).commit();
						sp.edit().putInt("roletype", role_type).commit();
						//初始化群测群防数据
						getGobal();
						asyncHttpTask.execute();
					} else{
						Toast.makeText(this, "登录失败！账号或密码错误", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} 
				break;
			case 1 : //驻守类型返回数据
				try {
					JSONObject jsonObject = new JSONObject(data);
					String status = jsonObject.getString("status");
					JSONArray message = jsonObject.getJSONArray("message");
					if(status.equals("200")){
						JSONObject json1 = message.getJSONObject(0);
						String name = json1.getString("disname");
						String areaid = json1.getString("area_id");
						JSONObject json2 = message.getJSONObject(1);
						String usertype = json2.getString("user_type"); //1代表片区专管员，2代表驻守人员
						
						//将返回的区县ID和用户名、用户类型存入share中
						sp.edit().putString("name", name).commit();
						sp.edit().putString("areaId", areaid).commit(); 
						sp.edit().putInt("roletype", Integer.parseInt(usertype)).commit();
						sp.edit().putString("phoneNum", phoneNum).commit();
						
						Intent intent = new Intent(this, MainActivity.class);
						startActivity(intent);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			default :
				break;
		}
		
	}

	/**
	 * 访问服务器失败后的回调方法
	 */
	@Override
	public void onFailure(HttpException exception, String str) {
		Toast.makeText(this, "登陆失败！请检查网络配置或服务器", Toast.LENGTH_SHORT).show();
	}
	
}
