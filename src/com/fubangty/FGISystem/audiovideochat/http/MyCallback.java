package com.fubangty.FGISystem.audiovideochat.http;

import com.lidroid.xutils.exception.HttpException;

/**
 * <font color="green">接口，作用是解析json数据</font>
 * @ClassName MyCallback
 * @author 樊艳红
 * @date 2016年3月4日 下午5:38:30
 *
 * @version
 */
public interface MyCallback {
	public void getJson(int type, String data);
	public void onFailure(HttpException exception, String str);
}
