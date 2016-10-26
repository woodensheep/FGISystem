package com.fubangty.FGISystem.deffend.util;

import java.io.File;

import android.os.Environment;

public class FileUtils {
	public static String SDPATH = Environment.getExternalStorageDirectory()
			+ "/Photo_cache/";
	public static File getFile(){
		//以系统时间为照片命名
		String picName = String.valueOf(System.currentTimeMillis()); 
		File dFile=new File(SDPATH);
		if(!dFile.exists()){//如果文件夹不存在，则创建
			dFile.mkdirs();   		 
		}
		File file=new File(SDPATH, picName + ".png");
		return file;
	}
}
