package com.fubangty.FGISystem.audiovideochat.http;

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
 * @author 樊艳红
 * @date 2016年3月4日 上午11:52:35
 *
 * @version
 */
public class httpBiz {
	private  HttpUtils mHttpClient;
	private  HttpClientListener mHttpClientListener;
	public final int POSTNUM=1;
	public final int GETNUM=0;
	private Context context;
	private Handler handler = new Handler();
	
    public void getPostData(int type, String url, RequestParams mRequestParams,
    		MyCallback callback, int sendMethod, Context context){
    	this.context=context;
    	this.mHttpClient = new HttpUtils(3*3000);//连接的超时时间，默认15秒
    	this.mHttpClient.configCurrentHttpCacheExpiry(3000);//设置超时的时间
		/** 网络客户端助手监听 */
		this.mHttpClientListener = new HttpClientListener(type,callback);
		if (sendMethod==POSTNUM) {
			//传递的方式，url地址，传递的参数，回调方法（RequestCallBack<String>返回的类型（一般是字符串））
			System.out.println("post方式");
			mHttpClient.send(HttpMethod.POST,url, mRequestParams, mHttpClientListener);
		}
		else if(sendMethod==GETNUM){
			mHttpClient.send(HttpMethod.GET, url, mHttpClientListener);
		}
    }
    
    private class HttpClientListener extends RequestCallBack<String> {
    	MyCallback callback;
    	int type;
    	
		public HttpClientListener(int type, MyCallback callback) {
			this.callback = callback;
			this.type = type;
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
		
		@Override
		public void onFailure(HttpException arg0, String arg1) {
			callback.onFailure(arg0, arg1);
			System.out.println("101请求失败："+arg1);
		}
	};
  
}
