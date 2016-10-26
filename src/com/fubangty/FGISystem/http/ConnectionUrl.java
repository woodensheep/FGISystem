package com.fubangty.FGISystem.http;

public class ConnectionUrl {
	/*总体的路径*/
	public static final String URL = "http://192.168.3.123:8080/data_service/";
	
	/* 用户登录  */
	public static String URL_LOGIN = URL + "findLogin.do"; 
	/* 用户定位  */
	public static String URL_POSITION = URL + "updatePosition.do"; 
	
	
	
}
