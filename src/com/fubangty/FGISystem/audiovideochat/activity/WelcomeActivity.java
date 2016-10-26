package com.fubangty.FGISystem.audiovideochat.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fubangty.FGISystem.R;
import com.fubangty.FGISystem.audiovideochat.http.CallHttpBiz;
import com.fubangty.FGISystem.audiovideochat.http.ConnectionUrl;
import com.fubangty.FGISystem.audiovideochat.http.MyCallback;
import com.lidroid.xutils.exception.HttpException;

/**
 * <font color="green">APP欢迎界面，检查更新</font>
 * 
 * @ClassName WelcomeActivity
 * @author Administrator
 * @date 2016年7月27日 下午1:38:52
 *
 * @version
 */
public class WelcomeActivity extends Activity implements MyCallback {
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
				case DOWNLOAD :
					pbUpdatePro.setProgress(progress);
					break;
				case DOWNLOAD_FINISH :
					installApk();
					break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		System.out.println("进入欢迎页面！");
		sp = getSharedPreferences("config", MODE_PRIVATE);
		checkUpdate();
	}

	/*
	 * 发送请求从服务器上获取版本号
	 */
	private void checkUpdate() {
		CallHttpBiz call = new CallHttpBiz(this);
		String url = ConnectionUrl.URL_VERSION;
		call.getData(3, url, null, this);
	}

	/*
	 * 安装APK文件
	 */
	protected void installApk() {
		File apkfile = new File(path, "AudioVideoChat.apk");
		if (!apkfile.exists()) {
			return;
		}
		// 通过Intent安装APK文件
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		startActivity(i);
	}

	/**
	 * 发现新版本提示对话框
	 */
	private void showNoticeDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("软件更新");
		builder.setMessage("发现新版本，立即更新吗？");
		builder.setPositiveButton("立即更新", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				showDownload();
			}
		});
		builder.setNegativeButton("下次更新", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (sp.getBoolean("isLogin", false)) { // 如果已登录，直接进入主界面
//					Intent intent = new Intent(WelcomeActivity.this,
//							SelectActivity.class);
//					startActivity(intent);
					finish();
				} else { // 否则，进入用户登录界面
					Intent intent = new Intent(WelcomeActivity.this,
							LoginActivity.class);
					startActivity(intent);
					finish();
				}
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
		View view = LayoutInflater.from(this).inflate(
				R.layout.dialog_update_progress, null);
		pbUpdatePro = (ProgressBar) view.findViewById(R.id.update_progress);
		builder.setView(view);
		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				cancelUpdate = true;
				if (sp.getBoolean("isLogin", false)) {
//					Intent intent = new Intent(WelcomeActivity.this,
//							SelectActivity.class);
//					startActivity(intent);
					finish();
				} else {
					Intent intent = new Intent(WelcomeActivity.this,
							LoginActivity.class);
					startActivity(intent);
					finish();
				}
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
					if (Environment.getExternalStorageState().equals(
							Environment.MEDIA_MOUNTED)) {
						path = Environment.getExternalStorageDirectory()
								+ "/download";
						File file = new File(path);
						// 判断文件目录是否存在
						if (!file.exists()) {
							file.mkdir();
						}
						File apkfile = new File(path, "AudioVideoChat.apk");
						URL url = new URL(ConnectionUrl.URL_APK);
						HttpURLConnection connection = (HttpURLConnection) url
								.openConnection();
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
					//如果抛出异常，忽略不管，直接进入APP
					if (sp.getBoolean("isLogin", false)) {
//						Intent intent = new Intent(WelcomeActivity.this,
//								SelectActivity.class);
//						startActivity(intent);
						finish();
					} else {
						Intent intent = new Intent(WelcomeActivity.this,
								LoginActivity.class);
						startActivity(intent);
						finish();
					}
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
			versionCode = getPackageManager().getPackageInfo(
					"com.fubangty", 0).versionCode;
			System.out.println("当前版本号：" + versionCode);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	@Override
	public void getJson(int type, String data) {
		if (type == 3) {
			currentVersion = Integer.valueOf(data);
			System.out.println("服务器返回版本号：" + currentVersion);
			int versionCode = getVersionCode();
			if (currentVersion > 100) {
				showNoticeDialog();
				System.out.println("发现新版本");
			} else {
				System.out.println("没有发现新版本");
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (sp.getBoolean("isLogin", false)) {
//							Intent intent = new Intent(WelcomeActivity.this,
//									SelectActivity.class);
//							startActivity(intent);
							finish();
						} else {
							Intent intent = new Intent(WelcomeActivity.this,
								LoginActivity.class);
							startActivity(intent);
							finish();
						}
					}
				}, 2000);
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK &&
				event.getAction()==KeyEvent.ACTION_DOWN){
			finish();
		} 
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onFailure(HttpException exception, String str) {
		// 获取版本信息失败
		System.out.println("获取版本号失败");
		Toast.makeText(WelcomeActivity.this, "获取版本失败！请检查网络或服务器",
				Toast.LENGTH_SHORT).show();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (sp.getBoolean("isLogin", false)) {
//					Intent intent = new Intent(WelcomeActivity.this,
//							SelectActivity.class);
//					startActivity(intent);
					finish();
				} else {
					Intent intent = new Intent(WelcomeActivity.this,
							LoginActivity.class);
					startActivity(intent);
					finish();
				}
			}
		}, 2000);
	}
}
