package com.fubangty.FGISystem.audiovideochat.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Handler;
import android.os.Message;

import com.fubangty.FGISystem.audiovideochat.utils.image.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

public class ImageUp {
	private static int success;
	private static int faile;
	
	public static void sendImg(final Context context, String uuid,
			final Handler handler) {
		System.out.println("进入了sendImg");
		HttpUtils httpUtils = new HttpUtils(60000 * 2); // 设置超时时间为2分钟
		
		for(int i=0; i<BitmapUtils.tempSelectBitmap.size(); i++){
			Bitmap bitmap = BitmapUtils.tempSelectBitmap.get(i).getBitmap();
			ByteArrayOutputStream ops = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.PNG, 100, ops);
			byte[] bs = ops.toByteArray();
			ByteArrayInputStream ips = new ByteArrayInputStream(bs);
			String filename = UUID.randomUUID().toString() + ".png";
			
			RequestParams params = new RequestParams();
			params.addBodyParameter("uuid", uuid);
			params.addBodyParameter("upload", ips, bs.length, filename,
					"image/png");
			
			System.out.println("uuid："+uuid);
			System.out.println("图片上传的url："+ConnectionUrl.UPIMAGE);
			httpUtils.send(HttpMethod.POST, ConnectionUrl.UPIMAGE, params,
					new RequestCallBack<String>() {
						// 在主线程中执行的回调方法
						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							String json = responseInfo.result; //得到服务器返回的信息
							System.out.println("json:"+json.toString());
							success++;
							if(success == BitmapUtils.tempSelectBitmap.size()){
								try {
									JSONObject obj = new JSONObject(json);
									if(obj.getString("status").equals("200")){
										Message msg = handler.obtainMessage(2);
										handler.sendMessage(msg);
									} else {
										Message msg = handler.obtainMessage(3);
										handler.sendMessage(msg);
									}
								} catch (Exception e) {
									Message msg = handler.obtainMessage(3);
									handler.sendMessage(msg);
									System.out.println("catch图片上传失败："+ e.toString());
								}
							} //if结束
						}
						
						@Override
						public void onFailure(HttpException e, String str) {
							faile++;
							if(faile == BitmapUtils.tempSelectBitmap.size()){
								Message msg = handler.obtainMessage(3);
								handler.sendMessage(msg);
								System.out.println("onFailure图片上传失败:"+e);
							}
						}
					});
			
		}
		
	}
	
}
