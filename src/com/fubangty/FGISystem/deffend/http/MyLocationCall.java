package com.fubangty.FGISystem.deffend.http;

import com.baidu.location.BDLocation;

/**
 * <font color="green">百度定位回调接口</font>
 * @ClassName MyLocationCall
 * @author 包宏燕
 * @date 2016年5月11日 下午4:12:31
 *
 * @version
 */
public interface MyLocationCall {
	public void getLocation(int type, BDLocation location);
	//释放唤醒锁
	public void releaseWackLock();
}
