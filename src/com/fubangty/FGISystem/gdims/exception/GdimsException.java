/**
 * Copyright 2008-2012. 重庆富邦科技发展有限公司, Inc. All rights reserved.
 * <a>http://www.3gcq.cn</a>
 */
package com.fubangty.FGISystem.gdims.exception;

/**
 * @author liyun create 2012-8-11
 * @version 0.1
 *
 */
public class GdimsException extends Exception {
	
	public GdimsException(String message ,Throwable throwable){
		super(message, throwable);
	}
	
	public GdimsException(){
		super();
	}
	
	public GdimsException(String message){
		super(message);
	}

}
