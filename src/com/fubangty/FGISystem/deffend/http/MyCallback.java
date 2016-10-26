package com.fubangty.FGISystem.deffend.http;

import com.lidroid.xutils.exception.HttpException;

/**
 * <font color="green">回调接口，解析服务器返回的json数据</font>
 * @ClassName MyCallback
 * @author 包宏燕
 * @date 2016年5月11日 下午4:08:58
 *
 * @version
 */
public interface MyCallback {
	public void getJson(int type,String  data);
	public void onFailure(HttpException exception);
}
