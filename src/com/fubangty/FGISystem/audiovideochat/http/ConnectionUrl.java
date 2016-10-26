package com.fubangty.FGISystem.audiovideochat.http;

public class ConnectionUrl {
	/*总体的路径*/
	public static final String URL1 = "http://183.230.108.112:8085/cmdcity/";
//	public static final String URL1 = "http://192.168.155.1:8080/cmdcity/";
	/* AnyChat服务器地址  */
	public static final String IP = "183.230.108.112";
	public static final String PORT = "8906";
	/* 获取区县Id */
	public static String URL_REGIST = URL1+"PhoneMeeting.do";
	/* 获取版本号  */
	public static String URL_VERSION = URL1+"haveNewVersion.do";
	/* 下载APK */
	public static String URL_APK = URL1+"downloadApk.do";
	/* 用户注册   */
	public static String URL_REGISTER = URL1+"androidRegister.do";
	/* 用户登录   */
	public static String URL_LOGIN = URL1+"androidLogin.do";
	/* 轮询接口  */
	public static String URL_HASVIDEO = URL1+"findPushMsg.do";
	
	public static String URL_FROM = URL1+"findPushMsg.do";
	
	public static String URL_CANCLE = URL1+"cancleId.do";
	/* 查找是否上传图片 */
	public static String From_image = URL1+"findPhoto.do";
	/* 图片上传 */
	public static String UPIMAGE = URL1+"uploadPhoto.do";
	
}
