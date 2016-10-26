/**
 * Copyright 2008-2012. 重庆富邦科技发展有限公司, Inc. All rights reserved.
 * <a>http://www.3gcq.cn</a>
 */
package com.fubangty.FGISystem.gdims.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.util.Log;

/**
 * @author liyun create 2012-6-6
 * @version 0.1
 * 
 */
public class HttpUtil {

	private static final int CONNECTION_TIMEOUT = 100000;// 连接超时时间
	private static final int SO_TIMEOUT = 100000;// socket 超时时间 接收数据时所等待的时间
	private static final String FILE_NAME = "uploads";

	/**
	 * 普通数据post上传
	 * 
	 * @param url
	 *            请求的url地址
	 * @param parames
	 *            key=参数 value=为要传的值
	 * @return 服务器响应值
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String doPost(String url, Map<String, String> params) throws ClientProtocolException, IOException {

		HttpPost request = new HttpPost(url);
		Log.d("request", "请求的地址为："+url);
		System.out.println(params);
		HttpParams httpParameters = new BasicHttpParams();
		// 设置超时时间
		HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParameters, SO_TIMEOUT);
		request.setParams(httpParameters);

		HttpClient client = new DefaultHttpClient();
		try {
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				NameValuePair value = new BasicNameValuePair(entry.getKey(), entry.getValue());
				list.add(value);
			}
			request.setEntity(new UrlEncodedFormEntity(list, "utf-8"));
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					return EntityUtils.toString(entity);
				}
			}
		} finally {
			client.getConnectionManager().shutdown();
		}
		return null;
	}
//	/**
//	 * 普通数据post上传
//	 * 
//	 * @param url
//	 *            请求的url地址
//	 * @param parames
//	 *            key=参数 value=为要传的值
//	 * @return 服务器响应值
//	 * @throws IOException
//	 * @throws ClientProtocolException
//	 */
//	public static String doPosts(String url,String data) {
//
//		HttpPost request = new HttpPost(url);
//		Log.d("request", "请求的地址为："+url);
//		HttpParams httpParameters = new BasicHttpParams();
//		// 设置超时时间
//		HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
//		HttpConnectionParams.setSoTimeout(httpParameters, SO_TIMEOUT);
//		request.setParams(httpParameters);
//
//		HttpClient client = new DefaultHttpClient();
//		try {
//			List<NameValuePair> list = new ArrayList<NameValuePair>();
//			for (Map.Entry<String, String> entry : params.entrySet()) {
//				NameValuePair value = new BasicNameValuePair(entry.getKey(), entry.getValue());
//				list.add(value);
//				HttpEntity entity = params.entry.getEntity();
//				data=EntityUtils.toString(entity);
//			}
//			request.setEntity(new UrlEncodedFormEntity(list, "utf-8"));
//			HttpResponse response = client.execute(request);
//			if (response.getStatusLine().getStatusCode() == 200) {
//				HttpEntity entity = response.getEntity();
//				if (entity != null) {
//					return EntityUtils.toString(entity);
//				}
//			}
//		} finally {
//			client.getConnectionManager().shutdown();
//		}
//		return null;
//	}

	/**
	 * 带文件的数据提交
	 * 
	 * @param url
	 *            请求url地址
	 * @param params
	 *            要提交的普通文本数据
	 * @param uploads
	 *            要提交的文件数据
	 * @return 服务器响应
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String uploadMulti(String url, Map<String, String> params, File[] uploads) throws ClientProtocolException, IOException {

		HttpPost request = new HttpPost(url);
		HttpParams httpParameters = new BasicHttpParams();
		// 设置超时时间
		HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParameters, SO_TIMEOUT);
		request.setParams(httpParameters);
		HttpClient client = new DefaultHttpClient();
		try {
			MultipartEntity me = new MultipartEntity();
			if (!params.isEmpty()) {
				// for(Map.Entry<String, String> entry : params.entrySet())
				for (String key : params.keySet()) {
					StringBody sb = new StringBody(params.get(key), Charset.forName("utf-8"));
					me.addPart(key, sb);
				}
			}

			for (File file : uploads) {
				if (file != null && file.exists()) {
					FileBody fb = new FileBody(file);
					me.addPart(FILE_NAME, fb);
				}
			}
			request.setEntity(me);
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					return EntityUtils.toString(entity);
				}
			}
		} finally {
			client.getConnectionManager().shutdown();
		}
		return null;
	}

}
