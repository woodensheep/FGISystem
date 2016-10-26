
package com.fubangty.FGISystem.gdims.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
import org.json.JSONException;
import com.fubangty.FGISystem.R;
import com.fubangty.FGISystem.gdims.base.BaseActivity;
import com.fubangty.FGISystem.gdims.dao.InitDataDao;
import com.fubangty.FGISystem.gdims.dao.TabSettingDao;
import com.fubangty.FGISystem.gdims.dialog.MyAlert;
import com.fubangty.FGISystem.gdims.dialog.SetIPDialog;
import com.fubangty.FGISystem.gdims.exception.GdimsException;
import com.fubangty.FGISystem.gdims.util.GobalApplication;
import com.fubangty.FGISystem.gdims.util.UpdateManager;
import com.fubangty.FGISystem.gdims.util.Utils;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;

/**
 * 首页LOGO界面
 * 
 * @author liqingsong
 * @version 0.1
 * 
 */
public class LogoActivity extends BaseActivity {

	private ProgressDialog pd;
	private InitDataDao dataDao;
	private SetIPDialog dialog;
	private String ipAddress="183.230.108.112";
	private String port="80";
	private String mobile="15702316923";
	private String imei;
	private static final int SETTING = 999;

	private String SYNC_MONITOR;
	private String SYNC_MACRO;
	private String SYNC_FUNCTION;
	private String SYNC_TOWN;
	private String SYNC_COUNTRY;
	private String SYNC_VERSION;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// pd = ProgressDialog.show(this, null, MSG, true, true);
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 取消状态栏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.logo);
//		Button settings = (Button) findViewById(R.id.settings);
//		Button joinUp = (Button) findViewById(R.id.joinUp);
//		settings.setOnClickListener(this);
//		joinUp.setOnClickListener(this);
	
//		Map<String, String> map = initConfig();
//		if (map == null) {
//			initDialog();
//		} else {
			getGobal();
			asyncHttpTask.execute();
//		}

	}

	private void getGobal() {
		 TabSettingDao dao = new TabSettingDao(context);
		dao.saveSetting(ipAddress,port,mobile);
		dao.close();
		Map<String, String> map = initConfig();
		imei = Utils.getImei(context);
		ipAddress = map.get("ipAddress");
		port = map.get("port");
		mobile = map.get("mobile");
		GobalApplication gobal = (GobalApplication) getApplication();
		gobal.setIpAddress(ipAddress);
		gobal.setMobile(mobile);
		gobal.setPort(port);

		SYNC_MONITOR = "http://" + ipAddress + ":" + port + "/meteor/findMonitor.do";
		SYNC_MACRO = "http://" + ipAddress + ":" + port + "/meteor/findMacro.do";
		SYNC_FUNCTION = "http://" + ipAddress + ":" + port + "/meteor/findFunCfg.do";
		SYNC_TOWN = "http://" + ipAddress + ":" + port + "/meteor/findTownsInfo.do";
//		SYNC_VERSION = "http://" + ipAddress + ":" + port + "/meteor/down/version.json";
//
//		UpdateManager update = new UpdateManager(context);
//		update.checkVersion(SYNC_VERSION);
//		update.setApkUrl("http://" + ipAddress + ":" + port + "/meteor/down/");

		// SYNC_COUNTRY =
		// "http://"+ipAddress+":"+port+"/meteor/findCountryInfo.do";
	}

//	private void initDialog() {
//		dialog = new SetIPDialog(this, this);
//		// 点击其他地方不会让dialog消失
//		dialog.setCanceledOnTouchOutside(false);
//		WindowManager windowManager = getWindowManager();
//		Display display = windowManager.getDefaultDisplay();
//		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
//		lp.width = (int) (display.getWidth() * 0.8); // 设置宽度
//		dialog.getWindow().setAttributes(lp);
//		dialog.show();
//
//		// 为弹出框设置IP和端口
//
//		dialog.IP_edit.setText(ipAddress);
//		dialog.Port_edit.setText(port);
//		dialog.Mobile_edit.setText(mobile);
//	}

	/**
	 * 
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
				showToast("初始化数据完毕");
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
			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			// 设置ProgressDialog 标题
			// pd.setTitle("初始化数据");
			// 设置ProgressDialog 提示信息
			pd.setMessage("正在准备登录");
			pd.setIndeterminate(false);
			pd.show();

			client = new DefaultHttpClient();
			dataDao = new InitDataDao(LogoActivity.this);
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

			BasicNameValuePair bvp = new BasicNameValuePair("mobile", mobile);
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
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.settings: // "设置"按钮
//			initDialog();
//			break;
//		case R.id.joinUp:// "登录按钮"
//			getGobal();
//			asyncHttpTask.execute();
//			break;
//		case R.id.sure_btns:
//			// if(TextUtils.isEmpty(dialog.IP_edit.getText().toString())){
//			// showToast("请设置IP");
//			// dialog.IP_edit.requestFocus();
//			// }if(TextUtils.isEmpty(dialog.Port_edit.getText().toString())){
//			// dialog.Port_edit.setError("端口不能为空");
//			// dialog.Port_edit.requestFocus();
//			// }if(TextUtils.isEmpty(dialog.Mobile_edit.getText().toString())){
//			// dialog.Mobile_edit.setError("手机号不能为空");
//			// dialog.Mobile_edit.requestFocus();
//			// }else{
//			// TabSettingDao dao = new TabSettingDao(context);
//			// dao.saveSetting(dialog.IP_edit.getText().toString(),dialog.Port_edit.getText().toString(),
//			// dialog.Mobile_edit.getText().toString());
//			// dao.close();
//			//
//			// dialog.dismiss();
//			// }
//			if (dialog.IP_edit.getText().length() != 0 && dialog.Port_edit.getText().length() != 0
//					&& dialog.Mobile_edit.getText().length() != 0) {
//				TabSettingDao dao = new TabSettingDao(context);
//				dao.saveSetting(dialog.IP_edit.getText().toString(), dialog.Port_edit.getText().toString(),
//						dialog.Mobile_edit.getText().toString());
//				dao.close();
//
//				dialog.dismiss();
//			} else {
//				showToast("请设置参数");
//				dialog.dismiss();
//			}
//			break;
//		case R.id.clean_btns:
//			dialog.dismiss();
//
//		}
//	}
}
