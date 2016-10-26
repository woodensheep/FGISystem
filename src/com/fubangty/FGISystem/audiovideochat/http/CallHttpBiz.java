package com.fubangty.FGISystem.audiovideochat.http;

import android.content.Context;

import com.lidroid.xutils.http.RequestParams;

/**
 * <font color="green">网络连接的工具类</font>
 * 
 * @ClassName CallHttpBiz
 * @author 樊艳红
 * @date 2016年3月4日 下午5:38:04
 *
 * @version
 */
public class CallHttpBiz extends httpBiz {
	private Context context;

	public CallHttpBiz(Context context) {
		super();
		this.context = context;
	}
	
	/**
	 * post请求
	 * 
	 * @param type  每次请求的标记
	 * @param url   目标地址
	 * @param mRequestParams   集合对象
	 * @param callback  传递this，底层已经实现了接口回调
	 */
	public void PostData(int type, String url, RequestParams mRequestParams,
			MyCallback callback) {
		System.out.println("url地址：" + url);
		getPostData(type, url, mRequestParams, callback, POSTNUM, context);
	}
	
	/**
	 * get请求
	 * 
	 * @param type
	 * @param url
	 * @param mRequestParams
	 * @param callback
	 */
	public void getData(int type, String url, RequestParams mRequestParams,
			MyCallback callback) {
		getPostData(type, url, mRequestParams, callback, GETNUM, context);
	}
}
