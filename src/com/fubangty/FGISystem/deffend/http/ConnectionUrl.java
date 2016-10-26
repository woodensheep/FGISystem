package com.fubangty.FGISystem.deffend.http;


/**
 * 上传url
 * @author lemon
 *
 */
public class ConnectionUrl {
	/* 总体的路径 */
	public static final String URL = "http://183.230.108.112:8076/wish/";
	public static final String URL1 = "http://192.168.3.138:8080/wish/";
	/* 登陆 */
	public String LOGIN = URL + "checkLogin.do";
	/* 注册*/
	public String REGISTER = URL + "checkRegister.do";
	/* 工作日志已上传获取*/
	public String LOGHASREPORT = URL + "findLogByPhoneAndTime.do";
	/* 工作日志上传*/
	public String UPLOG = URL + "saveWorkLog.do";
	/* 周报上传*/
	public String UPWEEKLY = URL + "saveWeekLog.do";
	/* SITUATION图片上传 */
	public String UPSITUATIONIMAGE = URL + "disPic.do";
	/* SITUATION文本上传 */
	public String UPSITUATIONTEXT = URL+ "saveDisater.do";
	/* SITUATION文本上传 */
	public String UPMAP = URL+ "saveLocation.do";
	/* 发送请求从服务器上获取版本号 */
	public static String URL_VERSION = URL+ "haveNewVersion.do";
	/* 下载apk文件 */
	public static String URL_APK =  URL+ "downloadApk.do";
	
	
}
