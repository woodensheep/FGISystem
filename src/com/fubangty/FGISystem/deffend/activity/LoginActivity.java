package com.fubangty.FGISystem.deffend.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;
import com.fubangty.FGISystem.R;
import com.fubangty.FGISystem.deffend.http.CallHttpBiz;
import com.fubangty.FGISystem.deffend.http.ConnectionUrl;
import com.fubangty.FGISystem.deffend.http.MyCallback;
import com.fubangty.FGISystem.deffend.parsjson.LoginJson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * 登陆界面
 * 
 * @author lemon
 *
 */
public class LoginActivity extends Activity implements MyCallback {
	private EditText etName;
	private EditText etPassword;
	private Button btnLogin;
	private Button btnRegister;
	private Button btnCeshi;
	private String phoneID;
	private Context context;
	private SharedPreferences shared;
	private String phoneNum;
	private String userPwd;

	/* 下载中 */
	private static final int DOWNLOAD = 1;
	/* 下载结束 */
	private static final int DOWNLOAD_FINISH = 2;
	/* 记录进度条数量 */
	private int progress;
	private ProgressBar pbUpdatePro;
	/* 是否取消更新 */
	protected String path;
	private SharedPreferences sp;
	private int currentVersion = 0;// 从服务器获取的版本号
	private boolean cancelUpdate = false; // 是否取消更新
	private AlertDialog downloadDialog;

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case DOWNLOAD:
				pbUpdatePro.setProgress(progress);
				break;
			case DOWNLOAD_FINISH:
				installApk();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		//检查更新
		//checkUpdate();
		context = this;
		TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
		phoneID = tm.getDeviceId();
		SharedPreferences shared = context.getSharedPreferences("defendInfo", Context.MODE_PRIVATE);
		shared.edit().putString("phoneID", phoneID).commit();
		initViews();
		setListener();
	}

	private void initViews() {
		etName = (EditText) findViewById(R.id.et_login_username);
		shared = context.getSharedPreferences("defendInfo", Context.MODE_PRIVATE);
		etPassword = (EditText) findViewById(R.id.et_login_password);
		btnLogin = (Button) findViewById(R.id.btn_login);
		btnRegister = (Button) findViewById(R.id.btn_register);
		etName.setText(shared.getString("phoneNum", ""));
		etPassword.setText(shared.getString("userPwd", ""));
	}

	private void setListener() {
		btnLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (etPassword.getText().toString().equals("")) {
					Toast.makeText(getApplicationContext(), "请输入用户名或密码", Toast.LENGTH_LONG).show();
				} else {

					sendMessage(1);
				}
			}
		});

		btnRegister.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, RegisterActivity.class);
				startActivity(intent);
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
		userPwd = etPassword.getText().toString().trim();
		params.addBodyParameter("userPwd", etPassword.getText().toString().trim()); // 密码
		params.addBodyParameter("phoneID", phoneID); // 手机唯一标识IMEI
		call.PostData(2, new ConnectionUrl().LOGIN, params, this);
	}

	/**
	 * 访问服务器成功后的回调方法
	 */
	@Override
	public void getJson(int type, String data) {
		switch (type) {
		case 2:
			// 判断返回数据state是否为200
			LoginJson loginJson = new LoginJson(context);
			if (loginJson.parserData(data)) {
				SharedPreferences shared = context.getSharedPreferences("defendInfo", Context.MODE_PRIVATE);
				shared.edit().putString("phoneNum", etName.getText().toString().trim()).commit();
				shared.edit().putString("userPwd", etPassword.getText().toString().trim()).commit();
				try {
					shared.edit().putString("userPwd", userPwd).commit();
					String userType = new JSONObject(data).optString("userType");
					// 2是驻守人员
					if ("2".equals(userType)) {
						Intent intent = new Intent(context, MainActivity.class);
						startActivity(intent);
						finish();
					} else if ("1".equals(userType)) {
						Intent intent = new Intent(context, HeaderActivity.class);
						startActivity(intent);
						finish();
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			break;
		case 3:
			try {
				JSONObject jsonObject = new JSONObject(data);
				Log.d("limeng", "jsonObject:"+jsonObject.toString());
				String status = jsonObject.getString("status");
				if(status.equals("200")){
				String datas = jsonObject.getString("data");
				currentVersion = Integer.valueOf(datas);
				System.out.println("服务器返回版本号：" + currentVersion);
				int versionCode = getVersionCode();
				if (currentVersion > versionCode) {
					showNoticeDialog();
					System.out.println("发现新版本");
				}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}

	}

	/**
	 * 访问服务器失败后的回调方法
	 */
	@Override
	public void onFailure(HttpException exception) {
		Toast.makeText(getApplicationContext(), "登陆失败！请检查网络配置或服务器", Toast.LENGTH_LONG).show();
	}

	/*
	 * 发送请求从服务器上获取版本号
	 */
	private void checkUpdate() {
		CallHttpBiz call = new CallHttpBiz(this);
		String url = ConnectionUrl.URL_VERSION;
		call.GetData(3, url, null, this);
	}

	/*
	 * 安装APK文件
	 */
	protected void installApk() {
		File apkfile = new File(path, "Defend.apk");
		if (!apkfile.exists()) {
			return;
		}
		// 通过Intent安装APK文件
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
		startActivity(i);
	}

	/**
     * 打开已经安装好的apk
     */
    private void openApk(Context context, String url) {
        PackageManager manager = context.getPackageManager();
        // 这里的是你下载好的文件路径
        PackageInfo info = manager.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            Intent intent = manager.getLaunchIntentForPackage(info.applicationInfo.packageName);
            startActivity(intent);
        }
    }
	
	/**
	 * 发现新版本提示对话框
	 */
	private void showNoticeDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("软件更新");
		builder.setMessage("发现新版本，立即更新吗？");
		builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				showDownload();
			}
		});
		builder.setNegativeButton("下次更新", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		Dialog dialog = builder.create();
		dialog.show();
	}

	/**
	 * 下载对话框
	 */
	protected void showDownload() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("正在更新");
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_update_progress, null);
		pbUpdatePro = (ProgressBar) view.findViewById(R.id.update_progress);
		builder.setView(view);
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				cancelUpdate = true;
			}
		});
		downloadDialog = builder.create();
		downloadDialog.show();
		downloadApk();
	}

	/**
	 * 下载apk文件线程
	 */
	private void downloadApk() {
		new Thread() {
			public void run() {
				try {
					if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
						path = Environment.getExternalStorageDirectory() + "/Download";
						File file = new File(path);
						// 判断文件目录是否存在
						if (!file.exists()) {
							file.mkdir();
						}
						File apkfile = new File(path, "Defend.apk");
						URL url = new URL(ConnectionUrl.URL_APK);
						HttpURLConnection connection = (HttpURLConnection) url.openConnection();
						connection.connect();
						// 获取文件大小
						int length = connection.getContentLength();
						System.out.println("文件大小" + length);
						// 创建输入流
						InputStream is = connection.getInputStream();
						FileOutputStream fos = new FileOutputStream(apkfile);
						int count = 0;
						// 缓存
						byte[] buf = new byte[1024];
						// 写入到文件中
						do {
							int numread = is.read(buf);
							count += numread;
							// 计算进度条位置
							progress = (int) (((float) count / length) * 100);
							System.out.println("更新位置" + progress);
							// 更新进度
							handler.sendEmptyMessage(DOWNLOAD);
							if (numread <= 0) {
								// 下载完成
								handler.sendEmptyMessage(DOWNLOAD_FINISH);
								break;
							}
							// 写入文件
							fos.write(buf, 0, numread);
						} while (!cancelUpdate);// 点击取消就停止下载.
						fos.close();
						is.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				downloadDialog.dismiss();
			};
		}.start();
	}

	/**
	 * 获取软件版本号
	 */
	private int getVersionCode() {
		int versionCode = 0;
		try {
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			versionCode = getPackageManager().getPackageInfo("com.fubangty.deffend", 0).versionCode;
			System.out.println("当前版本号：" + versionCode);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

}
