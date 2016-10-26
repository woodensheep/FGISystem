package com.fubangty.FGISystem.audiovideochat.utils.image;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.os.Environment;

public class FileUtils {

	public static String SDPATH = Environment.getExternalStorageDirectory()
			+ "/Photo_cache/";
	
	public static void saveBitmap(Bitmap bm, String picName){
		try {
			if (!isFileExist("")) {
				File tempf = createSDDir("");
			}
			File f = new File(SDPATH, picName + ".JPEG"); 
			if (f.exists()) {
				f.delete();
			}
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static File createSDDir(String dirName) throws IOException {
		File dir = new File(SDPATH + dirName);
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			System.out.println("createSDDir:" + dir.getAbsolutePath());
			System.out.println("createSDDir:" + dir.mkdir());
		}
		return dir;
	}
	
	//文件是否存在
	public static boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		file.isFile();
		return file.exists();
	}
	
	//删除文件
	public static void delFile(String fileName){
		File file = new File(SDPATH + fileName);
		if(file.isFile()){
			file.delete();
        }
		file.exists();
	}
	
	//删除文件夹
	public static void deleteDir() {
		File dir = new File(SDPATH);
		/*if (dir == null || !dir.exists() || !dir.isDirectory())
			return;
		
		for (File file : dir.listFiles()) {
			if (file.isFile())
				file.delete(); 
			else if (file.isDirectory())
				deleteDir(); 
		}
		dir.delete();*/
		if(dir.exists()){
			File[] files = dir.listFiles();
			if(files!=null){
				for (File f : files) {
					f.delete();
				}
			}
		} //if结束
	}

	public static boolean fileIsExists(String path) {
		try {
			File f = new File(path);
			if (!f.exists()) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
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
