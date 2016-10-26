/**
 * Copyright 2008-2012. 重庆富邦科技发展有限公司, Inc. All rights reserved.
 * <a>http://www.3gcq.cn</a>
 */
package com.fubangty.FGISystem.gdims.util;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.AsyncTask;

/**
 * @author liyun create 2012-6-2
 * @version 0.1
 *
 */
public class AsyncDataTask extends AsyncTask{
	
	private Context context;
	private Callback callback;
	
	public AsyncDataTask(Context context,Callback callback) {
		this.context = context;
		this.callback = callback;
	}

	@Override
	protected Object doInBackground(Object... params) {
		//return retSrc;
		return callback.onStart(params);
	}
	
	@Override
	protected void onPostExecute(Object result) {
		
		callback.onFinish(result);
	}
	
	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
	}
	
	@Override
	protected void onPreExecute() {
		
		callback.onPrepare();
	}
	
	@Override
	protected void onProgressUpdate(Object... values) {
		callback.onUpdate(values);
	}


	public interface Callback {
		/**
		 * 数据加载前操作 
		 * Description: <br>
		 */
		public void onPrepare();
		/**
		 * 加载数据操作
		 * Description: <br>
		 * @param params
		 * @return
		 */
		public Object onStart(Object... params);
		/**
		 * 数据加载完成
		 * Description: <br>
		 * @param result
		 */
		public void onFinish(Object result);
		
		/**
		 * 实时更新数据
		 * @param values
		 */
		public void onUpdate(Object... values);
	}
	
}




