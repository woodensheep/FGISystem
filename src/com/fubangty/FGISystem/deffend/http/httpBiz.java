package com.fubangty.FGISystem.deffend.http;

import android.content.Context;
import android.os.Handler;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * <font color="green">网络连接的工具类</font>
 * @ClassName httpBiz
 * @author 包宏燕
 * @date 2016年5月12日 上午11:32:13
 *
 * @version
 */
public class httpBiz {
	private Context context;
	private HttpUtils mHttpClient;
	private HttpClientListener mHttpClientListener;
	private Handler handler = new Handler(); 
	public final int POSTNUM=1;
	public final int GETNUM=0;
	
	public void getPostData(int type, String url, RequestParams mRequestParams,
			MyCallback callback,int sendMethod,Context context){
		this.context = context;
		this.mHttpClient = new HttpUtils(3*3000); //连接的超时时间，默认15秒
		this.mHttpClient.configCurrentHttpCacheExpiry(3000);//设置超时的时间
		this.mHttpClientListener = new HttpClientListener(type, callback);
		//post方式
		if(sendMethod == POSTNUM){
			//传递的方式，URL地址，传递的参数，回调方法(RequestCallBack<String>返回的类型（一般是字符串）)
			System.out.println("post方式");
			mHttpClient.send(HttpMethod.POST, url, mRequestParams, mHttpClientListener);
		}
		//get方式
		else if(sendMethod == GETNUM){
			System.out.println("post方式");
			mHttpClient.send(HttpMethod.GET, url, mHttpClientListener);
		}
	}
	
	/**
	 * 网络客户端助手监听
	 */
	private class HttpClientListener extends RequestCallBack<String>{
		MyCallback callback;
		int type;
		
		public HttpClientListener(int type, MyCallback callback) {
			this.type = type;
			this.callback = callback;
		}

		@Override
		public void onFailure(HttpException exception, String msg) {
			System.out.println("失败："+exception);
			callback.onFailure(exception);
		}

		@Override
		public void onSuccess(final ResponseInfo<String> responseInfo) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					System.out.println("返回的数据："+responseInfo.result);
					callback.getJson(type, responseInfo.result);
				}
			});
		}
	};
	
}
