
package com.fubangty.FGISystem.gdims.util;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import com.fubangty.FGISystem.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;


/**
 * @author liqingsong
 * @version 0.1
 *
 */
public class UpdateManager {
	protected String TAG = this.getClass().getSimpleName();
	
	private Context mContext;
	protected String path;
	//提示语
	private String updateMsg = "检测到服务器有新的版本，是否更新";
	
	private String verUrl ;
	
	//返回的安装包url
	private String apkUrl ;
	
	private Dialog noticeDialog;
	
	private Dialog downloadDialog;
	 /* 下载包安装路径 */
    private static final String basePath =getSDPath();
    private static final String savePath =basePath+ "/updatedemo/";
    private static final String saveFileName =savePath + "gdims_android_cq.apk";

    /* 进度条与通知ui刷新的handler和msg常量 */
    private ProgressBar mProgress;
    
    private String fileName;

    
    private static final int DOWN_UPDATE = 1;
    
    private static final int DOWN_OVER = 2;
    
    private static final int HAS_NEW_VERSION =3;
    
    private int progress;
    
    private Thread downLoadThread;
    
    private Thread checkVersionThread;
    
    private boolean interceptFlag = false;
    
    public static String getSDPath(){ 
        File sdDir = null; 
        boolean sdCardExist = Environment.getExternalStorageState()   
                            .equals(Environment.MEDIA_MOUNTED);   //表明对象正在磁盘检查
        if   (sdCardExist)   
        {                              
          sdDir = Environment.getExternalStorageDirectory();//获取跟目录 
       }   
        return sdDir.toString(); 
        
 } 


    private Handler mHandler = new Handler(){
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				break;
			case DOWN_OVER:
				installApk();
				break;
				
			case HAS_NEW_VERSION:
				showNoticeDialog();
			default:
				break;
			}
    	};
    };
    
	public UpdateManager(Context context) {
		this.mContext = context;
	}
	
	public void setApkUrl(String apkUrl) {
		this.apkUrl = apkUrl;
	}
	
	public void setVerUrl(String verUrl) {
		this.verUrl = verUrl;
	}
	
	//外部接口让主Activity调用
	public void checkUpdateInfo(){
		showNoticeDialog();
	}
	
	
	private void showNoticeDialog(){
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("软件版本更新");
		builder.setMessage(updateMsg);
		builder.setPositiveButton("下载", new OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				showDownloadDialog();			
			}
		});
		builder.setNegativeButton("以后再说", new OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();				
			}
		});
		noticeDialog = builder.create();
		noticeDialog.show();
		noticeDialog.setCanceledOnTouchOutside(false);
	}
	
	private void showDownloadDialog(){
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("软件版本更新");
	
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.progress, null);
		mProgress = (ProgressBar)v.findViewById(R.id.progress);
		
		builder.setView(v);
		builder.setNegativeButton("取消", new OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				interceptFlag = true;
			}
		});
		downloadDialog = builder.create();
		downloadDialog.setCanceledOnTouchOutside(false);
		downloadDialog.show();
		
		downloadApk();
	}
	
	private Runnable mdownApkRunnable = new Runnable() {	
		@Override
		public void run() {
			try{
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					path = Environment.getExternalStorageDirectory()
							+ "/download";
					File file = new File(path);
					// 判断文件目录是否存在
					if (!file.exists()) {
						file.mkdir();
					}
					File apkfile = new File(path, "gdims_android_cq.apk");
					URL url = new URL("http://"+apkUrl);
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
						mHandler.sendEmptyMessage(DOWN_UPDATE);
						if (numread <= 0) {
							// 下载完成
							mHandler.sendEmptyMessage(DOWN_OVER);
							System.out.println("下载完成？");
							break;
						}
						// 写入文件
						fos.write(buf, 0, numread);
					} while (!interceptFlag);// 点击取消就停止下载.
					fos.close();
					is.close();
				}
	
//			try {
//				URL url = new URL("http://"+apkUrl);
//			
//				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
//				conn.connect();
//				int length = conn.getContentLength();
//				InputStream is = conn.getInputStream();
//				
//				File file = new File(savePath);
//				if(!file.exists()){
//					file.mkdir();
//				}
//				String apkFile = savePath+fileName;
//				File ApkFile = new File(apkFile);
//				FileOutputStream fos = new FileOutputStream(ApkFile);
//				
//				int count = 0;
//				byte buf[] = new byte[1024];
//				
//				do{   		   		
//		    		int numread = is.read(buf);
//		    		count += numread;
//		    	    progress =(int)(((float)count / length) * 100);
//		    	    //更新进度
//		    	    mHandler.sendEmptyMessage(DOWN_UPDATE);
//		    		if(numread <= 0){	
//		    			//下载完成通知安装
//		    			Log.d(TAG, DOWN_OVER+"========");
//		    			mHandler.sendEmptyMessage(DOWN_OVER);
//		    			break;
//		    		}
//		    		fos.write(buf,0,numread);
//		    	}while(!interceptFlag);//点击取消就停止下载.
//				
//				fos.close();
//				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch(IOException e){
				e.printStackTrace();
			}
			downloadDialog.dismiss();
			
		}
	};
	
	/**
	 * 获取服务器当前版本信息
	 */
	private Runnable checkVerRunnable = new Runnable() {
		
		public void run() {
			
			try {
				
				String json = HttpUtil.doPost(verUrl, new HashMap<String, String>());
				Log.d("version", verUrl);
				System.out.println("数据"+json);
				JSONObject object;
				try {
					object = new JSONObject(json);
					
					int lastVersion = object.getInt("verCode");
					System.out.println("最新版本"+lastVersion);
					fileName = object.getString("apkname");
					Log.d("version", "last version:"+lastVersion);
					int thisVersion = getVerCode(mContext);
					Log.d("version", "this version:"+thisVersion);
					if(lastVersion>thisVersion){
						Message message = new Message();
						message.what = HAS_NEW_VERSION;
						mHandler.sendMessage(message);
					}
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
 				 
				
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	};
	
	/**
	 * 检查服务器端是否有新客户端版本,此方法开放给外部activity使用
	 * @param url
	 */
	public void checkVersion(String url){
		this.verUrl = url;
		checkVersionThread = new Thread(checkVerRunnable);
		checkVersionThread.start();
	}
	
	 /**
     * 下载apk
     * @param url
     */
	
	private void downloadApk(){
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}
	 /**
     * 安装apk
     * @param url
     */
	private void installApk(){
		Log.d(TAG, "路径"+saveFileName);
		File apkfile = new File(path, "gdims_android_cq.apk");
//		File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
        	Log.d(TAG, "软件下载失败");
            return;
        }    
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive"); 
        Log.d(TAG, Uri.parse("file://" + apkfile.toString())+"");
        mContext.startActivity(i);
	
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public  int getVerCode(Context context) {
		int versionNumber = -1;
		try {
			versionNumber = context.getPackageManager().getPackageInfo("com.fbty.gdims.mobile", 0).versionCode;
		System.out.println("当前版本"+versionNumber);
		} catch (NameNotFoundException e) {
			Log.e("version", e.getMessage());
		}
		return versionNumber;
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public  String getVerName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo("com.fbty.gdims.mobile", 0).versionName;
		} catch (NameNotFoundException e) {
			Log.e("version", e.getMessage());
		}
		return verName;
	}


}
